package com.evandev.spicedcider.content.block;

import com.evandev.spicedcider.content.block.entity.GraveBlockEntity;
import com.evandev.spicedcider.content.gravestone.Graves;
import com.evandev.spicedcider.registry.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public class GraveBlock extends GravestoneBlock implements EntityBlock {
    public static final MapCodec<GraveBlock> CODEC = simpleCodec(GraveBlock::new);
    public static final IntegerProperty DAMAGE = IntegerProperty.create("damage", 0, 2);

    public GraveBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(DAMAGE, 0));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(DAMAGE);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level instanceof ServerLevel serverLevel) {
            soulParticles(serverLevel, pos);
            Graves.collect(serverLevel, player, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && level instanceof ServerLevel serverLevel) {
            soulParticles(serverLevel, pos);
            if (level.getBlockEntity(pos) instanceof GraveBlockEntity grave) {
                Graves.spill(serverLevel, pos, grave);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected void onExplosionHit(BlockState state, Level level, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> dropConsumer) {
    }

    @Override
    public boolean canEntityDestroy(BlockState state, BlockGetter level, BlockPos pos, Entity entity) {
        return false;
    }

    public static void soulParticles(ServerLevel level, BlockPos pos) {
        RandomSource random = level.getRandom();
        for (int i = 0; i < 16; i++) {
            level.sendParticles(ParticleTypes.SOUL,
                    pos.getX() + random.nextFloat() * 0.6 + 0.2, pos.getY() + random.nextFloat() / 10 + 0.25, pos.getZ() + random.nextFloat() * 0.6 + 0.2,
                    1, (random.nextFloat() - 0.5) * 0.08, (random.nextFloat() - 0.5) * 0.08, (random.nextFloat() - 0.5) * 0.08, 0.1);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GraveBlockEntity(pos, state);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide() || type != ModBlockEntities.GRAVE.get()) return null;
        return (BlockEntityTicker<T>) (BlockEntityTicker<GraveBlockEntity>) GraveBlockEntity::serverTick;
    }
}
