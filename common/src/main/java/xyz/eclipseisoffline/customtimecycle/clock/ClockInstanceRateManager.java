package xyz.eclipseisoffline.customtimecycle.clock;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.clock.ClockNetworkState;
import net.minecraft.world.clock.ClockTimeMarker;
import org.jspecify.annotations.Nullable;

public interface ClockInstanceRateManager {

    boolean customTimeCycle$setRateMultiplier(float rateMultiplier);

    @Nullable ResourceKey<ClockTimeMarker> customTimeCycle$getLastMarker(boolean commandsOnly);

    // Implemented by vanilla
    ClockNetworkState packNetworkState(final MinecraftServer server);
}
