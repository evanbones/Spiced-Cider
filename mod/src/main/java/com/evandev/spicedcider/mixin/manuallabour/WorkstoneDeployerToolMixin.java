package com.evandev.spicedcider.mixin.manuallabour;

import com.evandev.manual_labour.content.block.entity.WorkstoneBlockEntity;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("manual_labour")
@Mixin(targets = "com.evandev.manual_labour.content.block.WorkstoneBlock", remap = false)
public class WorkstoneDeployerToolMixin {

    @WrapOperation(
            method = "useItemOn",
            at = @At(value = "INVOKE", target = "Lcom/evandev/manual_labour/content/block/entity/WorkstoneBlockEntity;canAddItem(Lnet/minecraft/world/item/ItemStack;)Z")
    )
    private boolean spicedcider$deployerKeepsTool(WorkstoneBlockEntity workstone, ItemStack stack, Operation<Boolean> original, @Local(argsOnly = true) Player player) {
        return !(player instanceof FakePlayer) && original.call(workstone, stack);
    }
}
