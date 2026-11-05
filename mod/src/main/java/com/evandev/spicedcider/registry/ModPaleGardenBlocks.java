package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.content.block.pale.CarvedPalePumpkinBlock;
import com.evandev.spicedcider.content.block.pale.JackOLanternBlock;
import com.evandev.spicedcider.content.block.pale.PaleBushBlock;
import com.evandev.spicedcider.content.block.pale.PalePumpkinBlock;
import com.evandev.spicedcider.content.block.pale.RedstoneJackOLanternBlock;
import com.evandev.spicedcider.content.item.CarvedPalePumpkinItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModPaleGardenBlocks {
    public static final List<DeferredBlock<? extends Block>> JACK_O_LANTERNS = new ArrayList<>();
    public static final List<DeferredBlock<FlowerPotBlock>> POTS = new ArrayList<>();

    public static final DeferredBlock<Block> PALE_PUMPKIN = ModBlocks.BLOCKS.register("pale_pumpkin",
            () -> new PalePumpkinBlock(pumpkinProperties()));
    public static final DeferredBlock<Block> CARVED_PALE_PUMPKIN = ModBlocks.BLOCKS.register("carved_pale_pumpkin",
            () -> new CarvedPalePumpkinBlock(pumpkinProperties().isValidSpawn(Blocks::always)));
    public static final DeferredBlock<Block> PALE_JACK_O_LANTERN = jackOLantern("pale_jack_o_lantern", 15);
    public static final DeferredBlock<Block> PALE_SOUL_JACK_O_LANTERN = jackOLantern("pale_soul_jack_o_lantern", 10);
    public static final DeferredBlock<Block> PALE_SULFUR_JACK_O_LANTERN = jackOLantern("pale_sulfur_jack_o_lantern", 10);
    @Nullable
    public static final DeferredBlock<Block> PALE_CUPRIC_JACK_O_LANTERN = ModList.get().isLoaded("caverns_and_chasms")
            ? jackOLantern("pale_cupric_jack_o_lantern", 15) : null;
    @Nullable
    public static final DeferredBlock<Block> PALE_GLOWLIGHT_JACK_O_LANTERN = ModList.get().isLoaded("infernalexp")
            ? jackOLantern("pale_glowlight_jack_o_lantern", 15) : null;
    public static final DeferredBlock<Block> PALE_REDSTONE_JACK_O_LANTERN = register("pale_redstone_jack_o_lantern",
            () -> new RedstoneJackOLanternBlock(pumpkinProperties().isValidSpawn(Blocks::always)
                    .lightLevel(state -> state.getValue(RedstoneJackOLanternBlock.LIT) ? 15 : 0)));
    public static final DeferredBlock<Block> SULFUR_JACK_O_LANTERN = register("sulfur_jack_o_lantern",
            () -> new JackOLanternBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.JACK_O_LANTERN).lightLevel(state -> 10)));

    public static final DeferredBlock<Block> PALE_ROSE = ModBlocks.BLOCKS.register("pale_rose",
            () -> new FlowerBlock(MobEffects.GLOWING, 5.0F, BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final DeferredBlock<Block> PALE_BUSH = ModBlocks.BLOCKS.register("pale_bush",
            () -> new PaleBushBlock(bushProperties()));
    public static final DeferredBlock<Block> TALL_PALE_BUSH = ModBlocks.BLOCKS.register("tall_pale_bush",
            () -> new DoublePlantBlock(bushProperties()));

    public static final DeferredBlock<FlowerPotBlock> POTTED_PALE_ROSE = pot("potted_pale_rose", PALE_ROSE);
    public static final DeferredBlock<FlowerPotBlock> POTTED_PALE_BUSH = pot("potted_pale_bush", PALE_BUSH);
    public static final DeferredBlock<FlowerPotBlock> POTTED_TALL_PALE_BUSH = pot("potted_tall_pale_bush", TALL_PALE_BUSH);
    public static final DeferredBlock<FlowerPotBlock> POTTED_ROSE_BUSH = pot("potted_rose_bush", () -> Blocks.ROSE_BUSH);
    public static final DeferredBlock<FlowerPotBlock> POTTED_PEONY = pot("potted_peony", () -> Blocks.PEONY);
    public static final DeferredBlock<FlowerPotBlock> POTTED_LILAC = pot("potted_lilac", () -> Blocks.LILAC);
    public static final DeferredBlock<FlowerPotBlock> POTTED_SUNFLOWER = pot("potted_sunflower", () -> Blocks.SUNFLOWER);
    public static final DeferredBlock<FlowerPotBlock> POTTED_PITCHER_PLANT = pot("potted_pitcher_plant", () -> Blocks.PITCHER_PLANT);
    @Nullable
    public static final DeferredBlock<FlowerPotBlock> POTTED_CACTUS_FLOWER = ModList.get().isLoaded("vanillabackport")
            ? pot("potted_cactus_flower", () -> BuiltInRegistries.BLOCK.get(ResourceLocation.withDefaultNamespace("cactus_flower"))) : null;

    public static final DeferredItem<BlockItem> PALE_PUMPKIN_ITEM = ModItems.ITEMS.registerSimpleBlockItem(PALE_PUMPKIN);
    public static final DeferredItem<Item> CARVED_PALE_PUMPKIN_ITEM = ModItems.ITEMS.register("carved_pale_pumpkin",
            () -> new CarvedPalePumpkinItem(CARVED_PALE_PUMPKIN.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> PALE_ROSE_ITEM = ModItems.ITEMS.registerSimpleBlockItem(PALE_ROSE);
    public static final DeferredItem<BlockItem> PALE_BUSH_ITEM = ModItems.ITEMS.registerSimpleBlockItem(PALE_BUSH);
    public static final DeferredItem<Item> TALL_PALE_BUSH_ITEM = ModItems.ITEMS.register("tall_pale_bush",
            () -> new DoubleHighBlockItem(TALL_PALE_BUSH.get(), new Item.Properties()));
    public static final List<DeferredItem<BlockItem>> JACK_O_LANTERN_ITEMS = JACK_O_LANTERNS.stream()
            .map(ModItems.ITEMS::registerSimpleBlockItem).toList();

    public static void register() {
    }

    private static DeferredBlock<Block> jackOLantern(String name, int light) {
        return register(name, () -> new JackOLanternBlock(pumpkinProperties().isValidSpawn(Blocks::always).lightLevel(state -> light)));
    }

    private static DeferredBlock<Block> register(String name, Supplier<Block> factory) {
        DeferredBlock<Block> block = ModBlocks.BLOCKS.register(name, factory);
        JACK_O_LANTERNS.add(block);
        return block;
    }

    private static DeferredBlock<FlowerPotBlock> pot(String name, Supplier<? extends Block> content) {
        DeferredBlock<FlowerPotBlock> pot = ModBlocks.BLOCKS.register(name,
                () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, content, BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_POPPY)));
        POTS.add(pot);
        return pot;
    }

    private static BlockBehaviour.Properties pumpkinProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_LIGHT_GRAY)
                .instrument(NoteBlockInstrument.DIDGERIDOO)
                .strength(1.0F)
                .sound(SoundType.WOOD)
                .pushReaction(PushReaction.DESTROY);
    }

    private static BlockBehaviour.Properties bushProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.TERRACOTTA_BROWN)
                .replaceable()
                .noCollission()
                .instabreak()
                .sound(SoundType.GRASS)
                .offsetType(BlockBehaviour.OffsetType.XZ)
                .ignitedByLava()
                .pushReaction(PushReaction.DESTROY);
    }
}
