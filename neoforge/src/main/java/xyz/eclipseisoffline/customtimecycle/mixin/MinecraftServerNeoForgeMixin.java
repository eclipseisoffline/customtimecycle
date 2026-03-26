package xyz.eclipseisoffline.customtimecycle.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerNeoForgeMixin {

    /*
    // NeoForge is special and has its own special way of sending time rate to clients
    // This doesn't work very well with the server-side nature of this mod, so we just disable time ticking on clients when a custom time rate is active
    // TimeManager takes care of actually updating time on clients when necessary
    @WrapOperation(method = "synchronizeTime", at = @At(value = "NEW", target = "net/neoforged/neoforge/network/payload/ClientboundCustomSetTimePayload"))
    public ClientboundCustomSetTimePayload broadcastCustomTimeRules(long gameTime, long dayTime, boolean tickDayTime, float dayTimeFraction, float dayTimePerTick, Operation<ClientboundCustomSetTimePayload> original, ServerLevel level) {
        if (!TimeManager.getInstance(level).isNormalTimeRate()) {
            tickDayTime = false;
            dayTimePerTick = 0.0F;
        }
        return original.call(gameTime, dayTime, tickDayTime, dayTimeFraction, dayTimePerTick);
    }
     */
}
