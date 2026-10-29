package com.evandev.spicedcider.compat.ponder.scenes;

import com.evandev.spicedcider.entities.sputter.SputterEntity;
import com.evandev.spicedcider.entities.strawgolem.StrawGolem;
import com.evandev.spicedcider.registry.ModItems;
import com.evandev.spicedcider.registry.ModEntityTypes;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.EntityElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.phys.Vec3;

public class GolemScenes {

    public static void ironGolem(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("iron_golem", "Building an Iron Golem");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        BlockPos base = util.grid().at(2, 1, 2);
        BlockPos head = util.grid().at(2, 3, 2);
        scene.world().showSection(util.select().position(base), Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(util.select().fromTo(1, 2, 2, 3, 2, 2), Direction.DOWN);
        scene.idle(10);

        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("Four Blocks of Iron in a T shape form the body")
                .pointAt(util.vector().centerOf(2, 2, 2))
                .placeNearTarget();
        scene.idle(80);

        Selection corners = util.select().position(1, 1, 2).add(util.select().position(3, 1, 2));
        scene.overlay().showOutline(PonderPalette.RED, "corners", corners, 70);
        scene.overlay().showText(70)
                .colored(PonderPalette.RED)
                .text("The spaces beside the bottom block have to stay empty")
                .pointAt(util.vector().centerOf(1, 1, 2))
                .placeNearTarget();
        scene.idle(80);

        scene.overlay().showControls(util.vector().topOf(2, 2, 2), Pointing.DOWN, 40)
                .rightClick()
                .withItem(new ItemStack(Items.CARVED_PUMPKIN));
        scene.idle(7);
        scene.world().showSection(util.select().position(head), Direction.DOWN);
        scene.idle(10);

        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("A Carved Pumpkin or Jack o'Lantern placed on top last brings it to life")
                .pointAt(util.vector().centerOf(head))
                .placeNearTarget();
        scene.idle(80);

        scene.world().setBlocks(util.select().fromTo(1, 1, 2, 3, 3, 2), Blocks.AIR.defaultBlockState(), true);
        SceneHelper.spawn(scene, EntityType.IRON_GOLEM, util.vector().of(2.5, 1, 2.5), 180,
                entity -> ((IronGolem) entity).setPlayerCreated(true));
        scene.idle(20);

        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("A built Iron Golem fights nearby monsters and never attacks players")
                .pointAt(util.vector().of(2.5, 3, 2.5))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showControls(util.vector().of(2.5, 2.5, 2.5), Pointing.DOWN, 40)
                .rightClick()
                .withItem(new ItemStack(Items.IRON_INGOT));
        scene.idle(10);
        scene.overlay().showText(70)
                .text("Hand it Iron Ingots to patch up its cracks")
                .pointAt(util.vector().of(2.5, 2, 2.5))
                .placeNearTarget();
        scene.idle(80);
        scene.markAsFinished();
    }

    public static void snowGolem(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("snow_golem", "Building a Snow Golem");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        BlockPos head = util.grid().at(2, 3, 2);
        scene.world().showSection(util.select().fromTo(2, 1, 2, 2, 2, 2), Direction.DOWN);
        scene.idle(10);

        scene.overlay().showControls(util.vector().topOf(2, 2, 2), Pointing.DOWN, 40)
                .rightClick()
                .withItem(new ItemStack(Items.CARVED_PUMPKIN));
        scene.idle(7);
        scene.world().showSection(util.select().position(head), Direction.DOWN);
        scene.idle(10);

        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("Two Snow Blocks topped with a Carved Pumpkin make a Snow Golem")
                .pointAt(util.vector().centerOf(2, 2, 2))
                .placeNearTarget();
        scene.idle(80);

        scene.world().setBlocks(util.select().fromTo(2, 1, 2, 2, 3, 2), Blocks.AIR.defaultBlockState(), true);
        SceneHelper.spawn(scene, EntityType.SNOW_GOLEM, util.vector().of(2.5, 1, 2.5), 180, entity -> {});
        scene.idle(20);

        scene.world().showSection(util.select().fromTo(1, 1, 1, 3, 1, 3).substract(util.select().position(2, 1, 2)), Direction.DOWN);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Snow Golems pelt monsters with snowballs and leave snow wherever they walk")
                .pointAt(util.vector().of(2.5, 2, 2.5))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showText(80)
                .colored(PonderPalette.RED)
                .text("They melt in warm biomes and are hurt by water and rain")
                .pointAt(util.vector().of(2.5, 1.5, 2.5))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showControls(util.vector().of(2.5, 2.2, 2.5), Pointing.DOWN, 40)
                .rightClick()
                .withItem(new ItemStack(Items.SHEARS));
        scene.idle(10);
        scene.overlay().showText(70)
                .text("Shears take off their pumpkin")
                .pointAt(util.vector().of(2.5, 1.8, 2.5))
                .placeNearTarget();
        scene.idle(80);
        scene.markAsFinished();
    }

    public static void pineconeGolem(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("pinecone_golem", "Building a Pinecone Golem");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        BlockPos head = util.grid().at(2, 2, 2);
        scene.world().showSection(util.select().position(2, 1, 2), Direction.DOWN);
        scene.idle(10);

        scene.overlay().showControls(util.vector().topOf(2, 1, 2), Pointing.DOWN, 40)
                .rightClick()
                .withItem(new ItemStack(Items.CARVED_PUMPKIN));
        scene.idle(7);
        scene.world().showSection(util.select().position(head), Direction.DOWN);
        scene.idle(10);

        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("A Carved Pumpkin placed on top of a Pinecone builds a Pinecone Golem")
                .pointAt(util.vector().centerOf(2, 1, 2))
                .placeNearTarget();
        scene.idle(80);

        scene.world().setBlocks(util.select().fromTo(2, 1, 2, 2, 2, 2), Blocks.AIR.defaultBlockState(), true);
        Vec3 start = util.vector().of(2.5, 1, 2.5);
        ElementLink<EntityElement> golem = SceneHelper.spawn(scene, "environmental:pinecone_golem", start, 180, entity -> {});
        scene.idle(20);

        ItemStack sapling = SceneHelper.item("nomansland:pine_sapling");
        scene.overlay().showControls(start.add(0, 1, 0), Pointing.DOWN, 40)
                .rightClick()
                .withItem(sapling);
        scene.idle(7);
        scene.world().modifyEntity(golem, entity -> ((LivingEntity) entity).setItemSlot(EquipmentSlot.MAINHAND, sapling.copy()));
        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("Hand it a Sapling and it carries it off")
                .pointAt(start.add(0, 0.6, 0))
                .placeNearTarget();
        scene.idle(80);

        Vec3 spot = util.vector().of(1.5, 1, 1.5);
        SceneHelper.walk(scene, golem, start, spot, 20);
        scene.world().modifyEntity(golem, entity -> ((LivingEntity) entity).setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY));
        scene.world().showSection(util.select().position(1, 1, 0), Direction.DOWN);
        scene.idle(10);
        scene.overlay().showText(70)
                .text("It plants the Sapling wherever there's room for a tree to grow")
                .pointAt(util.vector().centerOf(1, 1, 0))
                .placeNearTarget();
        scene.idle(80);

        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("With empty hands it follows anyone holding a Sapling and picks up Saplings lying nearby")
                .pointAt(spot.add(0, 0.6, 0))
                .placeNearTarget();
        scene.idle(90);
        scene.markAsFinished();
    }

