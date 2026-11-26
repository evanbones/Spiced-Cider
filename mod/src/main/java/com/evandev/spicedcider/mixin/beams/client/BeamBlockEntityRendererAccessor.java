package com.evandev.spicedcider.mixin.beams.client;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.syl.beams.client.renderer.beam.BeamBlockEntityRenderer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@IfModLoaded("beams")
@Mixin(value = BeamBlockEntityRenderer.class, remap = false)
public interface BeamBlockEntityRendererAccessor {

    @Invoker("stableJitter")
    static Vec3 spicedcider$stableJitter(Vec3 start, Vec3 end, String woodType) {
        throw new AssertionError();
    }
}
