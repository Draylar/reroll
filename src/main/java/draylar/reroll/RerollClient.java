package draylar.reroll;

import draylar.omegaconfiggui.OmegaConfigGui;
import net.fabricmc.api.ClientModInitializer;

public class RerollClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        OmegaConfigGui.registerConfigScreen(Reroll.CONFIG);
    }

    public static int getRerollButtonOffset() {
        return Reroll.CONFIG.moveButtonAboveList ? -69 : 0;
    }
}
