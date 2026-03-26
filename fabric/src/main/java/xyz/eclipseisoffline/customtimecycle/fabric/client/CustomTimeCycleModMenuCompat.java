package xyz.eclipseisoffline.customtimecycle.fabric.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import xyz.eclipseisoffline.customtimecycle.CustomTimeCycle;
import xyz.eclipseisoffline.customtimecycle.client.ManageTimeCycleScreen;

public class CustomTimeCycleModMenuCompat implements ModMenuApi {

    @Override
    public ConfigScreenFactory<ManageTimeCycleScreen> getModConfigScreenFactory() {
        return parent -> new ManageTimeCycleScreen(parent, configured -> CustomTimeCycle.getInstance().reloadConfig(configured));
    }
}
