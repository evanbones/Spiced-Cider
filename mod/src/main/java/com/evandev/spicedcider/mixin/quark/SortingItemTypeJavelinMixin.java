package com.evandev.spicedcider.mixin.quark;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("quark")
@Mixin(targets = "org.violetmoon.quark.base.handler.SortingHandler$ItemType", remap = false)
public class SortingItemTypeJavelinMixin {
    @Unique
    private static final TagKey<Item> spicedcider$JAVELINS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "tools/javelins"));
    @Unique
    private static final TagKey<Item> spicedcider$SPEARS = TagKey.create(Registries.ITEM, ResourceLocation.withDefaultNamespace("spears"));

    @ModifyReturnValue(method = "fitsInType", at = @At("RETURN"))
    private boolean spicedcider$sortJavelinsAsTridents(boolean fits, ItemStack stack) {
        if (fits || !((Enum<?>) (Object) this).name().equals("TRIDENT")) {
            return fits;
        }
        return stack.is(spicedcider$JAVELINS) || stack.is(spicedcider$SPEARS);
    }
}
