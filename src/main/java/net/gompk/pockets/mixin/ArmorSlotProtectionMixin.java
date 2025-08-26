package net.gompk.pockets.mixin;

import net.gompk.pockets.Pockets;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public class ArmorSlotProtectionMixin {

    @Inject(method = "canInsert", at = @At("HEAD"), cancellable = true)
    private void preventArmorSlotInsertion(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Slot slot = (Slot)(Object)this;

        if (slot.inventory instanceof PlayerInventory) {
            int index = slot.getIndex();

            // Block insertion into armor slots (36-39)
            if (index >= 36 && index <= 39) {
                Pockets.LOGGER.info("POCKETS DEBUG: Blocked manual insertion of {} into armor slot {}",
                        stack.getItem().getName().getString(), index);
                cir.setReturnValue(false);
                return;
            }
        }
    }

    // Allow removal from armor slots - we only want to prevent automatic insertion
}