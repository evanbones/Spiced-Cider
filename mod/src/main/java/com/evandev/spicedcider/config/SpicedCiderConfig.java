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
        public Common(ModConfigSpec.Builder builder) {

        }
    }
}