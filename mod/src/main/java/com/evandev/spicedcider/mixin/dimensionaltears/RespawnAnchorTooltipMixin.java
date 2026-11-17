package com.evandev.spicedcider.mixin.dimensionaltears;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@IfModLoaded("dimensional_tears")
@Mixin(Item.class)
public class RespawnAnchorTooltipMixin {

    @Inject(method = "appendHoverText", at = @At("TAIL"))
    private void spicedcider$dropAnchorTearsLine(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag, CallbackInfo ci) {
        if (!stack.is(Items.RESPAWN_ANCHOR)) return;
        tooltip.removeIf(line -> line.getContents() instanceof TranslatableContents contents
                && contents.getKey().equals("tooltip.dimensional_tears.crying_obsidian"));
    }
}
