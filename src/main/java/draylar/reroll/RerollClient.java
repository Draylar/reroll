package draylar.reroll;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.network.PacketByteBuf;

public class RerollClient implements ClientModInitializer {

    private static int cachedExp = -1;
    private static int cachedLapis = -1;

    @Override
    public void onInitializeClient() {
        ClientSidePacketRegistry.INSTANCE.register(Reroll.DATA_RESPONSE, (context, packet) -> {
            int exp = packet.readInt();
            int lapis = packet.readInt();

            context.getTaskQueue().execute(() -> {
                cachedExp = exp;
                cachedLapis = lapis;
            });
        });
    }

    public static int getExpPerReroll() {
        if(cachedExp == -1) {
            requestData();
            return 1;
        }

        return cachedExp;
    }

    public static int getLapisPerReroll() {
        if(cachedLapis == - 1) {
            requestData();
            return 0;
        }

        return cachedLapis;
    }

    private static void requestData() {
        ClientSidePacketRegistry.INSTANCE.sendToServer(Reroll.DATA_REQUEST, new PacketByteBuf(Unpooled.buffer()));
    }
}
