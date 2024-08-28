package xyz.eclipseisoffline.customtimecycle;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import xyz.eclipseisoffline.customtimecycle.TimeManager.DayPartTimeRate;

public class CustomTimeCycle implements ModInitializer {

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
                Commands.literal("timecycle")
                        .requires(Permissions.require("timecycle.command", 2))
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
                                            (int) timeManager.getDayTimeRate().getDuration(), 20);
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
        ));
    }
}
