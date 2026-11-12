package com.evandev.spicedcider.mixin.fieldguide;

import cc.cassian.slime.api.SlimeColor;
import cc.cassian.slime.util.SlimeHelpers;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@IfModLoaded("fieldguide")
@IfModLoaded("slime_time")
@Mixin(targets = "com.evandev.fieldguide.client.manager.ClientLootManager", remap = false)
public class ClientLootManagerSlimeColorMixin {

    @ModifyReturnValue(method = "getDrops(Ljava/lang/Object;Ljava/lang/String;)Ljava/util/List;", at = @At("RETURN"))
    private List<ItemStack> spicedcider$colorSlimeballs(List<ItemStack> drops, Object entry, String variantId) {
        if (entry != EntityType.SLIME || variantId == null) return drops;
        SlimeColor color = SlimeColor.byName(variantId, null);
        if (color == null || SlimeColor.isDefault(color)) return drops;

        List<ItemStack> colored = new ArrayList<>(drops.size());
        for (ItemStack stack : drops) {
            colored.add(stack.is(Items.SLIME_BALL) ? SlimeHelpers.dye(stack.copy(), color) : stack);
        }
        return colored;
    }
}
