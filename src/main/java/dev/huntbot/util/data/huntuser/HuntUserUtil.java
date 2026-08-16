package dev.huntbot.util.data.huntuser;

import dev.huntbot.util.data.DataUtil;

import java.util.concurrent.CompletableFuture;

public class HuntUserUtil extends DataUtil {
    public static CompletableFuture<Void> insert(String userId) {
        return executeUpdate(HuntUserQuery.INSERT, userId);
    }
}
