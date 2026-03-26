package xyz.eclipseisoffline.customtimecycle.neoforge.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import xyz.eclipseisoffline.customtimecycle.CustomTimeCycle;
import xyz.eclipseisoffline.customtimecycle.client.ManageTimeCycleScreen;

@Mod(value = CustomTimeCycle.MOD_ID, dist = Dist.CLIENT)
public class CustomTimeCycleNeoForgeClient {

    public CustomTimeCycleNeoForgeClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class,
                (ignored, parent) -> new ManageTimeCycleScreen(parent, configured -> CustomTimeCycle.getInstance().reloadConfig(configured)));
    }
}
