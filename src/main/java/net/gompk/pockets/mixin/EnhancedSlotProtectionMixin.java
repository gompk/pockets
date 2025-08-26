package net.gompk.pockets.mixin;

import net.gompk.pockets.Pockets;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.Items;
import net.minecraft.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public class EnhancedSlotProtectionMixin {

    // Block insertStack method from putting items in protected slots
    @Inject(method = "insertStack(Lnet/minecraft/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private void blockInsertStack(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        PlayerInventory inventory = (PlayerInventory)(Object)this;

        // Try to insert into allowed slots only
        if (insertIntoAllowedSlots(inventory, stack)) {
            cir.setReturnValue(true);
        } else {
            Pockets.LOGGER.info("POCKETS DEBUG: Failed to insert {} - no valid slots available",
                    stack.getItem().getName().getString());
            cir.setReturnValue(false);
        }
        cir.cancel();
    }

    // Override getEmptySlot to never return armor/offhand slots for regular items
    @Inject(method = "getEmptySlot()I", at = @At("HEAD"), cancellable = true)
    private void getEmptySlotProtected(CallbackInfoReturnable<Integer> cir) {
        PlayerInventory inventory = (PlayerInventory)(Object)this;

        // Check hotbar first (0-8)
        for (int i = 0; i <= 8; i++) {
            if (inventory.getStack(i).isEmpty()) {
                cir.setReturnValue(i);
                return;
            }
        }

        // Check main inventory (9-35)
        for (int i = 9; i <= 35; i++) {
            if (inventory.getStack(i).isEmpty()) {
                cir.setReturnValue(i);
                return;
            }
        }

        // Check extra slots (45-53) if they exist
        for (int i = 45; i <= 53; i++) {
            if (i < inventory.size() && inventory.getStack(i).isEmpty()) {
                cir.setReturnValue(i);
                return;
            }
        }

        // No empty slots found - never return armor/offhand slots (36-40)
        cir.setReturnValue(-1);
    }

    private boolean insertIntoAllowedSlots(PlayerInventory inventory, ItemStack stack) {
        // Try to add to existing stacks first
        if (addToExistingStacks(inventory, stack)) {
            return true;
        }

        // Find empty slot in allowed areas
        int emptySlot = getProtectedEmptySlot(inventory);
        if (emptySlot != -1) {
            inventory.getMainStacks().set(emptySlot, stack.copy());
            stack.setCount(0);
            return true;
        }

        return false;
    }

    private boolean addToExistingStacks(PlayerInventory inventory, ItemStack stack) {
        // Check hotbar (0-8)
        for (int i = 0; i <= 8; i++) {
            ItemStack existing = inventory.getStack(i);
            if (canStackWith(existing, stack)) {
                int transferred = Math.min(stack.getCount(), existing.getMaxCount() - existing.getCount());
                existing.increment(transferred);
                stack.decrement(transferred);
                if (stack.isEmpty()) return true;
            }
        }

        // Check main inventory (9-35)
        for (int i = 9; i <= 35; i++) {
            ItemStack existing = inventory.getStack(i);
            if (canStackWith(existing, stack)) {
                int transferred = Math.min(stack.getCount(), existing.getMaxCount() - existing.getCount());
                existing.increment(transferred);
                stack.decrement(transferred);
                if (stack.isEmpty()) return true;
            }
        }

        // Check extra slots (45-53)
        for (int i = 45; i <= 53; i++) {
            if (i < inventory.size()) {
                ItemStack existing = inventory.getStack(i);
                if (canStackWith(existing, stack)) {
                    int transferred = Math.min(stack.getCount(), existing.getMaxCount() - existing.getCount());
                    existing.increment(transferred);
                    stack.decrement(transferred);
                    if (stack.isEmpty()) return true;
                }
            }
        }

        return false;
    }

    private boolean canStackWith(ItemStack existing, ItemStack stack) {
        return !existing.isEmpty() &&
                existing.getItem() == stack.getItem() &&
                existing.getCount() < existing.getMaxCount() &&
                ItemStack.areEqual(existing, stack);
    }

    private int getProtectedEmptySlot(PlayerInventory inventory) {
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
            if (i < inventory.size() && inventory.getStack(i).isEmpty()) {
                return i;
            }
        }

        return -1;
    }
}