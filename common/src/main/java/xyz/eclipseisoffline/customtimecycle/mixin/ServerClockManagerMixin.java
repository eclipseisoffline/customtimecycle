package xyz.eclipseisoffline.customtimecycle.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.clock.ClockManager;
import net.minecraft.world.clock.ClockNetworkState;
import net.minecraft.world.clock.ClockTimeMarker;
import net.minecraft.world.clock.ServerClockManager;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.level.saveddata.SavedData;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.eclipseisoffline.customtimecycle.ClockRateManager;
import xyz.eclipseisoffline.customtimecycle.clock.ClockInstanceRateManager;
import xyz.eclipseisoffline.customtimecycle.clock.ServerClockManagerUtil;

import java.util.HashMap;
import java.util.Map;

@Mixin(ServerClockManager.class)
public abstract class ServerClockManagerMixin extends SavedData implements ClockManager, ServerClockManagerUtil {

    @Shadow
    @Final
    private Map<Holder<WorldClock>, ?> clocks;

    @Shadow
    private MinecraftServer server;

    @Shadow
    protected abstract long getGameTime();

    @Shadow
    protected abstract Object getInstance(Holder<WorldClock> definition);

    @Override
    public boolean customTimeCycle$isPeriodicClock(Holder<WorldClock> clock) {
        return ((ClockInstanceRateManager) getInstance(clock)).customTimeCycle$hasPeriodicMarker();
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Ljava/util/Collection;forEach(Ljava/util/function/Consumer;)V"))
    public void calculateManagedRateForClocks(CallbackInfo callbackInfo) {
        ClockRateManager rateManager = ClockRateManager.getInstance(server);
        Map<Holder<WorldClock>, ClockNetworkState> updates = new HashMap<>();

        // TODO this is called for every clock every tick, this can be optimised, but is it necessary to?
        clocks.forEach((clock, rawInstance) -> {
            ClockInstanceRateManager instance = (ClockInstanceRateManager) rawInstance;
            float rate = rateManager.getClockRate(clock, instance.customTimeCycle$getLastMarker(true));
            if (instance.customTimeCycle$setRateMultiplier(rate)) {
                updates.put(clock, instance.packNetworkState(server));
            }
        });

        if (!updates.isEmpty()) {
            server.getPlayerList().broadcastAll(new ClientboundSetTimePacket(getGameTime(), updates));
        }
    }

    @Mixin(targets = "net.minecraft.world.clock.ServerClockManager$ClockInstance")
    private abstract static class ClockInstanceMixin implements ClockInstanceRateManager {
        @Shadow
        @Final
        private Map<ResourceKey<ClockTimeMarker>, ClockTimeMarker> timeMarkers;
        @Shadow
        private long totalTicks;
        @Unique
        private float customTimeCycle$rateMultiplier = 1.0F;

        @ModifyExpressionValue(method = {"tick", "packNetworkState"}, at = @At(value = "FIELD", target = "Lnet/minecraft/world/clock/ServerClockManager$ClockInstance;rate:F"))
        public float multiplyRateByManagedValue(float original) {
            return original * customTimeCycle$rateMultiplier;
        }

        @Override
        public boolean customTimeCycle$hasPeriodicMarker() {
            return timeMarkers.values().stream().anyMatch(marker -> marker.periodTicks().isPresent());
        }

        @Override
        public boolean customTimeCycle$setRateMultiplier(float rateMultiplier) {
            if (customTimeCycle$rateMultiplier != rateMultiplier) {
                customTimeCycle$rateMultiplier = rateMultiplier;
                return true;
            }
            return false;
        }

        @Override
        public @Nullable ResourceKey<ClockTimeMarker> customTimeCycle$getLastMarker(boolean commandsOnly) {
            ResourceKey<ClockTimeMarker> furthestMarker = null;
            long furthestDuration = 0L;
            for (Map.Entry<ResourceKey<ClockTimeMarker>, ClockTimeMarker> entry : timeMarkers.entrySet()) {
                if (commandsOnly && !entry.getValue().showInCommands()) {
                    continue;
                }
                long durationToNext = customTimeCycle$getDurationToNext(entry.getValue());
                if (durationToNext > furthestDuration) {
                    furthestMarker = entry.getKey();
                    furthestDuration = durationToNext;
                }
            }
            return furthestMarker;
        }

        @Unique
        private long customTimeCycle$getDurationToNext(ClockTimeMarker marker) {
            if (marker.periodTicks().isEmpty()) {
                return -1L;
            }
            return marker.resolveTimeToMoveTo(totalTicks) - totalTicks;
        }
    }
}
