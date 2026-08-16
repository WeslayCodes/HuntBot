package dev.huntbot.util.data.huntuser;

public final class HuntUserQuery {
    public static final String INSERT = """
        INSERT IGNORE INTO hunt_user (user_id)
        VALUES (?);
        """;
}
