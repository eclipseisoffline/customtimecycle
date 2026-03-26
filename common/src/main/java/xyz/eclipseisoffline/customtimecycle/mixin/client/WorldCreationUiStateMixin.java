package xyz.eclipseisoffline.customtimecycle.mixin.client;

import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import xyz.eclipseisoffline.customtimecycle.TimeCycleConfiguration;
import xyz.eclipseisoffline.customtimecycle.screens.PreconfiguredTimeCycle;
import xyz.eclipseisoffline.customtimecycle.screens.PreconfiguredTimeCycleState;

@Mixin(WorldCreationUiState.class)
public abstract class WorldCreationUiStateMixin implements PreconfiguredTimeCycleState {

    @Unique
    private @Nullable PreconfiguredTimeCycle customTimeCycle$preconfiguredTimeCycle = TimeCycleConfiguration.getLoaded().toPreconfigured();

    @Override
    public @Nullable PreconfiguredTimeCycle customTimeCycle$getPreconfiguredTimeCycle() {
        return customTimeCycle$preconfiguredTimeCycle;
    }

    @Override
    public void customTimeCycle$setPreconfiguredTimeCycle(@Nullable PreconfiguredTimeCycle timeCycle) {
        customTimeCycle$preconfiguredTimeCycle = timeCycle;
    }
}
