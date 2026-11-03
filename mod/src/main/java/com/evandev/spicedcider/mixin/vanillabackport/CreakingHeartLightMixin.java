package com.evandev.spicedcider.mixin.vanillabackport;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.blackgear.vanillabackport.common.level.blocks.CreakingHeartBlock;
import com.blackgear.vanillabackport.common.level.blocks.CreakingHeartState;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@IfModLoaded("vanillabackport")
@Mixin(CreakingHeartBlock.class)
public abstract class CreakingHeartLightMixin {

    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.vanillaBackportLightTweaks, false) && state.getValue(CreakingHeartBlock.STATE) == CreakingHeartState.AWAKE ? 7 : state.getLightEmission();
    }
}
