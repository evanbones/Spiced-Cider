package com.evandev.spicedcider.compat.cavernsandchasms;

import com.teamabnormals.caverns_and_chasms.common.item.MonocleItem;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.fml.ModList;

public class MonocleJade {
    private static Boolean loaded;

    public static boolean canSeeTooltip(LocalPlayer player) {
        if (player.isScoping()) return true;
        if (loaded == null) loaded = ModList.get().isLoaded("caverns_and_chasms");
        return loaded && Monocle.isUsing(player);
    }

    private static class Monocle {
        private static boolean isUsing(LocalPlayer player) {
            return MonocleItem.isWearingMonocle(player) || MonocleItem.isScopingMonocle(player);
        }
    }
}
