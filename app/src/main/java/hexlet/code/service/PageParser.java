package hexlet.code.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/** SEO-данные страницы: title, первый h1 и meta description. Отсутствующий тег — пустая строка. */
public record PageParser(String title, String h1, String description) {

    public static PageParser parse(String html) {
        Document document = Jsoup.parse(html);
        var h1 = document.selectFirst("h1");
        var description = document.selectFirst("meta[name=description]");
        return new PageParser(
                document.title(),
                textOf(h1),
                description == null ? "" : description.attr("content"));
    }

    private static String textOf(Element element) {
        return element == null ? "" : element.text();
    }
}
