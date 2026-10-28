package com.evandev.spicedcider.mixin.leaks;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Function;

@IfModLoaded("modefite")
@Mixin(targets = "timmychips.modefiteitemdefinitions.property.resolver.ResolveRecursive", remap = false)
public interface ModefiteResolveRecursiveAccessor {
    @Accessor("MODEL_PROVIDER")
    static void spicedcider$setModelProvider(Function<ResourceLocation, BakedModel> provider) {
        throw new AssertionError();
    }
}
