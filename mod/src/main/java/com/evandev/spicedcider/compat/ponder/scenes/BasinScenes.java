package com.evandev.spicedcider.compat.ponder.scenes;

import com.evandev.spicedcider.content.brewing.Brewing;
import com.evandev.spicedcider.registry.ModPotions;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;
import java.util.Optional;

public class BasinScenes {

    public static void basinBrewing(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("basin_brewing", "Brewing Potions in a Basin");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        BlockPos basin = util.grid().at(2, 2, 2);
        BlockPos fire = util.grid().at(2, 1, 2);
        scene.world().showSection(util.select().position(fire), Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(util.select().position(basin), Direction.DOWN);
        scene.idle(15);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Potions are brewed in a Basin with heat right below it")
                .pointAt(util.vector().centerOf(basin))
                .placeNearTarget();
        scene.idle(90);
        scene.overlay().showText(70)
                .text("Campfires, Fire, Magma Blocks and Blaze Burners all give heat")
                .pointAt(util.vector().centerOf(fire))
                .placeNearTarget();
        scene.idle(80);

        setFluid(scene, basin, new FluidStack(Fluids.WATER, 1000));
        addItem(scene, util, basin, SceneHelper.item("minecraft:nether_wart"));
        stir(scene, util, basin);
        setFluid(scene, basin, potion(Potions.AWKWARD, List.of()));
        clearItems(scene, basin);
        scene.overlay().showText(90)
                .attachKeyFrame()
                .text("Add Nether Wart to Water and stir it with a Spoon to make a Clear Potion")
                .pointAt(util.vector().topOf(basin))
                .placeNearTarget();
        scene.idle(100);

        addItem(scene, util, basin, SceneHelper.item("minecraft:blaze_powder"));
        stir(scene, util, basin);
        setFluid(scene, basin, potion(Potions.STRENGTH, List.of()));
        clearItems(scene, basin);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Each ingredient turns the Clear Potion into the potion it belongs to")
                .pointAt(util.vector().topOf(basin))
                .placeNearTarget();
        scene.idle(90);

        addItem(scene, util, basin, SceneHelper.item("minecraft:blaze_rod"));
        scene.overlay().showText(90)
                .text("More ingredients of the same kind make it last longer and grow stronger, up to five in total")
                .pointAt(util.vector().topOf(basin))
                .placeNearTarget();
        stir(scene, util, basin);
        clearItems(scene, basin);
        scene.idle(80);

        scene.overlay().showText(80)
                .text("Ingredients can be thrown in or added with a right-click")
                .pointAt(util.vector().topOf(basin))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showControls(util.vector().topOf(basin), Pointing.DOWN, 40).rightClick()
                .withItem(new ItemStack(Items.GLASS_BOTTLE));
        scene.idle(10);
        setFluidAmount(scene, basin, 750);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Fill Glass Bottles from the Basin, four for a full Basin")
                .pointAt(util.vector().topOf(basin))
                .placeNearTarget();
        scene.idle(90);

        addItem(scene, util, basin, SceneHelper.item("minecraft:gunpowder"));
        stir(scene, util, basin);
        clearItems(scene, basin);
        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("Gunpowder turns the brew into Splash Potions")
                .pointAt(util.vector().topOf(basin))
                .placeNearTarget();
        scene.idle(80);
        scene.overlay().showText(70)
                .text("Creeper Spores turn it into Lingering Potions instead")
                .pointAt(util.vector().topOf(basin))
                .placeNearTarget();
        scene.idle(80);

        addItem(scene, util, basin, new ItemStack(Items.ARROW, 8));
        stir(scene, util, basin);
        clearItems(scene, basin);
        ItemStack tipped = PotionContents.createItemStack(Items.TIPPED_ARROW, Potions.STRENGTH);
        tipped.setCount(8);
        setOutput(scene, basin, tipped);
        setFluidAmount(scene, basin, 500);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Arrows stirred into the brew come out as Tipped Arrows, eight for each bottle's worth")
                .pointAt(util.vector().topOf(basin))
                .placeNearTarget();
        scene.idle(90);
        setOutput(scene, basin, ItemStack.EMPTY);

        addItem(scene, util, basin, SceneHelper.item("minecraft:magma_cream"));
        stir(scene, util, basin);
        clearItems(scene, basin);
        for (int i = 0; i < 4; i++) {
            scene.effects().emitParticles(util.vector().topOf(basin).add(0, -0.2, 0),
                    scene.effects().simpleParticleEmitter(ParticleTypes.SMOKE, util.vector().of(0, 0.05, 0)), 3, 1);
            scene.idle(5);
        }
        setFluid(scene, basin, tainted(500));
        scene.overlay().showText(90)
                .attachKeyFrame()
                .colored(PonderPalette.RED)
                .text("Ingredients of different kinds taint the brew, and it can't be brewed any further")
                .pointAt(util.vector().topOf(basin))
                .placeNearTarget();
        scene.idle(100);

        scene.overlay().showText(90)
                .text("A Mechanical Mixer above the Basin stirs the brew automatically")
                .pointAt(util.vector().topOf(basin))
                .placeNearTarget();
        scene.idle(100);
        scene.markAsFinished();
    }

    public static void ladleMixing(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("ladle_mixing", "Mixing in a Basin by hand");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        BlockPos basin = util.grid().at(2, 2, 2);
        BlockPos fire = util.grid().at(2, 1, 2);
        scene.world().modifyBlock(fire, state -> state.hasProperty(BlockStateProperties.LIT) ? state.setValue(BlockStateProperties.LIT, false) : state, false);
        scene.world().showSection(util.select().position(fire), Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(util.select().position(basin), Direction.DOWN);
        scene.idle(15);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("A Spoon stirs the contents of a Basin, mixing anything a Mechanical Mixer could")
                .pointAt(util.vector().centerOf(basin))
                .placeNearTarget();
        scene.idle(90);

        setFluid(scene, basin, new FluidStack(Fluids.WATER, 1000));
        addItem(scene, util, basin, SceneHelper.item("minecraft:dirt"));
        scene.overlay().showText(80)
                .text("Pour in fluids and drop or right-click in the ingredients")
                .pointAt(util.vector().topOf(basin))
                .placeNearTarget();
        scene.idle(90);

        stir(scene, util, basin);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Hold right-click with the Spoon until the mix is done")
                .pointAt(util.vector().topOf(basin))
                .placeNearTarget();
        clearItems(scene, basin);
        setFluidAmount(scene, basin, 750);
        setOutput(scene, basin, SceneHelper.item("minecraft:mud"));
        scene.idle(90);

        scene.overlay().showControls(util.vector().topOf(basin), Pointing.DOWN, 30).rightClick();
        scene.idle(10);
        setOutput(scene, basin, ItemStack.EMPTY);
        scene.overlay().showText(70)
                .text("Take the results out with an empty hand")
                .pointAt(util.vector().topOf(basin))
                .placeNearTarget();
        scene.idle(80);

        scene.world().modifyBlock(fire, state -> state.hasProperty(BlockStateProperties.LIT) ? state.setValue(BlockStateProperties.LIT, true) : state, false);
        scene.idle(10);
        scene.overlay().showText(90)
                .attachKeyFrame()
                .text("Heated recipes need a Campfire, Fire, Magma Block or Blaze Burner right below the Basin")
                .pointAt(util.vector().centerOf(fire))
                .placeNearTarget();
        scene.idle(100);

        scene.overlay().showText(90)
                .colored(PonderPalette.RED)
                .text("Superheated recipes can only be made with a Mechanical Mixer")
                .pointAt(util.vector().topOf(basin))
                .placeNearTarget();
        scene.idle(100);
        scene.markAsFinished();
    }

    public static void dripstoneLavaFarm(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("dripstone_lava_farm", "Farming Lava with Pointed Dripstone");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        BlockPos tip = util.grid().at(2, 3, 2);
        BlockPos basin = util.grid().at(2, 1, 2);
        scene.world().showSection(util.select().fromTo(2, 3, 2, 2, 4, 2), Direction.DOWN);
        scene.idle(15);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Pointed Dripstone hanging under a block drips whatever fluid sits on top of that block")
                .pointAt(util.vector().centerOf(tip))
                .placeNearTarget();
        scene.idle(90);

        scene.world().showSection(util.select().position(2, 5, 2), Direction.DOWN);
        scene.idle(10);
        scene.overlay().showText(70)
                .text("Put a Lava source on top")
                .pointAt(util.vector().centerOf(2, 5, 2))
                .placeNearTarget();
        scene.idle(80);

        scene.world().showSection(util.select().position(basin), Direction.DOWN);
        scene.idle(10);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("A Basin below the tip catches the drips, as long as there is only air in between")
                .pointAt(util.vector().centerOf(basin))
                .placeNearTarget();
        scene.idle(60);

        for (int i = 0; i < 3; i++) {
            emitDrip(scene, util, tip, ParticleTypes.DRIPPING_DRIPSTONE_LAVA);
            scene.idle(20);
        }
        setFluid(scene, basin, new FluidStack(Fluids.LAVA, 1000));
        scene.overlay().showText(80)
                .text("Lava drips slowly, but each drop fills the Basin with a full bucket")
                .pointAt(util.vector().centerOf(basin))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showControls(util.vector().topOf(basin), Pointing.DOWN, 40).rightClick().withItem(new ItemStack(Items.BUCKET));
        scene.idle(10);
        setFluid(scene, basin, FluidStack.EMPTY);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Collect the Lava with a Bucket, or pump it out of the Basin with pipes")
                .pointAt(util.vector().topOf(basin))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showText(80)
                .text("The Basin can sit up to 10 blocks below the tip. Water drips the same way, a quarter bucket at a time")
                .pointAt(util.vector().centerOf(tip))
                .placeNearTarget();
        scene.idle(90);
        scene.markAsFinished();
    }

    public static void dripstoneSaltFarm(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("dripstone_salt_farm", "Farming Salt with Pointed Dripstone");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        BlockPos tip = util.grid().at(1, 3, 2);
        BlockPos basin = util.grid().at(1, 2, 2);
        BlockPos fire = util.grid().at(1, 1, 2);
        scene.world().showSection(util.select().fromTo(1, 3, 2, 1, 5, 2), Direction.DOWN);
        scene.idle(15);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Pointed Dripstone under a block with Water on top drips Water")
                .pointAt(util.vector().centerOf(tip))
                .placeNearTarget();
        scene.idle(90);

        scene.world().showSection(util.select().position(fire), Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(util.select().position(basin), Direction.DOWN);
        scene.idle(10);
        scene.overlay().showText(90)
                .attachKeyFrame()
                .text("Catch it in a Basin with heat right below, such as a Campfire")
                .pointAt(util.vector().centerOf(basin))
                .placeNearTarget();
        scene.idle(60);

        for (int i = 0; i < 3; i++) {
            emitDrip(scene, util, tip, ParticleTypes.DRIPPING_DRIPSTONE_WATER);
            scene.idle(12);
            scene.effects().emitParticles(util.vector().topOf(basin).add(0, -0.2, 0),
                    scene.effects().simpleParticleEmitter(ParticleTypes.CLOUD, util.vector().of(0, 0.03, 0)), 2, 1);
            ItemStack salt = SceneHelper.item("spelunkery:salt");
            salt.setCount(i + 1);
            setOutput(scene, basin, salt);
            scene.idle(8);
        }
        scene.overlay().showText(90)
                .text("Every drop boils away and leaves Salt behind in the Basin")
                .pointAt(util.vector().topOf(basin))
                .placeNearTarget();
        scene.idle(100);

        scene.overlay().showText(80)
                .colored(PonderPalette.RED)
                .text("Without heat, the Basin just fills up with Water")
                .pointAt(util.vector().centerOf(fire))
                .placeNearTarget();
        scene.idle(90);

        BlockPos rock = util.grid().at(3, 4, 2);
        BlockPos rockTip = util.grid().at(3, 3, 2);
        BlockPos spike = util.grid().at(3, 1, 2);
        scene.world().showSection(util.select().fromTo(3, 3, 2, 3, 5, 2), Direction.DOWN);
        scene.idle(15);
        scene.overlay().showText(90)
                .attachKeyFrame()
                .text("Dripstone hanging under Rock Salt with Water on top grows Salt Spikes instead")
                .pointAt(util.vector().centerOf(rock))
                .placeNearTarget();
        scene.idle(60);
        for (int i = 0; i < 3; i++) {
            emitDrip(scene, util, rockTip, ParticleTypes.DRIPPING_DRIPSTONE_WATER);
            scene.idle(15);
        }
        scene.world().setBlock(spike, withFacing(SceneHelper.block("culturaldelights:salt_spike"), Direction.UP), false);
        scene.world().showSection(util.select().position(spike), Direction.UP);
        scene.idle(10);
        scene.overlay().showText(90)
                .text("A Salt Spike grows on the floor below the tip, one at a time until it is broken")
                .pointAt(util.vector().centerOf(spike))
                .placeNearTarget();
        scene.idle(100);
        scene.markAsFinished();
    }

    static void setFluid(SceneBuilder scene, BlockPos basin, FluidStack fluid) {
        scene.world().modifyBlockEntity(basin, BasinBlockEntity.class, be -> be.getTanks().getFirst().getPrimaryHandler().setFluid(fluid.copy()));
    }

    static void setFluidAmount(SceneBuilder scene, BlockPos basin, int amount) {
        scene.world().modifyBlockEntity(basin, BasinBlockEntity.class, be -> {
            FluidStack current = be.getTanks().getFirst().getPrimaryHandler().getFluid();
            be.getTanks().getFirst().getPrimaryHandler().setFluid(current.copyWithAmount(amount));
        });
    }

    private static void addItem(SceneBuilder scene, SceneBuildingUtil util, BlockPos basin, ItemStack stack) {
        scene.world().createItemEntity(util.vector().topOf(basin).add(0, 1, 0), util.vector().of(0, 0.05, 0), stack);
        scene.idle(15);
        scene.world().modifyEntities(net.minecraft.world.entity.item.ItemEntity.class, net.minecraft.world.entity.Entity::discard);
        scene.world().modifyBlockEntity(basin, BasinBlockEntity.class, be -> be.getInputInventory().insertItem(0, stack.copy(), false));
        scene.idle(5);
    }

    private static void clearItems(SceneBuilder scene, BlockPos basin) {
        scene.world().modifyBlockEntity(basin, BasinBlockEntity.class, be -> {
            for (int i = 0; i < be.getInputInventory().getSlots(); i++) {
                be.getInputInventory().setStackInSlot(i, ItemStack.EMPTY);
            }
        });
    }

    private static void setOutput(SceneBuilder scene, BlockPos basin, ItemStack stack) {
        scene.world().modifyBlockEntity(basin, BasinBlockEntity.class, be -> be.getOutputInventory().setStackInSlot(0, stack.copy()));
    }

    private static void stir(SceneBuilder scene, SceneBuildingUtil util, BlockPos basin) {
        scene.overlay().showControls(util.vector().topOf(basin), Pointing.DOWN, 50).rightClick()
                .withItem(SceneHelper.item("manual_labour:ladle"));
        for (int i = 0; i < 4; i++) {
            scene.effects().emitParticles(util.vector().topOf(basin).add(0, -0.25, 0),
                    scene.effects().simpleParticleEmitter(ParticleTypes.SPLASH, util.vector().of(0, 0.05, 0)), 3, 1);
            scene.idle(10);
        }
    }

    private static FluidStack potion(Holder<Potion> potion, List<net.minecraft.world.effect.MobEffectInstance> effects) {
        FluidStack stack = new FluidStack(potionFluid(), 1000);
        stack.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.of(potion), Optional.empty(), effects));
        return stack;
    }

    private static FluidStack tainted(int amount) {
        FluidStack stack = new FluidStack(potionFluid(), amount);
        stack.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.of(ModPotions.TAINTED), Optional.of(Brewing.TAINTED_COLOR), List.of()));
        return stack;
    }

    private static Fluid potionFluid() {
        return BuiltInRegistries.FLUID.get(ResourceLocation.fromNamespaceAndPath("create", "potion"));
    }

    static void emitDrip(SceneBuilder scene, SceneBuildingUtil util, BlockPos from, ParticleOptions particle) {
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
