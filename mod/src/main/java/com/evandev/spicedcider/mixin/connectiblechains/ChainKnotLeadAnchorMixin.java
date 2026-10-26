package com.evandev.spicedcider.mixin.connectiblechains;

import com.evandev.connectiblechains.entity.ChainKnotEntity;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("connectiblechains")
@Mixin(value = ChainKnotEntity.class, remap = false)
public class ChainKnotLeadAnchorMixin {

    @ModifyReturnValue(method = "getChainPos", at = @At("RETURN"))
    private Vec3 spicedcider$vanillaLeashHeight(Vec3 pos) {
        ChainKnotEntity knot = (ChainKnotEntity) (Object) this;
        if (knot.attachedFace != Direction.UP || knot.getSourceItem() != Items.LEAD) return pos;
        return pos.subtract(0.0, 0.275, 0.0);
    }
}
