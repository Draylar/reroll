package draylar.reroll;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;

public class RerollClient implements ClientModInitializer {

    private static int cachedExp = -1;
    private static int cachedLapis = -1;

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(Reroll.DATA_RESPONSE, (client, handler, buf, responseSender) -> {
            int exp = buf.readInt();
            int lapis = buf.readInt();

            client.execute(() -> {
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
        ClientPlayNetworking.send(Reroll.DATA_REQUEST, PacketByteBufs.create());
    }
}
