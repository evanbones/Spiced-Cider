package com.evandev.spicedcider.interfaces;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;

public interface ITrimStylingMenu {
    int ADDITION_SLOT = 38;

    Container spicedcider$addition();

    void spicedcider$beforeResultTaken(Player player);

    void spicedcider$afterResultTaken();
}
