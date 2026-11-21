package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MushroomBlock.class)
public class MushroomBlockLightMixin {

    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        int light = state.getLightEmission();
        return light == 1 && SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.darkMushrooms, false) ? 0 : light;
    }
}
