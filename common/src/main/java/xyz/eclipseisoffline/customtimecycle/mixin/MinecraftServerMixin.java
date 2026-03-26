package xyz.eclipseisoffline.customtimecycle.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.WorldData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.eclipseisoffline.customtimecycle.TimeManager;
import xyz.eclipseisoffline.customtimecycle.screens.PreconfiguredTimeCycle;
import xyz.eclipseisoffline.customtimecycle.screens.TimeCycleState;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Shadow
    @Final
    protected WorldData worldData;

    @Inject(method = "createLevels", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/ServerScoreboard;load(Lnet/minecraft/world/scores/ScoreboardSaveData$Packed;)V"))
    public void applyCustomTime(CallbackInfo callbackInfo, @Local ServerLevel level) {
        if (worldData instanceof TimeCycleState preconfiguredState) {
            PreconfiguredTimeCycle preconfigured = preconfiguredState.customTimeCycle$getPreconfiguredTimeCycle();
            if (preconfigured != null) {
                TimeManager.setInstance(level, preconfigured);
            }
        }
    }
}
