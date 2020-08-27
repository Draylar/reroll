package draylar.reroll.mixin;

import draylar.reroll.Reroll;
import draylar.reroll.RerollClient;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentScreen.class)
public abstract class EnchantmentScreenMixin extends HandledScreen<EnchantmentScreenHandler> {

    @Unique private static final Identifier REROLL_TEXTURE = new Identifier("reroll", "textures/reroll_button.png");
    @Unique private static final Identifier REROLL_TEXTURE_IN = new Identifier("reroll", "textures/reroll_button_in.png");

    private EnchantmentScreenMixin(EnchantmentScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Inject(
            method = "drawBackground",
            at = @At("RETURN")
    )
    private void onDrawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY, CallbackInfo ci) {
        assert client != null;

        int x = (this.width - this.backgroundWidth) / 2 + 160;
        int y = (this.height - this.backgroundHeight) / 2 + 73;

        if(mouseX >= x && mouseX <= x + 9 && mouseY >= y && mouseY <= y + 9) {
            this.client.getTextureManager().bindTexture(REROLL_TEXTURE_IN);
            DrawableHelper.drawTexture(matrices, x, y, 0, 0, 9, 9, 9, 9);

            // first line tooltip
            matrices.push();
            TranslatableText tooltipText = new TranslatableText("reroll.tooltip");
            String tooltipString = tooltipText.getString();
            matrices.translate(-client.textRenderer.getWidth(tooltipString) / 2f, 10, 0);
            this.client.textRenderer.drawWithShadow(matrices, tooltipText.formatted(Formatting.GRAY), mouseX, mouseY, 0xffffff);
            matrices.pop();

            matrices.push();
            TranslatableText expText = new TranslatableText("reroll.exp", RerollClient.getExpPerReroll());
            String expString = expText.getString();
            matrices.translate(-client.textRenderer.getWidth(expString) / 2f, 20, 0);
            this.client.textRenderer.drawWithShadow(matrices, expText, mouseX, mouseY, 0xffffff);
            matrices.pop();

            if(RerollClient.getLapisPerReroll() > 0) {
                matrices.push();
                TranslatableText lapisText = new TranslatableText("reroll.lapis", RerollClient.getLapisPerReroll());
                String lapisString = lapisText.getString();
                matrices.translate(-client.textRenderer.getWidth(lapisString) / 2f, 30, 0);
                this.client.textRenderer.drawWithShadow(matrices, lapisText, mouseX, mouseY, 0xffffff);
                matrices.pop();
            }
        } else {
            this.client.getTextureManager().bindTexture(REROLL_TEXTURE);
            DrawableHelper.drawTexture(matrices, x, y, 0, 0, 9, 9, 9, 9);
        }
    }

    @Inject(
            method = "mouseClicked",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        int x = (this.width - this.backgroundWidth) / 2 + 160;
        int y = (this.height - this.backgroundHeight) / 2 + 73;

        if(mouseX >= x && mouseX <= x + 9 && mouseY >= y && mouseY <= y + 9) {
            PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
            ClientSidePacketRegistry.INSTANCE.sendToServer(Reroll.REROLL_PACKET, packet);
            cir.setReturnValue(true);
        }
    }
}
