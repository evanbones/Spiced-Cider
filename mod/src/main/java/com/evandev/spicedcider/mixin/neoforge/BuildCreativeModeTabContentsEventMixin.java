package com.evandev.spicedcider.mixin.neoforge;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.InsertableLinkedOpenCustomHashSet;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BuildCreativeModeTabContentsEvent.class, remap = false)
public abstract class BuildCreativeModeTabContentsEventMixin {

    @Shadow
    @Final
    private InsertableLinkedOpenCustomHashSet<ItemStack> parentEntries;

    @Shadow
    @Final
    private InsertableLinkedOpenCustomHashSet<ItemStack> searchEntries;

    @Inject(method = "accept", at = @At("HEAD"), cancellable = true)
    private void spicedcider$skipDuplicateEntries(ItemStack newEntry, CreativeModeTab.TabVisibility visibility, CallbackInfo ci) {
        if (newEntry.getCount() != 1) return;
        boolean parent = visibility != CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY;
        boolean search = visibility != CreativeModeTab.TabVisibility.PARENT_TAB_ONLY;
        boolean parentDuplicate = parent && parentEntries.contains(newEntry);
        boolean searchDuplicate = search && searchEntries.contains(newEntry);
        if (!parentDuplicate && !searchDuplicate) return;
        ci.cancel();
        if (parent && !parentDuplicate) parentEntries.add(newEntry);
        if (search && !searchDuplicate) searchEntries.add(newEntry);
    }
}
