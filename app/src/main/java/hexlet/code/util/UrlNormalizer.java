package hexlet.code.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Optional;

/** Приводит введённый адрес к виду «схема://хост[:порт]». */
public final class UrlNormalizer {

    private UrlNormalizer() {}

    /** Пустой результат — адрес некорректен. */
    public static Optional<String> normalize(String input) {
        if (input == null || input.isBlank()) {
            return Optional.empty();
        }
        try {
            var url = new URI(input.trim()).toURL();
            var host = url.getHost();
            if (host == null || host.isEmpty()) {
                return Optional.empty();
            }
            var port = url.getPort() == -1 ? "" : ":" + url.getPort();
            var protocol = url.getProtocol().toLowerCase(Locale.ROOT);
            return Optional.of(protocol + "://" + host.toLowerCase(Locale.ROOT) + port);
        } catch (URISyntaxException | MalformedURLException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