    public static void gingerbreadGolem(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("gingerbread_golem", "Building a Gingerbread Golem");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        scene.world().showSection(util.select().position(2, 1, 2), Direction.DOWN);
        scene.idle(10);
        scene.overlay().showControls(util.vector().blockSurface(util.grid().at(2, 1, 2), Direction.NORTH), Pointing.RIGHT, 40)
                .rightClick()
                .withItem(SceneHelper.item("snowyspirit:gumdrop_red"));
        scene.idle(7);
        scene.world().showSection(util.select().position(2, 1, 1), Direction.SOUTH);
        scene.idle(10);
        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("Stick a Gumdrop on the front of a Gingerbread block")
                .pointAt(util.vector().blockSurface(util.grid().at(2, 1, 2), Direction.NORTH))
                .placeNearTarget();
        scene.idle(80);

        scene.overlay().showControls(util.vector().topOf(2, 1, 2), Pointing.DOWN, 40)
                .rightClick()
                .withItem(new ItemStack(Items.CARVED_PUMPKIN));
        scene.idle(7);
        scene.world().showSection(util.select().position(2, 2, 2), Direction.DOWN);
        scene.idle(10);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("A Carved Pumpkin on top, facing the same way as the Gumdrop, brings a Gingerbread Golem to life")
                .pointAt(util.vector().centerOf(2, 2, 2))
                .placeNearTarget();
        scene.idle(90);

        scene.world().setBlocks(util.select().fromTo(2, 1, 1, 2, 2, 2), Blocks.AIR.defaultBlockState(), true);
        Vec3 start = util.vector().of(2.5, 1, 2.5);
        SceneHelper.spawn(scene, "snowyspirit:gingerbread_golem", start, 180, entity -> {});
        scene.idle(20);

        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("It follows whoever built it")
                .pointAt(start.add(0, 0.8, 0))
                .placeNearTarget();
        scene.idle(80);

        scene.overlay().showControls(start.add(0, 1.2, 0), Pointing.DOWN, 40)
                .rightClick()
                .whileSneaking();
        scene.idle(10);
        scene.overlay().showText(70)
                .text("Sneak and right-click to make it sit or stand")
                .pointAt(start.add(0, 0.6, 0))
                .placeNearTarget();
        scene.idle(80);

        scene.overlay().showControls(start.add(0, 1.2, 0), Pointing.DOWN, 40)
                .rightClick()
                .withItem(SceneHelper.item("snowyspirit:gingerbread_cookie"));
        scene.idle(10);
        scene.overlay().showText(70)
                .text("Gingerbread Cookies heal it and patch up its bites")
                .pointAt(start.add(0, 0.6, 0))
                .placeNearTarget();
        scene.idle(80);
        scene.markAsFinished();
    }

