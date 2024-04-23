package xyz.eclipseisoffline.customtimecycle.mixin;

import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ReputationEventHandler;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.eclipseisoffline.customtimecycle.TimeManager;

@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillager implements ReputationEventHandler,
        VillagerDataHolder {

    public VillagerMixin(EntityType<? extends AbstractVillager> entityType,
            Level level) {
        super(entityType, level);
    }

    @Inject(method = "golemSpawnConditionsMet", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
    public void checkCustomTimeCycleLength(long gameTime, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        Optional<Long> optional = getBrain().getMemory(MemoryModuleType.LAST_SLEPT);
        if (optional.isPresent() && level() instanceof ServerLevel serverLevel) {
            TimeManager timeManager = TimeManager.getInstance(serverLevel);

            long dayNightDuration = timeManager.getDayTimeRate().getDuration() + timeManager.getNightTimeRate().getDuration();
            callbackInfoReturnable.setReturnValue(gameTime - optional.orElseThrow() < dayNightDuration);
        }
    }
}
