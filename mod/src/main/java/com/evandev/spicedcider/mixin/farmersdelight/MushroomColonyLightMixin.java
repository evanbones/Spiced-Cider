package com.evandev.spicedcider.mixin.farmersdelight;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@IfModLoaded("farmersdelight")
@Mixin(targets = "vectorwing.farmersdelight.common.block.MushroomColonyBlock")
public class MushroomColonyLightMixin {

    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        int light = state.getLightEmission();
        return light == 1 && SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.darkMushrooms, false) ? 0 : light;
    }
}
