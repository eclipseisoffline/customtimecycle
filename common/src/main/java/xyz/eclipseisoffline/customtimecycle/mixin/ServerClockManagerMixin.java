package xyz.eclipseisoffline.customtimecycle.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.clock.ClockManager;
import net.minecraft.world.clock.ClockTimeMarker;
import net.minecraft.world.clock.ServerClockManager;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.level.saveddata.SavedData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.eclipseisoffline.customtimecycle.ClockRateManager;

import java.util.Map;

@Mixin(ServerClockManager.class)
public abstract class ServerClockManagerMixin extends SavedData implements ClockManager {

    @Shadow
    @Final
    private Map<Holder<WorldClock>, ?> clocks;

    @Shadow
    private MinecraftServer server;

    @Inject(method = "tick", at = @At("TAIL"))
    public void calculateManagedRateForClocks(CallbackInfo callbackInfo) {
        ClockRateManager rateManager = ClockRateManager.getInstance(server);
        clocks.forEach((clock, rawInstance) -> {
            ClockInstanceMixin instance = (ClockInstanceMixin) rawInstance;
            instance.customTimeCycle$rateMultiplier = rateManager.getClockRate(clock, instance::customTimeCycle$getDurationToNext);
        } );
    }

    @Mixin(targets = "net.minecraft.world.clock.ServerClockManager$ClockInstance")
    private abstract static class ClockInstanceMixin {
        @Shadow
        @Final
        private Map<ResourceKey<ClockTimeMarker>, ClockTimeMarker> timeMarkers;
        @Shadow
        private long totalTicks;
        @Unique
        private float customTimeCycle$rateMultiplier;

        @ModifyExpressionValue(method = {"tick", "packNetworkState"}, at = @At(value = "FIELD", target = "Lnet/minecraft/world/clock/ServerClockManager$ClockInstance;rate:F"))
        public float multiplyRateByManagedValue(float original) {
            return original * customTimeCycle$rateMultiplier;
        }

        @Unique
        private long customTimeCycle$getDurationToNext(ResourceKey<ClockTimeMarker> marker) {
            ClockTimeMarker instance = timeMarkers.get(marker);
            if (instance == null || instance.periodTicks().isEmpty()) {
                return -1L;
            }
            return instance.resolveTimeToMoveTo(totalTicks) - totalTicks;
        }
    }
}
