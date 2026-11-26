package com.evandev.spicedcider.mixin.quark;

import com.evandev.spicedcider.compat.quark.VaultSorting;
import com.evandev.spicedcider.content.vault.VaultMenu;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.base.handler.SortingHandler;
import org.violetmoon.quark.content.management.module.InventorySortingModule;

@IfModLoaded("quark")
@Mixin(value = SortingHandler.class, remap = false)
public class SortingVaultMixin {

    @Inject(method = {"sortContainer(Lnet/minecraft/world/entity/player/Player;Z)V", "sortInventory(Lnet/minecraft/world/entity/player/Player;Z)V"}, at = @At("HEAD"), cancellable = true, require = 1)
    private static void spicedcider$sortVault(Player player, boolean forcePlayer, CallbackInfo ci) {
        if (forcePlayer || !(player.containerMenu instanceof VaultMenu menu)) return;
        if (!Quark.ZETA.modules.isEnabled(InventorySortingModule.class)) return;
        VaultSorting.sort(player, menu);
        ci.cancel();
    }
}
