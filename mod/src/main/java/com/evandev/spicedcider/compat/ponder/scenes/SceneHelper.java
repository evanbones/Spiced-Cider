package com.evandev.spicedcider.compat.ponder.scenes;

import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.EntityElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;

final class SceneHelper {

    static ItemStack item(String id) {
        return BuiltInRegistries.ITEM.get(ResourceLocation.parse(id)).getDefaultInstance();
    }

    static BlockState block(String id) {
        return BuiltInRegistries.BLOCK.get(ResourceLocation.parse(id)).defaultBlockState();
    }

    static ElementLink<EntityElement> spawn(SceneBuilder scene, EntityType<?> type, Vec3 pos, float yaw, Consumer<Entity> setup) {
        return scene.world().createEntity(level -> {
            Entity entity = type.create(level);
            entity.setPos(pos);
            entity.xo = pos.x;
            entity.yo = pos.y;
            entity.zo = pos.z;
            face(entity, yaw);
            setup.accept(entity);
            return entity;
        });
    }

    static ElementLink<EntityElement> spawn(SceneBuilder scene, String type, Vec3 pos, float yaw, Consumer<Entity> setup) {
        return spawn(scene, BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(type)), pos, yaw, setup);
    }

    static void walk(SceneBuilder scene, ElementLink<EntityElement> link, Vec3 from, Vec3 to, int ticks) {
        float yaw = (float) (Mth.atan2(to.z - from.z, to.x - from.x) * Mth.RAD_TO_DEG) - 90;
        scene.world().modifyEntity(link, entity -> face(entity, yaw));
        for (int i = 1; i <= ticks; i++) {
            Vec3 pos = from.lerp(to, i / (double) ticks);
            scene.world().modifyEntity(link, entity -> {
                entity.setPos(pos);
                if (entity instanceof LivingEntity living) {
                    living.walkAnimation.update(0.6F, 0.4F);
                }
            });
            scene.idle(1);
        }
    }

    static void face(Entity entity, float yaw) {
        entity.setYRot(yaw);
        entity.yRotO = yaw;
        if (entity instanceof LivingEntity living) {
            living.yBodyRot = yaw;
            living.yBodyRotO = yaw;
            living.yHeadRot = yaw;
            living.yHeadRotO = yaw;
        }
    }
}
