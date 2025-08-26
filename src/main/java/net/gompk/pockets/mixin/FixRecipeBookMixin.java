package net.gompk.pockets.mixin;

import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.ScreenPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InventoryScreen.class)
public abstract class FixRecipeBookMixin {

    @Inject(method = "getRecipeBookButtonPos", at = @At("HEAD"), cancellable = true)
    private void fixRecipeBookButtonPos(CallbackInfoReturnable<ScreenPos> cir) {
        // Cast this InventoryScreen to the accessor to get x/y
        HandledScreenAccessor accessor = (HandledScreenAccessor) (Object) this;

        int offsetX = 104; // horizontal offset from background
        int offsetY = 60;   // vertical offset from top of background

        int stableX = accessor.getGuiX() + offsetX;
        int stableY = accessor.getGuiY() + offsetY;

        cir.setReturnValue(new ScreenPos(stableX, stableY));
    }
}


