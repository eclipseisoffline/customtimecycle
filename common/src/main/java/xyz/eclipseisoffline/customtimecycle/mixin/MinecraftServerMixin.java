package xyz.eclipseisoffline.customtimecycle.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.eclipseisoffline.customtimecycle.TimeManager;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @WrapOperation(method = "synchronizeTime", at = @At(value = "NEW", target = "net/minecraft/network/protocol/game/ClientboundSetTimePacket"))
    public ClientboundSetTimePacket broadcastCustomTimeRules(long gameTime, long dayTime, boolean tickDayTime, Operation<ClientboundSetTimePacket> original, ServerLevel level) {
        if (!TimeManager.getInstance(level).isNormalTimeRate()) {
            // Tell the clients not to update time locally when using custom time rate to prevent sync issues
            tickDayTime = false;
        }
        return original.call(gameTime, dayTime, tickDayTime);
    }
}
