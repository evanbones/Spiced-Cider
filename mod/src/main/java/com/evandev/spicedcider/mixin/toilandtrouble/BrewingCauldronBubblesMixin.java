package com.evandev.spicedcider.mixin.toilandtrouble;

import com.evandev.spicedcider.client.particle.CauldronBubbles;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@IfModLoaded("toil_and_trouble")
@Mixin(targets = "cc.cassian.cauldrons.blocks.BrewingCauldronBlock", remap = false)
public abstract class BrewingCauldronBubblesMixin extends CauldronBlock {

    private BrewingCauldronBubblesMixin(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        CauldronBubbles.animate(state, level, pos, random);
    }
}