    public static void copperGolem(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("copper_golem", "Building a Copper Golem");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        BlockPos body = util.grid().at(2, 1, 2);
        BlockPos head = util.grid().at(2, 2, 2);
        scene.world().showSection(util.select().position(body), Direction.DOWN);
        scene.idle(10);

        scene.overlay().showControls(util.vector().topOf(body), Pointing.DOWN, 40)
                .rightClick()
                .withItem(new ItemStack(Items.CARVED_PUMPKIN));
        scene.idle(7);
        scene.world().showSection(util.select().position(head), Direction.DOWN);
        scene.idle(10);

        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("A Block of Copper topped with a Carved Pumpkin makes a Copper Golem")
                .pointAt(util.vector().centerOf(body))
                .placeNearTarget();
        scene.idle(80);

        scene.world().setBlocks(util.select().fromTo(2, 1, 2, 2, 2, 2), Blocks.AIR.defaultBlockState(), true);
        SceneHelper.spawn(scene, "minecraft:copper_golem", util.vector().of(2.5, 1, 2.5), 180, entity -> {});
        scene.idle(20);

        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Like copper blocks, Copper Golems slowly oxidize")
                .pointAt(util.vector().of(2.5, 1.5, 2.5))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showText(80)
                .colored(PonderPalette.RED)
                .text("A fully oxidized golem may freeze into a statue")
                .pointAt(util.vector().of(2.5, 1.5, 2.5))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showControls(util.vector().of(2.5, 2, 2.5), Pointing.DOWN, 40)
                .rightClick()
                .withItem(new ItemStack(Items.HONEYCOMB));
        scene.idle(10);
        scene.overlay().showText(80)
                .colored(PonderPalette.GREEN)
                .text("Wax it with Honeycomb to stop the aging, or scrape it back with an Axe")
                .pointAt(util.vector().of(2.5, 1.5, 2.5))
                .placeNearTarget();
        scene.idle(90);
        scene.markAsFinished();
    }

