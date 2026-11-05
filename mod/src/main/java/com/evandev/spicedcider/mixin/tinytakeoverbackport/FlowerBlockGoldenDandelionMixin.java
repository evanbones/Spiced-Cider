package com.evandev.spicedcider.mixin.tinytakeoverbackport;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.registry.ModParticleTypes;
import com.evandev.tiny_takeover_backport.registry.ModRegistry;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@IfModLoaded("tiny_takeover_backport")
@Mixin(FlowerBlock.class)
public abstract class FlowerBlockGoldenDandelionMixin extends BushBlock {
    protected FlowerBlockGoldenDandelionMixin(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        if (random.nextInt(3) != 0 || !state.is(ModRegistry.GOLDEN_DANDELION)) return;
        if (!SpicedCiderConfig.clientOr(SpicedCiderConfig.CLIENT.goldenDandelionParticles, true)) return;
        level.addParticle(ModParticleTypes.GOLDEN_DANDELION.get(), pos.getX() + 0.5, pos.getY() + 0.4, pos.getZ() + 0.5, 0.0, 0.0, 0.0);
    }
}
