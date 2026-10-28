package com.evandev.spicedcider.client.events;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.registry.ModDataMaps;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, value = Dist.CLIENT)
public class BrewingIngredientTooltips {
    private static final TagKey<Item> SPLASH = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("toil_and_trouble", "creates_splash_potions"));
    private static final TagKey<Item> LINGERING = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("toil_and_trouble", "creates_lingering_potions"));

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) {
            return;
        }
        Component line = describe(stack);
        if (line == null) {
            return;
        }
        List<Component> lines = event.getToolTip();
        lines.add(Component.translatable("spicedcider.tooltip.brewing.header").withStyle(ChatFormatting.GRAY));
        lines.add(Component.literal(" ").append(line.copy().withStyle(ChatFormatting.DARK_GRAY)));
    }

    private static Component describe(ItemStack stack) {
        if (stack.is(SPLASH)) {
            return Component.translatable("spicedcider.tooltip.brewing.splash");
        }
        if (stack.is(LINGERING)) {
            return Component.translatable("spicedcider.tooltip.brewing.lingering");
        }
        ModDataMaps.BrewingIngredient ingredient = stack.getItemHolder().getData(ModDataMaps.BREWING_INGREDIENTS);
        if (ingredient == null) {
            return null;
        }
        if (ingredient.potion().isEmpty()) {
            return Component.translatable(ingredient.seconds() > 0 ? "spicedcider.tooltip.brewing.longer" : "spicedcider.tooltip.brewing.stronger");
        }
        return effects(ingredient.potion().get());
    }

    private static Component effects(Holder<Potion> potion) {
        MutableComponent names = null;
        for (MobEffectInstance effect : potion.value().getEffects()) {
            Component name = effect.getEffect().value().getDisplayName();
            names = names == null ? name.copy() : names.append(", ").append(name);
        }
        return names == null ? Component.translatable(Potion.getName(Optional.of(potion), "item.minecraft.potion.effect.")) : names;
    }
}
