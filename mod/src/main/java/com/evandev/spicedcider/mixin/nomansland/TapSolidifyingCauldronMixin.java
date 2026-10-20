package com.evandev.spicedcider.mixin.nomansland;

import com.farcr.nomansland.common.block.tap.TapBlock;
import com.farcr.nomansland.common.block.tap.TapInteraction;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;

@IfModLoaded("nomansland")
@Mixin(value = TapBlock.class, remap = false)
public class TapSolidifyingCauldronMixin {

    @WrapMethod(method = "tryFill")
    private static void spicedcider$fillSolidifyingCauldron(BlockState cauldronState, BlockPos cauldronPos, Level level, TapInteraction interaction, Operation<Void> original) {
        Block cauldron = interaction.cauldron();
        Property<?> property = cauldron.getStateDefinition().getProperty("solid");
        if (!(property instanceof IntegerProperty solid)) {
            original.call(cauldronState, cauldronPos, level, interaction);
            return;
        }

        BlockState newState;
        if (cauldronState.is(cauldron)) {
            int value = cauldronState.getValue(solid);
            int max = solid.getPossibleValues().stream().max(Integer::compare).orElse(value);
            if (value >= max) return;
            newState = cauldronState.setValue(solid, value + 1);
        } else if (cauldronState.is(Blocks.CAULDRON)) {
            newState = cauldron.defaultBlockState();
        } else {
            return;
        }
        level.setBlockAndUpdate(cauldronPos, newState);
        level.gameEvent(GameEvent.BLOCK_CHANGE, cauldronPos, GameEvent.Context.of(newState));
    }
}
