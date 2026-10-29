package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.entities.projectiles.CobwebProjectileEntity;
import com.evandev.spicedcider.compat.tide.TideCompat;
import com.evandev.spicedcider.entities.balloon.BalloonEntity;
import com.evandev.spicedcider.entities.projectiles.GrapplingHookEntity;
import com.evandev.spicedcider.entities.sputter.SputterEntity;
import com.evandev.spicedcider.entities.strawgolem.StrawGolem;
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

import org.jetbrains.annotations.Nullable;

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

    public static final DeferredHolder<EntityType<?>, EntityType<BalloonEntity>> BALLOON = registerEntityWithoutEgg("balloon", () ->
            EntityType.Builder.<BalloonEntity>of(BalloonEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.625F)
                    .fireImmune()
                    .clientTrackingRange(10)
                    .updateInterval(3)
                    .build(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "balloon").toString())
    );

    public static final DeferredHolder<EntityType<?>, EntityType<SputterEntity>> SPUTTER = registerEntity("sputter", () ->
            EntityType.Builder.<SputterEntity>of(SputterEntity::new, MobCategory.MISC)
                    .sized(0.75F, 0.9375F)
                    .eyeHeight(0.609375F)
                    .clientTrackingRange(8)
                    .build(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "sputter").toString()),
            0x8C6239, 0xE38A1D
    );

    public static final DeferredHolder<EntityType<?>, EntityType<StrawGolem>> STRAW_GOLEM = registerEntity("straw_golem", () ->
            EntityType.Builder.<StrawGolem>of(StrawGolem::new, MobCategory.MISC)
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(8)
                    .build(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "straw_golem").toString()),
            0xC9A93A, 0xA4512B
    );

    @Nullable
    public static final DeferredHolder<EntityType<?>, EntityType<GrapplingHookEntity>> GRAPPLING_HOOK = TideCompat.isLoaded()
            ? registerEntityWithoutEgg("grappling_hook", () ->
            EntityType.Builder.<GrapplingHookEntity>of(GrapplingHookEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(5)
                    .noSave()
                    .noSummon()
                    .build(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "grappling_hook").toString())
    )
            : null;

    private static <T extends Mob> DeferredHolder<EntityType<?>, EntityType<T>> registerEntity(String key, Supplier<EntityType<T>> sup, int primaryColor, int secondaryColor) {
        DeferredHolder<EntityType<?>, EntityType<T>> entityType = ENTITY_TYPES.register(key, sup);

        ITEMS.register(key + "_spawn_egg", () -> new DeferredSpawnEggItem(entityType, primaryColor, secondaryColor, new Item.Properties()));

        return entityType;
    }

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> registerEntityWithoutEgg(String key, Supplier<EntityType<T>> sup) {
        return ENTITY_TYPES.register(key, sup);
    }
}