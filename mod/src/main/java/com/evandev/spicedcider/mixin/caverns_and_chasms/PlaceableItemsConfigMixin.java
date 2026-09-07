package com.evandev.spicedcider.mixin.caverns_and_chasms;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.teamabnormals.caverns_and_chasms.core.CCConfig;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("caverns_and_chasms")
@Mixin(value = Item.class, priority = 1500)
public abstract class PlaceableItemsConfigMixin {

    // i hate you C&C
    @TargetHandler(
            mixin = "com.teamabnormals.caverns_and_chasms.core.mixin.item.ItemMixin",
            name = "useItem"
    )
    @ModifyExpressionValue(
            method = "@MixinSquared:Handler",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/tags/TagKey;)Z"
            ),
            require = 0
    )
    private boolean spicedcider$respectPlaceableItemsConfig(boolean placeable) {
        return placeable && CCConfig.COMMON.placeableItems.get();
    }
}
