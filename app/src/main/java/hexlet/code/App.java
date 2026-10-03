package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.DirectoryCodeResolver;
import gg.jte.resolve.ResourceCodeResolver;
import hexlet.code.controller.ErrorsController;
import hexlet.code.controller.RootController;
import hexlet.code.controller.UrlChecksController;
import hexlet.code.controller.UrlsController;
import hexlet.code.repository.BaseRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinJte;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class App {

    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    private static final Path TEMPLATES_PATH = Path.of("src", "main", "resources", "templates");
    private static final Path STATIC_PATH = Path.of("src", "main", "resources", "static");
    private static final Path JTE_CLASSES_PATH = Path.of("jte-classes");

    private static int getPort() {
        return Integer.parseInt(System.getenv().getOrDefault("PORT", "7070"));
    }

    private static String getDatabaseUrl() {
        return System.getenv()
                .getOrDefault("JDBC_DATABASE_URL", "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1;");
    }

    private static String getMode() {
        return System.getenv().getOrDefault("APP_ENV", "production");
    }

    private static boolean isDevelopment() {
        return getMode().equals("development") && Files.isDirectory(TEMPLATES_PATH);
    }

    private static String readResourceFile(String fileName) throws IOException {
        var inputStream = App.class.getClassLoader().getResourceAsStream(fileName);
        try (var reader =
                new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    private static TemplateEngine createTemplateEngine() {
        if (isDevelopment()) {
            var codeResolver = new DirectoryCodeResolver(TEMPLATES_PATH);
            return TemplateEngine.create(codeResolver, JTE_CLASSES_PATH, ContentType.Html);
        }
        var classLoader = App.class.getClassLoader();
        var codeResolver = new ResourceCodeResolver("templates", classLoader);
        return TemplateEngine.create(codeResolver, ContentType.Html);
    }

    private static HikariDataSource createDataSource() throws IOException, SQLException {
        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(getDatabaseUrl());
        var dataSource = new HikariDataSource(hikariConfig);

        try (var connection = dataSource.getConnection();
                var statement = connection.createStatement()) {
            statement.execute(readResourceFile("schema.sql"));
        }
        return dataSource;
    }

    public static Javalin getApp() throws IOException, SQLException {
        BaseRepository.dataSource = createDataSource();

        LOG.info(
                "Mode: {}",
                isDevelopment()
                        ? "development, templates and static are read from src"
                        : "production, templates and static are read from classpath");

        return Javalin.create(
                config -> {
                    if (isDevelopment()) {
                        config.bundledPlugins.enableDevLogging();
                        config.staticFiles.add(STATIC_PATH.toString(), Location.EXTERNAL);
                    } else {
                        config.staticFiles.add("/static", Location.CLASSPATH);
                    }
                    config.fileRenderer(new JavalinJte(createTemplateEngine()));

                    config.routes.get(NamedRoutes.rootPath(), RootController::index);
                    config.routes.get(NamedRoutes.urlsPath(), UrlsController::index);
                    config.routes.post(NamedRoutes.urlsPath(), UrlsController::create);
                    config.routes.get(NamedRoutes.urlPath("{id}"), UrlsController::show);
                    config.routes.post(
                            NamedRoutes.urlChecksPath("{id}"), UrlChecksController::create);

                    config.routes.error(HttpStatus.NOT_FOUND, ErrorsController::notFound);
                    config.routes.exception(Exception.class, ErrorsController::serverError);
                });
    }

    public static void main(String[] args) throws IOException, SQLException {
        getApp().start(getPort());
    }
}
