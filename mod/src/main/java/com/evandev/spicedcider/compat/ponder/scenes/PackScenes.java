package com.evandev.spicedcider.compat.ponder.scenes;

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

    public static void dripstoneFarm(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("dripstone_farm", "Collecting fluids with Pointed Dripstone");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        BlockPos waterTip = util.grid().at(1, 2, 1);
        BlockPos lavaTip = util.grid().at(3, 2, 1);
        BlockPos waterCauldron = util.grid().at(1, 1, 1);
        BlockPos lavaCauldron = util.grid().at(3, 1, 1);
        scene.world().showSection(util.select().fromTo(1, 2, 1, 1, 3, 1).add(util.select().fromTo(3, 2, 1, 3, 3, 1)), Direction.DOWN);
        scene.idle(15);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Pointed Dripstone hanging under a block drips whatever fluid sits on top of that block")
                .pointAt(util.vector().centerOf(waterTip))
                .placeNearTarget();
        scene.idle(90);

        scene.world().showSection(util.select().position(1, 4, 1).add(util.select().position(3, 4, 1)), Direction.DOWN);
        scene.world().showSection(util.select().position(waterCauldron).add(util.select().position(lavaCauldron)), Direction.DOWN);
        scene.idle(15);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Place a Cauldron below the tip to catch the drips")
                .pointAt(util.vector().centerOf(waterCauldron))
                .placeNearTarget();
        scene.idle(60);

        BlockState water = SceneHelper.block("minecraft:water_cauldron");
        Property<?> waterLevel = water.getBlock().getStateDefinition().getProperty("level");
        for (int i = 1; i <= 3; i++) {
            emitDrip(scene, util, waterTip, ParticleTypes.DRIPPING_DRIPSTONE_WATER);
            scene.idle(10);
            if (waterLevel instanceof IntegerProperty levels) {
                scene.world().setBlock(waterCauldron, water.setValue(levels, i), false);
            }
            scene.idle(10);
        }
        scene.overlay().showText(80)
                .text("Water fills the Cauldron one level at a time")
                .pointAt(util.vector().centerOf(waterCauldron))
                .placeNearTarget();
        scene.idle(90);

        for (int i = 0; i < 3; i++) {
            emitDrip(scene, util, lavaTip, ParticleTypes.DRIPPING_DRIPSTONE_LAVA);
            scene.idle(20);
        }
        scene.world().setBlock(lavaCauldron, SceneHelper.block("minecraft:lava_cauldron"), false);
        scene.overlay().showText(80)
                .text("Lava drips far more slowly, but fills the Cauldron in one go")
                .pointAt(util.vector().centerOf(lavaCauldron))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showText(80)
                .text("The Cauldron can sit up to 10 blocks below the tip")
                .pointAt(util.vector().centerOf(waterTip))
                .placeNearTarget();
        scene.idle(90);
        scene.markAsFinished();
    }

    public static void weepingObsidian(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("weeping_obsidian", "Collecting Dimensional Tears");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        BlockPos obsidian = util.grid().at(2, 4, 2);
        BlockPos cauldron = util.grid().at(2, 1, 2);
        scene.world().showSection(util.select().position(obsidian), Direction.DOWN);
        scene.idle(15);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Crying Obsidian slowly weeps Dimensional Tears")
                .pointAt(util.vector().centerOf(obsidian))
                .placeNearTarget();
        scene.idle(90);

        scene.world().showSection(util.select().position(cauldron), Direction.DOWN);
        scene.idle(10);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("A Cauldron below it catches the Tears, as long as there is only air in between")
                .pointAt(util.vector().centerOf(cauldron))
                .placeNearTarget();
        scene.idle(60);

        BlockState tears = SceneHelper.block("dimensional_tears:dimensional_tears_cauldron");
        Property<?> level = tears.getBlock().getStateDefinition().getProperty("level");
        for (int i = 1; i <= 3; i++) {
            emitDrip(scene, util, obsidian, ParticleTypes.DRIPPING_OBSIDIAN_TEAR);
            scene.idle(15);
            if (level instanceof IntegerProperty levels && levels.getPossibleValues().contains(i)) {
                scene.world().setBlock(cauldron, tears.setValue(levels, i), false);
            }
            scene.idle(10);
        }
        scene.overlay().showText(80)
                .text("Collect them with a Glass Bottle or a Bucket")
                .pointAt(util.vector().centerOf(cauldron))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showText(80)
                .text("Every kind of Crying Obsidian weeps, including Bricks, Pillars and Polished")
                .pointAt(util.vector().centerOf(obsidian))
                .placeNearTarget();
        scene.idle(90);
        scene.markAsFinished();
    }

    public static void saltSpikes(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("salt_spikes", "Growing Salt Spikes");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        BlockPos salt = util.grid().at(2, 4, 2);
        BlockPos tip = util.grid().at(2, 3, 2);
        BlockPos spike = util.grid().at(2, 1, 2);
        scene.world().showSection(util.select().fromTo(2, 3, 2, 2, 4, 2), Direction.DOWN);
        scene.idle(15);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Hang Pointed Dripstone under a block of Rock Salt")
                .pointAt(util.vector().centerOf(salt))
                .placeNearTarget();
        scene.idle(90);

        scene.world().showSection(util.select().position(2, 5, 2), Direction.DOWN);
        scene.idle(10);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("With Water on top of the Rock Salt, the dripstone starts to drip")
                .pointAt(util.vector().centerOf(2, 5, 2))
                .placeNearTarget();
        scene.idle(60);

        for (int i = 0; i < 3; i++) {
            emitDrip(scene, util, tip, ParticleTypes.DRIPPING_DRIPSTONE_WATER);
            scene.idle(15);
        }
        scene.world().setBlock(spike, withFacing(SceneHelper.block("culturaldelights:salt_spike"), Direction.UP), false);
        scene.world().showSection(util.select().position(spike), Direction.UP);
        scene.idle(10);
        scene.overlay().showText(80)
                .text("Over time a Salt Spike grows on the floor below the tip")
                .pointAt(util.vector().centerOf(spike))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showText(80)
                .text("Only one spike grows under each tip until it is broken")
                .pointAt(util.vector().centerOf(spike))
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
