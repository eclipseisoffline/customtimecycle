package xyz.eclipseisoffline.customtimecycle;

import net.neoforged.fml.loading.FMLLoader;

import java.nio.file.Path;

public abstract class CustomTimeCycleNeoForgeBase implements CustomTimeCycle {

    @Override
    public Path getConfigDir() {
        return FMLLoader.getCurrent().getGameDir().resolve("config");
    }
}
