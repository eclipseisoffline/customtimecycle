package xyz.eclipseisoffline.customtimecycle;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.clock.ClockTimeMarker;
import net.minecraft.world.clock.ClockTimeMarkers;
import net.minecraft.world.clock.WorldClock;
import xyz.eclipseisoffline.customtimecycle.mixin.TimeCommandAccessor;

public class TimeCycleCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("timecycle")
                //.requires(); TODO
        ;
        dispatcher.register(addClockCommands(root, context -> TimeCommandAccessor.getDefaultClock(context.getSource())));
    }

    private static <A extends ArgumentBuilder<CommandSourceStack, A>> A addClockCommands(A root, ClockGetter clockGetter) {
        return root
                .then(Commands.literal("status")
                        .executes(context -> {
                            return 0; // TODO
                        })
                )
                .then(Commands.literal("rate")
                        .then(Commands.literal("at")
                                .then(Commands.argument("timemarker", IdentifierArgument.id())
                                        .suggests((c, p) -> TimeCommandAccessor.suggestTimeMarkers(c.getSource(), p, clockGetter.getClock(c)))
                                        .then(Commands.argument("rate", FloatArgumentType.floatArg(1.0E-5F, 1000.0F))
                                                .executes(context -> {
                                                    ResourceKey<ClockTimeMarker> marker = ResourceKey.create(ClockTimeMarkers.ROOT_ID, IdentifierArgument.getId(context, "timemarker"));
                                                    float rate = FloatArgumentType.getFloat(context, "rate");
                                                    ClockRateManager.getInstance(context.getSource().getServer()).setRateForClockAtMarker(clockGetter.getClock(context), marker, rate);
                                                    return 0;
                                                })
                                        )
                                )
                        )
                );
    }

    private interface ClockGetter {
        Holder<WorldClock> getClock(CommandContext<CommandSourceStack> context) throws CommandSyntaxException;
    }
}
