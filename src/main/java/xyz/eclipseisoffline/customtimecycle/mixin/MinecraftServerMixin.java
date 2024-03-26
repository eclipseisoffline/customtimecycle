package xyz.eclipseisoffline.customtimecycle.mixin;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.eclipseisoffline.customtimecycle.TimeManager;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Redirect(method = "synchronizeTime", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/resources/ResourceKey;)V"))
    public void broadcastCustomTimeRules(PlayerList instance, Packet<?> packet, ResourceKey<Level> dimension, ServerLevel level) {
        if (!TimeManager.getInstance(level).isNormalTimeRate()) {
            // Tell the clients not to update time locally when using custom time rate to prevent sync issues
            instance.broadcastAll(new ClientboundSetTimePacket(level.getGameTime(), level.getDayTime(), false), dimension);
        } else {
            instance.broadcastAll(packet, dimension);
        }
    }
}
