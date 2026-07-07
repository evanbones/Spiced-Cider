package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.entities.projectiles.CobwebProjectileEntity;
import com.evandev.spicedcider.entities.projectiles.GrapplingHookEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntityTypes {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, SpicedCider.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpicedCider.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<CobwebProjectileEntity>> COBWEB_PROJECTILE = registerEntityWithoutEgg("cobweb_projectile", () ->
            EntityType.Builder.<CobwebProjectileEntity>of(CobwebProjectileEntity::new, MobCategory.MISC)
                    .sized(0.3125F, 0.3125F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "cobweb_projectile").toString())
    );

    public static final DeferredHolder<EntityType<?>, EntityType<GrapplingHookEntity>> GRAPPLING_HOOK = registerEntityWithoutEgg("grappling_hook", () ->
            EntityType.Builder.<GrapplingHookEntity>of(GrapplingHookEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(5)
                    .noSave()
                    .noSummon()
                    .build(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "grappling_hook").toString())
    );

    private static <T extends Mob> DeferredHolder<EntityType<?>, EntityType<T>> registerEntity(String key, Supplier<EntityType<T>> sup, int primaryColor, int secondaryColor) {
        DeferredHolder<EntityType<?>, EntityType<T>> entityType = ENTITY_TYPES.register(key, sup);

        ITEMS.register(key + "_spawn_egg", () -> new DeferredSpawnEggItem(entityType, primaryColor, secondaryColor, new Item.Properties()));

        return entityType;
    }

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> registerEntityWithoutEgg(String key, Supplier<EntityType<T>> sup) {
        return ENTITY_TYPES.register(key, sup);
    }
}