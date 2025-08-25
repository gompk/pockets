package net.gompk.pockets.mixin;

import net.gompk.pockets.Pockets;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerScreenHandler.class)
public abstract class PlayerScreenHandlerMixinold {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void addExtraInventoryRows(PlayerInventory inventory, boolean onServer, PlayerEntity owner, CallbackInfo ci) {
        Pockets.LOGGER.info("POCKETS DEBUG: Adding extra inventory row...");

        // In vanilla inventory GUI:
        // - Main inventory rows are at Y: 18, 36, 54
        // - Hotbar is at Y: 76
        // So our extra row should be at Y: 72 (between main inventory and hotbar)

        int baseIndex = 36;
        int y = 72; // Position between main inventory and hotbar

        for (int x = 0; x < 9; x++) {
            Slot newSlot = new Slot(inventory, baseIndex + x, 8 + x * 18, y);
            ((ScreenHandlerAccessor)this).invokeAddSlot(newSlot);
        }

        Pockets.LOGGER.info("POCKETS DEBUG: Added 9 slots at Y position {}", y);
    }
}