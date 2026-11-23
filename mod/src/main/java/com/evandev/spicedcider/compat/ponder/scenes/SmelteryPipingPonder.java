package com.evandev.spicedcider.compat.ponder.scenes;

import com.simibubi.create.content.fluids.FluidFX;
import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.function.BiConsumer;
import java.util.function.IntConsumer;

final class SmelteryPipingPonder {

    static void show(SceneBuilder builder, SceneBuildingUtil util, BlockPos mould, Fluid metal, int metalLeft,
                     IntConsumer setMetal, BiConsumer<FluidStack, ItemStack> setMould, ItemStack casting) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        BlockPos pump = util.grid().at(4, 2, 3);
        BlockPos spout = util.grid().at(4, 3, 2);
        Selection pipes = util.select().fromTo(4, 1, 3, 4, 4, 3).add(util.select().position(4, 4, 2));
        Selection spoutSel = util.select().position(spout);

        scene.world().showSection(pipes, Direction.WEST);
        scene.idle(10);
        scene.world().setKineticSpeed(util.select().position(pump), 64);
        scene.overlay().showText(90)
                .attachKeyFrame()
                .text("Pipes connect to any side. A Mechanical Pump can push Lava or molten metal in, or pull molten metal out")
                .pointAt(util.vector().centerOf(pump))
                .placeNearTarget();
        scene.idle(100);

        scene.world().showSection(util.select().position(mould), Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(spoutSel, Direction.DOWN);
        scene.idle(15);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Pump the metal into a Spout above a Mould to cast automatically")
                .pointAt(util.vector().blockSurface(spout, Direction.EAST))
                .placeNearTarget();
        scene.world().modifyBlockEntityNBT(spoutSel, SpoutBlockEntity.class, nbt -> nbt.putInt("ProcessingTicks", 20));
        var particle = FluidFX.getFluidParticle(new FluidStack(metal, 1000));
        for (int i = 1; i <= 4; i++) {
            scene.effects().emitParticles(util.vector().topOf(mould).add(0, 1 / 16f, 0), scene.effects().simpleParticleEmitter(particle, Vec3.ZERO), 1, 1);
            setMould.accept(new FluidStack(metal, Math.min(250, i * 70)), ItemStack.EMPTY);
            setMetal.accept(metalLeft - Math.min(250, i * 70));
            scene.idle(5);
        }
        scene.idle(70);
        setMould.accept(FluidStack.EMPTY, casting);
        scene.overlay().showText(80)
                .text("Once the casting cools, a Hopper or Funnel underneath can carry it away")
                .pointAt(util.vector().topOf(mould))
                .placeNearTarget();
        scene.idle(90);
    }
}
