package net.gompk.pockets.mixin;

import net.gompk.pockets.Pockets;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.util.collection.DefaultedList;

@Mixin(PlayerInventory.class)
public class InventoryInsertionBlockerMixin {

    @Shadow public DefaultedList<ItemStack> main;

    // Block setStack from putting items in armor/offhand slots during automatic operations
    @Inject(method = "setStack", at = @At("HEAD"), cancellable = true)
    private void blockArmorSlotInsertion(int slot, ItemStack stack, CallbackInfo ci) {
        // Block setting items in armor slots (36-39) and offhand (40) unless it's appropriate gear
        if (slot >= 36 && slot <= 40 && !stack.isEmpty()) {
            // Allow actual armor/shield items through
            if (isValidSlotItem(stack, slot)) {
                Pockets.LOGGER.info("POCKETS DEBUG: Allowing valid item {} in slot {}",
                        stack.getItem().getName().getString(), slot);
                return;
            }

            // Block inappropriate items from going into armor/offhand slots
            Pockets.LOGGER.info("POCKETS DEBUG: Blocked item {} from protected slot {}",
                    stack.getItem().getName().getString(), slot);

            // Try to find an alternative slot
            PlayerInventory inventory = (PlayerInventory)(Object)this;
            int alternativeSlot = findAlternativeSlot(inventory, stack);

            if (alternativeSlot != -1) {
                Pockets.LOGGER.info("POCKETS DEBUG: Moving to alternative slot: {}", alternativeSlot);
                inventory.setStack(alternativeSlot, stack);
            } else {
                Pockets.LOGGER.info("POCKETS DEBUG: No alternative slot found, item will be dropped or rejected");
                // The item pickup will fail naturally
            }

            ci.cancel();
        }
    }

    private boolean isValidSlotItem(ItemStack stack, int slot) {
        String itemName = stack.getItem().toString().toLowerCase();

        // Armor slots (36-39)
        if (slot >= 36 && slot <= 39) {
            return itemName.contains("helmet") ||
                    itemName.contains("chestplate") ||
                    itemName.contains("leggings") ||
                    itemName.contains("boots") ||
                    itemName.contains("elytra");
        }

        // Offhand slot (40) - allow shields, totems, maps, etc.
        if (slot == 40) {
            return itemName.contains("shield") ||
                    itemName.contains("totem") ||
                    itemName.contains("map") ||
                    itemName.contains("compass") ||
                    itemName.contains("clock");
        }

        return false;
    }

    private int findAlternativeSlot(PlayerInventory inventory, ItemStack stack) {
        // Check hotbar first (0-8)
        for (int i = 0; i <= 8; i++) {
            if (inventory.getStack(i).isEmpty()) {
                return i;
            }
        }

        // Check main inventory (9-35)
        for (int i = 9; i <= 35; i++) {
            if (inventory.getStack(i).isEmpty()) {
                return i;
            }
        }

        // Check extra slots (45-53)
        for (int i = 45; i <= 53; i++) {
            if (inventory.getStack(i).isEmpty()) {
                return i;
            }
        }

        return -1; // No empty slot found
    }
}