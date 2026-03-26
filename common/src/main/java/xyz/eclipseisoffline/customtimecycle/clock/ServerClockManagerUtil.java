package xyz.eclipseisoffline.customtimecycle.clock;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.clock.ClockTimeMarker;
import net.minecraft.world.clock.WorldClock;

import java.util.List;

public interface ServerClockManagerUtil {

    boolean customTimeCycle$isPeriodicClock(Holder<WorldClock> clock);

    int customTimeCycle$getTicksFor(Holder<WorldClock> clock, ResourceKey<ClockTimeMarker> marker);

    List<ResourceKey<ClockTimeMarker>> customTimeCycle$getMarkersBetween(Holder<WorldClock> clock, ResourceKey<ClockTimeMarker> from, ResourceKey<ClockTimeMarker> to);
}
