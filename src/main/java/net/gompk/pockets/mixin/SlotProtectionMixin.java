package net.gompk.pockets.mixin;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public class SlotProtectionMixin {

    @Inject(method = "getEmptySlot()I", at = @At("RETURN"), cancellable = true)
    private void skipArmorSlots(CallbackInfoReturnable<Integer> cir) {
        int slot = cir.getReturnValue();

        // If empty slot finder returns an armor/offhand slot, find alternative
        if (slot >= 36 && slot <= 40) {
            PlayerInventory inv = (PlayerInventory)(Object)this;

            // Look in expanded main inventory first
            for (int i = 0; i < inv.getMainStacks().size(); i++) {
                if (inv.getStack(i).isEmpty()) {
                    cir.setReturnValue(i);
                    return;
                }
            }

            cir.setReturnValue(-1); // No space available
        }
    }
}