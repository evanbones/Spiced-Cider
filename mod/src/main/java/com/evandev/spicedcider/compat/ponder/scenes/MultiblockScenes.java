package com.evandev.spicedcider.compat.ponder.scenes;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherPortalBlock;

public class MultiblockScenes {

    public static void netherPortal(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("nether_portal", "Building a Nether Portal");
        scene.scaleSceneView(0.75f);
        scene.configureBasePlate(0, 0, 6);
        scene.showBasePlate();
        scene.idle(10);

        Selection bottom = util.select().fromTo(2, 1, 2, 3, 1, 2);
        Selection sides = util.select().fromTo(1, 2, 2, 1, 4, 2).add(util.select().fromTo(4, 2, 2, 4, 4, 2));
        Selection top = util.select().fromTo(2, 5, 2, 3, 5, 2);
        Selection corners = util.select().position(1, 1, 2).add(util.select().position(4, 1, 2))
                .add(util.select().position(1, 5, 2)).add(util.select().position(4, 5, 2));

        scene.world().showSection(bottom, Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(sides, Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(top, Direction.DOWN);
        scene.idle(10);

        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("A ring of Obsidian at least 4 wide and 5 tall makes a Nether Portal frame")
                .pointAt(util.vector().centerOf(1, 3, 2))
                .placeNearTarget();
        scene.idle(90);

        scene.world().showSection(corners, Direction.DOWN);
        scene.overlay().showOutline(PonderPalette.GREEN, "corners", corners, 70);
        scene.overlay().showText(70)
                .colored(PonderPalette.GREEN)
                .text("The corner blocks are optional")
                .pointAt(util.vector().centerOf(4, 5, 2))
                .placeNearTarget();
        scene.idle(80);

        scene.overlay().showControls(util.vector().topOf(2, 1, 2), Pointing.DOWN, 40)
                .rightClick()
                .withItem(SceneHelper.item("dimensional_tears:dimensional_tears_bottle"));
        scene.idle(10);
        scene.world().setBlocks(util.select().fromTo(2, 2, 2, 3, 4, 2),
                Blocks.NETHER_PORTAL.defaultBlockState().setValue(NetherPortalBlock.AXIS, Direction.Axis.X), false);
        scene.world().showSection(util.select().fromTo(2, 2, 2, 3, 4, 2), Direction.DOWN);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Use a Dimensional Tears Bottle on the inside of the frame to tear it open")
                .pointAt(util.vector().centerOf(2, 3, 2))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showText(80)
                .colored(PonderPalette.BLUE)
                .text("Every Ruined Portal has a bottle waiting in its chest")
                .pointAt(util.vector().centerOf(3, 3, 2))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showText(80)
                .text("More Tears can be bottled from a charged Respawn Anchor")
                .pointAt(util.vector().centerOf(3, 3, 2))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showText(80)
                .text("Frames can be as large as 23 by 23 blocks")
                .pointAt(util.vector().centerOf(3, 5, 2))
                .placeNearTarget();
        scene.idle(90);
        scene.markAsFinished();
    }

    public static void beacon(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("beacon", "Building a Beacon Pyramid");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        scene.world().showSection(util.select().layer(1), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().layer(2), Direction.DOWN);
        scene.idle(10);

        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("A Beacon sits on top of a pyramid of mineral blocks")
                .pointAt(util.vector().centerOf(1, 2, 1))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showText(80)
                .text("Blocks of Iron, Gold, Diamond and other metals can be mixed freely")
                .pointAt(util.vector().centerOf(4, 1, 2))
                .placeNearTarget();
        scene.idle(90);

        scene.world().showSection(util.select().position(2, 3, 2), Direction.DOWN);
        scene.idle(10);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Each layer is two blocks wider than the one above, and every extra layer unlocks stronger effects")
                .pointAt(util.vector().centerOf(2, 3, 2))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showText(80)
                .text("A full pyramid has four layers, the bottom one 9 by 9 blocks")
                .pointAt(util.vector().centerOf(0, 1, 0))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showText(70)
                .colored(PonderPalette.RED)
                .text("The beacon needs a clear view of the sky")
                .pointAt(util.vector().topOf(2, 3, 2))
                .placeNearTarget();
        scene.idle(80);

        scene.overlay().showControls(util.vector().topOf(2, 3, 2), Pointing.DOWN, 40)
                .rightClick()
                .withItem(new ItemStack(Items.GOLD_INGOT));
        scene.idle(10);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Right-click it and pay with an ingot or a Diamond to choose its effects")
                .pointAt(util.vector().centerOf(2, 3, 2))
                .placeNearTarget();
        scene.idle(90);
        scene.markAsFinished();
    }

    public static void conduit(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("conduit", "Activating a Conduit");
        scene.scaleSceneView(0.75f);
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        Selection frame = util.select().fromTo(0, 1, 2, 4, 1, 2)
                .add(util.select().fromTo(0, 5, 2, 4, 5, 2))
                .add(util.select().fromTo(0, 2, 2, 0, 4, 2))
                .add(util.select().fromTo(4, 2, 2, 4, 4, 2));
        Selection core = util.select().fromTo(1, 2, 1, 3, 4, 3);

        scene.world().showSection(util.select().position(2, 3, 2), Direction.DOWN);
        scene.idle(10);
        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("A Conduit needs a frame of Prismarine blocks around it")
                .pointAt(util.vector().centerOf(2, 3, 2))
                .placeNearTarget();
        scene.idle(80);

        scene.world().showSection(frame, Direction.DOWN);
        scene.idle(10);
        scene.overlay().showText(80)
                .text("Frame blocks go in 5 by 5 rings centered on it; 16 is enough to activate it")
                .pointAt(util.vector().centerOf(0, 3, 2))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showText(80)
                .text("Prismarine, Prismarine Bricks, Dark Prismarine and Sea Lanterns all count")
                .pointAt(util.vector().centerOf(4, 1, 2))
                .placeNearTarget();
        scene.idle(90);

        scene.world().showSection(core.substract(util.select().position(2, 3, 2)), Direction.DOWN);
        scene.idle(10);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .colored(PonderPalette.BLUE)
                .text("The conduit and every block touching it have to be water")
                .pointAt(util.vector().centerOf(2, 4, 2))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showText(90)
                .text("Players in water nearby get Conduit Power; every 7 frame blocks add 16 blocks of range")
                .pointAt(util.vector().centerOf(2, 3, 2))
                .placeNearTarget();
        scene.idle(100);

        scene.overlay().showText(80)
                .colored(PonderPalette.RED)
                .text("With all 42 frame blocks, it also attacks hostile mobs close by")
                .pointAt(util.vector().centerOf(2, 3, 2))
                .placeNearTarget();
        scene.idle(90);
        scene.markAsFinished();
    }
}
