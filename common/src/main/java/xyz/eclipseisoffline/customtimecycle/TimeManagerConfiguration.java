package xyz.eclipseisoffline.customtimecycle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

public record TimeManagerConfiguration(long dayTime, long nightTime) {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private static final Function<Long, DataResult<Long>> TIME_RATE_VALIDATOR = timeRate -> {
        if (TimeManager.invalidTimeRate(timeRate)) {
            return DataResult.error(() -> "Time rate must be at least 1");
        }
        return DataResult.success(timeRate);
    };
    public static final Codec<TimeManagerConfiguration> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.LONG.validate(TIME_RATE_VALIDATOR).fieldOf("daytime").forGetter(TimeManagerConfiguration::dayTime),
                    Codec.LONG.validate(TIME_RATE_VALIDATOR).fieldOf("nighttime").forGetter(TimeManagerConfiguration::nightTime)
            ).apply(instance, TimeManagerConfiguration::new)
    );
    public static final TimeManagerConfiguration DEFAULT = new TimeManagerConfiguration(TimeManager.NORMAL_DAY_TIME, TimeManager.NORMAL_NIGHT_TIME);
    private static TimeManagerConfiguration loaded;

    public static void load(Path path) {
        if (loaded != null) {
            return;
        }

        loaded = DEFAULT;
        if (Files.exists(path)) {
            if (Files.isReadable(path)) {
                try (Reader reader = new FileReader(path.toFile())) {
                    loaded = CODEC.parse(JsonOps.INSTANCE, JsonParser.parseReader(reader)).getOrThrow();
                } catch (IllegalStateException exception) {
                    CustomTimeCycle.LOGGER.warn("Failed to parse config file!", exception);
                    CustomTimeCycle.LOGGER.warn("Please fix the errors above, using default config!");
                } catch (IOException exception) {
                    CustomTimeCycle.LOGGER.warn("Failed to read config file, using default!", exception);
                }
            } else {
                CustomTimeCycle.LOGGER.warn("Failed to read config file, because it's not readable!");
            }
        } else {
            CustomTimeCycle.LOGGER.info("Configuration not found, writing default one");
            try {
                Files.writeString(path, GSON.toJson(CODEC.encodeStart(JsonOps.INSTANCE, loaded).getOrThrow()));
            } catch (IOException exception) {
                CustomTimeCycle.LOGGER.warn("Failed to write default config file!", exception);
            }
        }
    }

    public static TimeManagerConfiguration getLoaded() {
        if (loaded == null) {
            throw new IllegalStateException("Configuration accessed before it was loaded");
        }
        return loaded;
    }
}
