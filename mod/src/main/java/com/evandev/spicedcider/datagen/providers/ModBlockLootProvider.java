package com.evandev.spicedcider.datagen.providers;

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
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
        ModBlocks.WOOL_STAIRS.values().forEach(block -> this.dropSelf(block.get()));
        ModBlocks.WOOL_SLABS.values().forEach(block -> this.add(block.get(), this::createSlabItemTable));
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
        ModBlocks.WOOL_STAIRS.values().forEach(block -> blocks.add(block.get()));
        ModBlocks.WOOL_SLABS.values().forEach(block -> blocks.add(block.get()));
        return blocks;
    }
}