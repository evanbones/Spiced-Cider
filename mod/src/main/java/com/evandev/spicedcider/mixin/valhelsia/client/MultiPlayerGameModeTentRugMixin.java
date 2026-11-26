package com.evandev.spicedcider.mixin.valhelsia.client;

import com.evandev.spicedcider.compat.valhelsia.TentRugs;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.stal111.valhelsia_structures.common.block.ExplorersTentBlock;
import dev.tazer.offgrid.world.Layers;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@IfModLoaded("offgrid")
@IfModLoaded("valhelsia_structures")
@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeTentRugMixin {
    @ModifyVariable(method = "useItemOn", at = @At("HEAD"), argsOnly = true)
    private BlockHitResult spicedcider$rugIntoTent(BlockHitResult hit, LocalPlayer player, InteractionHand hand) {
        if (!TentRugs.enabled() || hit.getType() != BlockHitResult.Type.BLOCK) return hit;
        BlockPos pos = hit.getBlockPos();
        if (!(player.level().getBlockState(pos).getBlock() instanceof ExplorersTentBlock)) return hit;
        if (!(player.getItemInHand(hand).getItem() instanceof BlockItem item) || !Layers.isLayer(item.getBlock().defaultBlockState())) return hit;
        if (TentRugs.hasRug(player.level(), pos)) return hit;
        BlockPos floor = pos.below();
        return new BlockHitResult(new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5), Direction.UP, floor, false);
    }
}
