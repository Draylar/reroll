package draylar.reroll;

import draylar.reroll.config.RerollConfig;
import draylar.reroll.impl.PlayerEntityManipulator;
import draylar.reroll.mixin.EnchantmentScreenHandlerAccessor;
import io.netty.buffer.Unpooled;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.util.Identifier;

public class Reroll implements ModInitializer {

    public static final RerollConfig CONFIG = AutoConfig.register(RerollConfig.class, JanksonConfigSerializer::new).getConfig();
    public static final Identifier REROLL_PACKET = new Identifier("reroll", "reroll_packet");
    public static final Identifier DATA_REQUEST = new Identifier("reroll", "data_request");
    public static final Identifier DATA_RESPONSE = new Identifier("reroll", "data_response");

    @Override
    public void onInitialize() {
        ServerSidePacketRegistry.INSTANCE.register(DATA_REQUEST, (context, packet) -> {
            PlayerEntity playerEntity = context.getPlayer();

            context.getTaskQueue().execute(() -> {
                PacketByteBuf newPacket = new PacketByteBuf(Unpooled.buffer());
                newPacket.writeInt(CONFIG.levelsPerReroll);
                newPacket.writeInt(CONFIG.lapisPerReroll);
                ServerSidePacketRegistry.INSTANCE.sendToPlayer(playerEntity, DATA_RESPONSE, newPacket);
            });
        });

        ServerSidePacketRegistry.INSTANCE.register(REROLL_PACKET, (context, packet) -> {
            PlayerEntity player = context.getPlayer();
            int lapisToRemove = CONFIG.lapisPerReroll;
            int levelsPerReroll = CONFIG.levelsPerReroll;

            context.getTaskQueue().execute(() -> {
                if(player.currentScreenHandler instanceof EnchantmentScreenHandler) {
                    Inventory inventory = ((EnchantmentScreenHandlerAccessor) player.currentScreenHandler).getInventory();

                    int playerLevels = player.experienceLevel;
                    ItemStack lapisStack = inventory.getStack(1);

                    if(playerLevels > levelsPerReroll && lapisStack.getCount() > lapisToRemove) {
                        // update seed & enchantment screen
                        ((PlayerEntityManipulator) player).rerollEnchantmentSeed();
                        ((EnchantmentScreenHandlerAccessor) player.currentScreenHandler).getSeed().set(player.getEnchantmentTableSeed());
                        player.currentScreenHandler.onContentChanged(inventory);

                        // take cost from player
                        player.addExperienceLevels(-levelsPerReroll);
                        ItemStack newLapisStack = lapisStack.copy();
                        newLapisStack.decrement(lapisToRemove);
                        inventory.setStack(1, newLapisStack);
                    }
                }
            });
        });
    }
}
