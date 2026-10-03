package hexlet.code.service;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;

/** Запрашивает сайт и собирает по нему проверку. */
public final class UrlChecker {

    private UrlChecker() {}

    /**
     * @throws UrlCheckException сайт недоступен или ответил ошибкой 4xx/5xx — проверку не сохраняем
     */
    public static UrlCheck check(Url url) {
        try {
            var response = Unirest.get(url.getName()).asString();
            if (response.getStatus() >= 400) {
                throw new UrlCheckException("Site responded with status " + response.getStatus());
            }
            var page = PageParser.parse(response.getBody());
            return new UrlCheck(
                    response.getStatus(), page.title(), page.h1(), page.description(), url.getId());
        } catch (UnirestException e) {
            throw new UrlCheckException("Site is unavailable: " + e.getMessage(), e);
        }
    }
}
