package hexlet.code;

import static org.assertj.core.api.Assertions.assertThat;

import hexlet.code.model.Url;
import hexlet.code.repository.BaseRepository;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import io.javalin.Javalin;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.stream.Collectors;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AppTest {

    private static Javalin app;
    private static String baseUrl;
    private static MockWebServer mockServer;

    private static String readFixture(String fileName) throws IOException {
        return Files.readString(Path.of("src", "test", "resources", "fixtures", fileName));
    }

    private static String readSchema() throws IOException {
        var stream = AppTest.class.getClassLoader().getResourceAsStream("schema.sql");
        try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(stream))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    /** Адрес мок-сервера в том виде, в каком его сохраняет приложение: без пути и слеша. */
    private static String mockSiteName() {
        var url = mockServer.url("/");
        return "http://" + url.host() + ":" + url.port();
    }

    @BeforeAll
    static void beforeAll() throws IOException, SQLException {
        app = App.getApp();
        app.start(0);
        baseUrl = "http://localhost:" + app.port();
        Unirest.config().followRedirects(false);

        mockServer = new MockWebServer();
        mockServer.start();
    }

    @AfterAll
    static void afterAll() throws IOException {
        app.stop();
        mockServer.shutdown();
    }

    @BeforeEach
    void resetDatabase() throws IOException, SQLException {
        try (var connection = BaseRepository.dataSource.getConnection();
                var statement = connection.createStatement()) {
            statement.execute(readSchema());
        }
    }

    @Test
    void testRootPage() {
        HttpResponse<String> response = Unirest.get(baseUrl + "/").asString();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).contains("Анализатор страниц", "name=\"url\"", "Проверить");
    }

    @Test
    void testUrlsPage() throws SQLException {
        UrlRepository.save(new Url("https://old.example.com"));
        UrlRepository.save(new Url("https://new.example.com"));

        HttpResponse<String> response = Unirest.get(baseUrl + "/urls").asString();
        var body = response.getBody();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(body).contains("data-test=\"urls\"");
        assertThat(body.indexOf("https://new.example.com"))
                .isLessThan(body.indexOf("https://old.example.com"));
    }

    @Test
    void testUrlPage() throws SQLException {
        var url = new Url("https://example.com");
        UrlRepository.save(url);

        HttpResponse<String> response = Unirest.get(baseUrl + "/urls/" + url.getId()).asString();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody())
                .contains("data-test=\"url\"", "https://example.com", "Запустить проверку");
    }

    @Test
    void testUrlPageNotFound() {
        assertThat(Unirest.get(baseUrl + "/urls/999").asString().getStatus()).isEqualTo(404);
        HttpResponse<String> response = Unirest.get(baseUrl + "/urls/abc").asString();
        assertThat(response.getStatus()).isEqualTo(404);
        assertThat(response.getBody()).contains("Страница не найдена");
    }

    @Test
    void testCreateUrl() throws SQLException {
        HttpResponse<String> response =
                Unirest.post(baseUrl + "/urls")
                        .field("url", "HTTPS://Example.com:8080/some/path?q=1")
                        .asString();

        var url = UrlRepository.findByName("https://example.com:8080").orElseThrow();
        assertThat(response.getStatus()).isEqualTo(302);
        assertThat(response.getHeaders().getFirst("Location")).isEqualTo("/urls/" + url.getId());

        HttpResponse<String> page =
                Unirest.get(baseUrl + "/urls/" + url.getId())
                        .header("Cookie", sessionCookie(response))
                        .asString();
        assertThat(page.getBody()).contains("Страница успешно добавлена", "role=\"alert\"");
    }

    @Test
    void testCreateExistingUrl() throws SQLException {
        var existing = new Url("https://example.com");
        UrlRepository.save(existing);

        HttpResponse<String> response =
                Unirest.post(baseUrl + "/urls")
                        .field("url", "https://example.com/other")
                        .asString();

        assertThat(response.getStatus()).isEqualTo(302);
        assertThat(response.getHeaders().getFirst("Location"))
                .isEqualTo("/urls/" + existing.getId());
        assertThat(UrlRepository.getEntities()).hasSize(1);

        HttpResponse<String> page =
                Unirest.get(baseUrl + "/urls/" + existing.getId())
                        .header("Cookie", sessionCookie(response))
                        .asString();
        assertThat(page.getBody()).contains("Страница уже существует");
    }

    @Test
    void testCreateInvalidUrl() throws SQLException {
        HttpResponse<String> response =
                Unirest.post(baseUrl + "/urls")
                        .field("url", "httpsss://abcabca@test.ru")
                        .asString();

        assertThat(response.getStatus()).isEqualTo(422);
        assertThat(response.getBody()).contains("Некорректный URL");
        assertThat(UrlRepository.getEntities()).isEmpty();
    }

    @Test
    void testCheckUrl() throws IOException, SQLException {
        mockServer.enqueue(new MockResponse().setBody(readFixture("page.html")));
        var url = new Url(mockSiteName());
        UrlRepository.save(url);

        HttpResponse<String> response =
                Unirest.post(baseUrl + "/urls/" + url.getId() + "/checks").asString();

        assertThat(response.getStatus()).isEqualTo(302);
        var checks = UrlCheckRepository.findByUrlId(url.getId());
        assertThat(checks).hasSize(1);
        var check = checks.getFirst();
        assertThat(check.getStatusCode()).isEqualTo(200);
        assertThat(check.getTitle()).isEqualTo("Test page title");
        assertThat(check.getH1()).isEqualTo("Test page header");
        assertThat(check.getDescription()).isEqualTo("Test page description");

        HttpResponse<String> page =
                Unirest.get(baseUrl + "/urls/" + url.getId())
                        .header("Cookie", sessionCookie(response))
                        .asString();
        assertThat(page.getBody())
                .contains("Страница успешно проверена", "data-test=\"checks\"", "Test page header");

        HttpResponse<String> list = Unirest.get(baseUrl + "/urls").asString();
        assertThat(list.getBody()).contains(">200<");
    }

    @Test
    void testCheckUrlWithoutSeoTags() throws IOException, SQLException {
        mockServer.enqueue(new MockResponse().setBody(readFixture("empty-page.html")));
        var url = new Url(mockSiteName());
        UrlRepository.save(url);

        Unirest.post(baseUrl + "/urls/" + url.getId() + "/checks").asString();

        var check = UrlCheckRepository.findByUrlId(url.getId()).getFirst();
        assertThat(check.getTitle()).isEmpty();
        assertThat(check.getH1()).isEmpty();
        assertThat(check.getDescription()).isEmpty();
    }

    @Test
    void testCheckUrlWithErrorResponse() throws SQLException {
        mockServer.enqueue(new MockResponse().setResponseCode(500));
        var url = new Url(mockSiteName());
        UrlRepository.save(url);

        HttpResponse<String> response =
                Unirest.post(baseUrl + "/urls/" + url.getId() + "/checks").asString();

        assertThat(UrlCheckRepository.findByUrlId(url.getId())).isEmpty();
        HttpResponse<String> page =
                Unirest.get(baseUrl + "/urls/" + url.getId())
                        .header("Cookie", sessionCookie(response))
                        .asString();
        assertThat(page.getBody()).contains("Произошла ошибка при проверке");
    }

    @Test
    void testCheckUnavailableUrl() throws SQLException {
        var url = new Url("http://localhost:1");
        UrlRepository.save(url);

        Unirest.post(baseUrl + "/urls/" + url.getId() + "/checks").asString();

        assertThat(UrlCheckRepository.findByUrlId(url.getId())).isEmpty();
    }

    @Test
    void testCheckMissingUrl() {
        var response = Unirest.post(baseUrl + "/urls/999/checks").asString();

        assertThat(response.getStatus()).isEqualTo(404);
    }

    /** Flash живёт в сессии: следующий запрос должен прийти с той же cookie. */
    private static String sessionCookie(HttpResponse<?> response) {
        return response.getHeaders().get("Set-Cookie").stream()
                .map(cookie -> cookie.split(";", 2)[0])
                .collect(Collectors.joining("; "));
    }
}
