package com.evandev.spicedcider.mixin.reliablebackpacks;

import com.evandev.spicedcider.compat.reliablebackpacks.WornBackpackContainer;
import com.evandev.spicedcider.compat.reliablebackpacks.WornBackpackSlots;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.violetmoon.quark.api.IQuarkButtonAllowed;
import org.violetmoon.quark.api.ITransferManager;
import org.violetmoon.quark.base.config.QuarkGeneralConfig;
import org.violetmoon.quark.base.handler.InventoryTransferHandler;

@IfModLoaded("quark")
@Mixin(value = InventoryTransferHandler.class, remap = false)
public class QuarkTransferBackpackMixin {

    @ModifyReturnValue(method = "accepts", at = @At("RETURN"))
    private static boolean spicedcider$ignoreBackpackSlots(boolean original, AbstractContainerMenu container, Player player) {
        if (!original || container instanceof InventoryMenu || container instanceof ITransferManager || container instanceof IQuarkButtonAllowed) return original;
        if (WornBackpackSlots.first(container) < 0) return original;
        return container.slots.size() - WornBackpackContainer.SIZE - player.getInventory().items.size() >= QuarkGeneralConfig.chestButtonSlotTarget;
    }
}
