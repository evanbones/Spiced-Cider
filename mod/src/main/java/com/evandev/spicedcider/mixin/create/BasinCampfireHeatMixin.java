package com.evandev.spicedcider.mixin.create;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.compat.create.BasinHeat;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("create")
@Mixin(value = BasinBlockEntity.class, remap = false)
public class BasinCampfireHeatMixin {

    @ModifyReturnValue(method = "getHeatLevelOf", at = @At("RETURN"))
    private static HeatLevel spicedcider$campfireHeat(HeatLevel heat, @Local(argsOnly = true) BlockState state) {
        if ((heat == HeatLevel.NONE || heat == HeatLevel.SMOULDERING)
                && SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.basinCampfireHeat, false)
                && BasinHeat.isHeatSource(state)) {
            return HeatLevel.KINDLED;
        }
        return heat;
    }
}
