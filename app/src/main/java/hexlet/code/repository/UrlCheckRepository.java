package hexlet.code.repository;

import hexlet.code.model.UrlCheck;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class UrlCheckRepository extends BaseRepository {

    private UrlCheckRepository() {}

    public static void save(UrlCheck check) throws SQLException {
        var sql =
                "INSERT INTO url_checks (url_id, status_code, title, h1, description, created_at)"
                        + " VALUES (?, ?, ?, ?, ?, ?)";
        var createdAt = Timestamp.valueOf(LocalDateTime.now());
        try (var connection = dataSource.getConnection();
                var statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, check.getUrlId());
            statement.setInt(2, check.getStatusCode());
            statement.setString(3, check.getTitle());
            statement.setString(4, check.getH1());
            statement.setString(5, check.getDescription());
            statement.setTimestamp(6, createdAt);
            statement.executeUpdate();
            try (var keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("DB have not returned an id after saving an entity");
                }
                check.setId(keys.getLong(1));
                check.setCreatedAt(createdAt);
            }
        }
    }

    /** Проверки одного адреса, новые первыми. */
    public static List<UrlCheck> findByUrlId(Long urlId) throws SQLException {
        var sql = "SELECT * FROM url_checks WHERE url_id = ? ORDER BY created_at DESC, id DESC";
        try (var connection = dataSource.getConnection();
                var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, urlId);
            try (var resultSet = statement.executeQuery()) {
                var result = new ArrayList<UrlCheck>();
                while (resultSet.next()) {
                    result.add(toUrlCheck(resultSet));
                }
                return result;
            }
        }
    }

    /** Последняя проверка каждого адреса одним запросом: url_id → проверка. */
    public static Map<Long, UrlCheck> findLatestChecks() throws SQLException {
        var sql =
                "SELECT c.* FROM url_checks c"
                        + " JOIN (SELECT url_id, MAX(id) AS last_id FROM url_checks GROUP BY url_id)"
                        + " latest ON c.id = latest.last_id";
        try (var connection = dataSource.getConnection();
                var statement = connection.prepareStatement(sql);
                var resultSet = statement.executeQuery()) {
            var result = new HashMap<Long, UrlCheck>();
            while (resultSet.next()) {
                var check = toUrlCheck(resultSet);
                result.put(check.getUrlId(), check);
            }
            return result;
        }
    }

    private static UrlCheck toUrlCheck(ResultSet resultSet) throws SQLException {
        var check =
                new UrlCheck(
                        resultSet.getInt("status_code"),
                        resultSet.getString("title"),
                        resultSet.getString("h1"),
                        resultSet.getString("description"),
                        resultSet.getLong("url_id"));
        check.setId(resultSet.getLong("id"));
        check.setCreatedAt(resultSet.getTimestamp("created_at"));
        return check;
    }
}
