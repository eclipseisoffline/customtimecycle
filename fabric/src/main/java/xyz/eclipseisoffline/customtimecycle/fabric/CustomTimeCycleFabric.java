package xyz.eclipseisoffline.customtimecycle.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import xyz.eclipseisoffline.customtimecycle.CustomTimeCycle;

import java.nio.file.Path;

public class CustomTimeCycleFabric extends CustomTimeCycle implements ModInitializer {

    @Override
    public void onInitialize() {
        initialise(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT);
        CommandRegistrationCallback.EVENT.register((dispatcher, buildContext, _) -> registerCommand(dispatcher, buildContext));
    }

    @Override
    public Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }
}
