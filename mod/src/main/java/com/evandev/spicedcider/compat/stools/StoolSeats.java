package com.evandev.spicedcider.compat.stools;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.Nullable;

public class StoolSeats {
    private static Boolean modsPresent;

    public static boolean enabled() {
        if (modsPresent == null) {
            modsPresent = ModList.get().isLoaded("quark") && ModList.get().isLoaded("create");
        }
        return modsPresent && SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.quarkStoolSeats, false);
    }

    public static boolean isStool(BlockGetter level, BlockPos pos) {
        return enabled() && QuarkStools.isStool(level.getBlockState(pos));
    }

    @Nullable
    public static InteractionResult use(BlockState state, Level level, BlockPos pos, Player player) {
        if (!enabled() || player.isShiftKeyDown()) return null;
        return CreateStoolSeating.use(state, level, pos, player);
    }

    public static boolean landOn(Entity entity) {
        return enabled() && CreateStoolSeating.landOn(entity);
    }
}
