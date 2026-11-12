package com.evandev.spicedcider.datagen.providers;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.registry.ModBlocks;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import com.evandev.spicedcider.registry.ModFireBlocks;
import com.evandev.spicedcider.registry.ModPaleGardenBlocks;
import com.evandev.spicedcider.registry.ModBuildingBlocks;
import com.evandev.spicedcider.registry.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {

    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, SpicedCider.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.CAST_IRON_BLOCK.get())
                .addOptional(ResourceLocation.fromNamespaceAndPath("caverns_and_chasms", "tuff_tiles"))
                .addOptional(ResourceLocation.fromNamespaceAndPath("caverns_and_chasms", "tuff_tile_stairs"))
                .addOptional(ResourceLocation.fromNamespaceAndPath("caverns_and_chasms", "tuff_tile_slab"));

        this.tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(ModBlocks.WORMY_DIRT.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE)
                .add(ModBlocks.WORM_CRATE.get())
                .add(ModBlocks.GOLDEN_WORM_CRATE.get())
                .add(ModBlocks.TANNING_RACK.get());

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.DEPOSIT.get())
                .add(ModBlocks.DEEPSLATE_DEPOSIT.get())
                .add(ModBlocks.BLACKSTONE_DEPOSIT.get());

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.LATEX_CAULDRON.get(), ModBlocks.CHOCOLATE_CAULDRON.get(),
                ModBlocks.SPRING_WATER_CAULDRON.get(), ModBlocks.LUMISENE_CAULDRON.get());
        this.tag(BlockTags.CAULDRONS).add(ModBlocks.LATEX_CAULDRON.get(), ModBlocks.CHOCOLATE_CAULDRON.get(),
                ModBlocks.SPRING_WATER_CAULDRON.get(), ModBlocks.LUMISENE_CAULDRON.get());

        this.tag(BlockTags.NEEDS_STONE_TOOL)
                .add(ModBlocks.CAST_IRON_BLOCK.get());

        this.tag(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks")))
                .add(ModBlocks.CAST_IRON_BLOCK.get())
                .add(ModBlocks.TALLOW_BLOCK.get());
        this.tag(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/tallow")))
                .add(ModBlocks.TALLOW_BLOCK.get());
        this.tag(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/cast_iron")))
                .add(ModBlocks.CAST_IRON_BLOCK.get());

        paleGarden();
        fireVariants();
        this.tag(BlockTags.SAPLINGS).add(ModBlocks.GLOAMWOOD_SAPLING.get());
        this.tag(BlockTags.SAPLINGS).add(ModBlocks.CACAO_SAPLING.get());
        this.tag(BlockTags.FLOWER_POTS).add(ModBlocks.POTTED_CACAO_SAPLING.get());
        this.tag(BlockTags.LEAVES).add(ModBlocks.CACAO_LEAVES.get());
        this.tag(ModTags.Blocks.CACAO_LOGS).add(ModBlocks.CACAO_LOG.get(), ModBlocks.CACAO_WOOD.get(), ModBlocks.STRIPPED_CACAO_LOG.get(), ModBlocks.STRIPPED_CACAO_WOOD.get());
        this.tag(BlockTags.LOGS_THAT_BURN).addTag(ModTags.Blocks.CACAO_LOGS);
        this.tag(BlockTags.FLOWERS).add(ModBlocks.CACAO_LEAVES.get());
        this.tag(Tags.Blocks.STRIPPED_LOGS).add(ModBlocks.STRIPPED_CACAO_LOG.get());
        this.tag(Tags.Blocks.STRIPPED_WOODS).add(ModBlocks.STRIPPED_CACAO_WOOD.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).addTag(ModTags.Blocks.CACAO_LOGS);
        this.tag(BlockTags.MINEABLE_WITH_HOE).add(ModBlocks.CACAO_LEAVES.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(ModBlocks.CACAO_POD.get());
        this.tag(BlockTags.LEAVES).add(ModBlocks.FLOWERING_CACAO_LEAVES.get());
        this.tag(BlockTags.FLOWERS).add(ModBlocks.FLOWERING_CACAO_LEAVES.get());
        this.tag(BlockTags.MINEABLE_WITH_HOE).add(ModBlocks.FLOWERING_CACAO_LEAVES.get());
        this.tag(ModTags.Blocks.CACAO_LEAVES).add(ModBlocks.CACAO_LEAVES.get(), ModBlocks.FLOWERING_CACAO_LEAVES.get());

        this.tag(BlockTags.SAPLINGS).add(ModBlocks.PEAR_SAPLING.get());
        this.tag(BlockTags.FLOWER_POTS).add(ModBlocks.POTTED_PEAR_SAPLING.get());
        this.tag(ModTags.Blocks.PEAR_LEAVES).add(ModBlocks.PEAR_LEAVES.get(), ModBlocks.FLOWERING_PEAR_LEAVES.get());
        this.tag(BlockTags.LEAVES).addTag(ModTags.Blocks.PEAR_LEAVES);
        this.tag(BlockTags.MINEABLE_WITH_HOE).addTag(ModTags.Blocks.PEAR_LEAVES);
        this.tag(BlockTags.FLOWERS).add(ModBlocks.FLOWERING_PEAR_LEAVES.get());
        this.tag(ModTags.Blocks.PEAR_LOGS).add(ModBlocks.PEAR_LOG.get(), ModBlocks.PEAR_WOOD.get(), ModBlocks.STRIPPED_PEAR_LOG.get(), ModBlocks.STRIPPED_PEAR_WOOD.get());
        this.tag(BlockTags.LOGS_THAT_BURN).addTag(ModTags.Blocks.PEAR_LOGS);
        this.tag(BlockTags.MINEABLE_WITH_AXE).addTag(ModTags.Blocks.PEAR_LOGS);
        this.tag(Tags.Blocks.STRIPPED_LOGS).add(ModBlocks.STRIPPED_PEAR_LOG.get());
        this.tag(Tags.Blocks.STRIPPED_WOODS).add(ModBlocks.STRIPPED_PEAR_WOOD.get());

        this.tag(ModTags.Blocks.AVOCADO_LEAVES).add(ModBlocks.FLOWERING_AVOCADO_LEAVES.get())
                .addOptional(ResourceLocation.fromNamespaceAndPath("culturaldelights", "avocado_leaves"));
        this.tag(BlockTags.LEAVES).add(ModBlocks.FLOWERING_AVOCADO_LEAVES.get());
        this.tag(BlockTags.MINEABLE_WITH_HOE).add(ModBlocks.FLOWERING_AVOCADO_LEAVES.get());
        this.tag(BlockTags.FLOWERS).add(ModBlocks.FLOWERING_AVOCADO_LEAVES.get());
        this.tag(ModTags.Blocks.AVOCADO_LOGS).add(ModBlocks.AVOCADO_LOG.get(), ModBlocks.AVOCADO_WOOD.get(), ModBlocks.STRIPPED_AVOCADO_LOG.get(), ModBlocks.STRIPPED_AVOCADO_WOOD.get());
        this.tag(BlockTags.LOGS_THAT_BURN).addTag(ModTags.Blocks.AVOCADO_LOGS);
        this.tag(BlockTags.MINEABLE_WITH_AXE).addTag(ModTags.Blocks.AVOCADO_LOGS);
        this.tag(Tags.Blocks.STRIPPED_LOGS).add(ModBlocks.STRIPPED_AVOCADO_LOG.get());
        this.tag(Tags.Blocks.STRIPPED_WOODS).add(ModBlocks.STRIPPED_AVOCADO_WOOD.get());
        this.tag(BlockTags.FLOWER_POTS).add(ModBlocks.POTTED_GLOAMWOOD_SAPLING.get());

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.ACHROMARINE.get(), ModBlocks.CHROMARINE.get(), ModBlocks.BUDDING_PRISMARINE.get(), ModBlocks.SMALL_PRISMARINE_BUD.get(),
                        ModBlocks.MEDIUM_PRISMARINE_BUD.get(), ModBlocks.LARGE_PRISMARINE_BUD.get(), ModBlocks.PRISMARINE_CLUSTER.get(), ModBlocks.SULFIDE.get(),
                        ModBlocks.SULFIDE_VENT.get(), ModBlocks.CALCITE_VENT.get(), ModBlocks.TUBEWORM.get());
        this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(ModBlocks.ALGAL_SAND.get(), ModBlocks.ALGAL_GRAVEL.get(), ModBlocks.DEEPSILT.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.DRIED_DEEPSILT.get(), ModBlocks.CUT_DRIED_DEEPSILT.get(), ModBlocks.POLISHED_DRIED_DEEPSILT.get(),
                ModBlocks.CHISELED_DRIED_DEEPSILT.get());
        this.tag(BlockTags.MINEABLE_WITH_HOE).add(ModBlocks.ALGAE.get());
        this.tag(BlockTags.SAND).add(ModBlocks.ALGAL_SAND.get(), ModBlocks.DEEPSILT.get());
        this.tag(BlockTags.DIRT).add(ModBlocks.ALGAE.get());
        this.tag(BlockTags.REPLACEABLE).add(ModBlocks.CHROMATIC_BRAMBLE.get(), ModBlocks.ACHROMATIC_BRAMBLE.get(), ModBlocks.TALL_CHROMATIC_BRAMBLE.get(), ModBlocks.TALL_ACHROMATIC_BRAMBLE.get());
        this.tag(BlockTags.CRYSTAL_SOUND_BLOCKS).add(ModBlocks.BUDDING_PRISMARINE.get());

        this.tag(BlockTags.STAIRS).add(ModBlocks.GLOWSTONE_BRICK_STAIRS.get());
        this.tag(BlockTags.SLABS).add(ModBlocks.GLOWSTONE_BRICK_SLAB.get());
        this.tag(BlockTags.WALLS).add(ModBlocks.GLOWSTONE_BRICK_WALL.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.GLOWSTONE_BRICKS.get())
                .add(ModBlocks.GLOWSTONE_BRICK_STAIRS.get())
                .add(ModBlocks.GLOWSTONE_BRICK_SLAB.get())
                .add(ModBlocks.GLOWSTONE_BRICK_WALL.get())
                .add(ModBlocks.CHISELED_GLOWSTONE_BRICKS.get())
                .add(ModBlocks.POLISHED_GLOWSTONE_PILLAR.get());

        if (ModBlocks.POLISHED_GLOWSTONE_STAIRS != null) {
            this.tag(BlockTags.STAIRS).addOptional(ModBlocks.POLISHED_GLOWSTONE_STAIRS.getId());
            this.tag(BlockTags.SLABS).addOptional(ModBlocks.POLISHED_GLOWSTONE_SLAB.getId());
            this.tag(BlockTags.WALLS).addOptional(ModBlocks.POLISHED_GLOWSTONE_WALL.getId());
            this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .addOptional(ModBlocks.POLISHED_GLOWSTONE_STAIRS.getId())
                    .addOptional(ModBlocks.POLISHED_GLOWSTONE_SLAB.getId())
                    .addOptional(ModBlocks.POLISHED_GLOWSTONE_WALL.getId());
        }

        for (ModBuildingBlocks.Def def : ModBuildingBlocks.DEFS) {
            switch (def.kind()) {
                case STAIRS -> this.tag(BlockTags.STAIRS).addOptional(def.location());
                case SLAB -> this.tag(BlockTags.SLABS).addOptional(def.location());
                case WALL -> this.tag(BlockTags.WALLS).addOptional(def.location());
                default -> {
                }
            }
            switch (def.tool()) {
                case "none" -> {
                }
                case "axe" -> this.tag(BlockTags.MINEABLE_WITH_AXE).addOptional(def.location());
                case "shovel" -> this.tag(BlockTags.MINEABLE_WITH_SHOVEL).addOptional(def.location());
                default -> this.tag(BlockTags.MINEABLE_WITH_PICKAXE).addOptional(def.location());
            }
            if (def.needs() != null) {
                this.tag(switch (def.needs()) {
                    case "diamond" -> BlockTags.NEEDS_DIAMOND_TOOL;
                    case "iron" -> BlockTags.NEEDS_IRON_TOOL;
                    default -> BlockTags.NEEDS_STONE_TOOL;
                }).addOptional(def.location());
            }
        }


        if (ModBlocks.TWISTED_SIGN != null) {
            this.tag(BlockTags.STANDING_SIGNS).addOptional(ModBlocks.TWISTED_SIGN.getId());
            this.tag(BlockTags.WALL_SIGNS).addOptional(ModBlocks.TWISTED_WALL_SIGN.getId());
            this.tag(BlockTags.CEILING_HANGING_SIGNS).addOptional(ModBlocks.TWISTED_HANGING_SIGN.getId());
            this.tag(BlockTags.WALL_HANGING_SIGNS).addOptional(ModBlocks.TWISTED_WALL_HANGING_SIGN.getId());
        }
    }

    private void paleGarden() {
        List<ResourceLocation> jacks = ModPaleGardenBlocks.JACK_O_LANTERNS.stream().map(DeferredBlock::getId).toList();
        List<ResourceLocation> paleJacks = jacks.stream().filter(id -> id.getPath().startsWith("pale_")).toList();
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(ModPaleGardenBlocks.PALE_PUMPKIN.get(), ModPaleGardenBlocks.CARVED_PALE_PUMPKIN.get());
        optional(BlockTags.MINEABLE_WITH_AXE, jacks);
        this.tag(Tags.Blocks.PUMPKINS_NORMAL).add(ModPaleGardenBlocks.PALE_PUMPKIN.get());
        this.tag(Tags.Blocks.PUMPKINS_CARVED).add(ModPaleGardenBlocks.CARVED_PALE_PUMPKIN.get());
        optional(Tags.Blocks.PUMPKINS_JACK_O_LANTERNS, jacks);
        this.tag(BlockTags.ENDERMAN_HOLDABLE).add(ModPaleGardenBlocks.PALE_PUMPKIN.get());
        this.tag(ModTags.Blocks.GOLEM_HEADS).add(ModPaleGardenBlocks.CARVED_PALE_PUMPKIN.get());
        optional(ModTags.Blocks.GOLEM_HEADS, jacks);
        this.tag(ModTags.Blocks.PALE_GOLEM_HEADS).add(ModPaleGardenBlocks.CARVED_PALE_PUMPKIN.get());
        optional(ModTags.Blocks.PALE_GOLEM_HEADS, paleJacks);
        this.tag(BlockTags.SMALL_FLOWERS).add(ModPaleGardenBlocks.PALE_ROSE.get());
        this.tag(BlockTags.SWORD_EFFICIENT).add(ModPaleGardenBlocks.PALE_BUSH.get(), ModPaleGardenBlocks.TALL_PALE_BUSH.get());
        this.tag(BlockTags.REPLACEABLE_BY_TREES).add(ModPaleGardenBlocks.PALE_BUSH.get(), ModPaleGardenBlocks.TALL_PALE_BUSH.get());
        this.tag(BlockTags.FLOWER_POTS).add(ModPaleGardenBlocks.POTS.stream().map(DeferredBlock::get).toArray(Block[]::new));
        this.tag(BlockTags.FLOWER_POTS).addOptional(ResourceLocation.fromNamespaceAndPath("spicedcider", "potted_cactus_flower"));
        this.tag(BlockTags.FIRE).add(ModFireBlocks.SULFUR_FIRE.get());
        this.tag(BlockTags.CAMPFIRES).add(ModFireBlocks.SULFUR_CAMPFIRE.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(ModFireBlocks.SULFUR_CAMPFIRE.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModFireBlocks.SULFUR_LANTERN.get());
        this.tag(BlockTags.WALL_POST_OVERRIDE).add(ModFireBlocks.SULFUR_TORCH.get());
    }

    private void fireVariants() {
        optional(BlockTags.CANDLES, "sulfur_candle", "glowlight_candle");
        optional(BlockTags.CANDLE_CAKES, "sulfur_candle_cake", "glowlight_candle_cake");
        optional(BlockTags.MINEABLE_WITH_PICKAXE, "sulfur_brazier");
        optional(BlockTags.MINEABLE_WITH_AXE, "large_sulfur_jack_o_lantern_slice", "dwarf_spruce_sulfur_torch", "dwarf_spruce_plant_sulfur_torch");
        optional(BlockTags.SWORD_EFFICIENT, "large_sulfur_jack_o_lantern_slice");
        optional(Tags.Blocks.PUMPKINS_JACK_O_LANTERNS, "large_sulfur_jack_o_lantern_slice");
        optional(BlockTags.create(ResourceLocation.fromNamespaceAndPath("farmersdelight", "mineable/knife")), "large_sulfur_jack_o_lantern_slice");
        optional(BlockTags.create(ResourceLocation.fromNamespaceAndPath("supplementaries", "sconces")), "sconce_sulfur", "sconce_wall_sulfur");
        optional(BlockTags.create(ResourceLocation.fromNamespaceAndPath("supplementaries", "candle_holders")),
                "candle_holder_sulfur", "candle_holder_glowlight", "gold_candle_holder_sulfur", "gold_candle_holder_glowlight");
        optional(BlockTags.create(ResourceLocation.fromNamespaceAndPath("caverns_and_chasms", "braziers")), "sulfur_brazier");
    }

    private void optional(TagKey<Block> tag, List<ResourceLocation> ids) {
        var builder = this.tag(tag);
        for (ResourceLocation id : ids) builder.addOptional(id);
    }

    private void optional(TagKey<Block> tag, String... ids) {
        var builder = this.tag(tag);
        for (String id : ids) builder.addOptional(ResourceLocation.fromNamespaceAndPath("spicedcider", id));
    }
}
