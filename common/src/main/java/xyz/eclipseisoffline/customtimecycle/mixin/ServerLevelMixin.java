package xyz.eclipseisoffline.customtimecycle.mixin;

import java.util.List;
import java.util.concurrent.Executor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import net.minecraft.world.level.storage.ServerLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.eclipseisoffline.customtimecycle.TimeManager;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {

    @Unique
    private TimeManager customTimeCycle$timeManager;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void initialiseTimeManager(MinecraftServer server, Executor dispatcher,
            LevelStorageAccess levelStorageAccess, ServerLevelData serverLevelData,
            ResourceKey<Level> dimension, LevelStem levelStem, ChunkProgressListener progressListener,
            boolean isDebug, long biomeZoomSeed, List<CustomSpawner> customSpawners, boolean tickTime,
            RandomSequences randomSequences, CallbackInfo callbackInfo) {
        customTimeCycle$timeManager = TimeManager.getInstance((ServerLevel) (Object) this);
    }

    @Redirect(method = "tickTime", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setDayTime(J)V"))
    public void customDayTimeUpdate(ServerLevel level, long time) {
        customTimeCycle$timeManager.tickTime();
    }
}
