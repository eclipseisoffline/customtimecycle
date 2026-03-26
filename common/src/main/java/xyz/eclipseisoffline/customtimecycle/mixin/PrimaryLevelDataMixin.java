package xyz.eclipseisoffline.customtimecycle.mixin;

import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WorldData;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import xyz.eclipseisoffline.customtimecycle.screens.PreconfiguredTimeCycle;
import xyz.eclipseisoffline.customtimecycle.screens.PreconfiguredTimeCycleState;

@Mixin(PrimaryLevelData.class)
public abstract class PrimaryLevelDataMixin implements ServerLevelData, WorldData, PreconfiguredTimeCycleState {
    @Unique
    private @Nullable PreconfiguredTimeCycle customTimeCycle$preconfiguredTimeCycle;

    @Override
    public @Nullable PreconfiguredTimeCycle customTimeCycle$getPreconfiguredTimeCycle() {
        return customTimeCycle$preconfiguredTimeCycle;
    }

    @Override
    public void customTimeCycle$setPreconfiguredTimeCycle(@Nullable PreconfiguredTimeCycle timeCycle) {
        customTimeCycle$preconfiguredTimeCycle = timeCycle;
    }
}
