package com.evandev.spicedcider.compat.ponder.scenes;

import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class VaultScenes {

    public static void storage(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("item_vault_storage", "Storing Items in Vaults");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(5);

        Selection chests = util.select().fromTo(4, 3, 2, 3, 4, 3);
        Selection largeCog = util.select().position(3, 0, 5);
        Selection belt1 = util.select().fromTo(0, 2, 3, 2, 1, 5);
        Selection gearbox = util.select().position(2, 1, 2);
        Selection belt2 = util.select().fromTo(0, 1, 1, 2, 2, 1);
        Selection vault = util.select().fromTo(3, 2, 3, 4, 1, 1);

        scene.world().showSection(vault, Direction.NORTH);
        scene.idle(5);
        ElementLink<WorldSectionElement> chestLink = scene.world().showIndependentSection(chests, Direction.NORTH);
        scene.world().moveSection(chestLink, util.vector().of(-3, -2, 0), 0);
        scene.idle(10);

        scene.overlay().showOutline(PonderPalette.GREEN, "chestOutline", util.select().fromTo(1, 1, 2, 0, 2, 3), 40);
        scene.idle(10);
        scene.overlay().showLine(PonderPalette.GREEN, util.vector().of(2, 1, 2), util.vector().of(3, 1, 1), 30);
        scene.overlay().showLine(PonderPalette.GREEN, util.vector().of(2, 3, 2), util.vector().of(3, 3, 1), 30);
        scene.overlay().showLine(PonderPalette.GREEN, util.vector().of(2, 3, 4), util.vector().of(3, 3, 4), 30);
        scene.overlay().showOutline(PonderPalette.GREEN, "vaultOutline", vault, 40);
        scene.idle(10);

        BlockPos frontVault = util.grid().at(3, 2, 1);
        scene.overlay().showText(60)
                .text("Item Vaults can be used to store large amounts of items")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().blockSurface(frontVault, Direction.NORTH));
        scene.idle(70);

        scene.world().hideIndependentSection(chestLink, Direction.DOWN);
        scene.idle(15);

        scene.overlay().showControls(util.vector().blockSurface(frontVault, Direction.NORTH), Pointing.RIGHT, 50)
                .rightClick();
        scene.idle(7);

        scene.overlay().showText(70)
                .text("Right-click any part of a vault to open its inventory")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().blockSurface(frontVault, Direction.WEST));
        scene.idle(80);

        scene.overlay().showControls(util.vector().blockSurface(frontVault, Direction.NORTH), Pointing.RIGHT, 50)
                .rightClick()
                .whileSneaking();
        scene.idle(7);

        scene.overlay().showText(70)
                .text("Sneak, or hold a block, to place blocks against it instead")
                .placeNearTarget()
                .pointAt(util.vector().blockSurface(frontVault, Direction.WEST));
        scene.idle(80);

        scene.world().showSection(largeCog, Direction.UP);
        scene.idle(5);
        scene.world().showSection(belt1, Direction.EAST);
        scene.idle(20);

        scene.overlay().showText(80)
                .text("Any components for item transfer can both insert...")
                .attachKeyFrame()
                .colored(PonderPalette.GREEN)
                .placeNearTarget()
                .pointAt(util.vector().topOf(2, 2, 3));

        ItemStack[] junk = {new ItemStack(Items.APPLE), new ItemStack(Items.GOLD_INGOT, 8), new ItemStack(Items.TUFF, 32)};
        for (int i = 0; i < junk.length + 2; i++) {
            scene.idle(4);
            if (i > 1) {
                scene.world().removeItemsFromBelt(util.grid().at(2, 1, 3));
                scene.world().flapFunnel(util.grid().at(2, 2, 3), false);
            }
            scene.idle(5);
            if (i < junk.length) {
                scene.world().createItemOnBeltLike(util.grid().at(0, 1, 3), Direction.SOUTH, junk[i]);
            }
            scene.idle(9);
        }

        scene.world().showSection(gearbox, Direction.SOUTH);
        scene.idle(5);
        scene.world().showSection(belt2, Direction.EAST);
        scene.idle(15);

        scene.overlay().showText(80)
                .text("...and take contents from this container")
                .colored(PonderPalette.GREEN)
                .placeNearTarget()
                .pointAt(util.vector().topOf(2, 1, 1));

        for (ItemStack stack : junk) {
            scene.world().createItemOnBeltLike(util.grid().at(2, 1, 1), Direction.EAST, stack);
            scene.idle(18);
        }
    }
}
