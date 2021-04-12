package draylar.reroll.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import draylar.reroll.config.RerollConfig;
import me.shedaniel.autoconfig.AutoConfig;

public class ModMenuEntrypoint implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return screen -> AutoConfig.getConfigScreen(RerollConfig.class, screen).get();
    }
}
