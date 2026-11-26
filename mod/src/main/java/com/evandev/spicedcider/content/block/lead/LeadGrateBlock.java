package com.evandev.spicedcider.content.block.lead;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.WaterloggedTransparentBlock;

public class LeadGrateBlock extends WaterloggedTransparentBlock {
    public static final MapCodec<LeadGrateBlock> CODEC = simpleCodec(LeadGrateBlock::new);

    public LeadGrateBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends WaterloggedTransparentBlock> codec() {
        return CODEC;
    }
}
