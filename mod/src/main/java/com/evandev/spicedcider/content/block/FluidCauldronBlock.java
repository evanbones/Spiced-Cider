package com.evandev.spicedcider.content.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class FluidCauldronBlock extends AbstractCauldronBlock {
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 1, 4);
    public static final int MAX_LEVEL = 4;

    private final MapCodec<FluidCauldronBlock> codec = MapCodec.unit(() -> this);
    private final CauldronInteraction.InteractionMap interactions;
    private final Supplier<Fluid> fluid;
    @Nullable
    private final Supplier<Item> bottle;

    public FluidCauldronBlock(Properties properties, String name, Supplier<Fluid> fluid, @Nullable Supplier<Item> bottle) {
        this(properties, CauldronInteraction.newInteractionMap("spicedcider_" + name), fluid, bottle);
    }

    private FluidCauldronBlock(Properties properties, CauldronInteraction.InteractionMap interactions, Supplier<Fluid> fluid, @Nullable Supplier<Item> bottle) {
        super(properties, interactions);
        this.interactions = interactions;
        this.fluid = fluid;
        this.bottle = bottle;
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, 1));
    }

    public Fluid fluid() {
        return fluid.get();
    }

    @Override
    protected @NotNull MapCodec<? extends AbstractCauldronBlock> codec() {
        return codec;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
    }

    @Override
    public boolean isFull(BlockState state) {
        return state.getValue(LEVEL) == MAX_LEVEL;
    }

    @Override
    protected double getContentHeight(BlockState state) {
        return (4.0 + state.getValue(LEVEL) * 2.75) / 16.0;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return state.getValue(LEVEL);
    }

    public void registerInteractions() {
        CauldronInteraction.addDefaultInteractions(interactions.map());
        Item bottleItem = bottle == null ? Items.AIR : bottle.get();
        if (bottleItem == Items.AIR) return;
        interactions.map().put(Items.GLASS_BOTTLE, (state, level, pos, player, hand, stack) -> {
            int remaining = state.getValue(LEVEL) - 1;
            BlockState lowered = remaining == 0 ? Blocks.CAULDRON.defaultBlockState() : state.setValue(LEVEL, remaining);
            return exchange(level, pos, player, hand, stack, new ItemStack(bottleItem), lowered, SoundEvents.BOTTLE_FILL, GameEvent.FLUID_PICKUP);
        });
        interactions.map().put(bottleItem, (state, level, pos, player, hand, stack) -> {
            if (isFull(state)) return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
            return exchange(level, pos, player, hand, stack, new ItemStack(Items.GLASS_BOTTLE), state.setValue(LEVEL, state.getValue(LEVEL) + 1),
                    SoundEvents.BOTTLE_EMPTY, GameEvent.FLUID_PLACE);
        });
        CauldronInteraction.EMPTY.map().put(bottleItem, (state, level, pos, player, hand, stack) ->
                exchange(level, pos, player, hand, stack, new ItemStack(Items.GLASS_BOTTLE), defaultBlockState(), SoundEvents.BOTTLE_EMPTY, GameEvent.FLUID_PLACE));
    }

    private static ItemInteractionResult exchange(Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack, ItemStack result,
                                                  BlockState newState, SoundEvent sound, Holder<GameEvent> event) {
        if (!level.isClientSide) {
            player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, result));
            player.awardStat(Stats.USE_CAULDRON);
            player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
            level.setBlockAndUpdate(pos, newState);
            level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(null, event, pos);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }
}
