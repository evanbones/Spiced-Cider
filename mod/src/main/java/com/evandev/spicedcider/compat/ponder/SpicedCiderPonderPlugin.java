package com.evandev.spicedcider.compat.ponder;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.compat.ponder.scenes.GolemScenes;
import com.evandev.spicedcider.compat.ponder.scenes.MultiblockScenes;
import com.evandev.spicedcider.compat.ponder.scenes.PackScenes;
import com.evandev.spicedcider.compat.ponder.scenes.RedstoneScenes;
import net.createmod.ponder.api.registration.MultiSceneBuilder;
import net.createmod.ponder.api.registration.MultiTagBuilder;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

import java.util.Arrays;
import java.util.List;

public class SpicedCiderPonderPlugin implements PonderPlugin {

    private static final ResourceLocation GOLEMS = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "golems");
    private static final ResourceLocation MULTIBLOCKS = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "multiblocks");
    private static final ResourceLocation REDSTONE = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "redstone_components");

    @Override
    public String getModId() {
        return SpicedCider.MOD_ID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        components(helper, "minecraft:iron_block", "minecraft:carved_pumpkin", "minecraft:jack_o_lantern")
                .addStoryBoard("golems/iron_golem", GolemScenes::ironGolem);
        components(helper, "minecraft:snow_block", "minecraft:carved_pumpkin", "minecraft:jack_o_lantern")
                .addStoryBoard("golems/snow_golem", GolemScenes::snowGolem);
        components(helper, "minecraft:copper_block", "minecraft:carved_pumpkin", "minecraft:jack_o_lantern")
                .addStoryBoard("golems/copper_golem", GolemScenes::copperGolem);
        components(helper, "minecraft:copper_chest")
                .addStoryBoard("golems/copper_golem_sorting", GolemScenes::copperGolemSorting);
        components(helper, "minecraft:barrel", "minecraft:carved_pumpkin", "minecraft:jack_o_lantern", "spicedcider:sputter_spawn_egg")
                .addStoryBoard("golems/sputter", GolemScenes::sputter);
        components(helper, "minecraft:wither_skeleton_skull", "minecraft:soul_sand", "minecraft:soul_soil")
                .addStoryBoard("golems/wither", GolemScenes::wither);
        components(helper, "spelunkery:tuning_fork")
                .addStoryBoard("golems/tuning_fork", GolemScenes::tuningFork);

        components(helper, "minecraft:obsidian", "dimensional_tears:dimensional_tears_bottle")
                .addStoryBoard("multiblocks/nether_portal", MultiblockScenes::netherPortal);
        components(helper, "minecraft:beacon")
                .addStoryBoard("multiblocks/beacon", MultiblockScenes::beacon);
        components(helper, "minecraft:conduit")
                .addStoryBoard("multiblocks/conduit", MultiblockScenes::conduit);

        components(helper, "minecraft:comparator")
                .addStoryBoard("redstone/comparator_modes", RedstoneScenes::comparatorModes)
                .addStoryBoard("redstone/comparator_containers", RedstoneScenes::comparatorContainers);
        components(helper, "minecraft:repeater")
                .addStoryBoard("redstone/repeater", RedstoneScenes::repeater);
        components(helper, "minecraft:observer")
                .addStoryBoard("redstone/observer", RedstoneScenes::observer);
        components(helper, "minecraft:daylight_detector")
                .addStoryBoard("redstone/daylight_detector", RedstoneScenes::daylightDetector);
        components(helper, "minecraft:hopper")
                .addStoryBoard("redstone/hopper", RedstoneScenes::hopper);
        components(helper, "minecraft:crafter")
                .addStoryBoard("redstone/crafter", RedstoneScenes::crafter);
        components(helper, "minecraft:note_block")
                .addStoryBoard("redstone/note_block", RedstoneScenes::noteBlock);

        components(helper, "nomansland:tap")
                .addStoryBoard("misc/tapping", PackScenes::tapping);
        components(helper, "farmersdelight:cooking_pot", "farmersdelight:stove", "farmersdelight:skillet")
                .addStoryBoard("misc/cooking_pot", PackScenes::cookingPot);
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        helper.registerTag(GOLEMS)
                .addToIndex()
                .item(Items.CARVED_PUMPKIN, true, false)
                .title("Golems")
                .description("Blocks that can be built into living helpers, or something worse")
                .register();
        helper.registerTag(MULTIBLOCKS)
                .addToIndex()
                .item(Items.BEACON, true, false)
                .title("Multiblock Structures")
                .description("Blocks that only work as part of a larger structure")
                .register();
        helper.registerTag(REDSTONE)
                .addToIndex()
                .item(Items.COMPARATOR, true, false)
                .title("Redstone Components")
                .description("Blocks that send, shape or react to redstone signals")
                .register();

        tag(helper, GOLEMS, "minecraft:carved_pumpkin", "minecraft:iron_block", "minecraft:snow_block", "minecraft:copper_block",
                "minecraft:copper_chest", "minecraft:barrel", "minecraft:wither_skeleton_skull", "spelunkery:tuning_fork");
        tag(helper, MULTIBLOCKS, "minecraft:obsidian", "minecraft:beacon", "minecraft:conduit");
        tag(helper, REDSTONE, "minecraft:comparator", "minecraft:repeater", "minecraft:observer", "minecraft:daylight_detector",
                "minecraft:hopper", "minecraft:crafter", "minecraft:note_block");
    }

    private static MultiSceneBuilder components(PonderSceneRegistrationHelper<ResourceLocation> helper, String... ids) {
        return helper.forComponents(present(ids));
    }

    private static void tag(PonderTagRegistrationHelper<ResourceLocation> helper, ResourceLocation tag, String... ids) {
        MultiTagBuilder.Tag<ResourceLocation> builder = helper.addToTag(tag);
        present(ids).forEach(builder::add);
    }

    private static List<ResourceLocation> present(String... ids) {
        return Arrays.stream(ids)
                .map(ResourceLocation::parse)
                .filter(BuiltInRegistries.ITEM::containsKey)
                .toList();
    }
}
