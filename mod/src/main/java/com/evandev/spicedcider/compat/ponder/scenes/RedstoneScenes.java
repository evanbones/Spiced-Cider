package com.evandev.spicedcider.compat.ponder.scenes;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.EntityElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComparatorBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.DaylightDetectorBlock;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ComparatorMode;
import net.minecraft.world.phys.Vec3;

public class RedstoneScenes {

    private static void power(SceneBuilder scene, BlockPos pos, int power) {
        scene.world().modifyBlock(pos, state -> state.setValue(RedStoneWireBlock.POWER, power), false);
    }

    private static void powered(SceneBuilder scene, BlockPos pos, boolean powered) {
        scene.world().modifyBlock(pos, state -> state.setValue(BlockStateProperties.POWERED, powered), false);
    }

    private static void lamp(SceneBuilder scene, BlockPos pos, boolean lit) {
        scene.world().modifyBlock(pos, state -> state.setValue(RedstoneLampBlock.LIT, lit), false);
    }

    public static void comparatorModes(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("comparator_modes", "Comparing and subtracting signals");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        BlockPos comparator = util.grid().at(3, 1, 2);
        BlockPos lever = util.grid().at(2, 1, 0);
        BlockPos out1 = util.grid().at(2, 1, 2);
        BlockPos out2 = util.grid().at(1, 1, 2);
        BlockPos lampPos = util.grid().at(0, 1, 2);

        scene.world().showSection(util.select().fromTo(0, 1, 2, 4, 1, 2), Direction.DOWN);
        scene.idle(10);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Comparators pass the signal from their back out of their front")
                .pointAt(util.vector().topOf(comparator))
                .placeNearTarget();
        scene.idle(90);

        scene.world().showSection(util.select().fromTo(2, 1, 0, 3, 1, 1).substract(util.select().position(2, 1, 1)), Direction.DOWN);
        scene.idle(10);
        scene.overlay().showControls(util.vector().topOf(lever), Pointing.DOWN, 30).rightClick();
        scene.idle(7);
        powered(scene, lever, true);
        power(scene, util.grid().at(3, 1, 0), 15);
        power(scene, util.grid().at(3, 1, 1), 14);
        scene.effects().indicateRedstone(lever);
        scene.idle(10);

        scene.overlay().showText(90)
                .attachKeyFrame()
                .text("In compare mode, the output stays on while the back is at least as strong as both sides")
                .pointAt(util.vector().topOf(3, 1, 1))
                .placeNearTarget();
        scene.idle(100);

        scene.overlay().showControls(util.vector().topOf(comparator), Pointing.DOWN, 30).rightClick();
        scene.idle(7);
        scene.world().modifyBlock(comparator, state -> state.setValue(ComparatorBlock.MODE, ComparatorMode.SUBTRACT), false);
        power(scene, out1, 1);
        power(scene, out2, 0);
        lamp(scene, lampPos, false);
        scene.idle(10);

        scene.overlay().showText(90)
                .attachKeyFrame()
                .text("Right-click to switch to subtract mode: the strongest side is taken away from the back")
                .pointAt(util.vector().topOf(comparator))
                .placeNearTarget();
        scene.idle(100);

        scene.overlay().showText(80)
                .colored(PonderPalette.RED)
                .text("Here 15 minus 14 leaves a signal of 1, which fades out before it reaches the lamp")
                .pointAt(util.vector().topOf(out2))
                .placeNearTarget();
        scene.idle(90);
        scene.markAsFinished();
    }

