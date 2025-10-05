package xyz.eclipseisoffline.customtimecycle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.function.Function;

public class TimeManager extends SavedData {
    private static final Function<Long, DataResult<Long>> TIME_RATE_VALIDATOR = timeRate -> {
        if (invalidTimeRate(timeRate)) {
            return DataResult.error(() -> "Time rate must be at least 1");
        }
        return DataResult.success(timeRate);
    };

    private static final String TIME_MANAGER_SAVE = "timemanager";
    private static final long NORMAL_DAY_TIME = 12000;
    private static final long NORMAL_NIGHT_TIME = 12000;

    private final ServerLevel level;
    private DayPartTimeRate dayTimeRate;
    private DayPartTimeRate nightTimeRate;

    private TimeManager(ServerLevel level, long dayTimeRate, long nightTimeRate) {
        this.level = level;
        this.dayTimeRate = new DayPartTimeRate(dayTimeRate, NORMAL_DAY_TIME);
        this.nightTimeRate = new DayPartTimeRate(nightTimeRate, NORMAL_NIGHT_TIME);
    }

    private TimeManager(ServerLevel level) {
        this(level, NORMAL_DAY_TIME, NORMAL_NIGHT_TIME);
    }

    public void tickTime() {
        long oldDayTime = level.getDayTime();
        if (isDay()) {
            dayTimeRate.apply(level);
        } else {
            nightTimeRate.apply(level);
        }
        if (!isNormalTimeRate() && oldDayTime != level.getDayTime()) {
            // Send time sync packet to all clients in this level when not following normal time rate and the time has changed
            level.getServer().getPlayerList().broadcastAll(new ClientboundSetTimePacket(level.getGameTime(), level.getDayTime(), false), level.dimension());
        }
    }

    public DayPartTimeRate getTimeRate() {
        if (isDay()) {
            return dayTimeRate;
        } else {
            return nightTimeRate;
        }
    }

    public DayPartTimeRate getDayTimeRate() {
        return dayTimeRate;
    }

    public DayPartTimeRate getNightTimeRate() {
        return nightTimeRate;
    }

    public boolean isNormalTimeRate() {
        return getTimeRate().isNormal;
    }

    public void setTimeRate(long dayTimeRate, long nightTimeRate) {
        if (invalidTimeRate(dayTimeRate) || invalidTimeRate(nightTimeRate)) {
            throw new IllegalArgumentException("Invalid time rate");
        }
        actuallySetTimeRate(dayTimeRate, nightTimeRate);
    }

    public void resetTimeRate() {
        actuallySetTimeRate(NORMAL_DAY_TIME, NORMAL_NIGHT_TIME);
    }

    private void actuallySetTimeRate(long dayTimeRate, long nightTimeRate) {
        this.dayTimeRate = new DayPartTimeRate(dayTimeRate, NORMAL_DAY_TIME);
        this.nightTimeRate = new DayPartTimeRate(nightTimeRate, NORMAL_NIGHT_TIME);
        setDirty();
    }

    public boolean isDay() {
        return (level.getDayTime() % 24000) < NORMAL_DAY_TIME;
    }

    public static TimeManager getInstance(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TimeManager.type(level));
    }

    private static SavedDataType<TimeManager> type(ServerLevel level) {
        Codec<TimeManager> codec = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.LONG.validate(TIME_RATE_VALIDATOR).optionalFieldOf("daytime", NORMAL_DAY_TIME).forGetter(manager -> manager.dayTimeRate.duration),
                        Codec.LONG.validate(TIME_RATE_VALIDATOR).optionalFieldOf("nighttime", NORMAL_NIGHT_TIME).forGetter(manager -> manager.nightTimeRate.duration)
                ).apply(instance, (dayTimeRate, nightTimeRate) -> new TimeManager(level, dayTimeRate, nightTimeRate))
        );

        return new SavedDataType<>(TIME_MANAGER_SAVE, () -> new TimeManager(level), codec, null);
    }

    private static boolean invalidTimeRate(long timeRate) {
        return timeRate <= 0;
    }

    public static class DayPartTimeRate {
        private final long duration;
        private final long incrementModulus;
        private final double increment;
        private final boolean isNormal;
        private double dayTime = -1;

        private DayPartTimeRate(long tickDuration, long normal) {
            isNormal = tickDuration == normal;
            duration = tickDuration;

            if (isNormal) {
                incrementModulus = 1;
                increment = 1;
            } else if (tickDuration < normal) {
                incrementModulus = 1;
                increment = (double) normal / tickDuration;
            } else {
                double modulus = (double) tickDuration / normal;
                long roundedModulus;
                if (Mth.floor(modulus) != modulus) {
                    roundedModulus = Mth.lfloor(modulus);
                    increment = roundedModulus / modulus;
                } else {
                    roundedModulus = (long) modulus;
                    increment = 1;
                }
                incrementModulus = roundedModulus;
            }
        }

        private void apply(ServerLevel level) {
            long currentTime = Mth.lfloor(dayTime);
            if (currentTime != level.getDayTime()) {
                dayTime = level.getDayTime();
                currentTime = level.getDayTime();
            }
            if (level.getGameTime() % incrementModulus == 0) {
                dayTime += increment;
                long newTime = Mth.lfloor(dayTime);
                if (newTime != currentTime) {
                    level.setDayTime(newTime);
                }
            }
        }

        public long getDuration() {
            return duration;
        }

        public long getIncrementModulus() {
            return incrementModulus;
        }

        public double getIncrement() {
            return increment;
        }
    }
}
