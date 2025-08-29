package net.gompk.pockets.mixin;

import net.gompk.pockets.Pockets;
import net.gompk.pockets.mixin.accessor.InventoryScreenAccessor;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public class InventoryScreenMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        // Increase background height to accommodate extra row
        InventoryScreenAccessor accessor = (InventoryScreenAccessor) this;
        int originalHeight = accessor.getBackgroundHeight();
        accessor.setBackgroundHeight(originalHeight + 35); // Add 18 pixels for one extra row
        Pockets.LOGGER.info("POCKETS DEBUG: InventoryScreen background height increased from {} to {}", originalHeight, accessor.getBackgroundHeight());
    }

/*    // Inject after the background is drawn to add our extended section
    @Inject(method = "drawBackground", at = @At("TAIL"))
    private void drawExtendedBackground(DrawContext context, float delta, int mouseX, int mouseY, CallbackInfo ci) {
        if (Pockets.extraInventoryRows > 0) {
            // Get the screen position
            HandledScreenAccessor accessor = (HandledScreenAccessor) this;
            int x = accessor.getGuiX();
            int y = accessor.getGuiY();

            // Draw an extended section for the extra inventory row
            // This extends the inventory background by drawing a horizontal strip
            context.drawTexture(
                    net.minecraft.util.Identifier.of("minecraft", "textures/gui/container/inventory.png"),
                    x + 7, y + 126, 7, 83, 162, 18
            );
        }
    } */
}