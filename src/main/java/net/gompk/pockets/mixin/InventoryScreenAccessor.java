package net.gompk.pockets.mixin;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HandledScreen.class)
public interface InventoryScreenAccessor {
    @Accessor("backgroundHeight")
    void setBackgroundHeight(int height);

    @Accessor("backgroundHeight")
    int getBackgroundHeight();
}