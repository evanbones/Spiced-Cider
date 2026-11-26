package com.evandev.spicedcider.content.block.glass;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.IronBarsBlock;

public class LeadedGlassPaneBlock extends IronBarsBlock {
    public static final MapCodec<LeadedGlassPaneBlock> CODEC = simpleCodec(LeadedGlassPaneBlock::new);

    public LeadedGlassPaneBlock(Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<? extends LeadedGlassPaneBlock> codec() {
        return CODEC;
    }
}
