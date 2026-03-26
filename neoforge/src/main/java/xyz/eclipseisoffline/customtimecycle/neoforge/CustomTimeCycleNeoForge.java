package xyz.eclipseisoffline.customtimecycle.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import xyz.eclipseisoffline.customtimecycle.CustomTimeCycle;

import java.nio.file.Path;

@Mod(CustomTimeCycle.MOD_ID)
public class CustomTimeCycleNeoForge extends CustomTimeCycle {

    public CustomTimeCycleNeoForge() {
        initialise(FMLLoader.getCurrent().getDist() == Dist.CLIENT);

        NeoForge.EVENT_BUS.addListener(this::registerCommandsEvent);
    }

    private void registerCommandsEvent(RegisterCommandsEvent event) {
        registerCommand(event.getDispatcher(), event.getBuildContext());
    }

    @Override
    public Path getConfigDir() {
        return FMLLoader.getCurrent().getGameDir().resolve("config");
    }
}
