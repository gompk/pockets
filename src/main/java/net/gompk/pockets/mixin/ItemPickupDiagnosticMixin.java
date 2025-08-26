package net.gompk.pockets.mixin;

import net.gompk.pockets.Pockets;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public class ItemPickupDiagnosticMixin {

    // This method is called when trying to insert items into the inventory
    @Inject(method = "insertStack(Lnet/minecraft/item/ItemStack;)Z", at = @At("HEAD"))
    private void debugInsertStack(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Pockets.LOGGER.info("POCKETS DEBUG: insertStack called with item: {}",
                stack.getItem().getName().getString());
    }

    // This method finds the first available slot for an item
    @Inject(method = "getEmptySlot()I", at = @At("HEAD"))
    private void debugGetEmptySlot(CallbackInfoReturnable<Integer> cir) {
        Pockets.LOGGER.info("POCKETS DEBUG: getEmptySlot called");
    }

    // This method is called to add items to existing stacks
    @Inject(method = "addStack(Lnet/minecraft/item/ItemStack;)I", at = @At("HEAD"))
    private void debugAddStack(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        Pockets.LOGGER.info("POCKETS DEBUG: addStack called with item: {}",
                stack.getItem().getName().getString());
    }

    // Override getEmptySlot to skip armor slots (36-39)
    @Inject(method = "getEmptySlot()I", at = @At("RETURN"), cancellable = true)
    private void preventArmorSlotSelection(CallbackInfoReturnable<Integer> cir) {
        int slot = cir.getReturnValue();

        // If the selected slot is an armor slot (36-39), find an alternative
        if (slot >= 36 && slot <= 39) {
            Pockets.LOGGER.info("POCKETS DEBUG: Blocked armor slot {}, searching for alternative", slot);

            PlayerInventory inventory = (PlayerInventory)(Object)this;

            // Search for empty slot in main inventory (9-44) and hotbar (0-8)
            // Skip the extra slots we added (45-53)
            for (int i = 0; i < 45; i++) {
                if (inventory.getStack(i).isEmpty()) {
                    Pockets.LOGGER.info("POCKETS DEBUG: Found alternative slot: {}", i);
                    cir.setReturnValue(i);
                    return;
                }
            }

            // If no empty slots found in main inventory/hotbar, return -1 (no space)
            Pockets.LOGGER.info("POCKETS DEBUG: No alternative slots available");
            cir.setReturnValue(-1);
        }
    }
}