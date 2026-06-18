package noppes.npcs.roles;

public class TraderTimeUnit {
    public static final int SECOND = 0;
    public static final int MINUTE = 1;
    public static final int HOUR = 2;
    public static final int DAY = 3;
    public static final int WEEK = 4;
    public static final int MONTH = 5;

    public static final String[] KEYS = {
        "trader.time.second",
        "trader.time.minute",
        "trader.time.hour",
        "trader.time.day",
        "trader.time.week",
        "trader.time.month"
    };

    public static long toRealMillis(int amount, int unit) {
        if (amount <= 0) {
            return 0L;
        }
        return switch (unit) {
            case MINUTE -> (long)amount * 60_000L;
            case HOUR -> (long)amount * 3_600_000L;
            case DAY -> (long)amount * 86_400_000L;
            case WEEK -> (long)amount * 604_800_000L;
            case MONTH -> (long)amount * 2_592_000_000L;
            default -> (long)amount * 1_000L;
        };
    }

    public static long toGameTicks(int amount, int unit) {
        if (amount <= 0) {
            return 0L;
        }
        return switch (unit) {
            case MINUTE -> (long)amount * 1200L;
            case HOUR -> (long)amount * 72000L;
            case DAY -> (long)amount * 24000L;
            case WEEK -> (long)amount * 168000L;
            case MONTH -> (long)amount * 720000L;
            default -> (long)amount * 20L;
        };
    }

    public static long toOnlineTicks(int amount, int unit) {
        return TraderTimeUnit.toGameTicks(amount, unit);
    }
}
