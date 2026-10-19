package com.evandev.spicedcider.mixin.vanillabackport;

import com.blackgear.vanillabackport.common.level.blocks.EyeblossomPotBlock;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@IfModLoaded("vanillabackport")
@Mixin(EyeblossomPotBlock.class)
public abstract class EyeblossomPotTickMixin extends FlowerPotBlock {

    private EyeblossomPotTickMixin(Block potted, Properties properties) {
        super(potted, properties);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        this.randomTick(state, level, pos, random);
    }
}
