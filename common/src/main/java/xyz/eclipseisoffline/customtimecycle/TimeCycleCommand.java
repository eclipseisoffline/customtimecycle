package xyz.eclipseisoffline.customtimecycle;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.world.clock.ClockTimeMarker;
import net.minecraft.world.clock.ClockTimeMarkers;
import net.minecraft.world.clock.WorldClock;
import xyz.eclipseisoffline.commonpermissionsapi.api.CommonPermissionNode;
import xyz.eclipseisoffline.commonpermissionsapi.api.CommonPermissions;
import xyz.eclipseisoffline.customtimecycle.clock.ClockRateManager;
import xyz.eclipseisoffline.customtimecycle.clock.CustomTimeCycleClockMarkers;
import xyz.eclipseisoffline.customtimecycle.clock.ServerClockManagerUtil;
import xyz.eclipseisoffline.customtimecycle.mixin.TimeCommandAccessor;

import java.util.Comparator;
import java.util.List;

public class TimeCycleCommand {
    private static final DynamicCommandExceptionType ERROR_NOT_A_PERIODIC_CLOCK = new DynamicCommandExceptionType(clock -> Component.literal("Clock " + clock + " is not periodic"));
    private static final Dynamic3CommandExceptionType ERROR_INVALID_TIME_MARKER = new Dynamic3CommandExceptionType((clock, timeMarker1, timeMarker2)
            -> Component.literal("Invalid time marker " + timeMarker1 + " or " + timeMarker2 + " for clock " + clock));

