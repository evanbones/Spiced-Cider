package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.content.item.CleaverOfBeheadingItem;
import com.evandev.spicedcider.content.item.FireStrikerItem;
import com.evandev.spicedcider.content.item.HammerItem;
import com.evandev.spicedcider.content.item.MischiefArmorItem;
import com.li64.tide.registries.items.FishingHookItem;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpicedCider.MOD_ID);

    public static final DeferredItem<Item> WORKSTONE_ITEM = ITEMS.register("workstone",
            () -> new BlockItem(ModBlocks.WORKSTONE.get(), new Item.Properties()));

    public static final DeferredItem<Item> FLINT_HAMMER = ITEMS.register("flint_hammer", () -> new HammerItem(Tiers.STONE, 7.0f, -3.2f, new Item.Properties()));
    public static final DeferredItem<Item> IRON_HAMMER = ITEMS.register("iron_hammer", () -> new HammerItem(Tiers.IRON, 6.0f, -3.1f, new Item.Properties()));
    public static final DeferredItem<Item> GOLDEN_HAMMER = ITEMS.register("golden_hammer", () -> new HammerItem(Tiers.GOLD, 6.0f, -3.0f, new Item.Properties()));
    public static final DeferredItem<Item> DIAMOND_HAMMER = ITEMS.register("diamond_hammer", () -> new HammerItem(Tiers.DIAMOND, 5.0f, -3.0f, new Item.Properties()));
    public static final DeferredItem<Item> NETHERITE_HAMMER = ITEMS.register("netherite_hammer", () -> new HammerItem(Tiers.NETHERITE, 6.0f, -3.0f, new Item.Properties().fireResistant()));

    public static final DeferredItem<Item> FIRE_STRIKER = ITEMS.register("fire_striker",
            () -> new FireStrikerItem(new Item.Properties()));

    public static final DeferredItem<Item> GRAPPLING_HOOK = ITEMS.register("grappling_hook",
            () -> new FishingHookItem(new Item.Properties(), "item.spicedcider.grappling_hook.desc"));
    public static final DeferredItem<Item> STICKY_GRAPPLING_HOOK = ITEMS.register("sticky_grappling_hook",
            () -> new FishingHookItem(new Item.Properties(), "item.spicedcider.sticky_grappling_hook.desc"));

    public static final DeferredItem<Item> RUBBER_CABLE = ITEMS.register("rubber_cable",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> BLAST_PROOF_PLATING = ITEMS.register("blast_proof_plating",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> MISCHIEF_HELMET = ITEMS.register("mischief_helmet",
            () -> new MischiefArmorItem(ModArmorMaterials.MISCHIEF, ArmorItem.Type.HELMET,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(15))));
    public static final DeferredItem<Item> MISCHIEF_CHESTPLATE = ITEMS.register("mischief_chestplate",
            () -> new MischiefArmorItem(ModArmorMaterials.MISCHIEF, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(15))));
    public static final DeferredItem<Item> MISCHIEF_LEGGINGS = ITEMS.register("mischief_leggings",
            () -> new MischiefArmorItem(ModArmorMaterials.MISCHIEF, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(15))));
    public static final DeferredItem<Item> MISCHIEF_BOOTS = ITEMS.register("mischief_boots",
            () -> new MischiefArmorItem(ModArmorMaterials.MISCHIEF, ArmorItem.Type.BOOTS,
                    new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(15))));

    public static final DeferredItem<Item> CLEAVER_OF_BEHEADING = ITEMS.register("cleaver_of_beheading",
            () -> new CleaverOfBeheadingItem(ModItemTiers.CLEAVER,
                    new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1)
                            .attributes(net.minecraft.world.item.DiggerItem.createAttributes(ModItemTiers.CLEAVER, 11.0F, -3.4F))));
}