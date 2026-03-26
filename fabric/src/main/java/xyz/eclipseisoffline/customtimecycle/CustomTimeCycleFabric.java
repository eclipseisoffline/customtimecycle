package xyz.eclipseisoffline.customtimecycle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.permissions.PermissionCheck;

import java.nio.file.Path;
import java.util.function.Predicate;

public class CustomTimeCycleFabric extends CustomTimeCycle implements ModInitializer {

    @Override
    public void onInitialize() {
        initialise(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT);
        CommandRegistrationCallback.EVENT.register((dispatcher, _, _) -> registerCommand(dispatcher));
    }

    @Override
    public Predicate<CommandSourceStack> checkPermission(String permission, PermissionCheck vanillaFallback) {
        //return Permissions.require(permission).or(Commands.hasPermission(vanillaFallback));
        return s -> true;
    }

    @Override
    public Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }
}
