package net.gompk.pockets.mixin;

import net.gompk.pockets.Pockets;
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
        int startIndex = 45; // Use indices 45-53 to avoid conflicts with armor/offhand slots
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