package xyz.eclipseisoffline.customtimecycle.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.server.commands.TimeCommand;
import net.minecraft.world.clock.WorldClock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.concurrent.CompletableFuture;

@Mixin(TimeCommand.class)
public interface TimeCommandAccessor {

    @Accessor("ERROR_NO_DEFAULT_CLOCK")
    static DynamicCommandExceptionType getErrorNoDefaultClock() {
        throw new AssertionError();
    }

    @Accessor("ERROR_NO_TIME_MARKER_FOUND")
    static Dynamic2CommandExceptionType getErrorNoTimeMarkerFound() {
        throw new AssertionError();
    }

    @Invoker("suggestTimeMarkers")
    static CompletableFuture<Suggestions> suggestTimeMarkers(final CommandSourceStack source, final SuggestionsBuilder builder, final Holder<WorldClock> clock) {
        throw new AssertionError();
    }

    @Invoker("getDefaultClock")
    static Holder<WorldClock> getDefaultClock(final CommandSourceStack source) throws CommandSyntaxException {
        throw new AssertionError();
    }
}
