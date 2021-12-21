package draylar.reroll.config;

import draylar.omegaconfig.api.Comment;
import draylar.omegaconfig.api.Config;
import draylar.omegaconfig.api.Syncing;

public class RerollConfig implements Config {

    @Syncing
    @Comment(value = "Levels required/consumed per reroll in an Enchantment Table.")
    public int levelsPerReroll = 1;

    @Syncing
    @Comment(value = "Lapis Lazuli required/consumed per reroll in an Enchantment Table.")
    public int lapisPerReroll = 0;

    @Override
    public String getName() {
        return "reroll";
    }

    @Override
    public String getExtension() {
        return "json5";
    }
}
