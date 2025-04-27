package dev.huntbot.util.hunt;

import dev.huntbot.util.time.TimeUtil;

public record ProgressRecord(boolean bookshelf, boolean loremaster, long lastBlessed, boolean sailor, boolean napkin) {
    public ProgressRecord() {
        this(false, false, TimeUtil.getCurMilli() - TimeUtil.getThirtyMinMilli(), false, false);
    }
}
