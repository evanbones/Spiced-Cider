package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.content.block.entity.TubewormBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SpicedCider.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TubewormBlockEntity>> TUBEWORM = BLOCK_ENTITIES.register("tubeworm",
            () -> BlockEntityType.Builder.of(TubewormBlockEntity::new, ModBlocks.TUBEWORM.get()).build(null));
}
