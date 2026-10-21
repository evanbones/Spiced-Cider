package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.content.item.InferniteCleaverItem;
import com.evandev.spicedcider.content.item.FireStrikerItem;
import com.evandev.spicedcider.content.item.MischiefArmorItem;
import com.evandev.spicedcider.compat.architectspalette.ArchitectsPaletteCompat;
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

    @Nullable
    public static final DeferredItem<Item> FLUX_HOOK = tideHook("flux_hook");
    @Nullable
    public static final DeferredItem<Item> GOLD_HOOK = tideHook("gold_hook");
    @Nullable
    public static final DeferredItem<Item> HEAVY_HOOK = tideHook("heavy_hook");
    @Nullable
    public static final DeferredItem<Item> LIGHT_HOOK = tideHook("light_hook");
    @Nullable
    public static final DeferredItem<Item> DOUBLE_HOOK = tideHook("double_hook");
    @Nullable
    public static final DeferredItem<Item> ADAMANTITE_HOOK = tideHook("adamantite_hook");
    @Nullable
    public static final DeferredItem<Item> PRISMARITE_HOOK = tideHook("prismarite_hook");
    @Nullable
    public static final DeferredItem<Item> FEATHERLIGHT_HOOK = tideHook("featherlight_hook");
    @Nullable
    public static final DeferredItem<Item> NECROMIUM_HOOK = tideHook("necromium_hook");
    @Nullable
    public static final DeferredItem<Item> ADAMANTITE_FISHING_ROD = TideCompat.isLoaded()
            ? ITEMS.register("adamantite_fishing_rod",
            () -> TideCompat.createFishingRod(3, 512, new Item.Properties().fireResistant()))
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

    public static final DeferredItem<Item> LATEX_BUCKET = ITEMS.register("latex_bucket",
            () -> new BucketItem(ModFluids.LATEX.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final DeferredItem<Item> LATEX_BOTTLE = ITEMS.register("latex_bottle",
            () -> new Item(new Item.Properties().craftRemainder(Items.GLASS_BOTTLE).stacksTo(16)));

    public static final DeferredItem<Item> WORM = ITEMS.register("worm",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> GOLDEN_WORM = ITEMS.register("golden_worm",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final DeferredItem<BlockItem> WORMY_DIRT = ITEMS.registerSimpleBlockItem(ModBlocks.WORMY_DIRT);
    public static final DeferredItem<BlockItem> GLOAMWOOD_SAPLING = ITEMS.registerSimpleBlockItem(ModBlocks.GLOAMWOOD_SAPLING);
    public static final DeferredItem<BlockItem> WORM_CRATE = ITEMS.registerSimpleBlockItem(ModBlocks.WORM_CRATE);
    public static final DeferredItem<Item> GEODE = ITEMS.register("geode",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> DEEPSLATE_GEODE = ITEMS.register("deepslate_geode",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BLACKSTONE_GEODE = ITEMS.register("blackstone_geode",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<BlockItem> DEPOSIT = ITEMS.registerSimpleBlockItem(ModBlocks.DEPOSIT);
    public static final DeferredItem<BlockItem> DEEPSLATE_DEPOSIT = ITEMS.registerSimpleBlockItem(ModBlocks.DEEPSLATE_DEPOSIT);
    public static final DeferredItem<BlockItem> BLACKSTONE_DEPOSIT = ITEMS.registerSimpleBlockItem(ModBlocks.BLACKSTONE_DEPOSIT);
    public static final DeferredItem<BlockItem> GOLDEN_WORM_CRATE = ITEMS.registerSimpleBlockItem(ModBlocks.GOLDEN_WORM_CRATE);

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

    @Nullable
    public static final DeferredItem<Item> TWISTED_SIGN = ModBlocks.TWISTED_SIGN != null
            ? ITEMS.register("twisted_sign", () -> new SignItem(new Item.Properties().stacksTo(16), ModBlocks.TWISTED_SIGN.get(), ModBlocks.TWISTED_WALL_SIGN.get()))
            : null;
    @Nullable
    public static final DeferredItem<Item> TWISTED_HANGING_SIGN = ModBlocks.TWISTED_HANGING_SIGN != null
            ? ITEMS.register("twisted_hanging_sign", () -> new HangingSignItem(ModBlocks.TWISTED_HANGING_SIGN.get(), ModBlocks.TWISTED_WALL_HANGING_SIGN.get(), new Item.Properties().stacksTo(16)))
            : null;
    @Nullable
    public static final DeferredItem<Item> TWISTED_BOAT = ArchitectsPaletteCompat.isLoaded()
            ? ITEMS.register("twisted_boat", () -> new BoatItem(false, ModBoatTypes.TWISTED.getValue(), new Item.Properties().stacksTo(1)))
            : null;
    @Nullable
    public static final DeferredItem<Item> TWISTED_CHEST_BOAT = ArchitectsPaletteCompat.isLoaded()
            ? ITEMS.register("twisted_chest_boat", () -> new BoatItem(true, ModBoatTypes.TWISTED.getValue(), new Item.Properties().stacksTo(1)))
            : null;

    public static final Map<DyeColor, DeferredItem<BlockItem>> WOOL_STAIRS = new EnumMap<>(DyeColor.class);
    public static final Map<DyeColor, DeferredItem<BlockItem>> WOOL_SLABS = new EnumMap<>(DyeColor.class);

    static {
        for (DyeColor color : DyeColor.values()) {
            WOOL_STAIRS.put(color, ITEMS.registerSimpleBlockItem(ModBlocks.WOOL_STAIRS.get(color)));
            WOOL_SLABS.put(color, ITEMS.registerSimpleBlockItem(ModBlocks.WOOL_SLABS.get(color)));
        }
    }

    @Nullable
    private static DeferredItem<Item> tideHook(String name) {
        return TideCompat.isLoaded()
                ? ITEMS.register(name, () -> TideCompat.createHookItem(new Item.Properties(), "item.spicedcider." + name + ".desc"))
                : null;
    }
}
