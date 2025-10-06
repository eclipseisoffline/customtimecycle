package xyz.eclipseisoffline.customtimecycle.screens;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.TimeArgument;
import xyz.eclipseisoffline.customtimecycle.TimeManager;

public record PreconfiguredTimeCycle(String dayTimeInput, String nightTimeInput, int dayTime, int nightTime) {
    private static final TimeArgument TIME_PARSER = TimeArgument.time(1);
    public static final PreconfiguredTimeCycle DEFAULT = new PreconfiguredTimeCycle("0.5d", "0.5d", (int) TimeManager.NORMAL_DAY_TIME, (int) TimeManager.NORMAL_NIGHT_TIME);

    public PreconfiguredTimeCycle withDayTime(String input) {
        try {
            return new PreconfiguredTimeCycle(input, nightTimeInput, TIME_PARSER.parse(new StringReader(input)), nightTime);
        } catch (CommandSyntaxException exception) {
            return new PreconfiguredTimeCycle(input, nightTimeInput, -1, nightTime);
        }
    }

    public PreconfiguredTimeCycle withNightTime(String input) {
        try {
            return new PreconfiguredTimeCycle(dayTimeInput, input, dayTime, TIME_PARSER.parse(new StringReader(input)));
        } catch (CommandSyntaxException exception) {
            return new PreconfiguredTimeCycle(dayTimeInput, input, dayTime, -1);
        }
    }

    public boolean isValid() {
        return dayTime > 0 && nightTime > 0;
    }
}
