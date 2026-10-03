package hexlet.code.dto;

/** Главная страница: форма с введённым адресом (после ошибки он остаётся в поле). */
public final class MainPage extends BasePage {

    private final String url;

    public MainPage(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
