package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.registry.ModTags;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemStack.class)
public abstract class ItemStackHammerMiningMixin {

    @WrapMethod(method = "getDestroySpeed")
    private float cider$hammerHandSpeed(BlockState state, Operation<Float> original) {
        return cider$cannotMine() ? 1.0F : original.call(state);
    }

    @WrapMethod(method = "isCorrectToolForDrops")
    private boolean cider$hammerNotCorrectTool(BlockState state, Operation<Boolean> original) {
        return !cider$cannotMine() && original.call(state);
    }

    @Unique
    private boolean cider$cannotMine() {
        return SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.hammersCannotMine, false)
                && ((ItemStack) (Object) this).is(ModTags.Items.HAMMER_TOOLS);
    }
}
