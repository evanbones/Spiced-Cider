package com.evandev.spicedcider.mixin.caverns_and_chasms;

import com.teamabnormals.caverns_and_chasms.core.registry.CCBlocks;
import net.minecraft.world.level.material.MapColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(CCBlocks.CCProperties.class)
public abstract class IronPlatedBricksMapColorMixin {

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/teamabnormals/caverns_and_chasms/core/registry/CCBlocks$CCProperties;platedBricks(Lnet/minecraft/world/level/material/MapColor;Lnet/minecraft/world/level/block/SoundType;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;"
            ),
            index = 0
    )
    private static MapColor spicedcider$greyIronPlatedBricks(MapColor original) {
        return original == MapColor.RAW_IRON ? MapColor.DEEPSLATE : original;
    }
}
