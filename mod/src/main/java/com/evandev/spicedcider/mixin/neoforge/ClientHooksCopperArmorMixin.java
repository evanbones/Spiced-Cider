package com.evandev.spicedcider.mixin.neoforge;

import com.evandev.spicedcider.client.renderer.CopperPatina;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.ClientHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ClientHooks.class, remap = false)
public class ClientHooksCopperArmorMixin {

    @ModifyReturnValue(method = "getArmorTexture", at = @At("RETURN"))
    private static ResourceLocation spicedcider$agedCopperArmor(ResourceLocation texture, Entity entity, ItemStack armor, ArmorMaterial.Layer layer, boolean innerModel, EquipmentSlot slot) {
        return CopperPatina.texture(texture, armor);
    }
}
