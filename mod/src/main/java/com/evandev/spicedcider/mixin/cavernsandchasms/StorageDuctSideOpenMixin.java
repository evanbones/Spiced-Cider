package com.evandev.spicedcider.mixin.cavernsandchasms;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.teamabnormals.caverns_and_chasms.common.block.StorageDuctBlock;
import com.teamabnormals.caverns_and_chasms.common.block.entity.StorageDuctBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@IfModLoaded("caverns_and_chasms")
@Mixin(value = StorageDuctBlock.class, remap = false)
public class StorageDuctSideOpenMixin {

    @Inject(method = "useWithoutItem", at = @At("RETURN"), cancellable = true)
    private void spicedcider$openFromSide(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult result, CallbackInfoReturnable<InteractionResult> cir) {
        if (cir.getReturnValue() != InteractionResult.PASS || !SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.storageDuctSideAccess, false)) return;
        if (!(level.getBlockEntity(pos) instanceof StorageDuctBlockEntity)) return;
        if (player instanceof ServerPlayer serverPlayer) {
            StorageDuctBlock.openMenu(serverPlayer, level, pos, StorageDuctBlock.DuctEnd.FIRST, null);
            PiglinAi.angerNearbyPiglins(player, true);
        }
        cir.setReturnValue(InteractionResult.sidedSuccess(level.isClientSide));
    }
}
