package xyz.eclipseisoffline.customtimecycle;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.clock.ClockTimeMarker;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ClockRateManager extends SavedData {
    public static final Identifier ID = CustomTimeCycle.getModdedIdentifier("clock_rates");
    public static final Codec<ClockRateManager> CODEC = Codec.unboundedMap(WorldClock.CODEC, ClockRateSteps.CODEC)
            .xmap(ClockRateManager::new, steps -> steps.clockRateSteps);
    public static final SavedDataType<ClockRateManager> TYPE = new SavedDataType<>(ID, () -> new ClockRateManager(Map.of()), CODEC, null);
    private final Map<Holder<WorldClock>, ClockRateSteps> clockRateSteps;

    private ClockRateManager(Map<Holder<WorldClock>, ClockRateSteps> clockRateSteps) {
        this.clockRateSteps = new HashMap<>(clockRateSteps);
    }

    public void setRateForClockAtMarker(Holder<WorldClock> clock, ResourceKey<ClockTimeMarker> marker, float rate) {
        clockRateSteps.compute(clock, (_, steps) -> steps == null ? new ClockRateSteps(marker, rate) : steps.withRate(marker, rate));
        setDirty();
    }

    public void resetRatesForClock(Holder<WorldClock> clock) {
        clockRateSteps.remove(clock);
        setDirty();
    }

    public float getClockRate(Holder<WorldClock> clock, @Nullable ResourceKey<ClockTimeMarker> lastMarker) {
        if (lastMarker == null) {
            return 1.0F;
        }

        ClockRateSteps steps = clockRateSteps.get(clock);
        if (steps == null) {
            return 1.0F;
        }
        return steps.getActiveClockRate(lastMarker);
    }

    public static ClockRateManager getInstance(MinecraftServer server) {
        return server.getDataStorage().computeIfAbsent(TYPE);
    }

    public static @Nullable ClockRateManager getInstanceOrNull(MinecraftServer server) {
        return server.getDataStorage().get(TYPE);
    }

    public record ClockRateSteps(Map<ResourceKey<ClockTimeMarker>, Float> markerRates) {
        public static final Codec<ClockRateSteps> CODEC = Codec.unboundedMap(ClockTimeMarker.KEY_CODEC, Codec.FLOAT)
                .xmap(ClockRateSteps::new, ClockRateSteps::markerRates);

        public ClockRateSteps(ResourceKey<ClockTimeMarker> marker, float rate) {
            this(Map.of(marker, rate));
        }

        public ClockRateSteps withRate(ResourceKey<ClockTimeMarker> marker, float rate) {
            Map<ResourceKey<ClockTimeMarker>, Float> newRates = new HashMap<>(markerRates);
            if (rate == 1.0F) {
                newRates.remove(marker);
            } else {
                newRates.put(marker, rate);
            }
            return new ClockRateSteps(Collections.unmodifiableMap(newRates));
        }

        public float getActiveClockRate(ResourceKey<ClockTimeMarker> lastMarker) {
            return markerRates.getOrDefault(lastMarker, 1.0F);
        }
    }
}
