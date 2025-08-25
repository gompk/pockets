package net.gompk.pockets.mixin;

import net.gompk.pockets.Pockets;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public class InventoryScreenMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        InventoryScreenAccessor accessor = (InventoryScreenAccessor) this;
        accessor.setBackgroundHeight(accessor.getBackgroundHeight() + 38);
        // Remove the background height modification to fix recipe book positioning
        Pockets.LOGGER.info("POCKETS DEBUG: InventoryScreen initialized without background height changes");
    }
}