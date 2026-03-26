package xyz.eclipseisoffline.customtimecycle.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.commands.CommandSource;
import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerInfo;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.minecraft.world.clock.ServerClockManager;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.level.chunk.storage.ChunkIOErrorReporter;
import net.minecraft.world.level.storage.WorldData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.eclipseisoffline.customtimecycle.ClockRateManager;
import xyz.eclipseisoffline.customtimecycle.clock.CustomTimeCycleClockMarkers;
import xyz.eclipseisoffline.customtimecycle.clock.ServerClockManagerUtil;
import xyz.eclipseisoffline.customtimecycle.screens.PreconfiguredTimeCycle;
import xyz.eclipseisoffline.customtimecycle.screens.PreconfiguredTimeCycleState;

import java.util.Optional;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin extends ReentrantBlockableEventLoop<TickTask> implements CommandSource, ServerInfo, ChunkIOErrorReporter {

    @Shadow
    @Final
    protected WorldData worldData;

    @Shadow
    @Final
    private ServerClockManager clockManager;

    public MinecraftServerMixin(String name, boolean propagatesCrashes) {
        super(name, propagatesCrashes);
    }

    @Inject(method = "createLevels", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/ServerScoreboard;load(Lnet/minecraft/world/scores/ScoreboardSaveData$Packed;)V"))
    public void applyCustomTime(CallbackInfo callbackInfo, @Local(name = "overworld") ServerLevel level) {
        if (worldData instanceof PreconfiguredTimeCycleState preconfiguredState) {
            PreconfiguredTimeCycle preconfigured = preconfiguredState.customTimeCycle$getPreconfiguredTimeCycle();
            if (preconfigured != null) {
                Optional<Holder<WorldClock>> clock = level.dimensionType().defaultClock();
                clock.ifPresent(clockHolder -> {
                    ClockRateManager rateManager = ClockRateManager.getInstance((MinecraftServer) (Object) this);
                    try {
                        ((ServerClockManagerUtil) clockManager).setDurationBetweenMarkers(clockHolder,
                                CustomTimeCycleClockMarkers.SUNRISE, CustomTimeCycleClockMarkers.SUNSET, preconfigured.dayTime(),
                                rateManager);
                        ((ServerClockManagerUtil) clockManager).setDurationBetweenMarkers(clockHolder,
                                CustomTimeCycleClockMarkers.SUNSET, CustomTimeCycleClockMarkers.SUNRISE, preconfigured.nightTime(),
                                rateManager);
                    } catch (IllegalArgumentException ignored) {}
                });
            }
        }
    }
}
