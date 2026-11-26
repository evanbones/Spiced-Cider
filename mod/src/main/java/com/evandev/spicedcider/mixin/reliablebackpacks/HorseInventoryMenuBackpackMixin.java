package com.evandev.spicedcider.mixin.reliablebackpacks;

import com.evandev.spicedcider.compat.reliablebackpacks.WornBackpackContainer;
import com.evandev.spicedcider.compat.reliablebackpacks.WornBackpackSlots;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.Container;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.HorseInventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("reliable_backpacks")
@Mixin(HorseInventoryMenu.class)
public abstract class HorseInventoryMenuBackpackMixin extends AbstractContainerMenu {
    @Shadow
    @Final
    private Container horseContainer;

    protected HorseInventoryMenuBackpackMixin(@Nullable MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void spicedcider$addBackpackSlots(int containerId, Inventory inventory, Container horseContainer, AbstractHorse horse, int columns, CallbackInfo ci) {
        if ((Object) this.getClass() != HorseInventoryMenu.class || !WornBackpackContainer.enabled()) return;
        WornBackpackSlots.addTo(inventory.player, this::addSlot);
    }

    @WrapMethod(method = "quickMoveStack")
    private ItemStack spicedcider$quickMoveWithBackpack(Player player, int index, Operation<ItemStack> original) {
        if (WornBackpackSlots.first(this) < 0) return original.call(player, index);
        if (WornBackpackSlots.isBackpackSlot(this, index)) {
            return WornBackpackSlots.moveFromBackpack(this, player, index, this::moveItemStackTo);
        }
        if (index < this.horseContainer.getContainerSize() + 1) {
            return WornBackpackSlots.moveIntoPlayer(this, player, index, this::moveItemStackTo);
        }
        return original.call(player, index);
    }
}
