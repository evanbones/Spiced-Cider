package com.evandev.spicedcider.mixin.create;

import com.evandev.spicedcider.compat.stools.StoolSeats;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.simibubi.create.content.contraptions.actors.seat.SeatEntity;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("create")
@Mixin(value = SeatEntity.class, remap = false)
public class SeatEntityStoolMixin {
    @ModifyExpressionValue(method = "tick", at = @At(value = "CONSTANT", args = "classValue=com/simibubi/create/content/contraptions/actors/seat/SeatBlock"))
    private boolean spicedcider$stayOnStool(boolean original) {
        Entity self = (Entity) (Object) this;
        return original || StoolSeats.isStool(self.level(), self.blockPosition());
    }
}
