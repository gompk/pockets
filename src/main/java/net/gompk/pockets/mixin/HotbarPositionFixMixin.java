package net.gompk.pockets.mixin;

import net.gompk.pockets.Pockets;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerScreenHandler.class)
public class HotbarPositionFixMixin {

    // Intercept hotbar slot creation and adjust Y position
    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/slot/Slot;<init>(Lnet/minecraft/inventory/Inventory;III)V"), index = 3)
    private int adjustHotbarY(int y) {
        // If this is a hotbar slot (Y position around 142), move it down by 18 pixels
        if (y == 142) {
            Pockets.LOGGER.info("POCKETS DEBUG: Adjusting hotbar Y from {} to {}", y, y + 18);
            return y + 18;
        }
        return y;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addExtraInventoryRows(PlayerInventory inventory, boolean onServer, PlayerEntity owner, CallbackInfo ci) {
        Pockets.LOGGER.info("POCKETS DEBUG: Adding extra inventory row...");

        Pockets.extraInventoryRows = 1;

        // Add extra slots between main inventory (Y: 84) and hotbar (now at Y: 160)
        // Position them at Y: 102 to be visually between the main inventory and hotbar
        int startIndex = 45; // Use indices 45-53 to avoid conflicts with armor/offhand slots
        int y = 102; // Position between main inventory and moved hotbar

        for (int x = 0; x < 9; x++) {
            Slot newSlot = new Slot(inventory, startIndex + x, 8 + x * 18, y);
            ((ScreenHandlerAccessor)this).invokeAddSlot(newSlot);
            Pockets.LOGGER.info("POCKETS DEBUG: Added slot {} at ({}, {})", startIndex + x, 8 + x * 18, y);
        }

        Pockets.LOGGER.info("POCKETS DEBUG: Extra row added successfully");
    }
}