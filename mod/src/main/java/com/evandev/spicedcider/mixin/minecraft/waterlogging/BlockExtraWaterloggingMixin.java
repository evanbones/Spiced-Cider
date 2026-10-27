package com.evandev.spicedcider.mixin.minecraft.waterlogging;

import com.evandev.spicedcider.content.waterlogging.ExtraWaterloggable;
import com.evandev.spicedcider.content.waterlogging.ExtraWaterlogging;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Block.class)
public abstract class BlockExtraWaterloggingMixin implements ExtraWaterloggable {

    @Unique
    private boolean spicedcider$extraWaterloggable;

    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;createBlockStateDefinition(Lnet/minecraft/world/level/block/state/StateDefinition$Builder;)V"))
    private void spicedcider$addWaterlogged(Block block, StateDefinition.Builder<Block, BlockState> builder, Operation<Void> original) {
        original.call(block, builder);
        if (ExtraWaterlogging.listed(block.getClass()) && !((StateDefinitionBuilderAccessor) builder).spicedcider$getProperties().containsKey(BlockStateProperties.WATERLOGGED.getName())) {
            builder.add(BlockStateProperties.WATERLOGGED);
            this.spicedcider$extraWaterloggable = true;
        }
    }

    @ModifyVariable(method = "registerDefaultState", at = @At("HEAD"), argsOnly = true)
    private BlockState spicedcider$dryByDefault(BlockState state) {
        return this.spicedcider$extraWaterloggable ? state.setValue(BlockStateProperties.WATERLOGGED, false) : state;
    }

    @Override
    public boolean spicedcider$isExtraWaterloggable() {
        return this.spicedcider$extraWaterloggable;
    }
}
