package dev.huntbot.util.data.pingrequest;

import dev.huntbot.util.data.DataUtil;
import dev.huntbot.util.data.huntuser.HuntUserUtil;
import dev.huntbot.util.time.TimeUtil;

import java.sql.*;
import java.util.concurrent.CompletableFuture;

public class PingRequestUtil extends DataUtil {
    public static CompletableFuture<Void> merge(String userId, String pingId) {
        Object[] params = {userId, pingId, new Timestamp(TimeUtil.getCurMilli())};
        return HuntUserUtil.insert(userId).thenAccept(v -> executeUpdate(PingRequestQuery.MERGE, params));
    }

    public static CompletableFuture<Integer> demandGet(String pingId, long demandStartTimestamp) {
        Object[] params = {pingId, new Timestamp(demandStartTimestamp)};
        ResultSetMapper<Integer> mapper = rs -> {
            rs.next();
            return rs.getInt(1);
        };

        return executeQuery(PingRequestQuery.DEMAND_GET, mapper, params);
    }

    public static CompletableFuture<Void> demandClear(String pingId) {
        return executeUpdate(PingRequestQuery.DEMAND_CLEAR, pingId);
    }
}
