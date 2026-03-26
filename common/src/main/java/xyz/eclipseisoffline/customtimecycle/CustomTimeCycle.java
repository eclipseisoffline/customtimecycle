package xyz.eclipseisoffline.customtimecycle;

import com.mojang.brigadier.CommandDispatcher;

import java.nio.file.Path;
import java.util.function.Predicate;

import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.Identifier;
import net.minecraft.server.permissions.PermissionCheck;
import org.slf4j.Logger;
import xyz.eclipseisoffline.customtimecycle.screens.PreconfiguredTimeCycle;

public abstract class CustomTimeCycle {
    private static CustomTimeCycle instance;
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
        /*dispatcher.register(
                Commands.literal("timecycle")
                        .requires(checkPermission(MOD_ID + "." + COMMAND_PERMISSION, Commands.LEVEL_GAMEMASTERS))
                        .then(Commands.literal("status")
                                .executes(context -> {
                                    TimeManager timeManager = TimeManager.getInstance(context.getSource().getLevel());
                                    DayPartTimeRate timeRate = timeManager.getTimeRate();

                                    if (timeManager.isNormalTimeRate()) {
                                        context.getSource().sendSuccess(() -> Component.literal("Using normal time durations"), false);
                                    } else {
                                        boolean usingDayCycle = timeManager.isDay();

                                        context.getSource().sendSuccess(() -> Component.literal("Using " + (usingDayCycle ? "day time tick rate" : "night time tick rate") + " (duration=" + timeRate.getDuration() + ")"), false);
                                        context.getSource().sendSuccess(() -> Component.literal("Incrementing "
                                                + BigDecimal.valueOf(timeRate.getIncrement()).setScale(2, RoundingMode.HALF_UP).doubleValue()
                                                + " time ticks every " + timeRate.getIncrementModulus() + " server ticks"), false);
                                    }

                                    String dayTimeDuration = StringUtil.formatTickDuration(
                                            (int) timeManager.getDayTimeRate().getDuration(), 20); //FIXME
                                    String nightTimeDuration = StringUtil.formatTickDuration(
                                            (int) timeManager.getNightTimeRate().getDuration(), 20);
                                    context.getSource().sendSuccess(() -> Component.literal("Day time duration: " + dayTimeDuration), false);
                                    context.getSource().sendSuccess(() -> Component.literal("Night time duration: " + nightTimeDuration), false);

                                    return (int) timeManager.getTimeRate().getDuration();
                                })
                        )
                        .then(Commands.literal("set")
                                .then(Commands.argument("dayduration", TimeArgument.time(1))
                                        .then(Commands.argument("nightduration", TimeArgument.time(1))
                                                .executes(context -> {
                                                    TimeManager timeManager = TimeManager.getInstance(context.getSource().getLevel());

                                                    long dayRate = IntegerArgumentType.getInteger(context, "dayduration");
                                                    long nightRate = IntegerArgumentType.getInteger(context, "nightduration");
                                                    timeManager.setTimeRate(dayRate, nightRate);
                                                    context.getSource().sendSuccess(() -> Component.literal("Set day duration to " + dayRate + " server ticks and night duration to " + nightRate + " server ticks"), true);
                                                    return 0;
                                                })
                                        )
                                )
                        )
                        .then(Commands.literal("reset")
                                .executes(context -> {
                                    TimeManager.getInstance(context.getSource().getLevel()).resetTimeRate();
                                    context.getSource().sendSuccess(() -> Component.literal("Reset day and night durations"), true);
                                    return 0;
                                })
                        )
        );*/

    }

    public Predicate<CommandSourceStack> checkPermission(String permission, PermissionCheck vanillaFallback) {
        return Commands.hasPermission(vanillaFallback);
    }

    public abstract Path getConfigDir();

    public static CustomTimeCycle getInstance() {
        return instance;
    }

    public static Identifier getModdedIdentifier(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
