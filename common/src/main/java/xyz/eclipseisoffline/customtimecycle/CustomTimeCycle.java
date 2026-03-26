package xyz.eclipseisoffline.customtimecycle;

import com.mojang.brigadier.CommandDispatcher;

import java.nio.file.Path;

import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import xyz.eclipseisoffline.customtimecycle.screens.PreconfiguredTimeCycle;

public abstract class CustomTimeCycle {
    private static @Nullable CustomTimeCycle instance;
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "customtimecycle";
    public static final String COMMAND_PERMISSION = "command";
    public static final Path CONFIG_FILE = Path.of(MOD_ID + ".json");

    public void initialise(boolean client) {
        if (instance != null) {
            throw new IllegalStateException("CustomTimeCycle is already initialised");
        }
        instance = this;

        LOGGER.info("Custom Time Cycle initialising, reading configuration");
        TimeCycleConfiguration.load(getConfigDir().resolve(CONFIG_FILE), client);
    }

    public void reloadConfig(PreconfiguredTimeCycle timeCycle) {
        TimeCycleConfiguration.loadFromPreconfigured(getConfigDir().resolve(CONFIG_FILE), timeCycle);
    }

    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        TimeCycleCommand.register(dispatcher, buildContext);
    }

    public abstract Path getConfigDir();

    public static CustomTimeCycle getInstance() {
        if (instance == null) {
            throw new IllegalStateException("CustomTimeCycle accessed before it was initialised");
        }
        return instance;
    }

    public static Identifier getModdedIdentifier(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
