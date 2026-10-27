package com.evandev.spicedcider.mixin.snowyspirit;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.mehvahdjukaar.snowyspirit.reg.ClientRegistry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@IfModLoaded("snowyspirit")
@Mixin(ClientRegistry.class)
public abstract class GingerbreadCookieShapeMixin {

    @WrapMethod(method = "lambda$setup$0")
    private static float stableShapeForFakeItems(ItemStack stack, ClientLevel level, LivingEntity entity, int seed, Operation<Float> original) {
        if (entity == null && seed == 0) return 0.0F;
        return original.call(stack, level, entity, seed);
    }
}
