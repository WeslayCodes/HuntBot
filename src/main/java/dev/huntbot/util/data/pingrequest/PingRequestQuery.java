package dev.huntbot.util.data.pingrequest;

public final class PingRequestQuery {
    public static final String MERGE = """
        INSERT INTO ping_request (user_id, ping_id, last_request_at)
        VALUES (?, ?, ?)
        ON DUPLICATE KEY UPDATE
            last_request_at = VALUES(last_request_at);
        """;

    public static final String DEMAND_GET = """
        SELECT COUNT(*)
        FROM ping_request
        WHERE ping_id = ?
            AND last_request_at >= ?;
        """;

    public static final String DEMAND_CLEAR = """
        UPDATE ping_request
        SET last_request_at = '0000-00-00 00:00:00'
        WHERE ping_id = ?;
        """;
}
