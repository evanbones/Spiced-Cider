package com.evandev.spicedcider.mixin.minecraft.client;

import com.evandev.spicedcider.interfaces.IPlayerWithGrapplingHook;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemProperties.class)
public class ItemPropertiesMixin {

    @ModifyVariable(method = "register(Lnet/minecraft/world/item/Item;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/client/renderer/item/ClampedItemPropertyFunction;)V", at = @At("HEAD"), argsOnly = true)
    private static ClampedItemPropertyFunction wrapProperty(ClampedItemPropertyFunction original, Item item, ResourceLocation name) {
        if (name.getPath().equals("cast")) {
            return (stack, level, entity, seed) -> {
                float val = original.unclampedCall(stack, level, entity, seed);
                if (val == 0.0f && entity instanceof Player player) {
                    if (((IPlayerWithGrapplingHook) player).spicedcider$getHook() != null) {
                        return 1.0f;
                    }
                }
                return val;
            };
        }
        return original;
    }
}
