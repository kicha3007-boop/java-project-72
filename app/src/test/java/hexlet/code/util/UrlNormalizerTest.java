package hexlet.code.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class UrlNormalizerTest {

    @ParameterizedTest
    @CsvSource({
        "https://some-domain.org/example/path, https://some-domain.org",
        "https://some-domain.org:8080/example/path, https://some-domain.org:8080",
        "HTTP://Example.COM/Path?q=1#top, http://example.com",
        "'  https://example.com  ', https://example.com"
    })
    void testNormalize(String input, String expected) {
        assertThat(UrlNormalizer.normalize(input)).contains(expected);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(
            strings = {"httpsss://abcabca@test.ru", "example.com", "https://", "ht tp://x", " "})
    void testInvalid(String input) {
        assertThat(UrlNormalizer.normalize(input)).isEmpty();
    }
}
