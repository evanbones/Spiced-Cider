package com.evandev.spicedcider.mixin.minecraft;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FireBlock.class)
public abstract class FireBlockMixin {

    @Shadow
    @Final
    private Object2IntMap<Block> igniteOdds;

    @Shadow
    @Final
    private Object2IntMap<Block> burnOdds;

    /**
     * @author Evan
     * @reason Prevent race conditions and map corruption during setup (fucken bountiful fares)
     */
    @Overwrite
    public void setFlammable(Block block, int flameOdds, int burnOdds) {
        synchronized (this) {
            if (block == Blocks.AIR) {
                throw new IllegalArgumentException("Tried to set air on fire... This is bad.");
            } else {
                this.igniteOdds.put(block, flameOdds);
                this.burnOdds.put(block, burnOdds);
            }
        }
    }
}
