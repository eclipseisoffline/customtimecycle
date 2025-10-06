package xyz.eclipseisoffline.customtimecycle.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
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

    @Inject(method = "createLevels", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;readScoreboard(Lnet/minecraft/world/level/storage/DimensionDataStorage;)V"))
    public void applyCustomTime(CallbackInfo callbackInfo, @Local ServerLevel level) {
        if (worldData instanceof TimeCycleState preconfiguredState) {
            PreconfiguredTimeCycle preconfigured = preconfiguredState.customTimeCycle$getPreconfiguredTimeCycle();
            if (preconfigured != null) {
                TimeManager.setInstance(level, preconfigured);
            }
        }
    }

    @WrapOperation(method = "synchronizeTime", at = @At(value = "NEW", target = "net/minecraft/network/protocol/game/ClientboundSetTimePacket"))
    public ClientboundSetTimePacket broadcastCustomTimeRules(long gameTime, long dayTime, boolean tickDayTime, Operation<ClientboundSetTimePacket> original, ServerLevel level) {
        if (!TimeManager.getInstance(level).isNormalTimeRate()) {
            // Tell the clients not to update time locally when using custom time rate to prevent sync issues
            tickDayTime = false;
        }
        return original.call(gameTime, dayTime, tickDayTime);
    }
}
