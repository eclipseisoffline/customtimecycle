package xyz.eclipseisoffline.customtimecycle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jspecify.annotations.Nullable;
import xyz.eclipseisoffline.customtimecycle.screens.PreconfiguredTimeCycle;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

public record TimeCycleConfiguration(int dayTime, int nightTime) {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private static final Function<Integer, DataResult<Integer>> TIME_RATE_VALIDATOR = timeRate -> {
        if (timeRate < 1) {
            return DataResult.error(() -> "Time rate must be at least 1");
        }
        return DataResult.success(timeRate);
    };
    public static final Codec<TimeCycleConfiguration> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.validate(TIME_RATE_VALIDATOR).fieldOf("daytime").forGetter(TimeCycleConfiguration::dayTime),
                    Codec.INT.validate(TIME_RATE_VALIDATOR).fieldOf("nighttime").forGetter(TimeCycleConfiguration::nightTime)
            ).apply(instance, TimeCycleConfiguration::new)
    );
    public static final TimeCycleConfiguration DEFAULT = new TimeCycleConfiguration(12000, 12000);
    private static @Nullable TimeCycleConfiguration loaded;

    public PreconfiguredTimeCycle toPreconfigured() {
        if (equals(DEFAULT)) {
            return PreconfiguredTimeCycle.DEFAULT;
        }
        return new PreconfiguredTimeCycle(String.valueOf(dayTime), String.valueOf(nightTime), dayTime, nightTime);
    }

    private void save(Path path) {
        try {
            Files.writeString(path, GSON.toJson(CODEC.encodeStart(JsonOps.INSTANCE, this).getOrThrow()));
        } catch (IOException exception) {
            CustomTimeCycle.LOGGER.warn("Failed to write default config file!", exception);
        }
    }

    public static void load(Path path, boolean saveWhenMissing) {
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
        } else if (saveWhenMissing) {
            CustomTimeCycle.LOGGER.info("Configuration not found, writing default one");
            loaded.save(path);
        }
    }

    public static void loadFromPreconfigured(Path path, PreconfiguredTimeCycle timeCycle) {
        loaded = new TimeCycleConfiguration(timeCycle.dayTime(), timeCycle.nightTime());
        loaded.save(path);
    }

    public static TimeCycleConfiguration getLoaded() {
        if (loaded == null) {
            throw new IllegalStateException("Configuration accessed before it was loaded");
        }
        return loaded;
    }
}
