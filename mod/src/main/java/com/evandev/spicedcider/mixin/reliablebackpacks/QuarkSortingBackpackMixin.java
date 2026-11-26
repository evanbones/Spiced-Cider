package com.evandev.spicedcider.mixin.reliablebackpacks;

import com.evandev.spicedcider.compat.reliablebackpacks.WornBackpackSlots;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.base.handler.SortingHandler;
import org.violetmoon.quark.content.management.module.InventorySortingModule;

import java.util.ArrayList;
import java.util.List;

@IfModLoaded("quark")
@Mixin(value = SortingHandler.class, remap = false)
public class QuarkSortingBackpackMixin {

    @Inject(method = {"sortContainer(Lnet/minecraft/world/entity/player/Player;Z)V", "sortInventory(Lnet/minecraft/world/entity/player/Player;Z)V"}, at = @At("TAIL"), require = 1)
    private static void spicedcider$sortWornBackpack(Player player, boolean forcePlayer, CallbackInfo ci) {
        if (!Quark.ZETA.modules.isEnabled(InventorySortingModule.class)) return;
        AbstractContainerMenu shown = player.containerMenu == null ? player.inventoryMenu : player.containerMenu;
        if (shown != player.inventoryMenu && !forcePlayer) return;
        if (!WornBackpackSlots.isOpen(shown)) return;
        Container backpack = shown.slots.get(WornBackpackSlots.first(shown)).container;
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < backpack.getContainerSize(); i++) {
            ItemStack stack = backpack.getItem(i);
            if (stack.isEmpty()) continue;
            ItemStack remaining = stack.copy();
            for (ItemStack merged : stacks) {
                if (remaining.isEmpty()) break;
                if (!ItemStack.isSameItemSameComponents(merged, remaining)) continue;
                int moved = Math.min(remaining.getCount(), merged.getMaxStackSize() - merged.getCount());
                merged.grow(moved);
                remaining.shrink(moved);
            }
            if (!remaining.isEmpty()) stacks.add(remaining);
        }
        SortingHandler.sortStackList(stacks);
        for (int i = 0; i < backpack.getContainerSize(); i++) {
            backpack.setItem(i, i < stacks.size() ? stacks.get(i) : ItemStack.EMPTY);
        }
    }
}
