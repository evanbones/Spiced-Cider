package com.evandev.spicedcider.compat.horseman;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class TranslucentMounts {
    public record Deferred(Entity entity, double camX, double camY, double camZ, float partialTick) {
    }

    private static MethodHandle getAlpha;
    private static boolean lookedUp;
    private static Deferred deferred;

    public static boolean defer(Entity entity, double camX, double camY, double camZ, float partialTick) {
        if (!(entity instanceof AbstractHorse horse) || alpha(horse) >= 1.0F) {
            return false;
        }
        deferred = new Deferred(entity, camX, camY, camZ, partialTick);
        return true;
    }

    public static Deferred take() {
        Deferred d = deferred;
        deferred = null;
        return d;
    }

    private static float alpha(LivingEntity entity) {
        if (!lookedUp) {
            lookedUp = true;
            try {
                getAlpha = MethodHandles.publicLookup().findStatic(Class.forName("io.github.mortuusars.horseman.client.HorseRenderUtils"),
                        "getAlpha", MethodType.methodType(float.class, LivingEntity.class));
            } catch (ReflectiveOperationException ignored) {
            }
        }
        if (getAlpha == null) {
            return 1.0F;
        }
        try {
            return (float) getAlpha.invokeExact(entity);
        } catch (Throwable t) {
            return 1.0F;
        }
    }
}
