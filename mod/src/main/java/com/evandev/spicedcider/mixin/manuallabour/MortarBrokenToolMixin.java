package com.evandev.spicedcider.mixin.manuallabour;

import com.evandev.manual_labour.content.block.entity.MortarBlockEntity;
import com.evandev.manual_labour.registry.ModTags;
import com.evandev.spicedcider.util.BrokenItemUtil;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;

@IfModLoaded("manual_labour")
@Mixin(targets = "com.evandev.manual_labour.content.block.MortarBlock")
public class MortarBrokenToolMixin {
    @WrapMethod(method = "useItemOn")
    private ItemInteractionResult spicedcider$brokenToolCantGrind(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, Operation<ItemInteractionResult> original) {
        if (!player.isShiftKeyDown()) {
            ItemStack tool = stack;
            if (stack.isEmpty() && level.getBlockEntity(pos) instanceof MortarBlockEntity mortar) {
                tool = mortar.getDecorativeTool();
            }
            if ((tool.is(ModTags.Items.PESTLES) || tool.is(ModTags.Items.LADLES)) && BrokenItemUtil.isBroken(tool)) {
                return ItemInteractionResult.CONSUME;
            }
        }
        return original.call(stack, state, level, pos, player, hand, hit);
    }
}