    public static void comparatorContainers(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("comparator_containers", "Measuring containers with Comparators");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        BlockPos barrel = util.grid().at(4, 1, 2);
        BlockPos comparator = util.grid().at(3, 1, 2);
        scene.world().showSection(util.select().position(barrel), Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(util.select().fromTo(0, 1, 2, 3, 1, 2), Direction.DOWN);
        scene.idle(10);

        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("A Comparator can measure how full the block behind it is")
                .pointAt(util.vector().topOf(comparator))
                .placeNearTarget();
        scene.idle(90);

        ElementLink<EntityElement> drop = scene.world().createItemEntity(util.vector().topOf(barrel).add(0, 1, 0),
                Vec3.ZERO, new ItemStack(Items.COBBLESTONE));
        scene.idle(15);
        scene.world().modifyEntity(drop, Entity::discard);
        powered(scene, comparator, true);
        power(scene, util.grid().at(2, 1, 2), 1);
        scene.idle(10);
        scene.overlay().showText(70)
                .text("Even a single item gives a signal of 1")
                .pointAt(util.vector().topOf(2, 1, 2))
                .placeNearTarget();
        scene.idle(80);

        drop = scene.world().createItemEntity(util.vector().topOf(barrel).add(0, 1, 0),
                Vec3.ZERO, new ItemStack(Items.COBBLESTONE, 64));
        scene.idle(15);
        scene.world().modifyEntity(drop, Entity::discard);
        power(scene, util.grid().at(2, 1, 2), 8);
        power(scene, util.grid().at(1, 1, 2), 7);
        power(scene, util.grid().at(0, 1, 2), 6);
        scene.idle(10);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("The fuller it gets, the stronger the signal, up to 15 when completely full")
                .pointAt(util.vector().topOf(1, 1, 2))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showText(90)
                .text("Comparators also read Cauldrons, Composters, Cakes, Jukeboxes, Lecterns and more")
                .pointAt(util.vector().topOf(barrel))
                .placeNearTarget();
        scene.idle(100);
        scene.markAsFinished();
    }

    public static void repeater(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("repeater", "Delaying and locking signals with Repeaters");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        BlockPos lever = util.grid().at(4, 1, 2);
        BlockPos repeaterPos = util.grid().at(3, 1, 2);
        BlockPos lampPos = util.grid().at(0, 1, 2);
        BlockPos sideLever = util.grid().at(3, 1, 0);
        BlockPos sideRepeater = util.grid().at(3, 1, 1);

        scene.world().showSection(util.select().fromTo(0, 1, 2, 4, 1, 2), Direction.DOWN);
        scene.idle(10);
        scene.overlay().showControls(util.vector().topOf(lever), Pointing.DOWN, 30).rightClick();
        scene.idle(7);
        powered(scene, lever, true);
        scene.idle(4);
        powered(scene, repeaterPos, true);
        power(scene, util.grid().at(2, 1, 2), 15);
        power(scene, util.grid().at(1, 1, 2), 14);
        lamp(scene, lampPos, true);
        scene.idle(10);

        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Repeaters carry a signal forward and boost it back to full strength")
                .pointAt(util.vector().topOf(repeaterPos))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showControls(util.vector().topOf(repeaterPos), Pointing.DOWN, 60).rightClick();
        for (int delay = 2; delay <= 4; delay++) {
            int value = delay;
            scene.idle(12);
            scene.world().modifyBlock(repeaterPos, state -> state.setValue(RepeaterBlock.DELAY, value), false);
        }
        scene.idle(10);
        scene.overlay().showText(80)
                .text("Right-click to set a delay between 1 and 4 redstone ticks")
                .pointAt(util.vector().topOf(repeaterPos))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showText(70)
                .text("Signals only travel through them one way")
                .pointAt(util.vector().topOf(2, 1, 2))
                .placeNearTarget();
        scene.idle(80);

        scene.world().showSection(util.select().fromTo(sideLever, sideRepeater), Direction.DOWN);
        scene.idle(10);
        scene.overlay().showControls(util.vector().topOf(sideLever), Pointing.DOWN, 30).rightClick();
        scene.idle(7);
        powered(scene, sideLever, true);
        scene.idle(2);
        powered(scene, sideRepeater, true);
        scene.world().modifyBlock(repeaterPos, state -> state.setValue(RepeaterBlock.LOCKED, true), false);
        scene.idle(10);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("A powered Repeater or Comparator pointing into its side locks it")
                .pointAt(util.vector().topOf(sideRepeater))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showControls(util.vector().topOf(lever), Pointing.DOWN, 30).rightClick();
        scene.idle(7);
        powered(scene, lever, false);
        scene.idle(10);
        scene.overlay().showText(80)
                .colored(PonderPalette.BLUE)
                .text("While locked, it keeps its output no matter what its input does")
                .pointAt(util.vector().topOf(lampPos))
                .placeNearTarget();
        scene.idle(90);
        scene.markAsFinished();
    }

    public static void observer(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("observer", "Detecting changes with Observers");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        BlockPos observerPos = util.grid().at(2, 1, 2);
        BlockPos crop = util.grid().at(3, 1, 2);
        scene.world().showSection(util.select().fromTo(0, 1, 2, 2, 1, 2), Direction.DOWN);
        scene.idle(10);

        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Observers watch the block in front of their face")
                .pointAt(util.vector().blockSurface(observerPos, Direction.EAST))
                .placeNearTarget();
        scene.idle(90);

        scene.world().showSection(util.select().position(crop), Direction.DOWN);
        pulse(scene, util, observerPos);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Whenever that block changes, they send a short pulse out of their back")
                .pointAt(util.vector().topOf(1, 1, 2))
                .placeNearTarget();
        scene.idle(90);

        for (int age = 2; age <= 7; age += 2) {
            int value = age;
            scene.world().modifyBlock(crop, state -> state.setValue(CropBlock.AGE, value), false);
            pulse(scene, util, observerPos);
            scene.idle(15);
        }
        scene.overlay().showText(80)
                .text("Crops growing, doors opening and blocks being placed or broken all count")
                .pointAt(util.vector().topOf(crop))
                .placeNearTarget();
        scene.idle(90);
        scene.markAsFinished();
    }

    private static void pulse(SceneBuilder scene, SceneBuildingUtil util, BlockPos observerPos) {
        powered(scene, observerPos, true);
        power(scene, util.grid().at(1, 1, 2), 15);
        lamp(scene, util.grid().at(0, 1, 2), true);
        scene.idle(4);
        powered(scene, observerPos, false);
        power(scene, util.grid().at(1, 1, 2), 0);
        lamp(scene, util.grid().at(0, 1, 2), false);
    }

    public static void daylightDetector(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("daylight_detector", "Sensing the sun with Daylight Detectors");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        BlockPos detector = util.grid().at(2, 1, 2);
        BlockPos wire = util.grid().at(1, 1, 2);
        BlockPos lampPos = util.grid().at(0, 1, 2);
        scene.world().showSection(util.select().fromTo(0, 1, 2, 2, 1, 2), Direction.DOWN);
        scene.idle(10);

        scene.world().modifyBlock(detector, state -> state.setValue(DaylightDetectorBlock.POWER, 15), false);
        power(scene, wire, 14);
        lamp(scene, lampPos, true);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Daylight Detectors give off a signal based on how bright the sky is")
                .pointAt(util.vector().topOf(detector))
                .placeNearTarget();
        scene.idle(90);

        scene.world().modifyBlock(detector, state -> state.setValue(DaylightDetectorBlock.POWER, 1), false);
        power(scene, wire, 0);
        lamp(scene, lampPos, false);
        scene.overlay().showText(80)
                .text("The signal is strongest around noon and fades away towards night")
                .pointAt(util.vector().topOf(detector))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showControls(util.vector().topOf(detector), Pointing.DOWN, 30).rightClick();
        scene.idle(7);
        scene.world().modifyBlock(detector, state -> state.setValue(DaylightDetectorBlock.INVERTED, true)
                .setValue(DaylightDetectorBlock.POWER, 14), false);
        power(scene, wire, 13);
        lamp(scene, lampPos, true);
        scene.idle(10);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .colored(PonderPalette.BLUE)
                .text("Right-click it to invert it, so it measures darkness instead")
                .pointAt(util.vector().topOf(detector))
                .placeNearTarget();
        scene.idle(90);
        scene.markAsFinished();
    }

    public static void hopper(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("hopper", "Moving items with Hoppers");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        BlockPos hopperPos = util.grid().at(2, 2, 2);
        BlockPos target = util.grid().at(2, 1, 2);
        BlockPos source = util.grid().at(2, 3, 2);
        BlockPos lock = util.grid().at(1, 2, 2);

        scene.world().showSection(util.select().fromTo(target, hopperPos), Direction.DOWN);
        scene.idle(10);
        ElementLink<EntityElement> drop = scene.world().createItemEntity(util.vector().topOf(hopperPos).add(0, 1, 0),
                Vec3.ZERO, new ItemStack(Items.WHEAT));
        scene.idle(15);
        scene.world().modifyEntity(drop, Entity::discard);
        scene.effects().indicateSuccess(target);

        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Hoppers catch items dropped on top and pass them into the block they point at")
                .pointAt(util.vector().topOf(hopperPos))
                .placeNearTarget();
        scene.idle(90);

        scene.world().showSection(util.select().position(source), Direction.DOWN);
        scene.idle(10);
        scene.overlay().showText(80)
                .text("They also pull items out of containers above them")
                .pointAt(util.vector().centerOf(source))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showText(80)
                .text("Place a hopper against the side of a block to point its spout that way instead")
                .pointAt(util.vector().blockSurface(hopperPos, Direction.WEST))
                .placeNearTarget();
        scene.idle(90);

        scene.world().showSection(util.select().position(lock), Direction.DOWN);
        scene.world().modifyBlock(hopperPos, state -> state.setValue(HopperBlock.ENABLED, false), false);
        scene.effects().indicateRedstone(lock);
        scene.idle(10);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .colored(PonderPalette.RED)
                .text("A redstone signal locks the hopper, stopping it from moving anything")
                .pointAt(util.vector().centerOf(lock))
                .placeNearTarget();
        scene.idle(90);
        scene.markAsFinished();
    }

    public static void crafter(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("crafter", "Automatic crafting with the Crafter");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        BlockPos crafterPos = util.grid().at(2, 1, 2);
        BlockPos hopperPos = util.grid().at(2, 2, 2);
        BlockPos button = util.grid().at(3, 1, 2);

        scene.world().showSection(util.select().position(crafterPos), Direction.DOWN);
        scene.idle(10);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("The Crafter holds a crafting grid and crafts on its own")
                .pointAt(util.vector().topOf(crafterPos))
                .placeNearTarget();
        scene.idle(90);

        scene.world().showSection(util.select().position(hopperPos), Direction.DOWN);
        scene.idle(10);
        scene.overlay().showText(80)
                .text("Hoppers and Droppers can fill it; each item goes to the slot holding the fewest")
                .pointAt(util.vector().topOf(hopperPos))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showText(80)
                .text("Click a slot in its menu to disable it, keeping the recipe's shape intact")
                .pointAt(util.vector().blockSurface(crafterPos, Direction.SOUTH))
                .placeNearTarget();
        scene.idle(90);

        scene.world().showSection(util.select().position(button), Direction.WEST);
        scene.idle(10);
        scene.overlay().showControls(util.vector().centerOf(button), Pointing.DOWN, 30).rightClick();
        scene.idle(7);
        powered(scene, button, true);
        scene.world().modifyBlock(crafterPos, state -> state.setValue(BlockStateProperties.TRIGGERED, true)
                .setValue(BlockStateProperties.CRAFTING, true), false);
        scene.idle(6);
        ElementLink<EntityElement> result = scene.world().createItemEntity(util.vector().blockSurface(crafterPos, Direction.WEST),
                util.vector().of(-0.1, 0.1, 0), new ItemStack(Items.BREAD));
        scene.world().modifyBlock(crafterPos, state -> state.setValue(BlockStateProperties.CRAFTING, false), false);
        scene.idle(10);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("A redstone pulse makes it craft, pushing the result out of its front")
                .pointAt(util.vector().blockSurface(crafterPos, Direction.WEST))
                .placeNearTarget();
        scene.idle(30);
        powered(scene, button, false);
        scene.world().modifyBlock(crafterPos, state -> state.setValue(BlockStateProperties.TRIGGERED, false), false);
        scene.idle(60);
        scene.world().modifyEntity(result, Entity::discard);

        scene.overlay().showText(80)
                .text("A Comparator reads how many of its slots are filled or disabled")
                .pointAt(util.vector().blockSurface(crafterPos, Direction.NORTH))
                .placeNearTarget();
        scene.idle(90);
        scene.markAsFinished();
    }

    public static void noteBlock(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("note_block", "Playing music with Note Blocks");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        BlockPos note = util.grid().at(2, 2, 2);
        BlockPos below = util.grid().at(2, 1, 2);
        BlockPos above = util.grid().at(2, 3, 2);
        Vec3 particle = util.vector().topOf(note).add(0, 0.2, 0);

        scene.world().showSection(util.select().fromTo(below, note), Direction.DOWN);
        scene.idle(10);
        scene.overlay().showControls(util.vector().topOf(note), Pointing.DOWN, 30).leftClick();
        scene.idle(7);
        play(scene, particle);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Note Blocks play a note when they get a redstone pulse or are punched")
                .pointAt(util.vector().topOf(note))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showControls(util.vector().topOf(note), Pointing.DOWN, 50).rightClick();
        for (int i = 0; i < 3; i++) {
            scene.idle(12);
            scene.world().cycleBlockProperty(note, NoteBlock.NOTE);
            play(scene, particle);
        }
        scene.idle(10);
        scene.overlay().showText(80)
                .text("Right-click to raise the pitch, stepping through two octaves")
                .pointAt(util.vector().topOf(note))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("The block underneath picks the instrument")
                .pointAt(util.vector().centerOf(below))
                .placeNearTarget();
        scene.idle(30);
        String[] instruments = {"minecraft:stone", "minecraft:sand", "minecraft:gold_block", "minecraft:packed_ice"};
        for (String instrument : instruments) {
            scene.world().setBlock(below, SceneHelper.block(instrument), true);
            play(scene, particle);
            scene.idle(15);
        }
        scene.overlay().showText(90)
                .text("Wood gives a bass, Stone a drum, Sand a snare, Gold a bell and Packed Ice chimes")
                .pointAt(util.vector().centerOf(below))
                .placeNearTarget();
        scene.idle(100);

        scene.world().showSection(util.select().position(above), Direction.DOWN);
        scene.idle(10);
        play(scene, particle.add(0, 0.6, 0));
        scene.overlay().showText(80)
                .colored(PonderPalette.GREEN)
                .text("A mob head on top plays that mob's sound instead")
                .pointAt(util.vector().centerOf(above))
                .placeNearTarget();
        scene.idle(90);
        scene.markAsFinished();
    }

    private static void play(SceneBuilder scene, Vec3 at) {
        scene.effects().emitParticles(at, scene.effects().simpleParticleEmitter(ParticleTypes.NOTE, Vec3.ZERO), 1, 1);
    }
}
