package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.SpicedCider;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(Item.class)
public class ItemRepairMaterialTagMixin {

    @Unique
    private static final Map<Item, TagKey<Item>> spicedcider$REPAIR_TAGS = new ConcurrentHashMap<>();

    @ModifyReturnValue(method = "isValidRepairItem", at = @At("RETURN"))
    private boolean spicedcider$repairMaterialTag(boolean original, ItemStack toRepair, ItemStack repair) {
        if (original || repair.isEmpty()) return original;
        TagKey<Item> tag = spicedcider$REPAIR_TAGS.computeIfAbsent(toRepair.getItem(), item -> {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
            return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "repair_materials/" + id.getNamespace() + "/" + id.getPath()));
        });
        return repair.is(tag);
    }
}
