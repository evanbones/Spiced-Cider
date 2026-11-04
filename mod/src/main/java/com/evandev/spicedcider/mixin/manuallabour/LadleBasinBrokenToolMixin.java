package com.evandev.spicedcider.mixin.manuallabour;

import com.evandev.spicedcider.util.BrokenItemUtil;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@IfModLoaded("manual_labour")
@IfModLoaded("create")
@Mixin(targets = "com.evandev.manual_labour.compat.create.impl.LadleBasinInteraction", remap = false)
public class LadleBasinBrokenToolMixin {
    @WrapMethod(method = "tryMix")
    private static boolean spicedcider$brokenLadleCantStir(BasinBlockEntity basin, Player player, ItemStack ladle, Operation<Boolean> original) {
        return !BrokenItemUtil.isBroken(ladle) && original.call(basin, player, ladle);
    }
}
