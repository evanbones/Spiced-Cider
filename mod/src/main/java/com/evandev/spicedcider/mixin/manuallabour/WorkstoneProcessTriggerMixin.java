package com.evandev.spicedcider.mixin.manuallabour;

import com.evandev.spicedcider.registry.ModCriteria;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@IfModLoaded("manual_labour")
@Mixin(targets = "com.evandev.manual_labour.content.block.entity.WorkstoneBlockEntity", remap = false)
public abstract class WorkstoneProcessTriggerMixin {
    @Shadow
    public abstract ItemStack getStoredItem();

    @WrapMethod(method = "processStoredItemUsingTool")
    private boolean spicedcider$triggerAdvancement(ItemStack tool, Player player, Operation<Boolean> original) {
        ItemStack input = getStoredItem().copyWithCount(1);
        ItemStack toolCopy = tool.copy();
        boolean processed = original.call(tool, player);
        if (processed && player instanceof ServerPlayer serverPlayer) {
            ModCriteria.WORKSTONE_PROCESS.get().trigger(serverPlayer, input, toolCopy);
        }
        return processed;
    }
}
