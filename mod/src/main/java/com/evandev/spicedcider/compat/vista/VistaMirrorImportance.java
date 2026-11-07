package com.evandev.spicedcider.compat.vista;

import net.mehvahdjukaar.moonlight.api.util.math.Vec2i;
import net.mehvahdjukaar.vista.client.textures.perspective.MirrorTextureManager;
import net.mehvahdjukaar.vista.common.mirror.MirrorBlockEntity;
import net.minecraft.world.phys.Vec3;

public final class VistaMirrorImportance {
    private static final double IMPORTANCE_FACTOR = 150.0;

    public static double scale(MirrorBlockEntity mirror, Vec3 eye, int recursionDepth) {
        Vec2i connected = mirror.getConnectedCount();
        double area = Math.max(1, connected.x()) * (double) Math.max(1, connected.y());
        double distSq = Math.max(1.0, eye.distanceToSqr(Vec3.atCenterOf(mirror.getBlockPos())));
        double scale = IMPORTANCE_FACTOR * area / distSq;

        int lod = MirrorTextureManager.distanceLod(eye, mirror.getBlockPos());
        if (lod > 0) {
            scale *= 1 << (2 * lod);
        }
        if (recursionDepth > 0) {
            scale /= 1 << Math.min(recursionDepth, 3);
        }
        return Math.min(1.0, scale);
    }
}
