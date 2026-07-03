package com.evandev.spicedcider.mixin.environmental;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.teamabnormals.blueprint.common.levelgen.feature.BlueprintTreeFeature;
import com.teamabnormals.environmental.common.levelgen.feature.WisteriaTreeFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WisteriaTreeFeature.class)
public class WisteriaTreeFeatureMixin {

    @Inject(
            method = "createLeaves",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void spicedcider$replaceLeaves(BlockPos pos, Direction direction, RandomSource random, TreeConfiguration config, BlueprintTreeFeature.TreeInfo info, CallbackInfo ci) {
        if (!SpicedCiderConfig.COMMON.wisteriaLeafDensityFix.get()) return;

        for (int x = -1; x <= 1; ++x) {
            for (int z = -1; z <= 1; ++z) {
                int i = -1 - (random.nextInt(3) == 0 ? 1 : 0);

                for (int y = 1; y >= i; --y) {
                    if (y <= 0 || x == 0 || z == 0 || random.nextInt(3) == 0) {
                        BlockPos blockpos = pos.offset(x, y, z);
                        BlockState coloredLeaves = config.foliageProvider.getState(random, blockpos);
                        info.addFoliage(blockpos, coloredLeaves);
                    }
                }
            }
        }

        ci.cancel();
    }
}