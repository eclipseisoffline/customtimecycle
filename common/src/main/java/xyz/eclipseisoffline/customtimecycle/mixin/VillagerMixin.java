package xyz.eclipseisoffline.customtimecycle.mixin;

import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ReputationEventHandler;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerDataHolder;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.eclipseisoffline.customtimecycle.clock.ServerClockManagerUtil;

@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillager implements ReputationEventHandler, VillagerDataHolder {

    public VillagerMixin(EntityType<? extends AbstractVillager> entityType,
            Level level) {
        super(entityType, level);
    }

    @Inject(method = "golemSpawnConditionsMet", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
    public void checkCustomTimeCycleLength(long gameTime, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        Optional<Long> optional = getBrain().getMemory(MemoryModuleType.LAST_SLEPT);
        if (optional.isPresent() && level() instanceof ServerLevel serverLevel) {
            serverLevel.dimensionType().defaultClock().ifPresent(clock -> {
                // I think this should work, but I am not entirely sure. Mojang should just fix this themselves at this point
                long dayNightDuration = ((ServerClockManagerUtil) serverLevel.getServer().clockManager()).customTimeCycle$getAdjustedPeriodTicks(clock);
                callbackInfoReturnable.setReturnValue(gameTime - optional.orElseThrow() < dayNightDuration);
            });
        }
    }
}
