package com.evandev.spicedcider.mixin.create;

import com.evandev.spicedcider.compat.create.WindmillVisual;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.wind.client.ClientWind;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity;
import com.simibubi.create.content.contraptions.bearing.WindmillBearingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("create")
@Mixin(value = WindmillBearingBlockEntity.class, remap = false)
public abstract class WindmillBearingWindMixin extends MechanicalBearingBlockEntity implements WindmillVisual {
    @Unique
    private static final float MIN_FACTOR = 0.6F;
    @Unique
    private static final float MAX_FACTOR = 1.6F;
    @Unique
    private static final float FULL_STRENGTH = 1.6F;
    @Unique
    private static final float FACTOR_SMOOTHING = 0.02F;

    @Unique
    private float spicedcider$factor = 1.0F;
    @Unique
    private float spicedcider$offset;
    @Unique
    private float spicedcider$prevOffset;

    protected WindmillBearingWindMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void spicedcider$visualWind(CallbackInfo ci) {
        if (level == null || !level.isClientSide) return;
        spicedcider$prevOffset = spicedcider$offset;
        float target = 1.0F;
        if (SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.windCreateWindmills, false) && ClientWind.active()) {
            float strength = ClientWind.strengthAt(worldPosition.getY()) / FULL_STRENGTH;
            target = Mth.lerp(Mth.clamp(strength, 0.0F, 1.0F), MIN_FACTOR, MAX_FACTOR);
        }
        spicedcider$factor += (target - spicedcider$factor) * FACTOR_SMOOTHING;
        if (!running || movedContraption == null || movedContraption.isStalled()) return;
        spicedcider$offset += getAngularSpeed() * (spicedcider$factor - 1.0F);
        if (Math.abs(spicedcider$offset) > 3600.0F) {
            float wrap = spicedcider$offset - spicedcider$offset % 360.0F;
            spicedcider$offset -= wrap;
            spicedcider$prevOffset -= wrap;
        }
        applyRotation();
    }

    @Override
    public float spicedcider$visualOffset(float partialTicks) {
        return Mth.lerp(partialTicks, spicedcider$prevOffset, spicedcider$offset);
    }
}
