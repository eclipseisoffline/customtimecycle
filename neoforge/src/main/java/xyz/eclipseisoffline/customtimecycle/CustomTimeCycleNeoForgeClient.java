package xyz.eclipseisoffline.customtimecycle;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import xyz.eclipseisoffline.customtimecycle.screens.ManageTimeCycleScreen;

@Mod(value = CustomTimeCycle.MOD_ID, dist = Dist.CLIENT)
public class CustomTimeCycleNeoForgeClient extends CustomTimeCycleNeoForgeBase {

    public CustomTimeCycleNeoForgeClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, (ignored, parent) -> new ManageTimeCycleScreen(parent, this::saveConfiguration));
    }
}
