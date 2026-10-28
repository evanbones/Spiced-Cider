package com.evandev.spicedcider.datagen.providers;

import com.evandev.spicedcider.content.block.CacaoPodBlock;
import com.evandev.spicedcider.registry.ModBlocks;
import com.evandev.spicedcider.registry.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class ModBlockLootProvider extends BlockLootSubProvider {

    protected ModBlockLootProvider(HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    @Override
    protected void generate() {
        this.dropSelf(ModBlocks.CAST_IRON_BLOCK.get());
        this.dropSelf(ModBlocks.TALLOW_BLOCK.get());
        this.dropSelf(ModBlocks.WORM_CRATE.get());
        this.dropSelf(ModBlocks.GOLDEN_WORM_CRATE.get());
        this.add(ModBlocks.WORMY_DIRT.get(), this::createWormyDirtTable);
        this.add(ModBlocks.DEPOSIT.get(), block -> this.createDepositTable(block, ModItems.GEODE.get()));
        this.add(ModBlocks.DEEPSLATE_DEPOSIT.get(), block -> this.createDepositTable(block, ModItems.DEEPSLATE_GEODE.get()));
        this.add(ModBlocks.BLACKSTONE_DEPOSIT.get(), block -> this.createDepositTable(block, ModItems.BLACKSTONE_GEODE.get()));
        this.dropSelf(ModBlocks.GLOAMWOOD_SAPLING.get());
        this.dropPottedContents(ModBlocks.POTTED_GLOAMWOOD_SAPLING.get());
        this.dropSelf(ModBlocks.CACAO_SAPLING.get());
        this.dropPottedContents(ModBlocks.POTTED_CACAO_SAPLING.get());
        this.add(ModBlocks.CACAO_LEAVES.get(), block -> this.createLeavesDrops(block, ModBlocks.CACAO_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES));
        this.add(ModBlocks.FLOWERING_CACAO_LEAVES.get(), block -> this.createLeavesDrops(block, ModBlocks.CACAO_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES));
        this.dropSelf(ModBlocks.PEAR_SAPLING.get());
        this.dropPottedContents(ModBlocks.POTTED_PEAR_SAPLING.get());
        this.add(ModBlocks.PEAR_LEAVES.get(), block -> this.createLeavesDrops(block, ModBlocks.PEAR_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES));
        this.add(ModBlocks.FLOWERING_PEAR_LEAVES.get(), block -> this.createLeavesDrops(block, ModBlocks.PEAR_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES));
        this.add(ModBlocks.CACAO_POD.get(), block -> LootTable.lootTable().withPool(LootPool.lootPool()
                .add(this.applyExplosionDecay(block, LootItem.lootTableItem(Items.COCOA_BEANS)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(3.0F))
                                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CacaoPodBlock.AGE, CacaoPodBlock.MAX_AGE))))))));
        Stream.of(ModBlocks.CACAO_LOG, ModBlocks.CACAO_WOOD, ModBlocks.STRIPPED_CACAO_LOG, ModBlocks.STRIPPED_CACAO_WOOD, ModBlocks.PEAR_LOG, ModBlocks.PEAR_WOOD,
                ModBlocks.STRIPPED_PEAR_LOG, ModBlocks.STRIPPED_PEAR_WOOD, ModBlocks.AVOCADO_LOG, ModBlocks.AVOCADO_WOOD, ModBlocks.STRIPPED_AVOCADO_LOG,
                ModBlocks.STRIPPED_AVOCADO_WOOD, ModBlocks.DEEPSILT, ModBlocks.DRIED_DEEPSILT, ModBlocks.CUT_DRIED_DEEPSILT, ModBlocks.POLISHED_DRIED_DEEPSILT,
                ModBlocks.CHISELED_DRIED_DEEPSILT).forEach(block -> this.dropSelf(block.get()));
        this.dropSelf(ModBlocks.ALGAE.get());
        this.dropSelf(ModBlocks.ALGAL_SAND.get());
        this.dropSelf(ModBlocks.ALGAL_GRAVEL.get());
        this.dropSelf(ModBlocks.ACHROMARINE.get());
        this.dropSelf(ModBlocks.CHROMARINE.get());
        this.dropSelf(ModBlocks.BUDDING_PRISMARINE.get());
        this.dropWhenSilkTouch(ModBlocks.SMALL_PRISMARINE_BUD.get());
        this.dropWhenSilkTouch(ModBlocks.MEDIUM_PRISMARINE_BUD.get());
        this.dropWhenSilkTouch(ModBlocks.LARGE_PRISMARINE_BUD.get());
        this.add(ModBlocks.PRISMARINE_CLUSTER.get(), block -> this.createSilkTouchDispatchTable(block,
                this.applyExplosionDecay(block, LootItem.lootTableItem(Items.PRISMARINE_SHARD).apply(SetItemCountFunction.setCount(ConstantValue.exactly(4.0F))))));
        this.dropSelf(ModBlocks.SULFIDE.get());
        this.dropSelf(ModBlocks.SULFIDE_VENT.get());
        this.dropSelf(ModBlocks.CALCITE_VENT.get());
        this.dropSelf(ModBlocks.TUBEWORM.get());
        this.dropSelf(ModBlocks.CHROMATIC_BRAMBLE.get());
        this.dropSelf(ModBlocks.ACHROMATIC_BRAMBLE.get());
        this.add(ModBlocks.TALL_CHROMATIC_BRAMBLE.get(), block -> this.createSinglePropConditionTable(block, DoublePlantBlock.HALF, DoubleBlockHalf.LOWER));
        this.add(ModBlocks.TALL_ACHROMATIC_BRAMBLE.get(), block -> this.createSinglePropConditionTable(block, DoublePlantBlock.HALF, DoubleBlockHalf.LOWER));
        this.dropSelf(ModBlocks.GLOWSTONE_BRICKS.get());
        this.dropSelf(ModBlocks.GLOWSTONE_BRICK_STAIRS.get());
        this.add(ModBlocks.GLOWSTONE_BRICK_SLAB.get(), this::createSlabItemTable);
        this.dropSelf(ModBlocks.GLOWSTONE_BRICK_WALL.get());
        this.dropSelf(ModBlocks.CHISELED_GLOWSTONE_BRICKS.get());
        this.dropSelf(ModBlocks.POLISHED_GLOWSTONE_PILLAR.get());
        if (ModBlocks.POLISHED_GLOWSTONE_STAIRS != null) {
            this.dropSelf(ModBlocks.POLISHED_GLOWSTONE_STAIRS.get());
            this.add(ModBlocks.POLISHED_GLOWSTONE_SLAB.get(), this::createSlabItemTable);
            this.dropSelf(ModBlocks.POLISHED_GLOWSTONE_WALL.get());
        }
        if (ModBlocks.TWISTED_SIGN != null) {
            this.dropSelf(ModBlocks.TWISTED_SIGN.get());
            this.dropSelf(ModBlocks.TWISTED_HANGING_SIGN.get());
        }
    }

    private LootTable.Builder createDepositTable(Block block, Item geode) {
        HolderLookup.RegistryLookup<Enchantment> enchantments = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.createSilkTouchDispatchTable(block, this.applyExplosionDecay(block, LootItem.lootTableItem(geode)
                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                .apply(ApplyBonusCount.addUniformBonusCount(enchantments.getOrThrow(Enchantments.FORTUNE)))));
    }

    private LootTable.Builder createWormyDirtTable(Block block) {
        HolderLookup.RegistryLookup<Enchantment> enchantments = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return LootTable.lootTable()
                .withPool(LootPool.lootPool().when(this.doesNotHaveSilkTouch())
                        .add(LootItem.lootTableItem(ModItems.WORM.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0F, 6.0F)))
                                .apply(ApplyBonusCount.addOreBonusCount(enchantments.getOrThrow(Enchantments.FORTUNE)))
                                .apply(ApplyExplosionDecay.explosionDecay())))
                .withPool(LootPool.lootPool().when(this.doesNotHaveSilkTouch()).when(LootItemRandomChanceCondition.randomChance(0.35F))
                        .add(LootItem.lootTableItem(ModItems.GOLDEN_WORM.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                                .apply(ApplyExplosionDecay.explosionDecay())))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(block).when(this.hasSilkTouch())
                                .otherwise(LootItem.lootTableItem(Blocks.DIRT).when(ExplosionCondition.survivesExplosion()))));
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        List<Block> blocks = new ArrayList<>(List.of(ModBlocks.CAST_IRON_BLOCK.get(), ModBlocks.TALLOW_BLOCK.get()));
        blocks.add(ModBlocks.WORMY_DIRT.get());
        blocks.add(ModBlocks.DEPOSIT.get());
        blocks.add(ModBlocks.DEEPSLATE_DEPOSIT.get());
        blocks.add(ModBlocks.BLACKSTONE_DEPOSIT.get());
        blocks.add(ModBlocks.WORM_CRATE.get());
        blocks.add(ModBlocks.GOLDEN_WORM_CRATE.get());
        blocks.add(ModBlocks.GLOAMWOOD_SAPLING.get());
        blocks.add(ModBlocks.POTTED_GLOAMWOOD_SAPLING.get());
        Stream.of(ModBlocks.CACAO_LOG, ModBlocks.CACAO_WOOD, ModBlocks.STRIPPED_CACAO_LOG, ModBlocks.STRIPPED_CACAO_WOOD, ModBlocks.PEAR_LOG, ModBlocks.PEAR_WOOD,
                ModBlocks.STRIPPED_PEAR_LOG, ModBlocks.STRIPPED_PEAR_WOOD, ModBlocks.AVOCADO_LOG, ModBlocks.AVOCADO_WOOD, ModBlocks.STRIPPED_AVOCADO_LOG,
                ModBlocks.STRIPPED_AVOCADO_WOOD, ModBlocks.DEEPSILT, ModBlocks.DRIED_DEEPSILT, ModBlocks.CUT_DRIED_DEEPSILT, ModBlocks.POLISHED_DRIED_DEEPSILT,
                ModBlocks.CHISELED_DRIED_DEEPSILT).forEach(block -> blocks.add(block.get()));
        Stream.of(ModBlocks.CACAO_LEAVES, ModBlocks.FLOWERING_CACAO_LEAVES, ModBlocks.CACAO_POD, ModBlocks.CACAO_SAPLING, ModBlocks.POTTED_CACAO_SAPLING,
                ModBlocks.PEAR_LEAVES, ModBlocks.FLOWERING_PEAR_LEAVES, ModBlocks.PEAR_SAPLING, ModBlocks.POTTED_PEAR_SAPLING).forEach(block -> blocks.add(block.get()));
        Stream.of(ModBlocks.ALGAE, ModBlocks.ALGAL_SAND, ModBlocks.ALGAL_GRAVEL, ModBlocks.ACHROMARINE, ModBlocks.CHROMARINE, ModBlocks.BUDDING_PRISMARINE,
                ModBlocks.SMALL_PRISMARINE_BUD, ModBlocks.MEDIUM_PRISMARINE_BUD, ModBlocks.LARGE_PRISMARINE_BUD, ModBlocks.PRISMARINE_CLUSTER, ModBlocks.SULFIDE,
                ModBlocks.SULFIDE_VENT, ModBlocks.CALCITE_VENT, ModBlocks.TUBEWORM, ModBlocks.CHROMATIC_BRAMBLE, ModBlocks.ACHROMATIC_BRAMBLE,
                ModBlocks.TALL_CHROMATIC_BRAMBLE, ModBlocks.TALL_ACHROMATIC_BRAMBLE).forEach(block -> blocks.add(block.get()));
        blocks.add(ModBlocks.GLOWSTONE_BRICKS.get());
        blocks.add(ModBlocks.GLOWSTONE_BRICK_STAIRS.get());
        blocks.add(ModBlocks.GLOWSTONE_BRICK_SLAB.get());
        blocks.add(ModBlocks.GLOWSTONE_BRICK_WALL.get());
        blocks.add(ModBlocks.CHISELED_GLOWSTONE_BRICKS.get());
        blocks.add(ModBlocks.POLISHED_GLOWSTONE_PILLAR.get());
        if (ModBlocks.POLISHED_GLOWSTONE_STAIRS != null) {
            blocks.add(ModBlocks.POLISHED_GLOWSTONE_STAIRS.get());
            blocks.add(ModBlocks.POLISHED_GLOWSTONE_SLAB.get());
            blocks.add(ModBlocks.POLISHED_GLOWSTONE_WALL.get());
        }
        if (ModBlocks.TWISTED_SIGN != null) {
            blocks.add(ModBlocks.TWISTED_SIGN.get());
            blocks.add(ModBlocks.TWISTED_WALL_SIGN.get());
            blocks.add(ModBlocks.TWISTED_HANGING_SIGN.get());
            blocks.add(ModBlocks.TWISTED_WALL_HANGING_SIGN.get());
        }
        return blocks;
    }
}