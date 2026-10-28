package com.evandev.spicedcider.compat.ponder.scenes;

import com.evandev.spicedcider.content.brewing.CauldronBrewing;
import com.simibubi.create.content.fluids.FluidFX;
import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;

public class ProcessScenes {

    public static void cauldronBrewing(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("cauldron_brewing", "Brewing Potions in a Cauldron");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        BlockPos heat = util.grid().at(2, 1, 2);
        BlockPos cauldron = util.grid().at(2, 2, 2);
        Selection cauldronSel = util.select().position(cauldron);
        scene.world().showSection(util.select().position(heat), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(cauldronSel, Direction.DOWN);
        scene.idle(15);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Cauldrons brew potions while there is heat right below them")
                .pointAt(util.vector().centerOf(cauldron))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showText(70)
                .text("Campfires, Fire and Magma Blocks all give heat")
                .pointAt(util.vector().centerOf(heat))
                .placeNearTarget();
        scene.idle(80);

        throwIn(scene, util, cauldron, SceneHelper.item("minecraft:nether_wart"));
        brew(scene, util, cauldron, "minecraft:awkward");
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Add Nether Wart to a Cauldron of Water to make a Clear Potion")
                .pointAt(util.vector().topOf(cauldron))
                .placeNearTarget();
        scene.idle(90);

        throwIn(scene, util, cauldron, SceneHelper.item("minecraft:blaze_powder"));
        brew(scene, util, cauldron, "minecraft:strength");
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Each ingredient turns the Clear Potion into the potion it belongs to")
                .pointAt(util.vector().topOf(cauldron))
                .placeNearTarget();
        scene.idle(90);

        throwIn(scene, util, cauldron, SceneHelper.item("minecraft:blaze_rod"));
        brew(scene, util, cauldron, "minecraft:strength");
        scene.overlay().showText(90)
                .text("More ingredients of the same kind make it last longer and grow stronger, up to five in total")
                .pointAt(util.vector().topOf(cauldron))
                .placeNearTarget();
        scene.idle(100);

        scene.overlay().showText(70)
                .text("Ingredients can be thrown in or added with a right-click")
                .pointAt(util.vector().topOf(cauldron))
                .placeNearTarget();
        scene.idle(80);

        scene.overlay().showControls(util.vector().topOf(cauldron), Pointing.DOWN, 40).rightClick()
                .withItem(SceneHelper.item("minecraft:glass_bottle"));
        scene.idle(10);
        scene.world().modifyBlock(cauldron, state -> withInt(state, "potion_quantity", 2), false);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Fill Glass Bottles from the Cauldron, three in total")
                .pointAt(util.vector().topOf(cauldron))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showControls(util.vector().topOf(cauldron), Pointing.DOWN, 40).rightClick()
                .withItem(SceneHelper.item("minecraft:gunpowder"));
        scene.idle(10);
        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("Gunpowder turns the brew into Splash Potions")
                .pointAt(util.vector().topOf(cauldron))
                .placeNearTarget();
        scene.idle(80);

        scene.overlay().showControls(util.vector().topOf(cauldron), Pointing.DOWN, 40).rightClick()
                .withItem(SceneHelper.item("tolerable_creepers:creeper_spores"));
        scene.idle(10);
        scene.overlay().showText(70)
                .text("Creeper Spores turn it into Lingering Potions instead")
                .pointAt(util.vector().topOf(cauldron))
                .placeNearTarget();
        scene.idle(80);

        scene.overlay().showControls(util.vector().topOf(cauldron), Pointing.DOWN, 40).rightClick()
                .withItem(SceneHelper.item("minecraft:arrow"));
        scene.idle(10);
        scene.overlay().showText(70)
                .text("Arrows dipped into the brew come out as Tipped Arrows")
                .pointAt(util.vector().topOf(cauldron))
                .placeNearTarget();
        scene.idle(80);

        throwIn(scene, util, cauldron, SceneHelper.item("minecraft:magma_cream"));
        taint(scene, util, cauldron);
        scene.overlay().showText(90)
                .attachKeyFrame()
                .text("Ingredients of different kinds taint the brew, and the Cauldron takes nothing more until it is emptied")
                .pointAt(util.vector().topOf(cauldron))
                .placeNearTarget();
        scene.idle(100);
        scene.markAsFinished();
    }

    private static void throwIn(SceneBuilder scene, SceneBuildingUtil util, BlockPos cauldron, ItemStack stack) {
        scene.world().createItemEntity(util.vector().topOf(cauldron).add(0, 1, 0), util.vector().of(0, 0.05, 0), stack);
        scene.idle(20);
    }

    private static void taint(SceneBuilder scene, SceneBuildingUtil util, BlockPos cauldron) {
        scene.world().modifyEntities(net.minecraft.world.entity.item.ItemEntity.class, net.minecraft.world.entity.Entity::discard);
        for (int i = 0; i < 4; i++) {
            scene.effects().emitParticles(util.vector().topOf(cauldron).add(0, -0.2, 0),
                    scene.effects().simpleParticleEmitter(ParticleTypes.SMOKE, util.vector().of(0, 0.05, 0)), 3, 1);
            scene.idle(8);
        }
        CompoundTag contents = new CompoundTag();
        contents.putString("id", "minecraft:potion");
        contents.putString("potion", "spicedcider:tainted");
        contents.putInt("custom_color", CauldronBrewing.TAINTED_COLOR);
        contents.putInt("amount", 0);
        scene.world().modifyBlockEntityNBT(util.select().position(cauldron), BlockEntity.class, nbt -> nbt.put("cauldron.potion", contents));
        scene.idle(10);
    }

    private static void brew(SceneBuilder scene, SceneBuildingUtil util, BlockPos cauldron, String potion) {
        scene.world().modifyEntities(net.minecraft.world.entity.item.ItemEntity.class, net.minecraft.world.entity.Entity::discard);
        scene.world().modifyBlock(cauldron, state -> withBoolean(state, "brewing", true), false);
        for (int i = 0; i < 4; i++) {
            scene.effects().emitParticles(util.vector().topOf(cauldron).add(0, -0.2, 0),
                    scene.effects().simpleParticleEmitter(ParticleTypes.BUBBLE_POP, util.vector().of(0, 0.05, 0)), 3, 1);
            scene.idle(8);
        }
        CompoundTag contents = new CompoundTag();
        contents.putString("id", "minecraft:potion");
        contents.putString("potion", potion);
        contents.putInt("amount", 3);
        scene.world().modifyBlockEntityNBT(util.select().position(cauldron), BlockEntity.class, nbt -> nbt.put("cauldron.potion", contents));
        scene.world().modifyBlock(cauldron, state -> withString(withBoolean(state, "brewing", false), "contents", "potion"), false);
        scene.idle(10);
    }

    public static void lightroom(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("lightroom", "Printing Photographs");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        BlockPos lightroom = util.grid().at(2, 1, 2);
        scene.world().showSection(util.select().position(lightroom), Direction.DOWN);
        scene.idle(15);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("The Lightroom prints Photographs from developed film")
                .pointAt(util.vector().centerOf(lightroom))
                .placeNearTarget();
        scene.idle(90);

        scene.world().showSection(util.select().position(3, 1, 2), Direction.DOWN);
        scene.idle(10);
        scene.overlay().showText(80)
                .colored(PonderPalette.RED)
                .text("It only works in bright light, so keep a light source right next to it")
                .pointAt(util.vector().centerOf(3, 1, 2))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showControls(util.vector().topOf(lightroom), Pointing.DOWN, 40)
                .withItem(SceneHelper.item("exposure:developed_black_and_white_film"));
        scene.idle(10);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Load developed film, then add Paper and Black Dye for black and white prints")
                .pointAt(util.vector().topOf(lightroom))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showControls(util.vector().topOf(lightroom), Pointing.DOWN, 40)
                .withItem(SceneHelper.item("exposure:developed_color_film"));
        scene.idle(10);
        scene.overlay().showText(80)
                .text("Colour prints also need Cyan, Magenta and Yellow Dye")
                .pointAt(util.vector().topOf(lightroom))
                .placeNearTarget();
        scene.idle(90);

        scene.world().modifyBlock(lightroom, state -> withBoolean(state, "printing", true), false);
        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("Pick a frame and press Print")
                .pointAt(util.vector().topOf(lightroom))
                .placeNearTarget();
        scene.idle(60);
        scene.world().modifyBlock(lightroom, state -> withBoolean(state, "printing", false), false);
        scene.world().createItemEntity(util.vector().topOf(lightroom), util.vector().of(0, 0.2, 0), SceneHelper.item("exposure:photograph"));
        scene.idle(20);
        scene.overlay().showText(80)
                .text("Once printing is done, the Photograph is ready to take out")
                .pointAt(util.vector().topOf(lightroom))
                .placeNearTarget();
        scene.idle(90);
        scene.markAsFinished();
    }

    public static void filmDeveloping(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("film_developing", "Developing Film");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        BlockPos depot = util.grid().at(2, 1, 2);
        BlockPos spout = util.grid().at(2, 3, 2);
        Selection spoutSel = util.select().position(spout);
        scene.world().showSection(util.select().position(depot), Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(spoutSel, Direction.DOWN);
        scene.idle(15);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Film has to be developed before its photographs can be printed")
                .pointAt(util.vector().blockSurface(spout, Direction.WEST))
                .placeNearTarget();
        scene.idle(90);

        ItemStack film = SceneHelper.item("exposure:black_and_white_film");
        scene.world().createItemOnBeltLike(depot, Direction.NORTH, film);
        scene.idle(15);
        spoutFill(scene, util, spoutSel, depot, SceneHelper.item("exposure:developed_black_and_white_film"));
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("A Spout develops black and white film with Water")
                .pointAt(util.vector().topOf(depot))
                .placeNearTarget();
        scene.idle(90);

        scene.world().removeItemsFromBelt(depot);
        scene.world().createItemOnBeltLike(depot, Direction.NORTH, SceneHelper.item("exposure:color_film"));
        scene.idle(15);
        spoutFill(scene, util, spoutSel, depot, SceneHelper.item("exposure:developed_color_film"));
        scene.overlay().showText(90)
                .attachKeyFrame()
                .text("Colour film is developed with a Clear Potion")
                .pointAt(util.vector().topOf(depot))
                .placeNearTarget();
        scene.idle(100);

        scene.overlay().showText(80)
                .text("Film can also be developed by crafting it together with the same potion")
                .pointAt(util.vector().topOf(depot))
                .placeNearTarget();
        scene.idle(90);
        scene.markAsFinished();
    }

    private static void spoutFill(CreateSceneBuilder scene, SceneBuildingUtil util, Selection spout, BlockPos depot, ItemStack result) {
        scene.world().modifyBlockEntityNBT(spout, SpoutBlockEntity.class, nbt -> nbt.putInt("ProcessingTicks", 20));
        scene.idle(20);
        scene.world().removeItemsFromBelt(depot);
        scene.world().createItemOnBeltLike(depot, Direction.UP, result);
        ParticleOptions particle = FluidFX.getFluidParticle(new FluidStack(Fluids.WATER, 1000));
        for (int i = 0; i < 6; i++) {
            scene.effects().emitParticles(util.vector().topOf(depot).add(0, 1 / 16f, 0), scene.effects().simpleParticleEmitter(particle, Vec3.ZERO), 1, 1);
        }
        scene.idle(20);
    }

    private static BlockState withBoolean(BlockState state, String name, boolean value) {
        return state.getBlock().getStateDefinition().getProperty(name) instanceof BooleanProperty bool ? state.setValue(bool, value) : state;
    }

    private static BlockState withInt(BlockState state, String name, int value) {
        return state.getBlock().getStateDefinition().getProperty(name) instanceof IntegerProperty integer && integer.getPossibleValues().contains(value)
                ? state.setValue(integer, value) : state;
    }

    private static BlockState withString(BlockState state, String name, String value) {
        Property<?> property = state.getBlock().getStateDefinition().getProperty(name);
        return property == null ? state : with(state, property, value);
    }

    private static <T extends Comparable<T>> BlockState with(BlockState state, Property<T> property, String value) {
        return property.getValue(value).map(v -> state.setValue(property, v)).orElse(state);
    }
}
