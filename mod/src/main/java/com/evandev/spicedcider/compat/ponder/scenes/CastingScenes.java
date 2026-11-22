package com.evandev.spicedcider.compat.ponder.scenes;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.content.casting.CrucibleBlock;
import com.evandev.spicedcider.content.casting.CrucibleBlockEntity;
import com.evandev.spicedcider.content.casting.MouldBlockEntity;
import com.evandev.spicedcider.content.casting.smeltery.SmelteryBlock;
import com.evandev.spicedcider.content.casting.smeltery.SmelteryBlockEntity;
import com.evandev.spicedcider.registry.ModItems;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public class CastingScenes {

    private static Fluid molten(String metal) {
        return BuiltInRegistries.FLUID.get(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "molten_" + metal));
    }

    private static void crucible(SceneBuilder scene, BlockPos pos, FluidStack fluid, ItemStack... inputs) {
        scene.world().modifyBlockEntity(pos, CrucibleBlockEntity.class, be -> {
            for (int i = 0; i < CrucibleBlockEntity.INPUTS; i++) {
                be.takeLast();
            }
            for (ItemStack input : inputs) {
                be.insert(input);
            }
            be.setFluid(fluid.copy());
        });
    }

    private static void mould(SceneBuilder scene, BlockPos pos, FluidStack fluid, ItemStack result) {
        scene.world().modifyBlockEntity(pos, MouldBlockEntity.class, be -> be.setContents(fluid, result));
    }

    private static void smeltery(SceneBuilder scene, BlockPos pos, int fuel, List<FluidStack> metals, boolean active) {
        scene.world().modifyBlockEntity(pos, SmelteryBlockEntity.class, be -> be.setContents(new FluidStack(Fluids.LAVA, fuel), metals, active));
        scene.world().modifyBlock(pos, state -> state.setValue(SmelteryBlock.LIT, active), false);
        scene.world().modifyBlock(pos.east(), state -> state.setValue(SmelteryBlock.LIT, active), false);
    }

    private static void stream(SceneBuilder scene, BlockPos tap, Fluid fluid, float length) {
        if (!ModList.get().isLoaded("supplementaries")) {
            return;
        }
        scene.world().modifyBlockEntity(tap, net.minecraft.world.level.block.entity.BlockEntity.class, be -> TapStreamPonder.show(be, fluid, length));
    }

    public static void crucible(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("crucible", "Melting and pouring metal with a Crucible");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        BlockPos fire = util.grid().at(1, 1, 2);
        BlockPos pot = util.grid().at(1, 2, 2);
        BlockPos mould = util.grid().at(3, 1, 1);
        Fluid copper = molten("copper");

        scene.world().showSection(util.select().position(fire), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().position(pot), Direction.DOWN);
        scene.idle(15);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("A Crucible melts metal over heat")
                .pointAt(util.vector().centerOf(pot))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showControls(util.vector().topOf(pot), Pointing.DOWN, 50).rightClick().withItem(new ItemStack(Items.COPPER_INGOT));
        scene.idle(10);
        crucible(scene, pot, FluidStack.EMPTY, new ItemStack(Items.COPPER_INGOT), new ItemStack(Items.COPPER_INGOT), new ItemStack(Items.COPPER_INGOT), new ItemStack(Items.COPPER_INGOT));
        scene.overlay().showText(80)
                .text("Use metal on it to drop it in, up to four pieces at a time")
                .pointAt(util.vector().topOf(pot))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Campfires and Stoves give Low Heat, enough for Tin, Lead, Copper, Gold and Silver")
                .pointAt(util.vector().centerOf(fire))
                .placeNearTarget();
        scene.idle(40);
        for (int i = 1; i <= 4; i++) {
            scene.effects().emitParticles(util.vector().topOf(pot), scene.effects().simpleParticleEmitter(ParticleTypes.SMOKE, new Vec3(0, 0.05, 0)), 2, 3);
            scene.idle(10);
            ItemStack[] left = new ItemStack[4 - i];
            java.util.Arrays.fill(left, new ItemStack(Items.COPPER_INGOT));
            crucible(scene, pot, new FluidStack(copper, i * 250), left);
        }
        scene.idle(40);

        scene.world().setBlock(fire, Blocks.MAGMA_BLOCK.defaultBlockState(), true);
        scene.world().modifyBlock(pot, state -> state.setValue(CrucibleBlock.SEATED, false), false);
        scene.idle(10);
        scene.overlay().showText(90)
                .attachKeyFrame()
                .text("Iron, Cast Iron and Steel need High Heat: a Fire Pit, Lava, Magma, Fire or a lit Blaze Burner")
                .pointAt(util.vector().centerOf(fire))
                .placeNearTarget();
        scene.idle(100);

        scene.world().showSection(util.select().position(mould), Direction.DOWN);
        scene.idle(15);
        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("Moulds shape the molten metal into Ingots, Plates and other castings")
                .pointAt(util.vector().centerOf(mould))
                .placeNearTarget();
        scene.idle(80);

        scene.overlay().showControls(util.vector().topOf(pot), Pointing.DOWN, 40).rightClick().whileSneaking();
        scene.overlay().showText(80)
                .text("Sneak and use the Crucible with an empty hand to pick it up with its contents")
                .pointAt(util.vector().topOf(pot))
                .placeNearTarget();
        scene.idle(90);

        ItemStack held = new ItemStack(ModItems.CRUCIBLE.get());
        scene.overlay().showControls(util.vector().topOf(mould), Pointing.DOWN, 60).rightClick().withItem(held);
        scene.idle(10);
        for (int i = 1; i <= 5; i++) {
            scene.effects().emitParticles(util.vector().topOf(mould).add(0, 0.4, 0), scene.effects().simpleParticleEmitter(ParticleTypes.FALLING_LAVA, Vec3.ZERO), 2, 1);
            mould(scene, mould, new FluidStack(copper, i * 50), ItemStack.EMPTY);
            crucible(scene, pot, new FluidStack(copper, 1000 - i * 50));
            scene.idle(4);
        }
        scene.overlay().showText(80)
                .text("Hold use on a Mould to pour into it until it is full")
                .pointAt(util.vector().topOf(mould))
                .placeNearTarget();
        scene.idle(90);

        mould(scene, mould, FluidStack.EMPTY, new ItemStack(Items.COPPER_INGOT));
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Once it cools, take the casting out by hand, or let a Hopper underneath pull it out")
                .pointAt(util.vector().topOf(mould))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showText(80)
                .text("Pipes can also fill and drain a Crucible, and a full one fills a Bucket")
                .pointAt(util.vector().centerOf(pot))
                .placeNearTarget();
        scene.idle(90);
    }

    public static void smeltery(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("smeltery", "Melting and alloying metal in a Smeltery");
        scene.configureBasePlate(0, 0, 6);
        scene.showBasePlate();
        scene.idle(10);

        BlockPos controller = util.grid().at(2, 1, 2);
        BlockPos tap = util.grid().at(1, 2, 2);
        BlockPos mould = util.grid().at(1, 1, 2);
        Fluid copper = molten("copper");
        Fluid tin = molten("tin");
        Fluid bronze = molten("bronze");

        scene.world().showSection(util.select().fromTo(2, 1, 2, 3, 2, 3), Direction.DOWN);
        scene.idle(15);
        scene.overlay().showOutline(PonderPalette.GREEN, "smeltery", util.select().fromTo(2, 1, 2, 3, 2, 3), 80);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("The Smeltery is a large furnace that melts metal in bulk")
                .pointAt(util.vector().centerOf(controller))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showControls(util.vector().blockSurface(controller, Direction.NORTH), Pointing.RIGHT, 50).rightClick().withItem(new ItemStack(Items.LAVA_BUCKET));
        scene.idle(10);
        smeltery(scene, controller, 1000, List.of(), false);
        scene.overlay().showText(80)
                .text("Instead of sitting over heat it burns Lava from its own tank, filled by Bucket or by pumping it in")
                .pointAt(util.vector().blockSurface(controller, Direction.NORTH))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showText(90)
                .attachKeyFrame()
                .text("Up to eight items melt at once: put them in through its menu, or with a Hopper")
                .pointAt(util.vector().topOf(2, 2, 2))
                .placeNearTarget();
        scene.idle(20);
        smeltery(scene, controller, 950, List.of(new FluidStack(copper, 2250)), true);
        scene.idle(30);
        smeltery(scene, controller, 925, List.of(new FluidStack(copper, 2250), new FluidStack(tin, 750)), true);
        scene.idle(50);

        smeltery(scene, controller, 925, List.of(new FluidStack(bronze, 3000)), false);
        scene.overlay().showText(90)
                .attachKeyFrame()
                .text("Molten metals inside mix into alloys: three parts Copper to one part Tin make Bronze")
                .pointAt(util.vector().blockSurface(controller, Direction.NORTH))
                .placeNearTarget();
        scene.idle(100);

        scene.overlay().showText(80)
                .text("It also melts metals too hot for a Crucible, like Infernite and Prismarite")
                .pointAt(util.vector().blockSurface(controller, Direction.NORTH))
                .placeNearTarget();
        scene.idle(90);

        scene.world().showSection(util.select().position(mould), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().position(tap), Direction.EAST);
        scene.idle(15);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Hang a Tap on its side to pour onto a Mould below")
                .pointAt(util.vector().centerOf(tap))
                .placeNearTarget();
        scene.idle(20);
        stream(scene, tap, bronze, 0.9375F);
        for (int i = 1; i <= 4; i++) {
            mould(scene, mould, new FluidStack(bronze, Math.min(250, i * 70)), ItemStack.EMPTY);
            smeltery(scene, controller, 925, List.of(new FluidStack(bronze, 3000 - Math.min(250, i * 70))), false);
            scene.idle(10);
        }
        stream(scene, tap, null, 0.0F);
        scene.idle(40);

        mould(scene, mould, FluidStack.EMPTY, SceneHelper.item("alloyed:bronze_ingot"));
        scene.overlay().showText(80)
                .text("The metal that pours out first is the one at the bottom")
                .pointAt(util.vector().topOf(mould))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showText(90)
                .text("In the Smeltery's menu, click a metal to pour that one first, or shift-click it to empty it out")
                .pointAt(util.vector().blockSurface(controller, Direction.NORTH))
                .placeNearTarget();
        scene.idle(100);

        if (ModList.get().isLoaded("create")) {
            BlockPos spoutMould = util.grid().at(4, 1, 2);
            SmelteryPipingPonder.show(scene, util, spoutMould, bronze, 2750,
                    amount -> smeltery(scene, controller, 925, List.of(new FluidStack(bronze, amount)), false),
                    (fluid, result) -> mould(scene, spoutMould, fluid, result),
                    SceneHelper.item("alloyed:bronze_ingot"));
        }
    }
}
