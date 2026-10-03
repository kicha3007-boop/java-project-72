package hexlet.code.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class TextUtilsTest {

    @Test
    void testTruncateKeepsShortText() {
        var text = "a".repeat(TextUtils.MAX_LENGTH);
        assertThat(TextUtils.truncate(text)).isEqualTo(text);
    }

    @Test
    void testTruncateCutsLongText() {
        var text = "b".repeat(TextUtils.MAX_LENGTH + 1);
        assertThat(TextUtils.truncate(text)).isEqualTo("b".repeat(TextUtils.MAX_LENGTH) + "...");
    }

    @Test
    void testTruncateNull() {
        assertThat(TextUtils.truncate(null)).isEmpty();
    }

    @Test
    void testFormatDate() {
        var timestamp = Timestamp.valueOf(LocalDateTime.of(2024, 1, 2, 3, 4));
        assertThat(TextUtils.formatDate(timestamp)).isEqualTo("02/01/2024 03:04");
        assertThat(TextUtils.formatDate(null)).isEmpty();
    }
}
