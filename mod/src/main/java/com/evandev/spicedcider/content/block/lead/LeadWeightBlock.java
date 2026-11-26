package com.evandev.spicedcider.content.block.lead;

import com.evandev.spicedcider.entities.leadweight.LeadWeightEntity;
import com.evandev.spicedcider.registry.ModDataMaps;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;


public class LeadWeightBlock extends FallingBlock implements SimpleWaterloggedBlock {
    public static final MapCodec<LeadWeightBlock> CODEC = simpleCodec(LeadWeightBlock::new);
    public static final BooleanProperty HANGING = BlockStateProperties.HANGING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final VoxelShape SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 8.0, 12.0);
    private static final int DUST_COLOR = 0x524F6A;
    public static final double TRANSFORM_FALL_DISTANCE = 3.0;
    private static final float MIN_THUD_VOLUME = 0.3F;
    private static final float MAX_THUD_VOLUME = 1.0F;
    private static final double FULL_THUD_FALL = 10.0;

    public LeadWeightBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(HANGING, false).setValue(WATERLOGGED, false));
    }

    @Override
    protected MapCodec<? extends FallingBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HANGING, WATERLOGGED);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean waterlogged = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
        return defaultBlockState()
                .setValue(HANGING, canHang(context.getLevel(), context.getClickedPos()))
                .setValue(WATERLOGGED, waterlogged);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                     LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    private static boolean canHang(LevelReader level, BlockPos pos) {
        BlockPos above = pos.above();
        BlockState aboveState = level.getBlockState(above);
        return aboveState.getBlock() instanceof LeadWeightBlock
                || aboveState.getBlock() instanceof ChainBlock && aboveState.getValue(BlockStateProperties.AXIS) == Direction.Axis.Y
                || aboveState.isFaceSturdy(level, above, Direction.DOWN);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (pos.getY() < level.getMinBuildHeight()) return;
        if (state.getValue(HANGING)) {
            if (!canHang(level, pos)) detach(level, pos, state, null);
        } else if (isFree(level.getBlockState(pos.below()))) {
            LeadWeightEntity.fromBlock(level, pos, state, null);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!state.getValue(HANGING) || !player.getMainHandItem().isEmpty()) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide) {
            detach(level, pos, state, null);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) {
        super.onProjectileHit(level, state, hit, projectile);
        if (!level.isClientSide && state.getValue(HANGING)) {
            detach(level, hit.getBlockPos(), state, projectile.getOwner() instanceof Player player ? player : null);
        }
    }

    private static void detach(Level level, BlockPos pos, BlockState state, @Nullable Player owner) {
        level.playSound(null, pos, SoundEvents.CHAIN_BREAK, SoundSource.BLOCKS, 1.0F, 0.8F);
        BlockState detached = state.setValue(HANGING, false);
        BlockState below = level.getBlockState(pos.below());
        boolean falls = isFree(below) || below.getBlock() instanceof LeadWeightBlock;
        if (falls && pos.getY() >= level.getMinBuildHeight()) {
            LeadWeightEntity.fromBlock(level, pos, detached, owner);
        } else {
            level.setBlock(pos, detached, Block.UPDATE_ALL);
        }
    }

    @Override
    public void onLand(Level level, BlockPos pos, BlockState fallingState, BlockState landOnState, FallingBlockEntity entity) {
        BlockState surface = level.getBlockState(pos.below());
        float thud = thudVolume(entity, pos);
        if (!surface.isAir()) {
            SoundType surfaceSound = surface.getSoundType();
            level.playSound(null, pos, surfaceSound.getPlaceSound(), SoundSource.BLOCKS,
                    surfaceSound.getVolume() * 1.2F * thud, surfaceSound.getPitch() * 0.75F);
        }
        level.playSound(null, pos, SoundEvents.MACE_SMASH_GROUND_HEAVY, SoundSource.BLOCKS, thud, 0.9F);

        if (level instanceof ServerLevel server && !surface.isAir()) {
            double cx = pos.getX() + 0.5, cy = pos.getY() + 0.05, cz = pos.getZ() + 0.5;
            server.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, surface), cx, cy, cz, 30, 0.35, 0.05, 0.35, 0.15);
            server.sendParticles(ParticleTypes.POOF, cx, cy + 0.1, cz, 8, 0.3, 0.02, 0.3, 0.02);
        }

        double fall = entity.getStartPos().getY() - pos.getY();
        if (fall >= TRANSFORM_FALL_DISTANCE) {
            ModDataMaps.ImpactTransform transform = surface.getBlockHolder().getData(ModDataMaps.LEAD_WEIGHT_IMPACTS);
            if (transform != null) {
                level.setBlockAndUpdate(pos.below(), transform.block().withPropertiesOf(surface));
            }
        }

    }

    @Override
    public void onBrokenAfterFall(Level level, BlockPos pos, FallingBlockEntity entity) {
        if (entity instanceof LeadWeightEntity weight && weight.settleOnImpactedBlock(level, pos)) return;
        level.playSound(null, pos, SoundEvents.MACE_SMASH_GROUND_HEAVY, SoundSource.BLOCKS, thudVolume(entity, pos), 0.9F);
        if (level instanceof ServerLevel server) {
            server.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, defaultBlockState()),
                    pos.getX() + 0.5, pos.getY() + 0.25, pos.getZ() + 0.5, 20, 0.3, 0.1, 0.3, 0.1);
        }
    }

    private static float thudVolume(FallingBlockEntity entity, BlockPos pos) {
        double fall = entity.getStartPos().getY() - pos.getY();
        float t = (float) Mth.clamp((fall - 1.0) / (FULL_THUD_FALL - 1.0), 0.0, 1.0);
        return Mth.lerp(t, MIN_THUD_VOLUME, MAX_THUD_VOLUME);
    }

    @Override
    public int getDustColor(BlockState state, BlockGetter level, BlockPos pos) {
        return DUST_COLOR;
    }
}
