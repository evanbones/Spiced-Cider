package com.evandev.spicedcider.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class SpicedCiderConfig {
    public static final ModConfigSpec STARTUP_SPEC;
    public static final Startup STARTUP;

    public static final ModConfigSpec COMMON_SPEC;
    public static final Common COMMON;

    public static final ModConfigSpec CLIENT_SPEC;
    public static final Client CLIENT;

    static {
        final Pair<Startup, ModConfigSpec> startupPair = new ModConfigSpec.Builder().configure(Startup::new);
        STARTUP_SPEC = startupPair.getRight();
        STARTUP = startupPair.getLeft();

        final Pair<Common, ModConfigSpec> commonPair = new ModConfigSpec.Builder().configure(Common::new);
        COMMON_SPEC = commonPair.getRight();
        COMMON = commonPair.getLeft();

        final Pair<Client, ModConfigSpec> clientPair = new ModConfigSpec.Builder().configure(Client::new);
        CLIENT_SPEC = clientPair.getRight();
        CLIENT = clientPair.getLeft();
    }

    public static class Startup {
        public final ModConfigSpec.BooleanValue cooksCollectionDripstoneFix;
        public final ModConfigSpec.BooleanValue blockBoxWoodVariants;
        public final ModConfigSpec.BooleanValue skeletonHealthNerf;
        public final ModConfigSpec.BooleanValue filterUnusedResourcePackNamespaces;

        public Startup(ModConfigSpec.Builder builder) {
            cooksCollectionDripstoneFix = builder
                    .comment("Fix Cooks' Collection's salted dripstone block causing occlusion/culling issues by basing it on vanilla dripstone block. Requires Cooks' Collection. Requires a restart to take effect.")
                    .translation("option.spicedcider.cooksCollectionDripstoneFix")
                    .define("cooksCollectionDripstoneFix", true);

            blockBoxWoodVariants = builder
                    .comment("Register Every Compat wood-type variants (seats, palisades) for The Block Box. Requires The Block Box, Every Compat, and Moonlight Lib. Requires a restart to take effect.")
                    .translation("option.spicedcider.blockBoxWoodVariants")
                    .define("blockBoxWoodVariants", true);

            skeletonHealthNerf = builder
                    .comment("Skeletons and strays get 12 max health. Requires a restart to take effect.")
                    .translation("option.spicedcider.skeletonHealthNerf")
                    .define("skeletonHealthNerf", true);

            filterUnusedResourcePackNamespaces = builder
                    .comment("Filter out resource pack namespaces that aren't used by any loaded mod, speeding up client asset loading. Requires a restart to take effect.")
                    .translation("option.spicedcider.filterUnusedResourcePackNamespaces")
                    .define("filterUnusedResourcePackNamespaces", true);
        }
    }

    public static class Common {
        public final ModConfigSpec.BooleanValue wisteriaLeafDensityFix;

        public final ModConfigSpec.BooleanValue bedExplosionPrevention;
        public final ModConfigSpec.BooleanValue respawnAnchorExplosionPrevention;
        public final ModConfigSpec.BooleanValue endCrystalPlaceAnywhere;
        public final ModConfigSpec.BooleanValue endCrystalHealing;
        public final ModConfigSpec.BooleanValue keepBrokenItems;
        public final ModConfigSpec.BooleanValue wolvesWearAnyArmor;
        public final ModConfigSpec.BooleanValue spiderRangedWebAttacks;
        public final ModConfigSpec.BooleanValue removeQuasiConnectivity;

        public final ModConfigSpec.BooleanValue disableRecipeBookTracking;
        public final ModConfigSpec.BooleanValue skipRedundantBlockCacheRebuild;

        public Common(ModConfigSpec.Builder builder) {
            builder.push("compat");

            wisteriaLeafDensityFix = builder
                    .comment("Fix Environmental's wisteria trees generating with sparse/patchy leaves. Requires Environmental.")
                    .translation("option.spicedcider.wisteriaLeafDensityFix")
                    .define("wisteriaLeafDensityFix", true);

            builder.pop();
            builder.push("gameplay");

            bedExplosionPrevention = builder
                    .comment("Beds don't explode outside the Overworld; instead they show a message and wake sleeping villagers.")
                    .translation("option.spicedcider.bedExplosionPrevention")
                    .define("bedExplosionPrevention", true);

            respawnAnchorExplosionPrevention = builder
                    .comment("Respawn anchors don't explode outside dimensions where they work; instead they show a message.")
                    .translation("option.spicedcider.respawnAnchorExplosionPrevention")
                    .define("respawnAnchorExplosionPrevention", true);

            endCrystalPlaceAnywhere = builder
                    .comment("End crystals can be placed on any block, not just obsidian/bedrock.")
                    .translation("option.spicedcider.endCrystalPlaceAnywhere")
                    .define("endCrystalPlaceAnywhere", true);

            endCrystalHealing = builder
                    .comment("End crystals with no beam target heal nearby damaged entities and render a beam to their heal target.")
                    .translation("option.spicedcider.endCrystalHealing")
                    .define("endCrystalHealing", true);

            keepBrokenItems = builder
                    .comment("Enchanted, named, or elytra items become \"Broken\" instead of being destroyed when they run out of durability.")
                    .translation("option.spicedcider.keepBrokenItems")
                    .define("keepBrokenItems", true);

            wolvesWearAnyArmor = builder
                    .comment("Tamed wolves can be equipped with horse/animal armor as body armor.")
                    .translation("option.spicedcider.wolvesWearAnyArmor")
                    .define("wolvesWearAnyArmor", true);

            spiderRangedWebAttacks = builder
                    .comment("Spiders switch to shooting cobweb projectiles at range when their target is trapped.")
                    .translation("option.spicedcider.spiderRangedWebAttacks")
                    .define("spiderRangedWebAttacks", true);

            removeQuasiConnectivity = builder
                    .comment("Removes quasi-connectivity from pistons, dispensers, and droppers.")
                    .translation("option.spicedcider.removeQuasiConnectivity")
                    .define("removeQuasiConnectivity", true);

            builder.pop();
            builder.push("performance");

            disableRecipeBookTracking = builder
                    .comment("Disable recipe book unlock tracking/saving/syncing entirely, since it's unused. Saves memory and disk I/O.")
                    .translation("option.spicedcider.disableRecipeBookTracking")
                    .define("disableRecipeBookTracking", true);

            skipRedundantBlockCacheRebuild = builder
                    .comment("Skip the redundant block shape cache rebuild that happens on every tag reload.")
                    .translation("option.spicedcider.skipRedundantBlockCacheRebuild")
                    .define("skipRedundantBlockCacheRebuild", true);

            builder.pop();
        }
    }

    public static class Client {
        public final ModConfigSpec.BooleanValue randomWorldNaming;

        public final ModConfigSpec.BooleanValue customDeathSound;
        public final ModConfigSpec.BooleanValue hideMelancholicHungerTooltip;

        public Client(ModConfigSpec.Builder builder) {
            builder.push("naming");

            randomWorldNaming = builder
                    .comment("Auto-generate a random world name on the world creation screen, with a reroll button (if Modern World Creation is present).")
                    .translation("option.spicedcider.randomWorldNaming")
                    .define("randomWorldNaming", true);

            builder.pop();
            builder.push("misc");

            customDeathSound = builder
                    .comment("Play a custom sound when you die.")
                    .translation("option.spicedcider.customDeathSound")
                    .define("customDeathSound", true);

            hideMelancholicHungerTooltip = builder
                    .comment("Hide Melancholic Hunger's regeneration tooltip lines from item tooltips. Requires Melancholic Hunger.")
                    .translation("option.spicedcider.hideMelancholicHungerTooltip")
                    .define("hideMelancholicHungerTooltip", true);

            builder.pop();
        }
    }
}
