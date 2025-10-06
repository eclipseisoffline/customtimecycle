package xyz.eclipseisoffline.customtimecycle.mixin.client;

import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import xyz.eclipseisoffline.customtimecycle.TimeManagerConfiguration;
import xyz.eclipseisoffline.customtimecycle.screens.PreconfiguredTimeCycle;
import xyz.eclipseisoffline.customtimecycle.screens.TimeCycleState;

@Mixin(WorldCreationUiState.class)
public abstract class WorldCreationUiStateMixin implements TimeCycleState {
    @Unique
    private PreconfiguredTimeCycle customTimeCycle$preconfiguredTimeCycle = TimeManagerConfiguration.getLoaded().toPreconfigured();

    @Override
    public PreconfiguredTimeCycle customTimeCycle$getPreconfiguredTimeCycle() {
        return customTimeCycle$preconfiguredTimeCycle;
    }

    @Override
    public void customTimeCycle$setPreconfiguredTimeCycle(PreconfiguredTimeCycle timeCycle) {
        customTimeCycle$preconfiguredTimeCycle = timeCycle;
    }
}
