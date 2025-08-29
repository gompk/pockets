/*package net.gompk.pockets.mixin;

import net.gompk.pockets.Pockets;
import net.gompk.pockets.mixin.accessor.ScreenHandlerAccessor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerScreenHandler.class)
public abstract class PlayerScreenHandlerMixin {

    private boolean addingExtraSlots = false;

    // Move hotbar slots down by 18 pixels to make room for extra inventory row
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/PlayerScreenHandler;addSlot(Lnet/minecraft/screen/slot/Slot;)Lnet/minecraft/screen/slot/Slot;"))
    private Slot redirectAddSlot(PlayerScreenHandler instance, Slot slot) {
        if (!addingExtraSlots && slot.inventory instanceof PlayerInventory) {
            int index = slot.getIndex();

            // Move hotbar slots (indices 0-8) down by 18 pixels
            if (index >= 0 && index <= 8) {
                Pockets.LOGGER.info("POCKETS DEBUG: Moving hotbar slot {} from Y:{} to Y:{}", index, slot.y, slot.y + 18);
                slot = new Slot(slot.inventory, index, slot.x, slot.y + 18);
            }
            // Keep main inventory slots (9-35) in their original positions
            // Keep armor slots (36-39) and offhand (40) in their original positions
        }
        return ((ScreenHandlerAccessor)instance).invokeAddSlot(slot);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addExtraInventoryRows(PlayerInventory inventory, boolean onServer, PlayerEntity owner, CallbackInfo ci) {
        addingExtraSlots = true;
        Pockets.LOGGER.info("POCKETS DEBUG: Adding extra inventory row...");

        Pockets.extraInventoryRows = 1;

        // Add extra slots between main inventory (Y: 54) and hotbar (now at Y: 94)
        // Position them at Y: 72 to be visually between the main inventory and hotbar
        int startIndex = 36; // Use indices 45-53 to avoid conflicts with armor/offhand slots
        int y = 72;

        for (int x = 0; x < 9; x++) {
            Slot newSlot = new Slot(inventory, startIndex + x, 8 + x * 18, y);
            ((ScreenHandlerAccessor)this).invokeAddSlot(newSlot);
            Pockets.LOGGER.info("POCKETS DEBUG: Added slot {} at ({}, {})", startIndex + x, 8 + x * 18, y);
        }

        addingExtraSlots = false;
        Pockets.LOGGER.info("POCKETS DEBUG: Extra row added successfully");
    }


}

package net.gompk.pockets.mixin;

import net.gompk.pockets.Pockets;
import net.gompk.pockets.mixin.accessor.ScreenHandlerAccessor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerScreenHandler.class)
public abstract class PlayerScreenHandlerMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addExtraSlots(PlayerInventory inventory, boolean onServer, PlayerEntity owner, CallbackInfo ci) {
        addExtraInventoryRows(inventory, Pockets.extraInventoryRows);
    }

    private void addExtraInventoryRows(PlayerInventory inventory, int rowCount) {
        if (rowCount <= 0) return;

        // Standard vanilla slot positions
        int mainInvStartY = 54;
        int slotWidth = 18;
        int invStartX = 8;

        int startY = mainInvStartY + (3 * slotWidth); // Start after main inventory
        int nextAvailableIndex = 41; // Start at 41 to avoid vanilla conflicts

        for (int row = 0; row < rowCount; row++) {
            int y = startY + (row * slotWidth);
            addSingleRow(inventory, nextAvailableIndex + (row * 9), y, invStartX, slotWidth);
        }

        Pockets.LOGGER.info("Added {} extra inventory rows", rowCount);
    }

    private void addSingleRow(PlayerInventory inventory, int startIndex, int y, int startX, int slotWidth) {
        for (int col = 0; col < 9; col++) {
            int x = startX + (col * slotWidth);
            int slotIndex = startIndex + col;

            Slot slot = createExtraSlot(inventory, slotIndex, x, y);
            ((ScreenHandlerAccessor)this).invokeAddSlot(slot);
        }
    }

    private Slot createExtraSlot(PlayerInventory inventory, int index, int x, int y) {
        return new Slot(inventory, index, x, y) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return true; // Allow all items in extra slots
            }

            @Override
            public boolean canTakeItems(PlayerEntity player) {
                return true;
            }
        };
    }
}

 */

package net.gompk.pockets.mixin;

import net.gompk.pockets.Pockets;
import net.gompk.pockets.mixin.accessor.ScreenHandlerAccessor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerScreenHandler.class)
public abstract class PlayerScreenHandlerMixin {

    // Redirect addSlot to move hotbar down when adding it
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/PlayerScreenHandler;addSlot(Lnet/minecraft/screen/slot/Slot;)Lnet/minecraft/screen/slot/Slot;"))
    private Slot moveHotbarSlots(PlayerScreenHandler instance, Slot slot) {
        if (slot.inventory instanceof PlayerInventory) {
            int index = slot.getIndex();
            // Move hotbar slots (0-8) down by 18px per extra row
            if (index >= 0 && index <= 8) {
                int yOffset = Pockets.extraInventoryRows * 18;
                slot = new Slot(slot.inventory, index, slot.x, slot.y + yOffset);
            }
        }
        return ((ScreenHandlerAccessor)instance).invokeAddSlot(slot);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addExtraSlots(PlayerInventory inventory, boolean onServer, PlayerEntity owner, CallbackInfo ci) {
        if (Pockets.extraInventoryRows <= 0) return;

        int startY = 54 + (3 * 18); // After main inventory
        int startIndex = 36; // Start after main inventory (0-35)

        for (int row = 0; row < Pockets.extraInventoryRows; row++) {
            for (int col = 0; col < 9; col++) {
                int x = 8 + (col * 18);
                int y = startY + (row * 18);
                int index = startIndex + (row * 9) + col;

                Slot extraSlot = new Slot(inventory, index, x, y) {
                    @Override
                    public boolean canInsert(ItemStack stack) {
                        return true;
                    }
                };

                ((ScreenHandlerAccessor)this).invokeAddSlot(extraSlot);
            }
        }

        Pockets.LOGGER.info("POCKETS: Added {} extra rows with {} total slots",
                Pockets.extraInventoryRows, Pockets.extraInventoryRows * 9);
    }
}