package com.evandev.spicedcider.mixin.create;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@IfModLoaded("create")
@Mixin(targets = "com.simibubi.create.content.fluids.spout.SpoutBlockEntity", remap = false)
public class SpoutFoodsAdvancementMixin {
    @ModifyArg(method = "whenItemHeld", at = @At(value = "INVOKE", target = "Lcom/tterrag/registrate/util/entry/ItemEntry;isIn(Lnet/minecraft/world/item/ItemStack;)Z", ordinal = 0))
    private ItemStack spicedcider$countChocolateStrawberries(ItemStack out) {
        return spicedcider$swap(out, "neapolitan:chocolate_strawberries", "create:chocolate_glazed_berries");
    }

    @ModifyArg(method = "whenItemHeld", at = @At(value = "INVOKE", target = "Lcom/tterrag/registrate/util/entry/ItemEntry;isIn(Lnet/minecraft/world/item/ItemStack;)Z", ordinal = 1))
    private ItemStack spicedcider$countHoneyedApple(ItemStack out) {
        return spicedcider$swap(out, "nomansland:honeyed_apple", "create:honeyed_apple");
    }

    @Unique
    private static ItemStack spicedcider$swap(ItemStack out, String replacement, String original) {
        if (!out.is(BuiltInRegistries.ITEM.get(ResourceLocation.parse(replacement)))) {
            return out;
        }
        return new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(original)));
    }
}
