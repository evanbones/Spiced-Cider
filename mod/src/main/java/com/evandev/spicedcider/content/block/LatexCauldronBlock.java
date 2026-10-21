package com.evandev.spicedcider.content.block;

import com.evandev.spicedcider.registry.ModBlocks;
import com.evandev.spicedcider.registry.ModItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LatexCauldronBlock extends AbstractCauldronBlock {
    public static final MapCodec<LatexCauldronBlock> CODEC = simpleCodec(LatexCauldronBlock::new);
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 1, 4);
    public static final int MAX_LEVEL = 4;
    public static final CauldronInteraction.InteractionMap INTERACTIONS = CauldronInteraction.newInteractionMap("spicedcider_latex");

    public LatexCauldronBlock(Properties properties) {
        super(properties, INTERACTIONS);
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, 1));
    }

    @Override
    protected @NotNull MapCodec<? extends AbstractCauldronBlock> codec() {
        return CODEC;
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

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity instanceof LivingEntity && state.getValue(LEVEL) > 1 && this.isEntityInsideContent(state, pos, entity)) {
            entity.makeStuckInBlock(state, new Vec3(0.9, 0.9, 0.9));
        }
    }

    public static void registerInteractions() {
        BlockState full = ModBlocks.LATEX_CAULDRON.get().defaultBlockState().setValue(LEVEL, MAX_LEVEL);
        CauldronInteraction fillLatex = (state, level, pos, player, hand, stack) ->
                CauldronInteraction.emptyBucket(level, pos, player, hand, stack, full, SoundEvents.BUCKET_EMPTY);
        CauldronInteraction.addDefaultInteractions(INTERACTIONS.map());
        for (CauldronInteraction.InteractionMap map : List.of(CauldronInteraction.EMPTY, CauldronInteraction.WATER,
                CauldronInteraction.LAVA, CauldronInteraction.POWDER_SNOW, INTERACTIONS)) {
            map.map().put(ModItems.LATEX_BUCKET.get(), fillLatex);
        }
        CauldronInteraction.EMPTY.map().put(ModItems.LATEX_BOTTLE.get(), (state, level, pos, player, hand, stack) ->
                pour(level, pos, player, hand, stack, ModBlocks.LATEX_CAULDRON.get().defaultBlockState()));

        INTERACTIONS.map().put(Items.BUCKET, (state, level, pos, player, hand, stack) ->
                CauldronInteraction.fillBucket(state, level, pos, player, hand, stack, new ItemStack(ModItems.LATEX_BUCKET.get()),
                        s -> s.getValue(LEVEL) == MAX_LEVEL, SoundEvents.BUCKET_FILL));
        INTERACTIONS.map().put(Items.GLASS_BOTTLE, LatexCauldronBlock::bottle);
        INTERACTIONS.map().put(ModItems.LATEX_BOTTLE.get(), (state, level, pos, player, hand, stack) -> {
            if (state.getValue(LEVEL) == MAX_LEVEL) return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
            return pour(level, pos, player, hand, stack, state.setValue(LEVEL, state.getValue(LEVEL) + 1));
        });
    }

    private static ItemInteractionResult bottle(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
        if (!level.isClientSide) {
            player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(ModItems.LATEX_BOTTLE.get())));
            player.awardStat(Stats.USE_CAULDRON);
            player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
            int remaining = state.getValue(LEVEL) - 1;
            BlockState lowered = remaining == 0 ? Blocks.CAULDRON.defaultBlockState() : state.setValue(LEVEL, remaining);
            level.setBlockAndUpdate(pos, lowered);
            level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    private static ItemInteractionResult pour(Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack, BlockState result) {
        if (!level.isClientSide) {
            player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
            player.awardStat(Stats.USE_CAULDRON);
            player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
            level.setBlockAndUpdate(pos, result);
            level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }
}