    public static void copperGolemSorting(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("copper_golem_sorting", "Sorting items with Copper Golems");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        BlockPos copperChest = util.grid().at(0, 1, 2);
        BlockPos matchingChest = util.grid().at(4, 1, 1);
        BlockPos emptyChest = util.grid().at(4, 1, 3);
        scene.world().showSection(util.select().position(copperChest), Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(util.select().position(matchingChest), Direction.DOWN);
        scene.idle(3);
        scene.world().showSection(util.select().position(emptyChest), Direction.DOWN);
        scene.idle(10);

        Vec3 atCopper = util.vector().of(1.5, 1, 2.5);
        Vec3 atMatching = util.vector().of(3.5, 1, 1.5);
        Vec3 atEmpty = util.vector().of(3.5, 1, 3.5);
        ElementLink<EntityElement> golem = SceneHelper.spawn(scene, "minecraft:copper_golem", atCopper, 90, entity -> {});
        scene.idle(10);

        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Copper Golems carry items out of Copper Chests")
                .pointAt(util.vector().topOf(copperChest))
                .placeNearTarget();
        scene.idle(90);

        ItemStack wheat = new ItemStack(Items.WHEAT);
        scene.world().modifyEntity(golem, entity -> ((LivingEntity) entity).setItemSlot(EquipmentSlot.MAINHAND, wheat.copy()));
        scene.idle(10);
        SceneHelper.walk(scene, golem, atCopper, atMatching, 30);
        scene.world().modifyEntity(golem, entity -> SceneHelper.face(entity, -90));
        scene.idle(10);
        scene.world().modifyEntity(golem, entity -> ((LivingEntity) entity).setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY));
        scene.effects().indicateSuccess(matchingChest);

        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Each item goes into a nearby chest that already holds the same item...")
                .pointAt(util.vector().topOf(matchingChest))
                .placeNearTarget();
        scene.idle(90);

        SceneHelper.walk(scene, golem, atMatching, atCopper, 30);
        scene.world().modifyEntity(golem, entity -> {
            SceneHelper.face(entity, 90);
            ((LivingEntity) entity).setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.COBBLESTONE));
        });
        scene.idle(15);
        SceneHelper.walk(scene, golem, atCopper, atEmpty, 30);
        scene.world().modifyEntity(golem, entity -> SceneHelper.face(entity, -90));
        scene.idle(10);
        scene.world().modifyEntity(golem, entity -> ((LivingEntity) entity).setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY));
        scene.effects().indicateSuccess(emptyChest);

        scene.overlay().showText(80)
                .text("...or into an empty chest when nothing matches")
                .pointAt(util.vector().topOf(emptyChest))
                .placeNearTarget();
        scene.idle(90);
        scene.markAsFinished();
    }

    public static void sputter(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("sputter", "Building a Sputter");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        BlockPos body = util.grid().at(2, 1, 2);
        BlockPos head = util.grid().at(2, 2, 2);
        scene.world().showSection(util.select().position(body), Direction.DOWN);
        scene.idle(10);

        scene.overlay().showControls(util.vector().topOf(body), Pointing.DOWN, 40)
                .rightClick()
                .withItem(new ItemStack(Items.CARVED_PUMPKIN));
        scene.idle(7);
        scene.world().showSection(util.select().position(head), Direction.DOWN);
        scene.idle(10);

        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("A Carved Pumpkin on top of a Barrel makes a Sputter")
                .pointAt(util.vector().centerOf(body))
                .placeNearTarget();
        scene.idle(80);

        scene.world().setBlocks(util.select().fromTo(2, 1, 2, 2, 2, 2), Blocks.AIR.defaultBlockState(), true);
        ElementLink<EntityElement> sputter = SceneHelper.spawn(scene, ModEntityTypes.SPUTTER.get(), util.vector().of(2.5, 1.05, 2.5), 180, entity -> {});
        scene.idle(20);

        scene.overlay().showControls(util.vector().of(2.5, 2.2, 2.5), Pointing.DOWN, 40)
                .rightClick()
                .withItem(new ItemStack(Items.ARROW, 64));
        scene.idle(10);
        scene.world().modifyEntity(sputter, entity -> ((SputterEntity) entity).setItem(new ItemStack(Items.ARROW, 64)));
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Load it with a stack of Arrows and it shoots at nearby monsters")
                .pointAt(util.vector().of(2.5, 1.5, 2.5))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showText(70)
                .colored(PonderPalette.GREEN)
                .text("Tipped Arrows keep their effects")
                .pointAt(util.vector().of(2.5, 1.5, 2.5))
                .placeNearTarget();
        scene.idle(80);

        scene.overlay().showControls(util.vector().of(2.5, 2.2, 2.5), Pointing.DOWN, 40)
                .rightClick();
        scene.idle(10);
        scene.world().modifyEntity(sputter, entity -> ((SputterEntity) entity).setItem(ItemStack.EMPTY));
        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("Use an empty hand to take the Arrows back")
                .pointAt(util.vector().of(2.5, 1.5, 2.5))
                .placeNearTarget();
        scene.idle(80);

        scene.overlay().showControls(util.vector().of(2.5, 2.2, 2.5), Pointing.DOWN, 40)
                .rightClick()
                .withItem(new ItemStack(Items.RED_DYE));
        scene.idle(10);
        scene.world().modifyEntity(sputter, entity -> ((SputterEntity) entity).setWarpaintColor(DyeColor.RED));
        scene.overlay().showText(70)
                .text("A Dye gives it war paint")
                .pointAt(util.vector().of(2.5, 1.5, 2.5))
                .placeNearTarget();
        scene.idle(80);
        scene.markAsFinished();
    }

    public static void strawGolem(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("straw_golem", "Building a Straw Golem");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        BlockPos body = util.grid().at(2, 1, 2);
        BlockPos head = util.grid().at(2, 2, 2);
        BlockPos chest = util.grid().at(0, 1, 3);
        scene.world().showSection(util.select().position(body), Direction.DOWN);
        scene.idle(10);

        scene.overlay().showControls(util.vector().topOf(body), Pointing.DOWN, 40)
                .rightClick()
                .withItem(new ItemStack(Items.CARVED_PUMPKIN));
        scene.idle(7);
        scene.world().showSection(util.select().position(head), Direction.DOWN);
        scene.idle(10);

        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("A Carved Pumpkin on top of a Hay Bale makes a Straw Golem")
                .pointAt(util.vector().centerOf(body))
                .placeNearTarget();
        scene.idle(80);

        scene.world().setBlocks(util.select().fromTo(2, 1, 2, 2, 2, 2), Blocks.AIR.defaultBlockState(), true);
        Vec3 start = util.vector().of(2.5, 1, 2.5);
        Vec3 atCrops = util.vector().of(2.5, 1, 1.4);
        Vec3 atChest = util.vector().of(1.4, 1, 3.5);
        ElementLink<EntityElement> golem = SceneHelper.spawn(scene, ModEntityTypes.STRAW_GOLEM.get(), start, 180, entity -> ((Mob) entity).setNoAi(true));
        scene.idle(15);
        scene.world().setBlocks(util.select().fromTo(1, 1, 0, 3, 1, 0), Blocks.CARROTS.defaultBlockState().setValue(CropBlock.AGE, CropBlock.MAX_AGE), false);
        scene.world().showSection(util.select().fromTo(1, 1, 0, 3, 1, 0), Direction.DOWN);
        scene.idle(15);

        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("It harvests fully grown crops nearby...")
                .pointAt(util.vector().topOf(2, 0, 0))
                .placeNearTarget();
        scene.idle(30);
        SceneHelper.walk(scene, golem, start, atCrops, 20);
        scene.world().modifyEntity(golem, entity -> ((StrawGolem) entity).setPickupStatus(1));
        scene.idle(20);
        scene.world().setBlock(util.grid().at(2, 1, 0), Blocks.CARROTS.defaultBlockState(), false);
        scene.world().modifyEntity(golem, entity -> ((LivingEntity) entity).setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.CARROT)));
        scene.idle(20);
        scene.world().modifyEntity(golem, entity -> ((StrawGolem) entity).setPickupStatus(0));
        scene.idle(10);

        scene.overlay().showText(70)
                .text("...and replants them")
                .pointAt(util.vector().topOf(2, 0, 0))
                .placeNearTarget();
        scene.idle(80);

        scene.world().showSection(util.select().position(chest), Direction.DOWN);
        scene.idle(10);
        SceneHelper.walk(scene, golem, atCrops, atChest, 30);
        scene.world().modifyEntity(golem, entity -> SceneHelper.face(entity, 90));
        scene.idle(10);
        scene.world().modifyEntity(golem, entity -> ((LivingEntity) entity).setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY));
        scene.effects().indicateSuccess(chest);
        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("The harvest goes into the nearest container")
                .pointAt(util.vector().topOf(chest))
                .placeNearTarget();
        scene.idle(80);

        scene.overlay().showControls(atChest.add(0, 1.2, 0), Pointing.DOWN, 30)
                .rightClick()
                .whileSneaking();
        scene.idle(35);
        scene.overlay().showControls(util.vector().topOf(chest), Pointing.DOWN, 30)
                .rightClick()
                .whileSneaking();
        scene.idle(10);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Sneak and use an empty hand on the golem, then on a container, to choose where it delivers")
                .pointAt(util.vector().topOf(chest))
                .placeNearTarget();
        scene.idle(90);

        SceneHelper.walk(scene, golem, atChest, start, 20);
        scene.world().modifyEntity(golem, entity -> SceneHelper.face(entity, 180));
        scene.idle(10);

        scene.overlay().showControls(start.add(0, 1.2, 0), Pointing.DOWN, 40)
                .rightClick()
                .withItem(new ItemStack(ModItems.STRAW_HAT.get()));
        scene.idle(10);
        scene.world().modifyEntity(golem, entity -> ((StrawGolem) entity).setHat(true));
        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("A Straw Hat keeps the rain off its head")
                .pointAt(start.add(0, 0.9, 0))
                .placeNearTarget();
        scene.idle(80);

        scene.overlay().showControls(start.add(0, 1.2, 0), Pointing.DOWN, 40)
                .rightClick()
                .withItem(new ItemStack(Items.BARREL));
        scene.idle(10);
        scene.world().modifyEntity(golem, entity -> ((StrawGolem) entity).setBarrelHealth(StrawGolem.BARREL_HEALTH));
        scene.overlay().showText(70)
                .text("A Barrel lets it shrug off hits")
                .pointAt(start.add(0, 0.5, 0))
                .placeNearTarget();
        scene.idle(80);

        scene.overlay().showControls(start.add(0, 1.2, 0), Pointing.DOWN, 40)
                .rightClick()
                .withItem(new ItemStack(Items.WHEAT));
        scene.idle(10);
        scene.overlay().showText(70)
                .colored(PonderPalette.GREEN)
                .text("Wheat patches it up when it gets hurt")
                .pointAt(start.add(0, 0.5, 0))
                .placeNearTarget();
        scene.idle(80);
        scene.markAsFinished();
    }

    public static void wither(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("wither", "Summoning the Wither");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        scene.world().showSection(util.select().position(2, 1, 2), Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(util.select().fromTo(1, 2, 2, 3, 2, 2), Direction.DOWN);
        scene.idle(10);

        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("Soul Sand or Soul Soil in a T shape forms the Wither's body")
                .pointAt(util.vector().centerOf(2, 2, 2))
                .placeNearTarget();
        scene.idle(80);

        scene.world().showSection(util.select().position(1, 3, 2), Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(util.select().position(2, 3, 2), Direction.DOWN);
        scene.idle(10);
        scene.overlay().showControls(util.vector().topOf(3, 2, 2), Pointing.DOWN, 40)
                .rightClick()
                .withItem(new ItemStack(Items.WITHER_SKELETON_SKULL));
        scene.idle(7);
        scene.world().showSection(util.select().position(3, 3, 2), Direction.DOWN);
        scene.idle(10);

        scene.overlay().showText(70)
                .text("Three Wither Skeleton Skulls on top summon it")
                .pointAt(util.vector().centerOf(2, 3, 2))
                .placeNearTarget();
        scene.idle(80);

        scene.world().setBlocks(util.select().fromTo(1, 1, 2, 3, 3, 2), Blocks.AIR.defaultBlockState(), true);
        SceneHelper.spawn(scene, EntityType.WITHER, util.vector().of(2.5, 1, 2.5), 180, entity -> ((WitherBoss) entity).makeInvulnerable());
        scene.effects().emitParticles(util.vector().of(2.5, 2, 2.5),
                scene.effects().simpleParticleEmitter(ParticleTypes.LARGE_SMOKE, util.vector().of(0, 0.05, 0)), 3, 20);
        scene.idle(20);

        scene.overlay().showText(90)
                .attachKeyFrame()
                .colored(PonderPalette.RED)
                .text("It explodes as it wakes, then attacks everything alive and breaks blocks in its way")
                .pointAt(util.vector().of(2.5, 3, 2.5))
                .placeNearTarget();
        scene.idle(100);

        scene.overlay().showText(80)
                .text("Summon it far from anything you want to keep")
                .pointAt(util.vector().of(2.5, 2, 2.5))
                .placeNearTarget();
        scene.idle(90);
        scene.markAsFinished();
    }

    public static void tuningFork(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("tuning_fork_golems", "Conducting Copper Golems");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        Vec3 start = util.vector().of(1.5, 1, 2.5);
        Vec3 target = util.vector().of(3.5, 1, 2.5);
        ElementLink<EntityElement> golem = SceneHelper.spawn(scene, "minecraft:copper_golem", start, 180, entity -> {});
        scene.idle(15);

        ItemStack fork = SceneHelper.item("spelunkery:tuning_fork");
        scene.overlay().showControls(start.add(0, 1.2, 0), Pointing.DOWN, 40)
                .rightClick()
                .withItem(fork);
        scene.idle(10);
        scene.effects().emitParticles(start.add(0, 1.4, 0),
                scene.effects().simpleParticleEmitter(ParticleTypes.NOTE, Vec3.ZERO), 1, 3);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Right-click a Copper Golem with a Tuning Fork to start conducting it")
                .pointAt(start.add(0, 0.8, 0))
                .placeNearTarget();
        scene.idle(90);

        BlockPos spot = util.grid().at(3, 0, 2);
        scene.overlay().showControls(util.vector().topOf(spot), Pointing.DOWN, 40)
                .rightClick()
                .withItem(fork);
        scene.idle(10);
        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("Right-click a block to send the golem there")
                .pointAt(util.vector().topOf(spot))
                .placeNearTarget();
        scene.idle(20);
        SceneHelper.walk(scene, golem, start, target, 30);
        scene.idle(30);

        scene.overlay().showControls(target.add(0, 1.2, 0), Pointing.DOWN, 40)
                .rightClick()
                .withItem(fork);
        scene.idle(10);
        scene.overlay().showText(70)
                .text("Right-click the golem again to let it go")
                .pointAt(target.add(0, 0.8, 0))
                .placeNearTarget();
        scene.idle(80);

        scene.overlay().showControls(util.vector().of(2.5, 3, 2.5), Pointing.DOWN, 40)
                .rightClick()
                .withItem(fork);
        scene.idle(10);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .colored(PonderPalette.BLUE)
                .text("Used in the air, the fork rings out towards the nearest geode")
                .pointAt(util.vector().of(2.5, 2.5, 2.5))
                .placeNearTarget();
        scene.idle(90);
        scene.markAsFinished();
    }
}
