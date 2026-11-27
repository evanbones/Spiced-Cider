package com.evandev.spicedcider.mixin.wherewindsblow;

import com.jvn.wherewindsblow.client.lantern.LanternSway;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.LanternBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("where_winds_blow")
@Mixin(value = LanternSway.class, remap = false)
public class AllLanternModelsSwayMixin {

    @WrapOperation(method = "subjectFor", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/ResourceLocation;equals(Ljava/lang/Object;)Z", ordinal = 0))
    private static boolean spicedcider$anyLanternModel(ResourceLocation id, Object lantern, Operation<Boolean> original) {
        return original.call(id, lantern) || BuiltInRegistries.BLOCK.get(id) instanceof LanternBlock;
    }
}
