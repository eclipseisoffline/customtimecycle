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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.ToLongFunction;

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

    public float getClockRate(Holder<WorldClock> clock) {
        // TODO this is called for every clock every tick, this can be optimised, but is it necessary to?
        ClockRateSteps steps = clockRateSteps.get(clock);
        if (steps == null) {
            return 1.0F;
        }
        return steps.getActiveClockRate();
    }

    public ClockRateManager getInstance(MinecraftServer server) {
        return server.getDataStorage().computeIfAbsent(TYPE);
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

        public float getActiveClockRate(ToLongFunction<ResourceKey<ClockTimeMarker>> durationToNextGetter) {
            float furthestRate = 1.0F;
            long furthestDuration = 0L;
            for (Map.Entry<ResourceKey<ClockTimeMarker>, Float> entry : markerRates.entrySet()) {
                long durationToNext = durationToNextGetter.applyAsLong(entry.getKey());
                if (durationToNext > furthestDuration) {
                    furthestRate = entry.getValue();
                    furthestDuration = durationToNext;
                }
            }
            return furthestRate;
        }
    }
}
