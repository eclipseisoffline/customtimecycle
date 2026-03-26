package xyz.eclipseisoffline.customtimecycle.clock;

import net.minecraft.core.Holder;
import net.minecraft.world.clock.WorldClock;

public interface ServerClockManagerUtil {

    boolean customTimeCycle$isPeriodicClock(Holder<WorldClock> clock);
}
