package com.evandev.spicedcider.compat.atmospheric;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class AtmosphericHarvesting {
    private static final String NAMESPACE = "atmospheric";
    private static final int PASSION_VINE_RIPE = 4;
    private static final int PASSION_VINE_PICKED = 1;
    private static final int ALOE_RIPE = 5;
    private static final int ALOE_PICKED = 2;
    private static final List<String> DRAGON_STAGES = List.of("top", "bottom");

    public static boolean handles(BlockState state) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        if (!id.getNamespace().equals(NAMESPACE)) return false;
        return switch (id.getPath()) {
            case "passion_vine", "aloe_vera", "hanging_currant", "dragon_roots", "orange", "blood_orange", "yucca_bundle" -> true;
            default -> false;
        };
    }

    public static boolean isGrown(Level level, BlockPos pos, BlockState state) {
        return switch (path(state)) {
            case "passion_vine" -> intValue(state, "age") == PASSION_VINE_RIPE;
            case "aloe_vera" -> intValue(state, "age") == ALOE_RIPE;
            case "hanging_currant" -> true;
            case "dragon_roots" -> ripeDragonFruit(state) != null;
            case "orange", "blood_orange" -> hangsFromLeaves(level, pos, state);
            case "yucca_bundle" -> path(level.getBlockState(pos.above())).equals("yucca_branch");
            default -> false;
        };
    }

    public static ItemStack produce(BlockState state, RandomSource random) {
        return switch (path(state)) {
            case "passion_vine" -> stack("passion_fruit", 1 + random.nextInt(2) + random.nextInt(2) + random.nextInt(3));
            case "aloe_vera" -> stack("aloe_leaves", 3 + random.nextInt(5));
            case "hanging_currant" -> stack("currant", 2 + random.nextInt(3));
            case "dragon_roots" -> dragonFruit(state);
            case "orange", "blood_orange" -> stack(path(state), Math.max(1, intValue(state, "oranges")));
            case "yucca_bundle" -> stack("yucca_fruit", 2 + random.nextInt(5));
            default -> ItemStack.EMPTY;
        };
    }

    public static void pick(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        switch (path(state)) {
            case "passion_vine" -> {
                level.setBlock(pos, withInt(state, "age", PASSION_VINE_PICKED), 2);
                sound(level, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES);
            }
            case "aloe_vera" -> {
                level.setBlockAndUpdate(pos, withInt(state, "age", ALOE_PICKED));
                sound(level, pos, SoundEvents.SHEEP_SHEAR);
            }
            case "dragon_roots" -> {
                String ripe = ripeDragonFruit(state);
                BlockState picked = state;
                for (String stage : DRAGON_STAGES) {
                    if (ripe != null && ripe.equals(dragonFruitOf(state, stage))) {
                        picked = withEnum(picked, stage, "roots");
                    }
                }
                level.setBlock(pos, picked, 2);
                sound(level, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES);
            }
            case "hanging_currant", "orange", "blood_orange", "yucca_bundle" -> {
                sound(level, pos, state.getSoundType().getBreakSound());
                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            }
            default -> {
            }
        }
    }

    private static boolean hangsFromLeaves(Level level, BlockPos pos, BlockState state) {
        if (!state.hasProperty(BlockStateProperties.FACING)) return false;
        Direction facing = state.getValue(BlockStateProperties.FACING);
        return level.getBlockState(pos.relative(facing.getOpposite())).is(BlockTags.LEAVES);
    }

    private static ItemStack dragonFruit(BlockState state) {
        String ripe = ripeDragonFruit(state);
        if (ripe == null) return ItemStack.EMPTY;
        int count = 0;
        for (String stage : DRAGON_STAGES) {
            if (ripe.equals(dragonFruitOf(state, stage))) count++;
        }
        return stack(ripe, count);
    }

    @Nullable
    private static String ripeDragonFruit(BlockState state) {
        for (String stage : DRAGON_STAGES) {
            String fruit = dragonFruitOf(state, stage);
            if (fruit != null) return fruit;
        }
        return null;
    }

    @Nullable
    private static String dragonFruitOf(BlockState state, String stage) {
        return switch (enumValue(state, stage)) {
            case "fruit", "flowering" -> "dragon_fruit";
            case "ender", "flowering_ender" -> "ender_dragon_fruit";
            default -> null;
        };
    }

    private static String path(BlockState state) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        return id.getNamespace().equals(NAMESPACE) ? id.getPath() : "";
    }

    private static ItemStack stack(String item, int count) {
        Item found = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(NAMESPACE, item));
        return count <= 0 ? ItemStack.EMPTY : new ItemStack(found, count);
    }

    @Nullable
    private static Property<?> property(BlockState state, String name) {
        for (Property<?> property : state.getProperties()) {
            if (property.getName().equals(name)) return property;
        }
        return null;
    }

    private static int intValue(BlockState state, String name) {
        return property(state, name) instanceof IntegerProperty integer ? state.getValue(integer) : -1;
    }

    private static BlockState withInt(BlockState state, String name, int value) {
        return property(state, name) instanceof IntegerProperty integer && integer.getPossibleValues().contains(value)
                ? state.setValue(integer, value) : state;
    }

    private static String enumValue(BlockState state, String name) {
        Property<?> property = property(state, name);
        return property == null ? "" : valueName(state, property);
    }

    private static <T extends Comparable<T>> String valueName(BlockState state, Property<T> property) {
        return property.getName(state.getValue(property));
    }

    private static BlockState withEnum(BlockState state, String name, String value) {
        Property<?> property = property(state, name);
        return property == null ? state : withValue(state, property, value);
    }

    private static <T extends Comparable<T>> BlockState withValue(BlockState state, Property<T> property, String value) {
        return property.getValue(value).map(parsed -> state.setValue(property, parsed)).orElse(state);
    }

    private static void sound(Level level, BlockPos pos, SoundEvent sound) {
        level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
    }
}
