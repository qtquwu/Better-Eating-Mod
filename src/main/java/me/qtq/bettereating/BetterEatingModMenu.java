package me.qtq.bettereating;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;

public class BetterEatingModMenu implements ModMenuApi {
    @Override
    public String getModId() {
        return "bettereating";
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return screen -> AutoConfig.getConfigScreen(BetterEatingConfig.class, screen).get();
    }
}
