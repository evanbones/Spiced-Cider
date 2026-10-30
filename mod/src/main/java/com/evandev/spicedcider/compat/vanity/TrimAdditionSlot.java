package com.evandev.spicedcider.compat.vanity;

import com.evandev.spicedcider.content.smithing.TrimStyling;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TrimAdditionSlot extends Slot {
    private final Player player;

    public TrimAdditionSlot(Container container, Player player, int x, int y) {
        super(container, 0, x, y);
        this.player = player;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return TrimStyling.isAddition(this.player.level().registryAccess(), stack);
    }
}
