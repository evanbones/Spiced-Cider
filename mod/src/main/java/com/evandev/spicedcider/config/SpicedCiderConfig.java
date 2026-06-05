package com.evandev.spicedcider.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class SpicedCiderConfig {
    public static final ModConfigSpec COMMON_SPEC;
    public static final Common COMMON;

    public static final ModConfigSpec CLIENT_SPEC;
    public static final Client CLIENT;

    static {
        final Pair<Common, ModConfigSpec> commonPair = new ModConfigSpec.Builder().configure(Common::new);
        COMMON_SPEC = commonPair.getRight();
        COMMON = commonPair.getLeft();

        final Pair<Client, ModConfigSpec> clientPair = new ModConfigSpec.Builder().configure(Client::new);
        CLIENT_SPEC = clientPair.getRight();
        CLIENT = clientPair.getLeft();
    }

    public static class Client {
        public final ModConfigSpec.ConfigValue<String> exportDirectory;

        public Client(ModConfigSpec.Builder builder) {
            builder.push("general");
            exportDirectory = builder.comment("The directory path where assets are exported. Can be relative to the game directory or an absolute path (e.g., C:/Users/Name/Downloads).")
                    .define("exportDirectory", "resourcepacks/spicedcider_resources/assets");
            builder.pop();
        }
    }

    public static class Common {
        public final ModConfigSpec.IntValue structureSearchTimeout;
        public final ModConfigSpec.BooleanValue useFastStructureLookup;
        public final ModConfigSpec.IntValue mapSearchRadius;
        public final ModConfigSpec.IntValue globalSearchRadius;
        public final ModConfigSpec.IntValue locateSearchRadius;
        public final ModConfigSpec.DoubleValue spacingSeparationModifier;

        public final ModConfigSpec.IntValue minimumStructureDistance;
        public final ModConfigSpec.BooleanValue minimumStructureDistanceEnabled;

        public final ModConfigSpec.BooleanValue logDuplicatedSalt;

        public Common(ModConfigSpec.Builder builder) {
            builder.push("structures");
            structureSearchTimeout = builder.comment("Maximum time (in seconds) a structure search is allowed to take.")
                    .defineInRange("structureSearchTimeout", 50, 1, 300);
            useFastStructureLookup = builder.comment("Enables faster structure search.")
                    .define("useFastStructureLookup", true);
            mapSearchRadius = builder.comment("Maximum radius map items can search for structures.")
                    .defineInRange("mapSearchRadius", 40, 1, 200);
            globalSearchRadius = builder.comment("Global maximum structure search radius.")
                    .defineInRange("globalSearchRadius", 70, 1, 200);
            locateSearchRadius = builder.comment("Search radius for the locate structure command.")
                    .defineInRange("locateSearchRadius", 110, 1, 500);
            spacingSeparationModifier = builder.comment("Adjusts structure spacing and separation.")
                    .defineInRange("spacingSeparationModifier", 1.0, 0.1, 10.0);

            builder.push("minimum_structure_distance");
            minimumStructureDistance = builder.comment("Set a minimum distance in blocks between structures generated.")
                    .defineInRange("distance", 32, 16, 512);
            minimumStructureDistanceEnabled = builder.comment("Enable minimum structure distance.")
                    .define("enabled", false);
            builder.pop();

            logDuplicatedSalt = builder.comment("Log duplicated salt values to prevent overlapping structures.")
                    .define("logDuplicatedSalt", true);
            builder.pop();
        }
    }
}