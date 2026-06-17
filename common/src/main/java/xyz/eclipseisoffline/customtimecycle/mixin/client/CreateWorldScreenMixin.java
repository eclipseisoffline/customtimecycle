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
import net.minecraft.world.level.storage.PrimaryLevelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.eclipseisoffline.customtimecycle.client.ManageTimeCycleScreen;
import xyz.eclipseisoffline.customtimecycle.screens.PreconfiguredTimeCycleState;

import java.util.Objects;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin extends Screen {

    @Shadow
    @Final
    private WorldCreationUiState uiState;

    protected CreateWorldScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "onCreate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/worldselection/WorldOpenFlows;confirmWorldCreation(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/gui/screens/worldselection/CreateWorldScreen;Lcom/mojang/serialization/Lifecycle;Ljava/lang/Runnable;Z)V"))
    public void setPreconfiguredTimeCycle(CallbackInfo callbackInfo, @Local(name = "worldData") PrimaryLevelData levelData) {
        ((PreconfiguredTimeCycleState) levelData).customTimeCycle$setPreconfiguredTimeCycle(((PreconfiguredTimeCycleState) uiState).customTimeCycle$getPreconfiguredTimeCycle());
    }

    @Mixin(targets = "net/minecraft/client/gui/screens/worldselection/CreateWorldScreen$MoreTab")
    private static abstract class MoreTabMixin extends GridLayoutTab {

        public MoreTabMixin(Component title) {
            super(title);
        }

        @Inject(method = "<init>", at = @At("TAIL"))
        public void addTimeCycleOption(CreateWorldScreen createWorldScreen, CallbackInfo callbackInfo, @Local(name = "helper") GridLayout.RowHelper rowHelper) {
            rowHelper.addChild(Button.builder(Component.translatable("gui.customtimecycle.edit_button"),
                            button -> Minecraft.getInstance().gui.setScreen(new ManageTimeCycleScreen(createWorldScreen,
                                    Objects.requireNonNull(((PreconfiguredTimeCycleState) createWorldScreen.getUiState()).customTimeCycle$getPreconfiguredTimeCycle()),
                                    preconfigured -> ((PreconfiguredTimeCycleState) createWorldScreen.getUiState()).customTimeCycle$setPreconfiguredTimeCycle(preconfigured), false)))
                    .width(210)
                    .build());
        }
    }
}
