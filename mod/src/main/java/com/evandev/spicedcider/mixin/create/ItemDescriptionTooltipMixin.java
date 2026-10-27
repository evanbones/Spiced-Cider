package com.evandev.spicedcider.mixin.create;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.spongepowered.asm.mixin.Mixin;

@IfModLoaded("create")
@Mixin(targets = "com.simibubi.create.foundation.item.ItemDescription$Modifier", remap = false)
public class ItemDescriptionTooltipMixin {

    @WrapMethod(method = "modify")
    private void spicedcider$skipDescription(ItemTooltipEvent event, Operation<Void> original) {
    }
}
