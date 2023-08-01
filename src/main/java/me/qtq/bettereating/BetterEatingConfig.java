package me.qtq.bettereating;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.*;

@Config(name = "bettereating")
public class BetterEatingConfig implements ConfigData {
    @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
    @ConfigEntry.Gui.Tooltip
    public int foodTimerLength = 5;

    public boolean restrictPlaceBlocks = true;

    public boolean restrictUseBlocks = true;

    public boolean restrictUseItems = true;

    public boolean restrictEntityInteraction = true;
}
