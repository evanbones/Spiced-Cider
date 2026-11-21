package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.content.block.entity.GraveBlockEntity;
import com.evandev.spicedcider.content.block.entity.TubewormBlockEntity;
import com.evandev.spicedcider.content.casting.CrucibleBlockEntity;
import com.evandev.spicedcider.content.casting.MouldBlockEntity;
import com.evandev.spicedcider.content.casting.smeltery.SmelteryBlockEntity;
import com.evandev.spicedcider.content.fishtrap.FishTrapBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SpicedCider.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TubewormBlockEntity>> TUBEWORM = BLOCK_ENTITIES.register("tubeworm",
            () -> BlockEntityType.Builder.of(TubewormBlockEntity::new, ModBlocks.TUBEWORM.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GraveBlockEntity>> GRAVE = BLOCK_ENTITIES.register("grave",
            () -> BlockEntityType.Builder.of(GraveBlockEntity::new, ModBlocks.GRAVE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrucibleBlockEntity>> CRUCIBLE = BLOCK_ENTITIES.register("crucible",
            () -> BlockEntityType.Builder.of(CrucibleBlockEntity::new, ModBlocks.CRUCIBLE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MouldBlockEntity>> MOULD = BLOCK_ENTITIES.register("mould",
            () -> BlockEntityType.Builder.of(MouldBlockEntity::new, ModBlocks.MOULDS.values().stream().map(block -> (Block) block.get()).toArray(Block[]::new)).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SmelteryBlockEntity>> SMELTERY = BLOCK_ENTITIES.register("smeltery",
            () -> BlockEntityType.Builder.of(SmelteryBlockEntity::new, ModBlocks.SMELTERY.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FishTrapBlockEntity>> FISH_TRAP = BLOCK_ENTITIES.register("fish_trap",
            () -> BlockEntityType.Builder.of(FishTrapBlockEntity::new, ModBlocks.FISH_TRAP.get()).build(null));
}
