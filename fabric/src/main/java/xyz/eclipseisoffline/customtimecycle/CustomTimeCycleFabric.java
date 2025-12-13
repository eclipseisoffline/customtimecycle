package xyz.eclipseisoffline.customtimecycle;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.permissions.PermissionCheck;

import java.nio.file.Path;
import java.util.function.Predicate;

public class CustomTimeCycleFabric extends CustomTimeCycle implements ModInitializer {

    @Override
    public void onInitialize() {
        initialise(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT);
        CommandRegistrationCallback.EVENT.register((dispatcher, buildContext, selection) -> registerCommand(dispatcher));
    }

    @Override
    public Predicate<CommandSourceStack> checkPermission(String permission, PermissionCheck vanillaFallback) {
        return Permissions.require(permission).or(Commands.hasPermission(vanillaFallback));
    }

    @Override
    public Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }
}
