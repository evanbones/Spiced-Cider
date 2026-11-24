package com.evandev.spicedcider.mixin.supplementaries;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.mehvahdjukaar.supplementaries.common.block.blocks.JarBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("supplementaries")
@Mixin(JarBlock.class)
public class JarBlockJugShapeMixin {
    @Unique
    private static final VoxelShape spicedcider$JUG = Shapes.or(Block.box(3.0, 0.0, 3.0, 13.0, 12.0, 13.0), Block.box(5.0, 12.0, 5.0, 11.0, 16.0, 11.0));

    @ModifyReturnValue(method = "getShape", at = @At("RETURN"))
    private VoxelShape spicedcider$jugShape(VoxelShape shape) {
        return SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.jugShapedJars, false) ? spicedcider$JUG : shape;
    }
}
