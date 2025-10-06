package xyz.eclipseisoffline.customtimecycle.mixin;

import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WorldData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import xyz.eclipseisoffline.customtimecycle.screens.PreconfiguredTimeCycle;
import xyz.eclipseisoffline.customtimecycle.screens.TimeCycleState;

@Mixin(PrimaryLevelData.class)
public abstract class PrimaryLevelDataMixin implements ServerLevelData, WorldData, TimeCycleState {
    @Unique
    private PreconfiguredTimeCycle customTimeCycle$preconfiguredTimeCycle;

    @Override
    public PreconfiguredTimeCycle customTimeCycle$getPreconfiguredTimeCycle() {
        return customTimeCycle$preconfiguredTimeCycle;
    }

    @Override
    public void customTimeCycle$setPreconfiguredTimeCycle(PreconfiguredTimeCycle timeCycle) {
        customTimeCycle$preconfiguredTimeCycle = timeCycle;
    }
}
