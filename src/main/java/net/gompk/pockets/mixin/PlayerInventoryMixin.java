// Modifies PlayerInventory class to increase the main inventory size from 36 to 45 slots.
/*
package net.gompk.pockets.mixin;

import net.gompk.pockets.Pockets;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {
    @ModifyConstant(method = "<init>", constant = @Constant(intValue = 36))
    private int modifyMainSize(int original) {
        Pockets.LOGGER.info("POCKETS DEBUG: PlayerInventory size modified from {} to 45", original);
        return 45; // your new size
    }
} */

package net.gompk.pockets.mixin;

import net.gompk.pockets.Pockets;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {
    @ModifyConstant(method = "<init>", constant = @Constant(intValue = 36))
    private int expandMainInventory(int original) {
        int newSize = original + (Pockets.extraInventoryRows * 9);
        Pockets.LOGGER.info("POCKETS: Expanding inventory from {} to {} slots", original, newSize);
        return newSize;
    }
}