    private static final CommonPermissionNode PERMISSION_NODE = CommonPermissions.node(CustomTimeCycle.getModdedIdentifier("command"));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("timecycle")
                .requires(CommonPermissions.require(PERMISSION_NODE, PermissionLevel.GAMEMASTERS));
        dispatcher.register(addClockCommands(root, context -> TimeCommandAccessor.getDefaultClock(context.getSource())));
        dispatcher.register(root
                .then(Commands.literal("of")
                        .then(addClockCommands(Commands.argument("clock", ResourceArgument.resource(buildContext, Registries.WORLD_CLOCK)),
                                context -> ResourceArgument.getClock(context, "clock")))
                )
        );
    }

    private static <A extends ArgumentBuilder<CommandSourceStack, A>> A addClockCommands(A root, ClockGetter clockGetter) {
        return root
                .then(Commands.literal("status")
                        .executes(context -> {
                            ClockRateManager rateManager = ClockRateManager.getInstance(context.getSource().getServer());
                            Holder<WorldClock> clock = clockGetter.getPeriodicClock(context);
                            float rate = getServerClockManager(context).customTimeCycle$getRateForClock(clock);

                            context.getSource().sendSuccess(() -> Component.literal("Clock " + clock.getRegisteredName() + " is currently running at a rate multiplier of " + rate), false);
                            context.getSource().sendSuccess(() -> Component.literal("The following rates are set for clock " + clock.getRegisteredName() + ":"), false);

                            List<ResourceKey<ClockTimeMarker>> markers = context.getSource().getServer().clockManager().commandTimeMarkersForClock(clock)
                                    .sorted(Comparator.comparingInt(marker -> getServerClockManager(context).customTimeCycle$getTicksFor(clock, marker)))
                                    .toList();
                            MutableComponent markerRatesComponent = Component.empty();

                            for (int i = 0; i < markers.size(); i++) {
                                if (i > 0) {
                                    markerRatesComponent.append(" -> ");
                                }
                                ResourceKey<ClockTimeMarker> marker = markers.get(i);
                                markerRatesComponent.append(Component.literal("[" + rateManager.getClockRate(clock, marker) + "]")
                                        .withStyle(style -> style.withHoverEvent(new HoverEvent.ShowText(Component.literal(marker.identifier().toString())))));
                            }
                            context.getSource().sendSuccess(() -> markerRatesComponent, false);

                            return 0;
                        })
                )
                .then(Commands.literal("set")
                        .then(Commands.literal("from")
                                .then(periodicTimeMarkerArgument("from", clockGetter)
                                        .then(Commands.literal("to")
                                                .then(periodicTimeMarkerArgument("to", clockGetter)
                                                        .then(Commands.literal("duration")
                                                                .then(Commands.argument("duration", TimeArgument.time(1))
                                                                        .executes(context -> setDurationBetweenMarkers(context,
                                                                                getTimeMarker(context, "from"), getTimeMarker(context, "to"),
                                                                                IntegerArgumentType.getInteger(context, "duration"), clockGetter, true))
                                                                )
                                                        )
                                                        .then(Commands.literal("rate")
                                                                .then(Commands.argument("rate", FloatArgumentType.floatArg(1.0E-5F, 1000.0F))
                                                                        .executes(context -> setRateBetweenMarkers(context,
                                                                                getTimeMarker(context, "from"), getTimeMarker(context, "to"),
                                                                                FloatArgumentType.getFloat(context, "rate"), clockGetter, true))
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                        .then(Commands.argument("dayduration", TimeArgument.time(1))
                                .then(Commands.argument("nightduration", TimeArgument.time(1))
                                        .executes(context -> {
                                            int dayRate = IntegerArgumentType.getInteger(context, "dayduration");
                                            int nightRate = IntegerArgumentType.getInteger(context, "nightduration");
                                            setDurationBetweenMarkers(context, CustomTimeCycleClockMarkers.SUNRISE, CustomTimeCycleClockMarkers.SUNSET, dayRate, clockGetter, false);
                                            setDurationBetweenMarkers(context, CustomTimeCycleClockMarkers.SUNSET, CustomTimeCycleClockMarkers.SUNRISE, nightRate, clockGetter, false);
                                            context.getSource().sendSuccess(() -> Component.literal("Set day duration to " + dayRate + " server ticks and night duration to " + nightRate + " server ticks"), true);
                                            return 0;
                                        })
                                )
                        )
                )
                .then(Commands.literal("reset")
                        .executes(context -> {
                            Holder<WorldClock> clock = clockGetter.getPeriodicClock(context);
                            ClockRateManager.getInstance(context.getSource().getServer()).resetRatesForClock(clock);
                            context.getSource().sendSuccess(() -> Component.literal("Reset rates for clock " + clock.getRegisteredName()), true);
                            return 0;
                        })
                );
    }

    private static int setDurationBetweenMarkers(CommandContext<CommandSourceStack> context,
                                                 ResourceKey<ClockTimeMarker> from,
                                                 ResourceKey<ClockTimeMarker> to,
                                                 int duration,
                                                 ClockGetter clockGetter,
                                                 boolean feedback) throws CommandSyntaxException {
        Holder<WorldClock> clock = clockGetter.getPeriodicClock(context);
        int timeBetweenMarkers = getServerClockManager(context).customTimeCycle$getTicksBetween(clock, from, to);
        if (timeBetweenMarkers < 1) {
            throw ERROR_INVALID_TIME_MARKER.create(clock.getRegisteredName(), from.identifier(), to.identifier());
        }
        if (feedback) {
            context.getSource().sendSuccess(() -> Component.literal("Set duration for clock " + clock.getRegisteredName() + " between " + from.identifier() + " and " + to.identifier() + " to " + duration + " server ticks"), true);
        }
        return setRateBetweenMarkers(context, from, to, (float) timeBetweenMarkers / duration, clockGetter, false);
    }

    private static int setRateBetweenMarkers(CommandContext<CommandSourceStack> context,
                                             ResourceKey<ClockTimeMarker> from,
                                             ResourceKey<ClockTimeMarker> to,
                                             float rate,
                                             ClockGetter clockGetter,
                                             boolean feedback) throws CommandSyntaxException {
        Holder<WorldClock> clock = clockGetter.getPeriodicClock(context);
        List<ResourceKey<ClockTimeMarker>> markers = getServerClockManager(context).customTimeCycle$getMarkersBetween(clock, from, to, true);
        if (markers.isEmpty()) {
            throw ERROR_INVALID_TIME_MARKER.create(clock.getRegisteredName(), from.identifier(), to.identifier());
        }

        for (ResourceKey<ClockTimeMarker> marker : markers) {
            ClockRateManager.getInstance(context.getSource().getServer()).setRateForClockAtMarker(clock, marker, rate);
        }

        if (feedback) {
            context.getSource().sendSuccess(() -> Component.literal("Set rate multiplier for clock " + clock.getRegisteredName() + " between " + from.identifier() + " and " + to.identifier() + " to " + rate), true);
        }
        return markers.size();
    }

    private static RequiredArgumentBuilder<CommandSourceStack, Identifier> periodicTimeMarkerArgument(String name, ClockGetter clockGetter) {
        return Commands.argument(name, IdentifierArgument.id())
                .suggests((c, builder) -> TimeCommandAccessor.suggestTimeMarkers(c.getSource(), builder, clockGetter.getPeriodicClock(c)));
    }

    private static ResourceKey<ClockTimeMarker> getTimeMarker(CommandContext<CommandSourceStack> context, String argumentName) {
        return ResourceKey.create(ClockTimeMarkers.ROOT_ID, IdentifierArgument.getId(context, argumentName));
    }

    private static ServerClockManagerUtil getServerClockManager(CommandContext<CommandSourceStack> context) {
        return (ServerClockManagerUtil) context.getSource().getServer().clockManager();
    }

    @FunctionalInterface
    private interface ClockGetter {

        Holder<WorldClock> getClock(CommandContext<CommandSourceStack> context) throws CommandSyntaxException;

        default Holder<WorldClock> getPeriodicClock(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            Holder<WorldClock> clock = getClock(context);
            if (!getServerClockManager(context).customTimeCycle$isPeriodicClock(clock)) {
                throw ERROR_NOT_A_PERIODIC_CLOCK.create(clock.getRegisteredName());
            }
            return clock;
        }
    }
}
