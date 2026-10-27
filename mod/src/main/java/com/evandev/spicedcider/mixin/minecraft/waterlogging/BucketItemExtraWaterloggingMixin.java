package com.evandev.spicedcider.mixin.minecraft.waterlogging;

import com.evandev.spicedcider.content.waterlogging.ExtraWaterlogging;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BucketItem.class)
public abstract class BucketItemExtraWaterloggingMixin {

    @Shadow
    @Final
    public Fluid content;

    @Shadow
    protected abstract void playEmptySound(@Nullable Player player, LevelAccessor level, BlockPos pos);

    @ModifyReturnValue(method = "canBlockContainFluid", at = @At("RETURN"))
    private boolean spicedcider$canFillExtra(boolean original, @Local(argsOnly = true) BlockState state) {
        return original || this.content == Fluids.WATER && ExtraWaterlogging.isExtra(state.getBlock()) && !state.getValue(BlockStateProperties.WATERLOGGED);
    }

    @Inject(method = "emptyContents(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/BlockHitResult;Lnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private void spicedcider$fillExtra(@Nullable Player player, Level level, BlockPos pos, @Nullable BlockHitResult result, @Nullable ItemStack container, CallbackInfoReturnable<Boolean> cir) {
        if (this.content != Fluids.WATER || level.dimensionType().ultraWarm()) return;
        BlockState state = level.getBlockState(pos);
        if (!ExtraWaterlogging.isExtra(state.getBlock()) || state.getValue(BlockStateProperties.WATERLOGGED)) return;
        if (!level.isClientSide()) {
            level.setBlock(pos, state.setValue(BlockStateProperties.WATERLOGGED, true), 3);
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        this.playEmptySound(player, level, pos);
        cir.setReturnValue(true);
    }

    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;", ordinal = 0), cancellable = true)
    private void spicedcider$drainExtra(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir, @Local(ordinal = 0) BlockPos pos) {
        if (this.content != Fluids.EMPTY) return;
        BlockState state = level.getBlockState(pos);
        if (!ExtraWaterlogging.isWaterlogged(state)) return;
        level.setBlock(pos, state.setValue(BlockStateProperties.WATERLOGGED, false), 3);
        if (!state.canSurvive(level, pos)) level.destroyBlock(pos, true);
        ItemStack filled = new ItemStack(Items.WATER_BUCKET);
        player.awardStat(Stats.ITEM_USED.get((BucketItem) (Object) this));
        player.playSound(SoundEvents.BUCKET_FILL, 1.0F, 1.0F);
        level.gameEvent(player, GameEvent.FLUID_PICKUP, pos);
        ItemStack result = ItemUtils.createFilledResult(player.getItemInHand(hand), player, filled);
        if (player instanceof ServerPlayer serverPlayer) CriteriaTriggers.FILLED_BUCKET.trigger(serverPlayer, filled);
        cir.setReturnValue(InteractionResultHolder.sidedSuccess(result, level.isClientSide()));
    }
}
