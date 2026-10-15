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
@Mixin(targets = "cc.cassian.effects.entity.Frostbite", remap = false)
public class FrostbiteIceCubesMixin {

    @WrapOperation(
            method = {"populateDefaultEquipmentSlots", "performRangedAttack", "shouldDoRangedAttack"},
            at = @At(
                    value = "FIELD",
                    target = "Lcc/cassian/effects/registry/ModItems;ICE_BALL:Lnet/minecraft/world/item/Item;",
                    opcode = Opcodes.GETSTATIC
            )
    )
    private Item spicedcider$useIceCubes(Operation<Item> original) {
        return IceCubes.itemOr(original.call());
    }
}
