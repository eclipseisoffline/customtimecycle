package xyz.eclipseisoffline.customtimecycle;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

public class TimeManager extends SavedData {
    private static final String TIME_MANAGER_SAVE = "timemanager";
    private static final long NORMAL_DAY_TIME = 12000;
    private static final long NORMAL_NIGHT_TIME = 12000;

    private final ServerLevel level;
    private DayPartTimeRate dayTimeRate;
    private DayPartTimeRate nightTimeRate;

    public TimeManager(ServerLevel level) {
        this.level = level;
        dayTimeRate = new DayPartTimeRate(NORMAL_DAY_TIME, NORMAL_DAY_TIME);
        nightTimeRate = new DayPartTimeRate(NORMAL_NIGHT_TIME, NORMAL_NIGHT_TIME);
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
        setTimeRate(dayTimeRate, nightTimeRate, true);
    }

    public void resetTimeRate() {
        setTimeRate(NORMAL_DAY_TIME, NORMAL_NIGHT_TIME);
    }

    private void setTimeRate(long dayTimeRate, long nightTimeRate, boolean dirty) {
        this.dayTimeRate = new DayPartTimeRate(dayTimeRate, NORMAL_DAY_TIME);
        this.nightTimeRate = new DayPartTimeRate(nightTimeRate, NORMAL_NIGHT_TIME);
        if (dirty) {
            setDirty();
        }
    }

    public boolean isDay() {
        return (level.getDayTime() % 24000) < NORMAL_DAY_TIME;
    }

    public static TimeManager getInstance(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TimeManager.timeManagerFactory(level), TIME_MANAGER_SAVE);
    }

    public static SavedData.Factory<TimeManager> timeManagerFactory(ServerLevel level) {
        return new Factory<>(() -> new TimeManager(level), (tag, provider) -> TimeManager.read(level, tag), null);
    }

    private static TimeManager read(ServerLevel level, CompoundTag compoundTag) {
        TimeManager manager = new TimeManager(level);
        long daytime = compoundTag.getLong("daytime");
        long nighttime = compoundTag.getLong("nighttime");

        boolean dirty = false;
        if (invalidTimeRate(daytime)) {
            daytime = NORMAL_DAY_TIME;
            dirty = true;
        }
        if (invalidTimeRate(nighttime)) {
            nighttime = NORMAL_NIGHT_TIME;
            dirty = true;
        }

        manager.setTimeRate(daytime, nighttime, dirty);
        return manager;
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        compoundTag.putLong("daytime", dayTimeRate.duration);
        compoundTag.putLong("nighttime", nightTimeRate.duration);
        return compoundTag;
    }

    private static boolean invalidTimeRate(long timeRate) {
        return timeRate <= 0;
    }

    public static class DayPartTimeRate {
        private final long duration;
        private final long incrementModulus;
        private final long increment;
        private final boolean isNormal;

        private DayPartTimeRate(long tickDuration, long normal) {
            isNormal = tickDuration == normal;
            duration = tickDuration;
            if (isNormal) {
                incrementModulus = 1;
                increment = 1;
            } else if (tickDuration < normal) {
                incrementModulus = 1;
                increment = normal / tickDuration;
            } else {
                incrementModulus = tickDuration / normal;
                increment = 1;
            }
        }

        private void apply(ServerLevel level) {
            if (level.getGameTime() % incrementModulus == 0) {
                level.setDayTime(level.getDayTime() + increment);
            }
        }

        public long getDuration() {
            return duration;
        }

        public long getIncrementModulus() {
            return incrementModulus;
        }

        public long getIncrement() {
            return increment;
        }
    }
}
