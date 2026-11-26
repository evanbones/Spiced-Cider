package com.evandev.spicedcider.mixin.blockbox;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbility;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.blockbox.common.block.PalisadeBlock;

import java.util.function.Supplier;

@IfModLoaded("blockbox")
@Mixin(PalisadeBlock.class)
public class PalisadeMissingStrippedFormMixin {
    @Shadow
    @Final
    public Supplier<Block> strippedForm;

    @Inject(method = "getToolModifiedState", at = @At("HEAD"), cancellable = true)
    private void spicedcider$missingStrippedForm(BlockState state, UseOnContext context, ItemAbility ability, boolean simulate, CallbackInfoReturnable<BlockState> cir) {
        if (this.strippedForm != null && this.strippedForm.get() == null) {
            cir.setReturnValue(null);
        }
    }
}
