package draylar.reroll;

import draylar.omegaconfig.OmegaConfig;
import draylar.reroll.config.RerollConfig;
import draylar.reroll.impl.PlayerEntityManipulator;
import draylar.reroll.mixin.EnchantmentScreenHandlerAccessor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.util.Identifier;

public class Reroll implements ModInitializer {

    public static final RerollConfig CONFIG = OmegaConfig.register(RerollConfig.class);
    public static final Identifier REROLL_PACKET = new Identifier("reroll", "reroll_packet");

    public static void reroll(PlayerEntity player) {
        int lapisToRemove = CONFIG.lapisPerReroll;
        int levelsPerReroll = CONFIG.levelsPerReroll;

        if(player.currentScreenHandler instanceof EnchantmentScreenHandler) {
            Inventory inventory = ((EnchantmentScreenHandlerAccessor) player.currentScreenHandler).getInventory();
            ItemStack input = inventory.getStack(0);

            // If the input stack does not have an item, do not reroll.
            if(input.isEmpty()) {
                return;
            }

            int playerLevels = player.experienceLevel;
            ItemStack lapisStack = inventory.getStack(1);

            if((playerLevels > levelsPerReroll && lapisStack.getCount() > lapisToRemove) || player.isCreative()) {
                // update seed & enchantment screen
                ((PlayerEntityManipulator) player).rerollEnchantmentSeed();
                ((EnchantmentScreenHandlerAccessor) player.currentScreenHandler).getSeed().set(player.getEnchantmentTableSeed());
                player.currentScreenHandler.onContentChanged(inventory);

                // take cost from player
                if(!player.isCreative()) {
                    player.addExperienceLevels(-levelsPerReroll);
                    ItemStack newLapisStack = lapisStack.copy();
                    newLapisStack.decrement(lapisToRemove);
                    inventory.setStack(1, newLapisStack);
                }
            }
        }
    }

    @Override
    public void onInitialize() {
        ServerPlayNetworking.registerGlobalReceiver(REROLL_PACKET, (server, player, handler, buf, responseSender) ->
                server.execute(() -> reroll(player)));
    }
}
