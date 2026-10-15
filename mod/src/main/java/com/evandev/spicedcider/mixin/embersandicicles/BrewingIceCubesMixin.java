package com.evandev.spicedcider.mixin.embersandicicles;

import com.evandev.spicedcider.compat.embersandicicles.IceCubes;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.item.Item;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("embers_and_icicles")
@Mixin(targets = "cc.cassian.effects.neoforge.NeoForgeEntrypoint", remap = false)
public class BrewingIceCubesMixin {

    @WrapOperation(
            method = "register(Lnet/neoforged/neoforge/event/brewing/RegisterBrewingRecipesEvent;)V",
            at = @At(
                    value = "FIELD",
                    target = "Lcc/cassian/effects/registry/ModItems;ICE_BALL:Lnet/minecraft/world/item/Item;",
                    opcode = Opcodes.GETSTATIC
            )
    )
    private static Item spicedcider$useIceCubes(Operation<Item> original) {
        return IceCubes.itemOr(original.call());
    }
}
