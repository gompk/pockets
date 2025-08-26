package net.gompk.pockets.mixin;

import net.gompk.pockets.Pockets;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public class ArmorVisualFixMixin {

    @Inject(method = "setStack", at = @At("TAIL"))
    private void onArmorEquipped(int slot, ItemStack stack, CallbackInfo ci) {
        // When armor is equipped in slots 36-39, force a visual update
        if (slot >= 36 && slot <= 39) {
            Pockets.LOGGER.info("POCKETS DEBUG: Armor equipped in slot {}: {}",
                    slot, stack.isEmpty() ? "empty" : stack.getItem().getName().getString());

            PlayerInventory inventory = (PlayerInventory)(Object)this;
            // The visual update will happen automatically through the setStack method
            // No additional action needed - just log for debugging
            if (inventory.player != null) {
                Pockets.LOGGER.info("POCKETS DEBUG: Player equipment updated for {}",
                        inventory.player.getName().getString());
            }
        }
    }
}