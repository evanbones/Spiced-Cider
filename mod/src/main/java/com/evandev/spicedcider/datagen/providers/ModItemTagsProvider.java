package com.evandev.spicedcider.datagen.providers;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.registry.ModBuildingBlocks;
import com.evandev.spicedcider.registry.ModItems;
import com.evandev.spicedcider.registry.ModPaleGardenBlocks;
import com.evandev.spicedcider.registry.ModFireBlocks;
import com.evandev.spicedcider.registry.ModTags;
import com.evandev.spicedcider.registry.ModTierItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends ItemTagsProvider {

    public ModItemTagsProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, CompletableFuture<TagLookup<Block>> pBlockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pLookupProvider, pBlockTags, SpicedCider.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        this.tag(ModTags.Items.TIDE_HOOKS)
                .addOptional(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "grappling_hook"))
                .addOptional(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "sticky_grappling_hook"))
                .addOptional(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "flux_hook"))
                .addOptional(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "gold_hook"))
                .addOptional(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "heavy_hook"))
                .addOptional(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "light_hook"))
                .addOptional(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "double_hook"))
                .addOptional(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "adamantite_hook"))
                .addOptional(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "prismarite_hook"))
                .addOptional(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "featherlight_hook"))
                .addOptional(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "necromium_hook"));

        this.tag(Tags.Items.PUMPKINS_NORMAL).add(ModPaleGardenBlocks.PALE_PUMPKIN_ITEM.get());
        this.tag(Tags.Items.PUMPKINS_CARVED).add(ModPaleGardenBlocks.CARVED_PALE_PUMPKIN_ITEM.get());
        this.tag(Tags.Items.PUMPKINS_JACK_O_LANTERNS).add(ModPaleGardenBlocks.JACK_O_LANTERN_ITEMS.stream().map(DeferredItem::get).toArray(Item[]::new));
        this.tag(ItemTags.SMALL_FLOWERS).add(ModPaleGardenBlocks.PALE_ROSE_ITEM.get());
        this.tag(ModTags.Items.SULFUR_TORCHES).add(ModFireBlocks.SULFUR_TORCH_ITEM.get());
        fireVariants();
        this.tag(ItemTags.SAPLINGS).add(ModItems.GLOAMWOOD_SAPLING.get());
        this.tag(ItemTags.SAPLINGS).add(ModItems.CACAO_SAPLING.get());
        this.tag(ItemTags.LEAVES).add(ModItems.CACAO_LEAVES.get());
        this.tag(ModTags.Items.CACAO_LOGS).add(ModItems.CACAO_LOG.get(), ModItems.CACAO_WOOD.get(), ModItems.STRIPPED_CACAO_LOG.get(), ModItems.STRIPPED_CACAO_WOOD.get());
        this.tag(ItemTags.LOGS_THAT_BURN).addTag(ModTags.Items.CACAO_LOGS);
        this.tag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("bountifulfares", "fruit_logs"))).addTag(ModTags.Items.CACAO_LOGS);
        this.tag(ItemTags.FLOWERS).add(ModItems.CACAO_LEAVES.get());
        this.tag(Tags.Items.STRIPPED_LOGS).add(ModItems.STRIPPED_CACAO_LOG.get());
        this.tag(Tags.Items.STRIPPED_WOODS).add(ModItems.STRIPPED_CACAO_WOOD.get());
        this.tag(ItemTags.LEAVES).add(ModItems.FLOWERING_CACAO_LEAVES.get());
        this.tag(ItemTags.FLOWERS).add(ModItems.FLOWERING_CACAO_LEAVES.get());

        this.tag(ItemTags.SAPLINGS).add(ModItems.PEAR_SAPLING.get());
        this.tag(ItemTags.LEAVES).add(ModItems.PEAR_LEAVES.get(), ModItems.FLOWERING_PEAR_LEAVES.get());
        this.tag(ItemTags.FLOWERS).add(ModItems.FLOWERING_PEAR_LEAVES.get());
        this.tag(ModTags.Items.PEAR_LOGS).add(ModItems.PEAR_LOG.get(), ModItems.PEAR_WOOD.get(), ModItems.STRIPPED_PEAR_LOG.get(), ModItems.STRIPPED_PEAR_WOOD.get());
        this.tag(ItemTags.LOGS_THAT_BURN).addTag(ModTags.Items.PEAR_LOGS);
        this.tag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("bountifulfares", "fruit_logs"))).addTag(ModTags.Items.PEAR_LOGS);
        this.tag(Tags.Items.STRIPPED_LOGS).add(ModItems.STRIPPED_PEAR_LOG.get());
        this.tag(Tags.Items.STRIPPED_WOODS).add(ModItems.STRIPPED_PEAR_WOOD.get());

        this.tag(ItemTags.LEAVES).add(ModItems.FLOWERING_AVOCADO_LEAVES.get());
        this.tag(ItemTags.FLOWERS).add(ModItems.FLOWERING_AVOCADO_LEAVES.get());
        this.tag(ModTags.Items.AVOCADO_LOGS).add(ModItems.AVOCADO_LOG.get(), ModItems.AVOCADO_WOOD.get(), ModItems.STRIPPED_AVOCADO_LOG.get(), ModItems.STRIPPED_AVOCADO_WOOD.get());
        this.tag(ItemTags.LOGS_THAT_BURN).addTag(ModTags.Items.AVOCADO_LOGS);
        this.tag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("bountifulfares", "fruit_logs"))).addTag(ModTags.Items.AVOCADO_LOGS);
        this.tag(Tags.Items.STRIPPED_LOGS).add(ModItems.STRIPPED_AVOCADO_LOG.get());
        this.tag(Tags.Items.STRIPPED_WOODS).add(ModItems.STRIPPED_AVOCADO_WOOD.get());
        this.tag(ItemTags.SAND).add(ModItems.DEEPSILT.get());

        this.tag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("tide", "fishing_rods")))
                .addOptional(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "adamantite_fishing_rod"));

        this.tag(Tags.Items.TOOLS)
                .add(ModItems.FIRE_STRIKER.get());
        this.tag(ItemTags.CREEPER_IGNITERS)
                .add(ModItems.FIRE_STRIKER.get());

        this.tag(ItemTags.SWORDS)
                .add(ModItems.PIGLIN_BROADSWORD.get());
        this.tag(ItemTags.AXES)
                .add(ModItems.PIGLIN_BATTLEAXE.get());
        this.tag(ItemTags.PIGLIN_LOVED)
                .add(ModItems.PIGLIN_BROADSWORD.get(), ModItems.PIGLIN_BATTLEAXE.get());
        this.tag(externalTag("quark", "counts_as_weapon_for_gold_tools_have_fortune"))
                .add(ModItems.PIGLIN_BROADSWORD.get());

        this.tag(Tags.Items.TOOLS)
                .add(ModItems.INFERNITE_CLEAVER.get());
        this.tag(Tags.Items.MELEE_WEAPON_TOOLS)
                .add(ModItems.INFERNITE_CLEAVER.get());
        this.tag(ItemTags.SWORD_ENCHANTABLE)
                .add(ModItems.INFERNITE_CLEAVER.get());
        this.tag(ItemTags.SHARP_WEAPON_ENCHANTABLE)
                .add(ModItems.INFERNITE_CLEAVER.get());
        this.tag(ItemTags.WEAPON_ENCHANTABLE)
                .add(ModItems.INFERNITE_CLEAVER.get());
        this.tag(ItemTags.FIRE_ASPECT_ENCHANTABLE)
                .add(ModItems.INFERNITE_CLEAVER.get());
        this.tag(ItemTags.DURABILITY_ENCHANTABLE)
                .add(ModItems.INFERNITE_CLEAVER.get())
                .add(ModItems.MISCHIEF_HELMET.get())
                .add(ModItems.MISCHIEF_CHESTPLATE.get())
                .add(ModItems.MISCHIEF_LEGGINGS.get())
                .add(ModItems.MISCHIEF_BOOTS.get());
        this.tag(ItemTags.VANISHING_ENCHANTABLE)
                .add(ModItems.INFERNITE_CLEAVER.get())
                .add(ModItems.MISCHIEF_HELMET.get())
                .add(ModItems.MISCHIEF_CHESTPLATE.get())
                .add(ModItems.MISCHIEF_LEGGINGS.get())
                .add(ModItems.MISCHIEF_BOOTS.get());

        this.tag(Tags.Items.ARMORS)
                .add(ModItems.MISCHIEF_HELMET.get())
                .add(ModItems.MISCHIEF_CHESTPLATE.get())
                .add(ModItems.MISCHIEF_LEGGINGS.get())
                .add(ModItems.MISCHIEF_BOOTS.get());
        this.tag(ItemTags.ARMOR_ENCHANTABLE)
                .add(ModItems.MISCHIEF_HELMET.get())
                .add(ModItems.MISCHIEF_CHESTPLATE.get())
                .add(ModItems.MISCHIEF_LEGGINGS.get())
                .add(ModItems.MISCHIEF_BOOTS.get());
        this.tag(ItemTags.EQUIPPABLE_ENCHANTABLE)
                .add(ModItems.MISCHIEF_HELMET.get())
                .add(ModItems.MISCHIEF_CHESTPLATE.get())
                .add(ModItems.MISCHIEF_LEGGINGS.get())
                .add(ModItems.MISCHIEF_BOOTS.get());
        this.tag(ItemTags.HEAD_ARMOR)
                .add(ModItems.MISCHIEF_HELMET.get());
        this.tag(ItemTags.CHEST_ARMOR)
                .add(ModItems.MISCHIEF_CHESTPLATE.get());
        this.tag(ItemTags.LEG_ARMOR)
                .add(ModItems.MISCHIEF_LEGGINGS.get());
        this.tag(ItemTags.FOOT_ARMOR)
                .add(ModItems.MISCHIEF_BOOTS.get());
        this.tag(ItemTags.HEAD_ARMOR_ENCHANTABLE)
                .add(ModItems.MISCHIEF_HELMET.get());
        this.tag(ItemTags.CHEST_ARMOR_ENCHANTABLE)
                .add(ModItems.MISCHIEF_CHESTPLATE.get());
        this.tag(ItemTags.LEG_ARMOR_ENCHANTABLE)
                .add(ModItems.MISCHIEF_LEGGINGS.get());
        this.tag(ItemTags.FOOT_ARMOR_ENCHANTABLE)
                .add(ModItems.MISCHIEF_BOOTS.get());

        this.tag(ItemTags.create(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("c", "ingots")))
                .add(ModItems.CAST_IRON_INGOT.get());
        this.tag(ItemTags.create(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("c", "ingots/cast_iron")))
                .add(ModItems.CAST_IRON_INGOT.get());

        this.tag(ItemTags.create(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("c", "nuggets")))
                .add(ModItems.CAST_IRON_NUGGET.get());
        this.tag(ItemTags.create(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("c", "nuggets/cast_iron")))
                .add(ModItems.CAST_IRON_NUGGET.get());

        this.tag(ItemTags.create(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("c", "storage_blocks")))
                .add(ModItems.CAST_IRON_BLOCK.get())
                .add(ModItems.TALLOW_BLOCK.get());
        this.tag(ItemTags.create(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/tallow")))
                .add(ModItems.TALLOW_BLOCK.get());

        this.tag(ModTags.Items.WAX_OR_TALLOW)
                .add(Items.HONEYCOMB)
                .add(ModItems.TALLOW.get());
        this.tag(ItemTags.create(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("c", "cooking_oil")))
                .add(ModItems.TALLOW.get());
        this.tag(ItemTags.create(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/cast_iron")))
                .add(ModItems.CAST_IRON_BLOCK.get());

        this.tag(ItemTags.STAIRS).add(ModItems.GLOWSTONE_BRICK_STAIRS.get());
        this.tag(ItemTags.SLABS).add(ModItems.GLOWSTONE_BRICK_SLAB.get());
        this.tag(ItemTags.WALLS).add(ModItems.GLOWSTONE_BRICK_WALL.get());
        if (ModItems.POLISHED_GLOWSTONE_STAIRS != null) {
            this.tag(ItemTags.STAIRS).addOptional(ModItems.POLISHED_GLOWSTONE_STAIRS.getId());
            this.tag(ItemTags.SLABS).addOptional(ModItems.POLISHED_GLOWSTONE_SLAB.getId());
            this.tag(ItemTags.WALLS).addOptional(ModItems.POLISHED_GLOWSTONE_WALL.getId());
        }
        for (ModBuildingBlocks.Def def : ModBuildingBlocks.DEFS) {
            switch (def.kind()) {
                case STAIRS -> this.tag(ItemTags.STAIRS).addOptional(def.location());
                case SLAB -> this.tag(ItemTags.SLABS).addOptional(def.location());
                case WALL -> this.tag(ItemTags.WALLS).addOptional(def.location());
                default -> {
                }
            }
        }
        if (ModItems.TWISTED_SIGN != null) {
            this.tag(ItemTags.SIGNS).addOptional(ModItems.TWISTED_SIGN.getId());
            this.tag(ItemTags.HANGING_SIGNS).addOptional(ModItems.TWISTED_HANGING_SIGN.getId());
            this.tag(ItemTags.BOATS).addOptional(ModItems.TWISTED_BOAT.getId());
            this.tag(ItemTags.CHEST_BOATS).addOptional(ModItems.TWISTED_CHEST_BOAT.getId());
        }
        ModItems.BALLOONS.forEach((color, item) -> {
            this.tag(ModTags.Items.BALLOONS).add(item.get());
            this.tag(Tags.Items.DYED).add(item.get());
            this.tag(color.getDyedTag()).add(item.get());
        });

        this.tag(ItemTags.create(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("c", "plates")))
                .add(ModItems.CAST_IRON_SHEET.get());
        this.tag(ItemTags.create(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("c", "plates/cast_iron")))
                .add(ModItems.CAST_IRON_SHEET.get());

        addTierItemTags();
    }

    private void addTierItemTags() {
        Item[] hammers = ModTierItems.HAMMERS.stream().map(DeferredItem::get).toArray(Item[]::new);
        Item pickaxe = ModTierItems.ELECTRUM_PICKAXE.get();
        Item knife = ModTierItems.ELECTRUM_KNIFE.get();
        Item lance = ModTierItems.ELECTRUM_LANCE.get();
        Item steelShield = ModTierItems.STEEL_SHIELD.get();
        Item electrumShield = ModTierItems.ELECTRUM_SHIELD.get();
        Item helmet = ModTierItems.ELECTRUM_HELMET.get();
        Item chestplate = ModTierItems.ELECTRUM_CHESTPLATE.get();
        Item leggings = ModTierItems.ELECTRUM_LEGGINGS.get();
        Item boots = ModTierItems.ELECTRUM_BOOTS.get();

        for (TagKey<Item> tag : List.of(externalTag("manual_labour", "hammers"), externalTag("c", "tools/hammer"),
                ItemTags.CLUSTER_MAX_HARVESTABLES, ItemTags.MINING_ENCHANTABLE, ItemTags.MINING_LOOT_ENCHANTABLE)) {
            this.tag(tag).add(hammers);
        }
        this.tag(Tags.Items.MINING_TOOL_TOOLS).add(hammers).add(pickaxe);
        this.tag(Tags.Items.TOOLS).add(hammers).add(knife);

        this.tag(ItemTags.PICKAXES).add(pickaxe);
        this.tag(ItemTags.AXES).add(ModTierItems.ELECTRUM_AXE.get());
        this.tag(ItemTags.SHOVELS).add(ModTierItems.ELECTRUM_SHOVEL.get());
        this.tag(ItemTags.HOES).add(ModTierItems.ELECTRUM_HOE.get());
        this.tag(ItemTags.CLUSTER_MAX_HARVESTABLES).add(pickaxe);

        this.tag(externalTag("farmersdelight", "tools/knives")).add(knife);
        this.tag(externalTag("c", "tools/knife")).add(knife);

        this.tag(externalTag("minecraft", "spears")).add(lance);
        this.tag(externalTag("minecraft", "lunge_enchantable")).add(lance);
        this.tag(Tags.Items.TOOLS_SPEAR).add(lance);
        this.tag(Tags.Items.MELEE_WEAPON_TOOLS).add(lance);
        for (TagKey<Item> tag : List.of(ItemTags.SWORD_ENCHANTABLE, ItemTags.SHARP_WEAPON_ENCHANTABLE,
                ItemTags.WEAPON_ENCHANTABLE, ItemTags.FIRE_ASPECT_ENCHANTABLE)) {
            this.tag(tag).add(lance);
        }

        this.tag(Tags.Items.TOOLS_SHIELD).add(steelShield, electrumShield);

        this.tag(ItemTags.DURABILITY_ENCHANTABLE).add(hammers).add(knife, lance, steelShield, electrumShield,
                helmet, chestplate, leggings, boots);
        this.tag(ItemTags.VANISHING_ENCHANTABLE).add(helmet, chestplate, leggings, boots);

        this.tag(Tags.Items.ARMORS).add(helmet, chestplate, leggings, boots);
        this.tag(ItemTags.ARMOR_ENCHANTABLE).add(helmet, chestplate, leggings, boots);
        this.tag(ItemTags.EQUIPPABLE_ENCHANTABLE).add(helmet, chestplate, leggings, boots);
        this.tag(ItemTags.HEAD_ARMOR).add(helmet);
        this.tag(ItemTags.CHEST_ARMOR).add(chestplate);
        this.tag(ItemTags.LEG_ARMOR).add(leggings);
        this.tag(ItemTags.FOOT_ARMOR).add(boots);
        this.tag(ItemTags.HEAD_ARMOR_ENCHANTABLE).add(helmet);
        this.tag(ItemTags.CHEST_ARMOR_ENCHANTABLE).add(chestplate);
        this.tag(ItemTags.LEG_ARMOR_ENCHANTABLE).add(leggings);
        this.tag(ItemTags.FOOT_ARMOR_ENCHANTABLE).add(boots);

        for (String javelin : List.of("copper_javelin", "rose_gold_javelin", "silver_javelin", "electrum_javelin", "necromium_javelin")) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, javelin);
            this.tag(externalTag("modestmining", "javelins")).addOptional(id);
            this.tag(externalTag("c", "tools/javelins")).addOptional(id);
        }
        this.tag(ItemTags.TRIM_MATERIALS).add(ModItems.CAST_IRON_INGOT.get());
        for (String material : List.of("alloyed:bronze_ingot", "alloyed:steel_ingot", "oooh_pinky:rose_gold_ingot",
                "modestmining:electrum_ingot", "modestmining:lead_ingot", "modestmining:prismarite_ingot",
                "modestmining:valkyrium_ingot", "spelunkery:cinnabar", "modestmining:pearl")) {
            this.tag(ItemTags.TRIM_MATERIALS).addOptional(ResourceLocation.parse(material));
        }
        this.tag(ItemTags.PIGLIN_LOVED).addOptional(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "rose_gold_javelin"));
        this.tag(ItemTags.PIGLIN_LOVED).addOptional(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "electrum_javelin"));

        ModTierItems.ITEMS.getEntries().stream()
                .filter(holder -> holder.getId().getPath().startsWith("electrum_") || holder.getId().getPath().startsWith("rose_gold_"))
                .filter(holder -> !holder.getId().getPath().endsWith("_javelin"))
                .forEach(holder -> this.tag(ItemTags.PIGLIN_LOVED).add(holder.get()));
    }

    private static TagKey<Item> externalTag(String namespace, String path) {
        return ItemTags.create(ResourceLocation.fromNamespaceAndPath(namespace, path));
    }

    private void fireVariants() {
        optional(ItemTags.CANDLES, "sulfur_candle", "glowlight_candle");
        optional(Tags.Items.PUMPKINS_JACK_O_LANTERNS, "large_sulfur_jack_o_lantern_slice");
        optional(ItemTags.create(ResourceLocation.fromNamespaceAndPath("supplementaries", "sconces")), "sconce_sulfur");
        optional(ItemTags.create(ResourceLocation.fromNamespaceAndPath("supplementaries", "candle_holders")),
                "candle_holder_sulfur", "candle_holder_glowlight", "gold_candle_holder_sulfur", "gold_candle_holder_glowlight");
    }

    private void optional(TagKey<Item> tag, String... ids) {
        var builder = this.tag(tag);
        for (String id : ids) builder.addOptional(ResourceLocation.fromNamespaceAndPath("spicedcider", id));
    }
}
