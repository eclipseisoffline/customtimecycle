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
import xyz.eclipseisoffline.customtimecycle.clock.ClockInstanceUtil;
import xyz.eclipseisoffline.customtimecycle.clock.ServerClockManagerUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    @Override
    public boolean customTimeCycle$isPeriodicClock(Holder<WorldClock> clock) {
        return customTimeCycle$getInstance(clock).customTimeCycle$hasPeriodicMarker();
    }

    @Override
    public int customTimeCycle$getTicksFor(Holder<WorldClock> clock, ResourceKey<ClockTimeMarker> markerKey) {
        ClockTimeMarker marker = customTimeCycle$getInstance(clock).customTimeCycle$getTimeMarkers().get(markerKey);
        if (marker == null) {
            return -1;
        }
        return marker.ticks();
    }

    @Override
    public List<ResourceKey<ClockTimeMarker>> customTimeCycle$getMarkersBetween(Holder<WorldClock> clock, ResourceKey<ClockTimeMarker> fromKey, ResourceKey<ClockTimeMarker> toKey) {
        Map<ResourceKey<ClockTimeMarker>, ClockTimeMarker> timeMarkers = customTimeCycle$getInstance(clock).customTimeCycle$getTimeMarkers();
        if (timeMarkers.isEmpty()) {
            return List.of();
        }
        ClockTimeMarker from = timeMarkers.get(fromKey);
        ClockTimeMarker to = timeMarkers.get(toKey);
        if (from == null || to == null) {
            return List.of();
        }

        int startTick = from.ticks();
        int endTick = to.ticks();

        List<ResourceKey<ClockTimeMarker>> markersBetween = new ArrayList<>();
        if (endTick < startTick) {
            for (Map.Entry<ResourceKey<ClockTimeMarker>, ClockTimeMarker> marker : timeMarkers.entrySet()) {
                if (marker.getValue().ticks() < endTick || marker.getValue().ticks() >= startTick) {
                    markersBetween.add(marker.getKey());
                }
            }
        } else {
            for (Map.Entry<ResourceKey<ClockTimeMarker>, ClockTimeMarker> marker : timeMarkers.entrySet()) {
                if (marker.getValue().ticks() >= startTick && marker.getValue().ticks() < endTick) {
                    markersBetween.add(marker.getKey());
                }
            }
        }

        return markersBetween;
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Ljava/util/Collection;forEach(Ljava/util/function/Consumer;)V"))
    public void calculateManagedRateForClocks(CallbackInfo callbackInfo) {
        ClockRateManager rateManager = ClockRateManager.getInstance(server);
        Map<Holder<WorldClock>, ClockNetworkState> updates = new HashMap<>();

        // TODO this is called for every clock every tick, this can be optimised, but is it necessary to?
        clocks.forEach((clock, rawInstance) -> {
            ClockInstanceUtil instance = (ClockInstanceUtil) rawInstance;
            float rate = rateManager.getClockRate(clock, instance.customTimeCycle$getLastMarker(true));
            if (instance.customTimeCycle$setRateMultiplier(rate)) {
                updates.put(clock, instance.packNetworkState(server));
            }
        });

        if (!updates.isEmpty()) {
            server.getPlayerList().broadcastAll(new ClientboundSetTimePacket(getGameTime(), updates));
        }
    }

    @Unique
    private ClockInstanceUtil customTimeCycle$getInstance(Holder<WorldClock> clock) {
        ClockInstanceUtil instance = (ClockInstanceUtil) this.clocks.get(clock);
        if (instance == null) {
            throw new IllegalStateException("No clock initialized for definition: " + clock);
        } else {
            return instance;
        }
    }

    @Mixin(targets = "net.minecraft.world.clock.ServerClockManager$ClockInstance")
    private abstract static class ClockInstanceMixin implements ClockInstanceUtil {
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

        @Override
        public Map<ResourceKey<ClockTimeMarker>, ClockTimeMarker> customTimeCycle$getTimeMarkers() {
            return timeMarkers;
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
