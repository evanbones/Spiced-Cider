package com.evandev.spicedcider.compat.ponder;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.compat.ponder.scenes.GolemScenes;
import com.evandev.spicedcider.compat.ponder.scenes.MultiblockScenes;
import com.evandev.spicedcider.compat.ponder.scenes.PackScenes;
import com.evandev.spicedcider.compat.ponder.scenes.ProcessScenes;
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
        components(helper, "environmental:pinecone", "minecraft:carved_pumpkin", "minecraft:jack_o_lantern")
                .addStoryBoard("golems/pinecone_golem", GolemScenes::pineconeGolem);
        components(helper, "snowyspirit:gingerbread", "snowyspirit:gingerbread_frosted", "minecraft:carved_pumpkin", "minecraft:jack_o_lantern",
                "snowyspirit:gumdrop_white", "snowyspirit:gumdrop_light_gray", "snowyspirit:gumdrop_gray", "snowyspirit:gumdrop_dark_gray", "snowyspirit:gumdrop_black", "snowyspirit:gumdrop_brown", "snowyspirit:gumdrop_red", "snowyspirit:gumdrop_orange", "snowyspirit:gumdrop_yellow", "snowyspirit:gumdrop_lime", "snowyspirit:gumdrop_green", "snowyspirit:gumdrop_cyan", "snowyspirit:gumdrop_light_blue", "snowyspirit:gumdrop_blue", "snowyspirit:gumdrop_purple", "snowyspirit:gumdrop_magenta", "snowyspirit:gumdrop_pink")
                .addStoryBoard("golems/gingerbread_golem", GolemScenes::gingerbreadGolem);
        components(helper, "minecraft:copper_block", "minecraft:carved_pumpkin", "minecraft:jack_o_lantern",
                "minecraft:copper_golem_statue", "minecraft:exposed_copper_golem_statue", "minecraft:weathered_copper_golem_statue",
                "minecraft:oxidized_copper_golem_statue", "minecraft:waxed_copper_golem_statue", "minecraft:waxed_exposed_copper_golem_statue",
                "minecraft:waxed_weathered_copper_golem_statue", "minecraft:waxed_oxidized_copper_golem_statue")
                .addStoryBoard("golems/copper_golem", GolemScenes::copperGolem);
        components(helper, "minecraft:copper_chest")
                .addStoryBoard("golems/copper_golem_sorting", GolemScenes::copperGolemSorting);
        components(helper, "minecraft:barrel", "minecraft:carved_pumpkin", "minecraft:jack_o_lantern")
                .addStoryBoard("golems/sputter", GolemScenes::sputter);
        components(helper, "minecraft:hay_block", "minecraft:carved_pumpkin", "minecraft:jack_o_lantern", "bountifulfares:sun_hat")
                .addStoryBoard("golems/straw_golem", GolemScenes::strawGolem);
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
        components(helper, "minecraft:pointed_dripstone")
                .addStoryBoard("misc/dripstone_farm", PackScenes::dripstoneFarm);
        components(helper, "minecraft:crying_obsidian", "frame_changer:crying_obsidian_bricks", "frame_changer:crying_polished_obsidian",
                "frame_changer:crying_obsidian_pillar", "frame_changer:crying_chiseled_obsidian")
                .addStoryBoard("misc/weeping_obsidian", PackScenes::weepingObsidian);
        components(helper, "culturaldelights:salt_block", "culturaldelights:salt_spike")
                .addStoryBoard("misc/salt_spikes", PackScenes::saltSpikes);
        components(helper, "minecraft:cauldron", "minecraft:nether_wart")
                .addStoryBoard("misc/cauldron_brewing", ProcessScenes::cauldronBrewing);
        components(helper, "exposure:lightroom", "exposure:developed_black_and_white_film", "exposure:developed_color_film", "exposure:photograph")
                .addStoryBoard("misc/lightroom", ProcessScenes::lightroom);
        components(helper, "exposure:black_and_white_film", "exposure:color_film", "exposure:high_sensitivity_black_and_white_film",
                "exposure:high_sensitivity_color_film", "exposure:camera")
                .addStoryBoard("misc/film_developing", ProcessScenes::filmDeveloping);
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
                "minecraft:copper_chest", "minecraft:barrel", "minecraft:hay_block", "minecraft:wither_skeleton_skull", "spelunkery:tuning_fork", "environmental:pinecone", "snowyspirit:gingerbread");
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
