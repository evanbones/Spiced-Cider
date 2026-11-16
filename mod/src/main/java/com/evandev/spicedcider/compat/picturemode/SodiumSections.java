package com.evandev.spicedcider.compat.picturemode;

import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
import net.minecraft.core.SectionPos;

final class SodiumSections {

    static boolean built(int chunkX, int y, int chunkZ) {
        SodiumWorldRenderer renderer = SodiumWorldRenderer.instanceNullable();
        return renderer == null || renderer.isSectionReady(chunkX, SectionPos.blockToSectionCoord(y), chunkZ);
    }
}
