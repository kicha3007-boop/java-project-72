package hexlet.code.repository;

import hexlet.code.model.Url;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class UrlRepository extends BaseRepository {

    private UrlRepository() {}

    public static void save(Url url) throws SQLException {
        var sql = "INSERT INTO urls (name, created_at) VALUES (?, ?)";
        var createdAt = Timestamp.valueOf(LocalDateTime.now());
        try (var connection = dataSource.getConnection();
                var statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, url.getName());
            statement.setTimestamp(2, createdAt);
            statement.executeUpdate();
            try (var keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("DB have not returned an id after saving an entity");
                }
                url.setId(keys.getLong(1));
                url.setCreatedAt(createdAt);
            }
        }
    }

    public static Optional<Url> find(Long id) throws SQLException {
        var sql = "SELECT * FROM urls WHERE id = ?";
        try (var connection = dataSource.getConnection();
                var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (var resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(toUrl(resultSet)) : Optional.empty();
            }
        }
    }

    public static Optional<Url> findByName(String name) throws SQLException {
        var sql = "SELECT * FROM urls WHERE name = ?";
        try (var connection = dataSource.getConnection();
                var statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            try (var resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(toUrl(resultSet)) : Optional.empty();
            }
        }
    }

    /** Новые адреса первыми. */
    public static List<Url> getEntities() throws SQLException {
        var sql = "SELECT * FROM urls ORDER BY created_at DESC, id DESC";
        try (var connection = dataSource.getConnection();
                var statement = connection.prepareStatement(sql);
                var resultSet = statement.executeQuery()) {
            var result = new ArrayList<Url>();
            while (resultSet.next()) {
                result.add(toUrl(resultSet));
            }
            return result;
        }
    }

    private static Url toUrl(ResultSet resultSet) throws SQLException {
        return new Url(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getTimestamp("created_at"));
    }
}
