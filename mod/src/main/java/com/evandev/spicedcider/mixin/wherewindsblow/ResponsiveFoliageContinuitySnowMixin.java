package com.evandev.spicedcider.mixin.wherewindsblow;

import com.jvn.wherewindsblow.client.foliage.ResponsiveFoliage;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.resources.model.BakedModel;
import org.spongepowered.asm.mixin.Mixin;

import java.lang.reflect.Field;

@IfModLoaded("where_winds_blow")
@IfModLoaded("snowrealmagic")
@Mixin(value = ResponsiveFoliage.class, remap = false)
public class ResponsiveFoliageContinuitySnowMixin {

    @WrapMethod(method = "wrapModel")
    private static BakedModel spicedcider$keepSnowModelsUnderContinuity(BakedModel model, Operation<BakedModel> original) {
        BakedModel inner = model;
        while (inner != null && inner.getClass().getName().startsWith("me.pepperbell.continuity.")) {
            inner = spicedcider$wrapped(inner);
        }
        if (inner != model && inner != null && inner.getClass().getName().startsWith("snownee.snow.client.model.")) {
            original.call(inner);
            return model;
        }
        return original.call(model);
    }

    private static BakedModel spicedcider$wrapped(BakedModel model) {
        for (Class<?> type = model.getClass(); type != null; type = type.getSuperclass()) {
            try {
                Field field = type.getDeclaredField("wrapped");
                field.setAccessible(true);
                return field.get(model) instanceof BakedModel wrapped ? wrapped : null;
            } catch (NoSuchFieldException ignored) {
            } catch (ReflectiveOperationException | RuntimeException e) {
                return null;
            }
        }
        return null;
    }
}
