package draylar.reroll.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "reroll")
public class RerollConfig implements ConfigData {
    @Comment(value = "Levels required/consumed per reroll in an Enchantment Table.")
    public int levelsPerReroll = 1;

    @Comment(value = "Lapis Lazuli required/consumed per reroll in an Enchantment Table.")
    public int lapisPerReroll = 0;
}
