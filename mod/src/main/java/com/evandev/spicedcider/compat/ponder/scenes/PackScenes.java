package com.evandev.spicedcider.compat.ponder.scenes;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public class PackScenes {

    public static void tapping(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("tapping", "Collecting sap with Taps");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        BlockPos tap = util.grid().at(2, 2, 1);
        BlockPos basin = util.grid().at(2, 1, 1);
        scene.world().showSection(util.select().layersFrom(1).substract(util.select().position(tap)).substract(util.select().position(basin)), Direction.DOWN);
        scene.idle(15);

        scene.world().showSection(util.select().position(tap), Direction.SOUTH);
        scene.idle(10);
        scene.overlay().showOutline(PonderPalette.GREEN, "tap", util.select().position(tap), 80);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Taps draw sap out of tree trunks")
                .pointAt(util.vector().centerOf(tap))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showText(80)
                .colored(PonderPalette.RED)
                .text("The trunk has to belong to a natural tree, with its leaves still attached")
                .pointAt(util.vector().centerOf(2, 5, 2))
                .placeNearTarget();
        scene.idle(90);

        scene.world().showSection(util.select().position(basin), Direction.DOWN);
        scene.idle(10);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Place a Basin up to 3 blocks below the tap to catch what drips out")
                .pointAt(util.vector().centerOf(basin))
                .placeNearTarget();
        scene.idle(60);

        Fluid syrup = BuiltInRegistries.FLUID.get(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "maple_syrup"));
        for (int i = 1; i <= 3; i++) {
            drip(scene, util, tap);
            scene.idle(10);
            BasinScenes.setFluid(scene, basin, new FluidStack(syrup, i * 333));
            scene.idle(10);
        }
        scene.overlay().showText(80)
                .text("Over time the Basin slowly fills up")
                .pointAt(util.vector().centerOf(basin))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showText(90)
                .attachKeyFrame()
                .text("Maple gives Maple Syrup, Spruce and Pine give Resin, and Kapok gives Latex")
                .pointAt(util.vector().centerOf(2, 2, 2))
                .placeNearTarget();
        scene.idle(100);

        scene.overlay().showText(80)
                .text("Tapping a full Beehive or Bee Nest collects Honey")
                .pointAt(util.vector().centerOf(tap))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showText(80)
                .text("Mortars and Fluid Tanks can catch the drips as well")
                .pointAt(util.vector().centerOf(basin))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showControls(util.vector().centerOf(tap), Pointing.DOWN, 30).rightClick();
        scene.idle(7);
        scene.world().modifyBlock(tap, state -> withBoolean(state, "closed", true), false);
        scene.overlay().showText(70)
                .text("Right-click the tap to close or open it")
                .pointAt(util.vector().centerOf(tap))
                .placeNearTarget();
        scene.idle(80);
        scene.markAsFinished();
    }

    private static void drip(SceneBuilder scene, SceneBuildingUtil util, BlockPos tap) {
        Vec3 spout = util.vector().centerOf(tap).add(0, -0.4, -0.2);
        scene.effects().emitParticles(spout, scene.effects().simpleParticleEmitter(ParticleTypes.DRIPPING_HONEY, Vec3.ZERO), 1, 1);
    }

    private static BlockState withBoolean(BlockState state, String name, boolean value) {
        Property<?> property = state.getBlock().getStateDefinition().getProperty(name);
        if (property instanceof BooleanProperty bool) {
            return state.setValue(bool, value);
        }
        return state;
    }

    public static void cookingPot(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("cooking_pot_heat", "Heating the Cooking Pot");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        scene.world().showSection(util.select().fromTo(0, 1, 2, 0, 2, 2), Direction.DOWN);
        scene.idle(10);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("The Cooking Pot only cooks while there is heat right below it")
                .pointAt(util.vector().centerOf(0, 2, 2))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showText(80)
                .text("Stoves, Campfires, Fire, Lava and Magma Blocks all give heat")
                .pointAt(util.vector().centerOf(0, 1, 2))
                .placeNearTarget();
        scene.idle(90);

        scene.world().showSection(util.select().fromTo(2, 1, 2, 2, 2, 2), Direction.DOWN);
        scene.idle(10);
        scene.effects().emitParticles(util.vector().topOf(2, 1, 2),
                scene.effects().simpleParticleEmitter(ParticleTypes.CAMPFIRE_COSY_SMOKE, util.vector().of(0, 0.05, 0)), 1, 2);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Over a Campfire or open fire, it rests on a tray")
                .pointAt(util.vector().centerOf(2, 2, 2))
                .placeNearTarget();
        scene.idle(90);

        scene.world().showSection(util.select().fromTo(4, 1, 2, 4, 3, 2), Direction.DOWN);
        scene.idle(10);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("A Hopper between the heat and the pot passes the heat up")
                .pointAt(util.vector().centerOf(4, 2, 2))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showText(70)
                .colored(PonderPalette.BLUE)
                .text("The Skillet uses heat the same way")
                .pointAt(util.vector().centerOf(4, 3, 2))
                .placeNearTarget();
        scene.idle(80);
        scene.markAsFinished();
    }



    public static void weepingObsidian(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("weeping_obsidian", "Collecting Dimensional Tears");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        BlockPos obsidian = util.grid().at(2, 4, 2);
        BlockPos basin = util.grid().at(2, 1, 2);
        scene.world().showSection(util.select().position(obsidian), Direction.DOWN);
        scene.idle(15);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Crying Obsidian slowly weeps Dimensional Tears")
                .pointAt(util.vector().centerOf(obsidian))
                .placeNearTarget();
        scene.idle(90);

        scene.world().showSection(util.select().position(basin), Direction.DOWN);
        scene.idle(10);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("A Basin below it catches the Tears, as long as there is only air in between")
                .pointAt(util.vector().centerOf(basin))
                .placeNearTarget();
        scene.idle(60);

        Fluid tears = BuiltInRegistries.FLUID.get(ResourceLocation.fromNamespaceAndPath("dimensional_tears", "dimensional_tears"));
        for (int i = 1; i <= 3; i++) {
            emitDrip(scene, util, obsidian, ParticleTypes.DRIPPING_OBSIDIAN_TEAR);
            scene.idle(15);
            BasinScenes.setFluid(scene, basin, new FluidStack(tears, i * 250));
            scene.idle(10);
        }
        scene.overlay().showText(80)
                .text("Collect them with a Glass Bottle or a Bucket")
                .pointAt(util.vector().centerOf(basin))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showText(80)
                .text("Every kind of Crying Obsidian weeps, including Bricks, Pillars and Polished")
                .pointAt(util.vector().centerOf(obsidian))
                .placeNearTarget();
        scene.idle(90);
        scene.markAsFinished();
    }



    private static void emitDrip(SceneBuilder scene, SceneBuildingUtil util, BlockPos from, ParticleOptions particle) {
        Vec3 tip = util.vector().centerOf(from).add(0, -0.55, 0);
        scene.effects().emitParticles(tip, scene.effects().simpleParticleEmitter(particle, Vec3.ZERO), 1, 1);
    }

    private static BlockState withFacing(BlockState state, Direction direction) {
        Property<?> property = state.getBlock().getStateDefinition().getProperty("facing");
        if (property instanceof DirectionProperty facing && facing.getPossibleValues().contains(direction)) {
            return state.setValue(facing, direction);
        }
        return state;
    }
}
