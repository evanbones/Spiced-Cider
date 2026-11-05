package com.evandev.spicedcider.mixin.autumnity;

import com.evandev.spicedcider.compat.firevariants.AutumnityFireBlocks;
import com.evandev.spicedcider.registry.ModFireBlocks;
import com.evandev.spicedcider.registry.ModPaleGardenBlocks;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.teamabnormals.autumnity.common.block.util.JackOLanternUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("autumnity")
@Mixin(JackOLanternUtil.class)
public abstract class JackOLanternUtilSulfurMixin {
    @ModifyReturnValue(method = "getJackOLantern", at = @At("RETURN"))
    private static Block spicedcider$sulfurJack(Block original, ItemStack stack) {
        return stack.is(ModFireBlocks.SULFUR_TORCH_ITEM.get()) ? ModPaleGardenBlocks.SULFUR_JACK_O_LANTERN.get() : original;
    }

    @ModifyReturnValue(method = "getLargeJackOLantern", at = @At("RETURN"))
    private static Block spicedcider$sulfurSlice(Block original, ItemStack stack) {
        return stack.is(ModFireBlocks.SULFUR_TORCH_ITEM.get()) ? AutumnityFireBlocks.largeSulfurSlice.get() : original;
    }
}
