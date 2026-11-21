package com.evandev.spicedcider.compat;

import net.neoforged.fml.ModList;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

public class ModVersions {
    public static final String CONNECTIBLE_CHAINS_MIN = "2.6.0";

    public static boolean atLeast(String modId, String version) {
        return ModList.get().getModContainerById(modId)
                .map(container -> container.getModInfo().getVersion().compareTo(new DefaultArtifactVersion(version)) >= 0)
                .orElse(false);
    }

    public static boolean connectibleChains() {
        return atLeast("connectiblechains", CONNECTIBLE_CHAINS_MIN);
    }
}
