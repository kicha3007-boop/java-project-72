package hexlet.code.dto.urls;

import hexlet.code.dto.BasePage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import java.util.List;
import java.util.Map;

public final class UrlsPage extends BasePage {

    private final List<Url> urls;
    private final Map<Long, UrlCheck> latestChecks;

    public UrlsPage(List<Url> urls, Map<Long, UrlCheck> latestChecks) {
        this.urls = urls;
        this.latestChecks = latestChecks;
    }

    public List<Url> getUrls() {
        return urls;
    }

    /** Последняя проверка адреса или null, если его ещё не проверяли. */
    public UrlCheck getLatestCheck(Url url) {
        return latestChecks.get(url.getId());
    }
}
