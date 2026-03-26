package xyz.eclipseisoffline.customtimecycle.clock;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.clock.ClockNetworkState;
import net.minecraft.world.clock.ClockTimeMarker;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public interface ClockInstanceUtil {

    boolean customTimeCycle$hasPeriodicMarker();

    float customTimeCycle$getRateMultiplier();

    boolean customTimeCycle$setRateMultiplier(float rateMultiplier);

    @Nullable ResourceKey<ClockTimeMarker> customTimeCycle$getLastMarker(boolean commandsOnly);

    Map<ResourceKey<ClockTimeMarker>, ClockTimeMarker> customTimeCycle$getTimeMarkers();

    // Implemented by vanilla
    ClockNetworkState packNetworkState(final MinecraftServer server);
}
