package xyz.eclipseisoffline.customtimecycle.clock;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.clock.ClockTimeMarker;
import net.minecraft.world.clock.WorldClock;
import xyz.eclipseisoffline.customtimecycle.ClockRateManager;

import java.util.List;

public interface ServerClockManagerUtil {

    boolean customTimeCycle$isPeriodicClock(Holder<WorldClock> clock);

    int customTimeCycle$getTicksFor(Holder<WorldClock> clock, ResourceKey<ClockTimeMarker> marker);

    int customTimeCycle$getTicksBetween(Holder<WorldClock> clock, ResourceKey<ClockTimeMarker> from, ResourceKey<ClockTimeMarker> to);

    float customTimeCycle$getRateForClock(Holder<WorldClock> clock);

    List<ResourceKey<ClockTimeMarker>> customTimeCycle$getMarkersBetween(Holder<WorldClock> clock, ResourceKey<ClockTimeMarker> fromKey, ResourceKey<ClockTimeMarker> toKey,
                                                                         boolean commandsOnly);

    default void setDurationBetweenMarkers(Holder<WorldClock> clock, ResourceKey<ClockTimeMarker> from, ResourceKey<ClockTimeMarker> to, int duration,
                                           ClockRateManager rateManager) {
        int timeBetweenMarkers = customTimeCycle$getTicksBetween(clock, from, to);
        if (timeBetweenMarkers < 1) {
            throw new IllegalArgumentException("Invalid time between from and to markers: " + timeBetweenMarkers);
        }
        float rate = (float) timeBetweenMarkers / duration;

        List<ResourceKey<ClockTimeMarker>> markers = customTimeCycle$getMarkersBetween(clock, from, to, true);
        for (ResourceKey<ClockTimeMarker> marker : markers) {
            rateManager.setRateForClockAtMarker(clock, marker, rate);
        }
    }
}
