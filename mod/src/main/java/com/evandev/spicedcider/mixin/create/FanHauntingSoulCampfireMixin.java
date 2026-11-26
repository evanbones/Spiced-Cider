package com.evandev.spicedcider.mixin.create;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("create")
@Mixin(targets = "com.simibubi.create.compat.jei.category.FanHauntingCategory", remap = false)
public class FanHauntingSoulCampfireMixin {
    @WrapOperation(method = "renderAttachedBlock", at = @At(value = "INVOKE", target = "Lnet/createmod/catnip/gui/element/GuiGameElement;of(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/createmod/catnip/gui/element/GuiGameElement$GuiRenderBuilder;"))
    private GuiGameElement.GuiRenderBuilder spicedcider$soulCampfire(BlockState state, Operation<GuiGameElement.GuiRenderBuilder> original) {
        if (!SpicedCiderConfig.clientOr(SpicedCiderConfig.CLIENT.emiCreateFanRework, false)) return original.call(state);
        return original.call(Blocks.SOUL_CAMPFIRE.defaultBlockState());
    }
}
