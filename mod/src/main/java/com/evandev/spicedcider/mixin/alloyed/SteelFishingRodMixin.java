package com.evandev.spicedcider.mixin.alloyed;

import com.evandev.spicedcider.compat.tide.TideCompat;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("alloyed")
@Mixin(targets = "com.molybdenum.alloyed.common.registry.ModItems", remap = false)
public class SteelFishingRodMixin {

    @WrapOperation(method = "registerFishingRod", at = @At(value = "NEW", target = "net/minecraft/world/item/FishingRodItem"))
    private static FishingRodItem spicedcider$tideSteelRod(Item.Properties properties, Operation<FishingRodItem> original) {
        if (!TideCompat.isLoaded()) return original.call(properties);
        return TideCompat.createFishingRod(2, 256, properties);
    }
}
