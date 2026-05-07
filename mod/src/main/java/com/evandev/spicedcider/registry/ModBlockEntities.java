package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.content.block.entity.WorkstoneBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, SpicedCider.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WorkstoneBlockEntity>> WORKSTONE =
            BLOCK_ENTITIES.register("workstone", () -> BlockEntityType.Builder.of(WorkstoneBlockEntity::new, ModBlocks.WORKSTONE.get()).build(null));
}