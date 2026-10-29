package com.evandev.spicedcider.mixin.controlify;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.isxander.controlify.bindings.InputBindingImpl;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;
import java.util.Optional;

@IfModLoaded("controlify")
@Mixin(value = InputBindingImpl.class, remap = false)
public class InputBindingRadialIconMixin {

    @Unique
    private static final Map<String, String> spicedcider$ICONS = Map.of(
            "key.fieldguide.open", "fieldguide:item/field_guide",
            "key.map_atlases.open_minimap", "map_atlases:item/atlas",
            "key.personality.sit", "quark:item/red_stool",
            "key.personality.crawl", "minecraft:item/leather_boots",
            "supplementaries.keybind.quiver", "supplementaries:item/quiver",
            "key.lodged.use_bandage", "lodged:item/bandage",
            "key.advancements", "minecraft:item/book"
    );

    @Shadow
    @Final
    private ResourceLocation id;

    @ModifyReturnValue(method = "radialIcon", at = @At("RETURN"))
    private Optional<ResourceLocation> spicedcider$itemIcon(Optional<ResourceLocation> original) {
        if (!"fabric-key-binding-api-v1".equals(id.getNamespace())) return original;
        String icon = spicedcider$ICONS.get(id.getPath());
        return icon == null ? original : Optional.of(ResourceLocation.parse(icon));
    }
}
