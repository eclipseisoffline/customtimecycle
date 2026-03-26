package xyz.eclipseisoffline.customtimecycle.clock;

import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.clock.ClockTimeMarker;
import net.minecraft.world.clock.ClockTimeMarkers;
import xyz.eclipseisoffline.customtimecycle.CustomTimeCycle;

public interface CustomTimeCycleClockMarkers {
    ResourceKey<ClockTimeMarker> SUNRISE = createKey("sunrise");
    ResourceKey<ClockTimeMarker> SUNSET = createKey("sunset");

    private static ResourceKey<ClockTimeMarker> createKey(final String name) {
        return ResourceKey.create(ClockTimeMarkers.ROOT_ID, Identifier.fromNamespaceAndPath(CustomTimeCycle.MOD_ID, name));
    }
}
