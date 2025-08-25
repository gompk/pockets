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

    // Intercept hotbar slot additions and move them down by 18 pixels
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/PlayerScreenHandler;addSlot(Lnet/minecraft/screen/slot/Slot;)Lnet/minecraft/screen/slot/Slot;"))
    private Slot redirectAddSlot(PlayerScreenHandler instance, Slot slot) {
        if (!addingExtraSlots && slot.inventory instanceof PlayerInventory && slot.getIndex() < 9) {
            // This is a hotbar slot (indices 0-8), move it down by 18 pixels
            Pockets.LOGGER.info("POCKETS DEBUG: Moving hotbar slot {} from Y:{} to Y:{}", slot.getIndex(), slot.y, slot.y + 18);
            slot = new Slot(slot.inventory, slot.getIndex(), slot.x, slot.y + 18);
        }
        return ((ScreenHandlerAccessor)instance).invokeAddSlot(slot);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addExtraInventoryRows(PlayerInventory inventory, boolean onServer, PlayerEntity owner, CallbackInfo ci) {
        addingExtraSlots = true;
        Pockets.LOGGER.info("POCKETS DEBUG: Adding extra inventory row...");

        // The vanilla inventory has:
        // Main inventory rows at Y: 18, 36, 54 (slots 9-35)
        // Hotbar at Y: 76 (slots 0-8) - now moved to Y: 94
        // We want to add our row at Y: 72 (between main inventory and hotbar)

        // Use indices 43-51 for our extra slots (after all vanilla slots including body slots 41-42)
        int baseIndex = 43;
        int y = 169; // Between main inventory and hotbar

        for (int x = 0; x < 9; x++) {
            Slot newSlot = new Slot(inventory, baseIndex + x, 8 + x * 18, y);
            ((ScreenHandlerAccessor)this).invokeAddSlot(newSlot);
            Pockets.LOGGER.info("POCKETS DEBUG: Added slot {} at ({}, {})", baseIndex + x, 8 + x * 18, y);
        }

        addingExtraSlots = false;
        Pockets.LOGGER.info("POCKETS DEBUG: Extra row added at Y: {}, hotbar moved to Y: {}", y, 94);
    }
}