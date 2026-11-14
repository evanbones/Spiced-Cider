package com.evandev.spicedcider.mixin.supplementaries;

import com.evandev.spicedcider.interfaces.WindVaneSignal;
import com.evandev.spicedcider.wind.client.ClientWind;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.mehvahdjukaar.supplementaries.common.block.tiles.WindVaneBlockTile;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("supplementaries")
@Mixin(value = WindVaneBlockTile.class, remap = false)
public class WindVaneTileWindMixin implements WindVaneSignal {

    @Shadow
    private float yaw;

    @Shadow
    private int windChargedTicks;

    @Unique
    private int spicedcider$windSignal = -1;

    @Unique
    private boolean spicedcider$steered;

    @Unique
    private float spicedcider$bearing;

    @Unique
    private float spicedcider$spin;

    @Override
    public int spicedcider$getWindSignal() {
        return spicedcider$windSignal;
    }

    @Override
    public void spicedcider$setWindSignal(int signal) {
        spicedcider$windSignal = signal;
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private static void spicedcider$faceWind(Level level, BlockPos pos, BlockState state, WindVaneBlockTile tile, CallbackInfo ci) {
        if (!level.isClientSide) return;
        WindVaneTileWindMixin vane = (WindVaneTileWindMixin) (Object) tile;
        if (!ClientWind.active() || vane.windChargedTicks > 0) {
            vane.spicedcider$steered = false;
            return;
        }
        if (!vane.spicedcider$steered) {
            vane.spicedcider$steered = true;
            vane.spicedcider$bearing = vane.yaw;
            vane.spicedcider$spin = 0.0F;
        }
        float strength = ClientWind.exposedStrength(pos);
        if (strength > 0.03F) {
            float flutter = Mth.sin(ClientWind.time() * 0.11F + pos.getX() * 0.7F + pos.getZ() * 1.3F) * (2.0F + 10.0F * ClientWind.turbulence());
            float target = ClientWind.heading() * Mth.RAD_TO_DEG + 180.0F + flutter;
            vane.spicedcider$spin += Mth.wrapDegrees(target - vane.spicedcider$bearing) * 0.02F * Mth.clamp(strength, 0.1F, 1.0F);
        }
        vane.spicedcider$spin *= 0.85F;
        vane.spicedcider$bearing = Mth.wrapDegrees(vane.spicedcider$bearing + vane.spicedcider$spin);
        vane.yaw = vane.spicedcider$bearing;
    }
}
