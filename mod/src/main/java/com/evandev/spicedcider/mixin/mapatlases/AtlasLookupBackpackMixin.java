package com.evandev.spicedcider.mixin.mapatlases;

import com.evandev.spicedcider.content.handler.WornBackpackItemEvents;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import pepjebs.mapatlases.MapAtlasesMod;
import pepjebs.mapatlases.config.MapAtlasesConfig;
import pepjebs.mapatlases.utils.AtlasLookup;

@IfModLoaded("map_atlases")
@Mixin(value = AtlasLookup.class, remap = false)
public class AtlasLookupBackpackMixin {

    @ModifyReturnValue(method = "getAtlasFromPlayerByConfig", at = @At("RETURN"))
    private static ItemStack spicedcider$atlasInWornBackpack(ItemStack original, Player player) {
        if (!original.isEmpty() || !MapAtlasesConfig.activationLocation.get().scanAll()) return original;
        return WornBackpackItemEvents.find(player, stack -> stack.is(MapAtlasesMod.MAP_ATLAS.get()));
    }
}
