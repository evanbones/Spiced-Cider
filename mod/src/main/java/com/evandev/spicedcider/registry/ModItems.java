package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.content.item.InferniteCleaverItem;
import com.evandev.spicedcider.content.item.FireStrikerItem;
import com.evandev.spicedcider.content.item.MischiefArmorItem;
import com.evandev.spicedcider.compat.tide.TideCompat;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpicedCider.MOD_ID);

    public static final DeferredItem<Item> FIRE_STRIKER = ITEMS.register("fire_striker",
            () -> new FireStrikerItem(new Item.Properties()));

    @Nullable
    public static final DeferredItem<Item> GRAPPLING_HOOK = TideCompat.isLoaded()
            ? ITEMS.register("grappling_hook",
            () -> TideCompat.createHookItem(new Item.Properties(), "item.spicedcider.grappling_hook.desc"))
            : null;
    @Nullable
    public static final DeferredItem<Item> STICKY_GRAPPLING_HOOK = TideCompat.isLoaded()
            ? ITEMS.register("sticky_grappling_hook",
            () -> TideCompat.createHookItem(new Item.Properties(), "item.spicedcider.sticky_grappling_hook.desc"))
            : null;

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

    public static final DeferredItem<Item> INFERNITE_CLEAVER = ITEMS.register("infernite_cleaver",
            () -> new InferniteCleaverItem(ModItemTiers.CLEAVER,
                    new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1)
                            .attributes(DiggerItem.createAttributes(ModItemTiers.CLEAVER, 11.0F, -3.4F))));

    public static final DeferredItem<Item> CAST_IRON_INGOT = ITEMS.register("cast_iron_ingot",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CAST_IRON_NUGGET = ITEMS.register("cast_iron_nugget",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CAST_IRON_BLOCK = ITEMS.register("cast_iron_block",
            () -> new BlockItem(ModBlocks.CAST_IRON_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<Item> CAST_IRON_SHEET = ITEMS.register("cast_iron_sheet",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> TALLOW = ITEMS.register("tallow",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> TALLOW_BLOCK = ITEMS.register("tallow_block",
            () -> new BlockItem(ModBlocks.TALLOW_BLOCK.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> GLOWSTONE_BRICKS = ITEMS.registerSimpleBlockItem(ModBlocks.GLOWSTONE_BRICKS);
    public static final DeferredItem<BlockItem> GLOWSTONE_BRICK_STAIRS = ITEMS.registerSimpleBlockItem(ModBlocks.GLOWSTONE_BRICK_STAIRS);
    public static final DeferredItem<BlockItem> GLOWSTONE_BRICK_SLAB = ITEMS.registerSimpleBlockItem(ModBlocks.GLOWSTONE_BRICK_SLAB);
    public static final DeferredItem<BlockItem> GLOWSTONE_BRICK_WALL = ITEMS.registerSimpleBlockItem(ModBlocks.GLOWSTONE_BRICK_WALL);
    public static final DeferredItem<BlockItem> CHISELED_GLOWSTONE_BRICKS = ITEMS.registerSimpleBlockItem(ModBlocks.CHISELED_GLOWSTONE_BRICKS);
    public static final DeferredItem<BlockItem> POLISHED_GLOWSTONE_PILLAR = ITEMS.registerSimpleBlockItem(ModBlocks.POLISHED_GLOWSTONE_PILLAR);

    @Nullable
    public static final DeferredItem<BlockItem> POLISHED_GLOWSTONE_STAIRS = ModBlocks.POLISHED_GLOWSTONE_STAIRS != null
            ? ITEMS.registerSimpleBlockItem(ModBlocks.POLISHED_GLOWSTONE_STAIRS) : null;
    @Nullable
    public static final DeferredItem<BlockItem> POLISHED_GLOWSTONE_SLAB = ModBlocks.POLISHED_GLOWSTONE_SLAB != null
            ? ITEMS.registerSimpleBlockItem(ModBlocks.POLISHED_GLOWSTONE_SLAB) : null;
    @Nullable
    public static final DeferredItem<BlockItem> POLISHED_GLOWSTONE_WALL = ModBlocks.POLISHED_GLOWSTONE_WALL != null
            ? ITEMS.registerSimpleBlockItem(ModBlocks.POLISHED_GLOWSTONE_WALL) : null;

    public static final Map<DyeColor, DeferredItem<BlockItem>> WOOL_STAIRS = new EnumMap<>(DyeColor.class);
    public static final Map<DyeColor, DeferredItem<BlockItem>> WOOL_SLABS = new EnumMap<>(DyeColor.class);

    static {
        for (DyeColor color : DyeColor.values()) {
            WOOL_STAIRS.put(color, ITEMS.registerSimpleBlockItem(ModBlocks.WOOL_STAIRS.get(color)));
            WOOL_SLABS.put(color, ITEMS.registerSimpleBlockItem(ModBlocks.WOOL_SLABS.get(color)));
        }
    }
}