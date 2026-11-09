package com.evandev.spicedcider.compat.nomansland.tap;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.mixin.nomansland.TapBlockEntityInvoker;
import com.farcr.nomansland.common.block.tap.TapBlock;
import com.farcr.nomansland.common.blockentity.TapBlockEntity;
import net.mehvahdjukaar.moonlight.api.fluids.FluidOffer;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluidStack;
import net.mehvahdjukaar.supplementaries.common.block.faucet.FaucetBehaviorsManager;
import net.mehvahdjukaar.supplementaries.common.block.faucet.FaucetSource;
import net.mehvahdjukaar.supplementaries.common.block.faucet.FaucetTarget;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.CauldronFluidContent;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public class TapFaucetBridge {
    public static final float SPOUT_HEIGHT = 3.0F / 16.0F;
    private static final int BOTTLE_MB = 250;
    private static final int INTERVAL = 80;
    private static final int RETRY = 20;
    private static final int STREAM_GRACE = 30;
    private static final String STREAM_KEY = "spicedcider:stream";
    private static final TagKey<Block> CANT_INTERACT = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("supplementaries", "faucet_cant_interact"));
    @Nullable
    private static final Field FLOW = findFlowField();

    public static boolean enabled() {
        return SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.tapFaucetFluids, false);
    }

    @Nullable
    public static BlockPos target(Level level, BlockPos tap, @Nullable BlockPos found) {
        return found == null && enabled() ? tap.below() : found;
    }

    public static void tick(TapBlockEntity tap, TapFluidState fluids, ServerLevel level, BlockPos pos, BlockState state) {
        if (!enabled() || state.getValue(TapBlock.CLOSED)) {
            fluids.sourceTimer = -1;
            clearStream(tap, fluids, level, pos, state);
            return;
        }
        if (fluids.stream != null && level.getGameTime() > fluids.streamUntil) {
            clearStream(tap, fluids, level, pos, state);
        }
        if (hasOwnFlow(tap)) {
            fluids.sourceTimer = -1;
            return;
        }
        if (fluids.sourceTimer > 0) {
            fluids.sourceTimer--;
            return;
        }
        fluids.sourceTimer = pourFromSource(tap, fluids, level, pos, state);
        tap.setChanged();
    }

    private static int pourFromSource(TapBlockEntity tap, TapFluidState fluids, ServerLevel level, BlockPos pos, BlockState state) {
        Direction facing = state.getValue(TapBlock.FACING);
        BlockPos behind = TapBlock.getSourcePos(pos, state);
        BlockState back = level.getBlockState(behind);
        if (back.isAir() || back.is(CANT_INTERACT)) return RETRY;
        BlockPos target = target(level, pos, TapBlockEntity.findTarget(level, pos));
        if (target == null) return RETRY;
        FaucetBehaviorsManager behaviors = FaucetBehaviorsManager.getInstance(level);
        Integer result = pourFrom(behaviors.getBlockInteractions(), tap, fluids, level, pos, facing, behind, back, target);
        if (result != null) return result;
        BlockEntity tile = level.getBlockEntity(behind);
        if (tile != null) {
            result = pourFrom(behaviors.getTileInteractions(), tap, fluids, level, pos, facing, behind, tile, target);
            if (result != null) return result;
        }
        FluidState fluid = level.getFluidState(behind);
        result = pourFrom(behaviors.getSourceFluidInteractions(), tap, fluids, level, pos, facing, behind, fluid, target);
        return result != null ? result : RETRY;
    }

    @Nullable
    private static <T, S extends FaucetSource<T>> Integer pourFrom(Iterable<S> sources, TapBlockEntity tap, TapFluidState fluids, ServerLevel level, BlockPos pos, Direction facing, BlockPos behind, T source, BlockPos target) {
        for (S interaction : sources) {
            FluidOffer offer = interaction.getProvidedFluid(level, behind, facing, source);
            if (offer == null || offer.fluid().isEmpty()) continue;
            Integer filled = fill(level, target, offer, true);
            if (filled == null) continue;
            if (filled <= 0) return RETRY;
            interaction.drain(level, behind, facing, source, filled);
            int interval = INTERVAL * filled;
            showStream(tap, fluids, level, pos, target, offer.fluid(), interval);
            return interval;
        }
        return null;
    }

    @Nullable
    private static Integer fill(Level level, BlockPos target, FluidOffer offer, boolean viaTap) {
        BlockState state = level.getBlockState(target);
        if (viaTap) {
            Holder<Fluid> vanilla = offer.fluid().getVanillaFluid();
            if (vanilla != null && vanilla.value() != Fluids.EMPTY) {
                int bottles = Math.max(1, offer.minAmount());
                FluidStack stack = new FluidStack(vanilla.value(), BOTTLE_MB * bottles);
                if (TapBlockEntityInvoker.spicedcider$putFluid(level, target, state, stack)) return bottles;
            }
        }
        FaucetBehaviorsManager behaviors = FaucetBehaviorsManager.getInstance(level);
        for (FaucetTarget.BlState interaction : behaviors.getTargetBlockInteractions()) {
            Integer filled = interaction.fill(level, target, state, offer);
            if (filled != null) return filled;
        }
        BlockEntity tile = level.getBlockEntity(target);
        if (tile != null) {
            for (FaucetTarget.Tile interaction : behaviors.getTargetTileInteractions()) {
                Integer filled = interaction.fill(level, target, tile, offer);
                if (filled != null) return filled;
            }
        }
        FluidState fluid = state.getFluidState();
        for (FaucetTarget.Fluid interaction : behaviors.getTargetFluidInteractions()) {
            Integer filled = interaction.fill(level, target, fluid, offer);
            if (filled != null) return filled;
        }
        return null;
    }

    public static boolean fillFallback(Level level, BlockPos target, BlockState state, FluidStack stack) {
        if (!enabled() || stack.isEmpty()) return false;
        int bottles = Math.max(1, stack.getAmount() / BOTTLE_MB);
        SoftFluidStack soft = SoftFluidStack.fromFluid(stack.getFluid(), bottles, level.registryAccess());
        if (soft.isEmpty()) return false;
        Integer filled = fill(level, target, FluidOffer.of(soft, bottles), false);
        return filled != null && filled > 0;
    }

    public static boolean cauldronFallback(Level level, BlockPos target, BlockState state, Block cauldron) {
        FluidStack stack = cauldronUnit(cauldron);
        return stack != null && fillFallback(level, target, state, stack);
    }

    @Nullable
    private static FluidStack cauldronUnit(Block cauldron) {
        CauldronFluidContent content = CauldronFluidContent.getForBlock(cauldron);
        if (content == null || content.fluid == Fluids.EMPTY) return null;
        return new FluidStack(content.fluid, Math.max(1, content.totalAmount / Math.max(1, content.maxLevel)));
    }

    public static void showStream(TapBlockEntity tap, TapFluidState fluids, Level level, BlockPos pos, BlockPos target, Fluid fluid) {
        if (!enabled() || fluid == Fluids.EMPTY) return;
        SoftFluidStack soft = SoftFluidStack.fromFluid(fluid, 1, level.registryAccess());
        if (!soft.isEmpty()) showStream(tap, fluids, level, pos, target, soft, INTERVAL);
    }

    public static void showCauldronStream(TapBlockEntity tap, TapFluidState fluids, Level level, BlockPos pos, BlockPos target, Block cauldron) {
        FluidStack unit = cauldronUnit(cauldron);
        if (unit != null) showStream(tap, fluids, level, pos, target, unit.getFluid());
    }

    private static void showStream(TapBlockEntity tap, TapFluidState fluids, Level level, BlockPos pos, BlockPos target, SoftFluidStack fluid, int duration) {
        fluids.streamUntil = level.getGameTime() + duration + STREAM_GRACE;
        SoftFluidStack single = fluid.copyWithCount(1);
        float length = streamLength(level, pos, target);
        if (fluids.stream != null && fluids.stream.isSameFluidSameComponents(single) && Math.abs(fluids.streamLength - length) < 0.01F) return;
        fluids.stream = single;
        fluids.streamLength = length;
        sync(tap, level, pos);
    }

    private static float streamLength(Level level, BlockPos pos, BlockPos target) {
        BlockState state = level.getBlockState(target);
        double top;
        if (state.getBlock() instanceof AbstractCauldronBlock) {
            top = 0.25;
        } else {
            VoxelShape shape = state.getShape(level, target);
            top = shape.isEmpty() ? 0.0 : shape.max(Direction.Axis.Y);
        }
        return (float) Math.max(0.0, pos.getY() + SPOUT_HEIGHT - (target.getY() + top));
    }

    private static void clearStream(TapBlockEntity tap, TapFluidState fluids, Level level, BlockPos pos, BlockState state) {
        if (fluids.stream == null) return;
        fluids.stream = null;
        sync(tap, level, pos);
    }

    private static void sync(TapBlockEntity tap, Level level, BlockPos pos) {
        tap.setChanged();
        BlockState state = level.getBlockState(pos);
        level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
    }

    public static void writeStream(TapFluidState fluids, CompoundTag tag, HolderLookup.Provider registries) {
        CompoundTag stream = new CompoundTag();
        if (fluids.stream != null) {
            stream.put("fluid", fluids.stream.save(registries));
            stream.putFloat("length", fluids.streamLength);
        }
        tag.put(STREAM_KEY, stream);
    }

    public static void readStream(TapFluidState fluids, CompoundTag tag, HolderLookup.Provider registries) {
        if (!tag.contains(STREAM_KEY)) {
            fluids.stream = null;
            return;
        }
        CompoundTag stream = tag.getCompound(STREAM_KEY);
        if (!stream.contains("fluid")) {
            fluids.stream = null;
            return;
        }
        try {
            SoftFluidStack fluid = SoftFluidStack.load(registries, stream.get("fluid"));
            fluids.stream = fluid.isEmpty() ? null : fluid;
        } catch (RuntimeException e) {
            fluids.stream = null;
        }
        fluids.streamLength = stream.getFloat("length");
    }

    private static boolean hasOwnFlow(TapBlockEntity tap) {
        if (FLOW == null) return true;
        try {
            return FLOW.get(tap) != null;
        } catch (IllegalAccessException e) {
            return true;
        }
    }

    @Nullable
    private static Field findFlowField() {
        try {
            Field field = TapBlockEntity.class.getDeclaredField("flow");
            field.setAccessible(true);
            return field;
        } catch (ReflectiveOperationException | RuntimeException e) {
            SpicedCider.LOGGER.warn("No Man's Land tap flow field not found; Supplementaries tap sources disabled", e);
            return null;
        }
    }
}
