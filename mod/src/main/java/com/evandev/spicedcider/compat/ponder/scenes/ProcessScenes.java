package com.evandev.spicedcider.compat.ponder.scenes;

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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;

public class ProcessScenes {

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
}
