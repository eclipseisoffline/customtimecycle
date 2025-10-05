package xyz.eclipseisoffline.customtimecycle;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;

import java.util.function.Predicate;

public class CustomTimeCycleFabric implements ModInitializer, CustomTimeCycle {

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, buildContext, selection) -> registerCommand(dispatcher));
    }

    @Override
    public Predicate<CommandSourceStack> checkPermission(String permission, int operatorLevel) {
        return Permissions.require(permission, operatorLevel);
    }
}
