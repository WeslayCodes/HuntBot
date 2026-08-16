package dev.huntbot.util.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.huntbot.HuntBotApp;

import java.io.File;
import java.sql.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class DataUtil {
    private static final HikariConfig hikariConfig = new HikariConfig();
    protected static HikariDataSource ds;
    protected static final ExecutorService dbExecutor = Executors.newFixedThreadPool(10);

    public static void setupDatabase() {
        if (ds != null) return;

        // Database host must be referenced by container name when running in Docker
        String host = new File("/.dockerenv").exists()
            ? "db:3306"
            : "localhost:3307";

        hikariConfig.setJdbcUrl("jdbc:mariadb://%s/huntbot?allowMultiQueries=true".formatted(host));
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setUsername("root");
        hikariConfig.setPassword(HuntBotApp.getEnv("DB_PASS"));

        ds = new HikariDataSource(hikariConfig);
    }

    protected static CompletableFuture<Void> executeUpdate(String sql, Object... params) {
        return CompletableFuture.supplyAsync(() -> {
            try (
                Connection conn = ds.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
            ) {
                for (int i = 0; i < params.length; i++) {
                    stmt.setObject(i + 1, params[i]);
                }

                stmt.executeUpdate();
                return null;
            } catch (SQLException e) {
                throw new DatabaseException(e);
            }
        }, dbExecutor);
    }

    protected static <T> CompletableFuture<T> executeQuery(String sql, ResultSetMapper<T> mapper, Object... params) {
        return CompletableFuture.supplyAsync(() -> {
            try (
                Connection conn = ds.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
            ) {
                for (int i = 0; i < params.length; i++) {
                    stmt.setObject(i + 1, params[i]);
                }

                try (ResultSet rs = stmt.executeQuery()) {
                    return mapper.map(rs);
                }
            } catch (SQLException e) {
                throw new DatabaseException(e);
            }
        }, dbExecutor);
    }

    @FunctionalInterface
    public interface ResultSetMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }

    public static class DatabaseException extends RuntimeException {
        public DatabaseException(SQLException ex) {
            super(ex);
        }
    }
}
