package com.evandev.spicedcider.mixin.redstone;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComparatorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ComparatorBlock.class)
public abstract class WaterloggableComparatorMixin extends Block {

    public WaterloggableComparatorMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "createBlockStateDefinition", at = @At("TAIL"))
    protected void spicedcider$addWaterloggedProperty(@NotNull StateDefinition.Builder<Block, BlockState> builder, @NotNull CallbackInfo ci) {
        builder.add(BlockStateProperties.WATERLOGGED);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void spicedcider$setDefaultWaterloggedState(@NotNull Properties properties, @NotNull CallbackInfo ci) {
        this.registerDefaultState(this.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false));
    }
}