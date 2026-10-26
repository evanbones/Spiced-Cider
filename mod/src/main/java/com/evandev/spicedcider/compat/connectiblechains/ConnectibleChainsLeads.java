package com.evandev.spicedcider.compat.connectiblechains;

import com.evandev.connectiblechains.entity.ChainKnotEntity;
import com.evandev.connectiblechains.entity.Chainable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

public final class ConnectibleChainsLeads {
    public static boolean isLeadKnot(Entity entity) {
        return entity instanceof ChainKnotEntity knot && knot.getSourceItem() == Items.LEAD;
    }

    @Nullable
    public static BlockPos heldLeadKnot(Player player, boolean detach) {
        for (ChainKnotEntity knot : player.level().getEntitiesOfClass(ChainKnotEntity.class, player.getBoundingBox().inflate(16.0))) {
            for (Chainable.ChainData data : knot.getChainDataSet()) {
                if (data.sourceItem == Items.LEAD && data.getResolvedHolder() == player) {
                    BlockPos pos = knot.blockPosition();
                    if (detach) knot.detachChainWithoutDrop(data);
                    return pos;
                }
            }
        }
        return null;
    }
}
