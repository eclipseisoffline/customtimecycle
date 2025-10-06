package xyz.eclipseisoffline.customtimecycle.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.WorldData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.eclipseisoffline.customtimecycle.client.ManageTimeCycleScreen;
import xyz.eclipseisoffline.customtimecycle.screens.TimeCycleState;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin extends Screen {

    @Shadow
    @Final
    WorldCreationUiState uiState;

    protected CreateWorldScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "createNewWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;createWorldOpenFlows()Lnet/minecraft/client/gui/screens/worldselection/WorldOpenFlows;"))
    public void setPreconfiguredTimeCycle(CallbackInfo callbackInfo, @Local WorldData levelData) {
        ((TimeCycleState) levelData).customTimeCycle$setPreconfiguredTimeCycle(((TimeCycleState) uiState).customTimeCycle$getPreconfiguredTimeCycle());
    }

    @Mixin(targets = "net/minecraft/client/gui/screens/worldselection/CreateWorldScreen$MoreTab")
    private static abstract class MoreTabMixin extends GridLayoutTab {

        public MoreTabMixin(Component title) {
            super(title);
        }

        @Inject(method = "<init>", at = @At("TAIL"))
        public void addTimeCycleOption(CreateWorldScreen createWorldScreen, CallbackInfo callbackInfo, @Local GridLayout.RowHelper rowHelper) {
            rowHelper.addChild(Button.builder(Component.translatable("gui.customtimecycle.edit_button"),
                            button -> Minecraft.getInstance().setScreen(new ManageTimeCycleScreen(createWorldScreen,
                                    ((TimeCycleState) createWorldScreen.getUiState()).customTimeCycle$getPreconfiguredTimeCycle(),
                                    preconfigured -> ((TimeCycleState) createWorldScreen.getUiState()).customTimeCycle$setPreconfiguredTimeCycle(preconfigured), false)))
                    .width(210)
                    .build());
        }
    }
}
