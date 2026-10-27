package com.evandev.spicedcider.compat.ponder.scenes;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public class PackScenes {

    public static void tapping(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("tapping", "Collecting sap with Taps");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        BlockPos tap = util.grid().at(2, 2, 1);
        BlockPos cauldron = util.grid().at(2, 1, 1);
        scene.world().showSection(util.select().layersFrom(1).substract(util.select().position(tap)).substract(util.select().position(cauldron)), Direction.DOWN);
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

        scene.world().showSection(util.select().position(cauldron), Direction.DOWN);
        scene.idle(10);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Place a Cauldron up to 3 blocks below the tap to catch what drips out")
                .pointAt(util.vector().centerOf(cauldron))
                .placeNearTarget();
        scene.idle(60);

        BlockState syrup = SceneHelper.block("nomansland:maple_syrup_cauldron");
        Property<?> level = syrup.getBlock().getStateDefinition().getProperty("level");
        for (int i = 1; i <= 4; i++) {
            drip(scene, util, tap);
            scene.idle(10);
            if (level instanceof IntegerProperty levels && levels.getPossibleValues().contains(i)) {
                scene.world().setBlock(cauldron, syrup.setValue(levels, i), false);
            }
            scene.idle(10);
        }
        scene.overlay().showText(80)
                .text("Over time the cauldron slowly fills up")
                .pointAt(util.vector().centerOf(cauldron))
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
                .text("Mortars and Basins can catch the drips as well")
                .pointAt(util.vector().centerOf(cauldron))
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
}
