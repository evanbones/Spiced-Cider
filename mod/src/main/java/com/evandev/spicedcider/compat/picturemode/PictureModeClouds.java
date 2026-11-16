package com.evandev.spicedcider.compat.picturemode;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.Arrays;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, value = Dist.CLIENT)
public final class PictureModeClouds {
    private static final boolean LOADED = ModList.get().isLoaded("picturemode");
    private static final boolean SODIUM = ModList.get().isLoaded("sodium");
    private static final int STEP = 8;
    private static final int COARSE = 4;
    private static final float FAR = 30000.0F;
    private static final float INSIDE = 6.0F;
    private static final float OUTSIDE = 72.0F;
    private static final float[][] LAYERS = {
            {-6.0F, 1.10F, 0.76F, 0.0F},
            {-2.5F, 0.50F, 0.88F, 37.0F},
            {1.0F, 0.28F, 1.00F, 91.0F}
    };

    private static Field field;
    private static long fieldTime;

    public static boolean active() {
        return LOADED && SpicedCiderConfig.CLIENT_SPEC.isLoaded() && SpicedCiderConfig.CLIENT.pictureModeClouds.get() && PictureModeBridge.enabled();
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES || !active()) return;
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) return;

        Vec3 camera = event.getCamera().getPosition();
        int chunkX = SectionPos.blockToSectionCoord(camera.x);
        int chunkZ = SectionPos.blockToSectionCoord(camera.z);
        int renderDistance = minecraft.options.getEffectiveRenderDistance();
        long now = Util.getMillis();
        if (field == null || field.chunkX != chunkX || field.chunkZ != chunkZ || field.renderDistance != renderDistance || now - fieldTime > 250) {
            field = Field.build(level, chunkX, chunkZ, renderDistance, camera);
            fieldTime = now;
        }

        float time = (now % 3_600_000L) / 1000.0F * 0.8F;
        Vec3 color = level.getCloudColor(event.getPartialTick().getGameTimeDeltaPartialTick(false));
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        for (int l = 0; l < LAYERS.length; l++) {
            draw(buffer, field, camera, LAYERS[l], l == 0, time, color);
        }
        MeshData mesh = buffer.build();
        if (mesh == null) return;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferUploader.drawWithShader(mesh);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    private static void draw(BufferBuilder buffer, Field f, Vec3 camera, float[] layer, boolean base, float time, Vec3 color) {
        for (int cz = 0; cz < f.coarseSize; cz++) {
            for (int cx = 0; cx < f.coarseSize; cx++) {
                int x0 = cx * COARSE, z0 = cz * COARSE;
                if (f.coarseCells[cz * f.coarseSize + cx]) {
                    quad(buffer, f, camera, layer, base, time, color, x0, z0, COARSE);
                    continue;
                }
                for (int z = z0; z < z0 + COARSE; z++) {
                    for (int x = x0; x < x0 + COARSE; x++) {
                        if (f.visible(x, z)) quad(buffer, f, camera, layer, base, time, color, x, z, 1);
                    }
                }
            }
        }
        if (base) {
            frame(buffer, f, camera, layer, color);
            curtain(buffer, f, camera, layer, time, color);
        }
    }

    private static void curtain(BufferBuilder buffer, Field f, Vec3 camera, float[] layer, float time, Vec3 color) {
        int n = f.size;
        for (int z = 0; z < n; z++) {
            for (int x = 0; x < n; x++) {
                if (x + 1 < n && f.inside(x, z - 1) != f.inside(x, z)) {
                    wall(buffer, f, camera, layer, time, color, x, z, x + 1, z, 0.0F, f.inside(x, z - 1) ? 0.5F : -0.5F);
                }
                if (z + 1 < n && f.inside(x - 1, z) != f.inside(x, z)) {
                    wall(buffer, f, camera, layer, time, color, x, z, x, z + 1, f.inside(x - 1, z) ? 0.5F : -0.5F, 0.0F);
                }
            }
        }
    }

    private static void wall(BufferBuilder buffer, Field f, Vec3 camera, float[] layer, float time, Vec3 color, int ax, int az, int bx, int bz, float ox, float oz) {
        float bottom = (float) (f.bottom - camera.y);
        float top0 = (float) (surface(f, layer, time, ax, az) - camera.y);
        float top1 = (float) (surface(f, layer, time, bx, bz) - camera.y);
        float x0 = (float) (f.originX + ax * STEP + ox - camera.x), z0 = (float) (f.originZ + az * STEP + oz - camera.z);
        float x1 = (float) (f.originX + bx * STEP + ox - camera.x), z1 = (float) (f.originZ + bz * STEP + oz - camera.z);
        float top = layer[2] * 0.94F, low = layer[2] * 0.7F;
        float tr = (float) color.x * top, tg = (float) color.y * top, tb = (float) color.z * top;
        float lr = (float) color.x * low, lg = (float) color.y * low, lb = (float) color.z * low;
        buffer.addVertex(x0, bottom, z0).setColor(lr, lg, lb, 1.0F);
        buffer.addVertex(x0, top0, z0).setColor(tr, tg, tb, 1.0F);
        buffer.addVertex(x1, top1, z1).setColor(tr, tg, tb, 1.0F);
        buffer.addVertex(x1, bottom, z1).setColor(lr, lg, lb, 1.0F);
    }

    private static float surface(Field f, float[] layer, float time, int gx, int gz) {
        int i = gz * f.size + gx;
        float x = f.originX + gx * STEP;
        float z = f.originZ + gz * STEP;
        float edgeDistance = Math.max(0.0F, f.edge[i]);
        float far = smooth(OUTSIDE - 24.0F, OUTSIDE, edgeDistance);
        float bump = (fbm(x / 29.0F - layer[3], z / 29.0F + layer[3], time * 0.012F) - 0.35F) * 10.0F * smooth(0.0F, 24.0F, edgeDistance) * (1.0F - far);
        return f.height[i] + layer[0] + 6.0F + bump;
    }

    private static void quad(BufferBuilder buffer, Field f, Vec3 camera, float[] layer, boolean base, float time, Vec3 color, int x, int z, int span) {
        vertex(buffer, f, camera, layer, base, time, color, x, z);
        vertex(buffer, f, camera, layer, base, time, color, x, z + span);
        vertex(buffer, f, camera, layer, base, time, color, x + span, z + span);
        vertex(buffer, f, camera, layer, base, time, color, x + span, z);
    }

    private static void vertex(BufferBuilder buffer, Field f, Vec3 camera, float[] layer, boolean base, float time, Vec3 color, int gx, int gz) {
        int i = gz * f.size + gx;
        float x = f.originX + gx * STEP;
        float z = f.originZ + gz * STEP;
        float edgeDistance = Math.max(0.0F, f.edge[i]);
        float n = fbm((x + time * 6.0F) / 40.0F + layer[3], z / 40.0F - layer[3], time * 0.02F);
        float far = smooth(OUTSIDE - 24.0F, OUTSIDE, edgeDistance);
        float coverage = layer[1];
        float alpha = coverage >= 1.0F ? 1.0F : smooth(1.0F - coverage - 0.12F, 1.0F - coverage + 0.12F, n) * (1.0F - far);
        float y = surface(f, layer, time, gx, gz);
        float shade = base ? Mth.lerp(far, layer[2], 0.94F) : layer[2];
        float light = shade * (0.88F + 0.12F * Mth.lerp(far, n, 0.5F));
        buffer.addVertex((float) (x - camera.x), (float) (y - camera.y), (float) (z - camera.z))
                .setColor((float) color.x * light, (float) color.y * light, (float) color.z * light, alpha);
    }

    private static void frame(BufferBuilder buffer, Field f, Vec3 camera, float[] layer, Vec3 color) {
        float minX = f.originX, minZ = f.originZ;
        float maxX = f.originX + (f.size - 1) * STEP, maxZ = f.originZ + (f.size - 1) * STEP;
        float outMinX = (float) camera.x - FAR, outMaxX = (float) camera.x + FAR;
        float outMinZ = (float) camera.z - FAR, outMaxZ = (float) camera.z + FAR;
        float y = (float) (f.base + layer[0] + 6.0F - camera.y);
        float light = 0.94F * 0.94F;
        float r = (float) color.x * light, g = (float) color.y * light, b = (float) color.z * light;
        float[][] rects = {
                {outMinX, outMinZ, outMaxX, minZ},
                {outMinX, maxZ, outMaxX, outMaxZ},
                {outMinX, minZ, minX, maxZ},
                {maxX, minZ, outMaxX, maxZ}
        };
        for (float[] rect : rects) {
            float x0 = (float) (rect[0] - camera.x), z0 = (float) (rect[1] - camera.z);
            float x1 = (float) (rect[2] - camera.x), z1 = (float) (rect[3] - camera.z);
            buffer.addVertex(x0, y, z0).setColor(r, g, b, 1.0F);
            buffer.addVertex(x0, y, z1).setColor(r, g, b, 1.0F);
            buffer.addVertex(x1, y, z1).setColor(r, g, b, 1.0F);
            buffer.addVertex(x1, y, z0).setColor(r, g, b, 1.0F);
        }
    }

    private static final class Field {
        int chunkX;
        int chunkZ;
        int renderDistance;
        int size;
        int coarseSize;
        float originX;
        float originZ;
        float base;
        float[] edge;
        float[] height;
        boolean[] coarseCells;

        float bottom;

        boolean visible(int x, int z) {
            return !inside(x, z);
        }

        boolean inside(int x, int z) {
            if (x < 0 || z < 0 || x >= size - 1 || z >= size - 1) return false;
            int i = z * size + x;
            return edge[i] < 0.0F || edge[i + 1] < 0.0F || edge[i + size] < 0.0F || edge[i + size + 1] < 0.0F;
        }

        static Field build(ClientLevel level, int chunkX, int chunkZ, int renderDistance, Vec3 camera) {
            Field f = new Field();
            f.bottom = level.getMinBuildHeight();
            f.chunkX = chunkX;
            f.chunkZ = chunkZ;
            f.renderDistance = renderDistance;
            int radius = renderDistance + 5;
            int chunks = radius * 2 + 1;
            boolean[] rendered = new boolean[chunks * chunks];
            float limit = renderDistance * 16.0F;
            for (int dz = -radius; dz <= radius; dz++) {
                for (int dx = -radius; dx <= radius; dx++) {
                    int cx = chunkX + dx, cz = chunkZ + dz;
                    double nearX = Mth.clamp(camera.x, cx * 16 - 1, cx * 16 + 17) - camera.x;
                    double nearZ = Mth.clamp(camera.z, cz * 16 - 1, cz * 16 + 17) - camera.z;
                    if (nearX * nearX + nearZ * nearZ >= limit * limit) continue;
                    if (!level.hasChunk(cx, cz)) continue;
                    float top = level.getMinBuildHeight();
                    for (int sz = 0; sz < 16; sz += 5) {
                        for (int sx = 0; sx < 16; sx += 5) {
                            top = Math.max(top, level.getHeight(Heightmap.Types.MOTION_BLOCKING, cx * 16 + sx, cz * 16 + sz));
                        }
                    }
                    if (SODIUM && !SodiumSections.built(cx, Mth.floor(top) - 1, cz)) continue;
                    int i = (dz + radius) * chunks + dx + radius;
                    rendered[i] = true;
                }
            }

            float[] edgeHeights = new float[chunks * chunks];
            int edgeCount = 0;
            for (int dz = -radius + 1; dz < radius; dz++) {
                for (int dx = -radius + 1; dx < radius; dx++) {
                    int i = (dz + radius) * chunks + dx + radius;
                    if (rendered[i] && (!rendered[i - 1] || !rendered[i + 1] || !rendered[i - chunks] || !rendered[i + chunks])) {
                        edgeHeights[edgeCount++] = level.getHeight(Heightmap.Types.MOTION_BLOCKING, (chunkX + dx) * 16 + 8, (chunkZ + dz) * 16 + 8);
                    }
                }
            }
            float sea = level.getSeaLevel() + 1;
            f.base = edgeCount == 0 ? sea : Math.max(sea, percentile(edgeHeights, edgeCount, 0.6F) + 2.0F);

            int extent = radius * 16;
            f.originX = chunkX * 16 + 8 - extent;
            f.originZ = chunkZ * 16 + 8 - extent;
            f.coarseSize = Mth.ceil(extent * 2.0F / STEP / COARSE);
            int cells = f.coarseSize * COARSE;
            f.size = cells + 1;
            int points = f.size * f.size;
            f.edge = new float[points];
            f.height = new float[points];
            int reach = Mth.ceil(OUTSIDE / 16.0F) + 1;
            float chunkOriginX = (chunkX - radius) * 16, chunkOriginZ = (chunkZ - radius) * 16;
            for (int gz = 0; gz < f.size; gz++) {
                for (int gx = 0; gx < f.size; gx++) {
                    float x = f.originX + gx * STEP, z = f.originZ + gz * STEP;
                    int ccx = Mth.floor((x - chunkOriginX) / 16.0F), ccz = Mth.floor((z - chunkOriginZ) / 16.0F);
                    boolean inside = ccx >= 0 && ccz >= 0 && ccx < chunks && ccz < chunks && rendered[ccz * chunks + ccx];
                    float best = inside ? INSIDE + 8.0F : OUTSIDE;
                    int nearX = 0, nearZ = 0;
                    boolean near = false;
                    for (int oz = -reach; oz <= reach; oz++) {
                        for (int ox = -reach; ox <= reach; ox++) {
                            int kx = ccx + ox, kz = ccz + oz;
                            boolean other = kx >= 0 && kz >= 0 && kx < chunks && kz < chunks && rendered[kz * chunks + kx];
                            if (other == inside) continue;
                            float minX = chunkOriginX + kx * 16, minZ = chunkOriginZ + kz * 16;
                            float ddx = Math.max(Math.max(minX - x, 0.0F), x - (minX + 16));
                            float ddz = Math.max(Math.max(minZ - z, 0.0F), z - (minZ + 16));
                            float d = Mth.sqrt(ddx * ddx + ddz * ddz);
                            if (d < best) {
                                best = d;
                                nearX = Mth.floor(Mth.clamp(x, minX, minX + 15.0F));
                                nearZ = Mth.floor(Mth.clamp(z, minZ, minZ + 15.0F));
                                near = true;
                            }
                        }
                    }
                    int i = gz * f.size + gx;
                    f.edge[i] = inside ? -best : best;
                    if (inside) {
                        f.height[i] = level.getHeight(Heightmap.Types.MOTION_BLOCKING, Mth.floor(x), Mth.floor(z)) + 2.0F;
                    } else {
                        if (near) {
                            float edgeHeight = level.getHeight(Heightmap.Types.MOTION_BLOCKING, nearX, nearZ) + 2.0F;
                            float width = Mth.clamp((edgeHeight - f.base) * 2.5F, 40.0F, OUTSIDE - 8.0F);
                            f.height[i] = Mth.lerp(smooth(4.0F, width, best), edgeHeight, f.base);
                        } else {
                            f.height[i] = f.base;
                        }
                    }
                }
            }


            f.coarseCells = new boolean[f.coarseSize * f.coarseSize];
            for (int cz = 0; cz < f.coarseSize; cz++) {
                for (int cx = 0; cx < f.coarseSize; cx++) {
                    boolean far = true;
                    for (int z = cz * COARSE; z <= cz * COARSE + COARSE && far; z++) {
                        for (int x = cx * COARSE; x <= cx * COARSE + COARSE; x++) {
                            int i = z * f.size + x;
                            if (f.edge[i] < OUTSIDE || f.height[i] != f.base) {
                                far = false;
                                break;
                            }
                        }
                    }
                    f.coarseCells[cz * f.coarseSize + cx] = far;
                }
            }
            return f;
        }
    }

    private static float percentile(float[] values, int count, float fraction) {
        float[] sorted = Arrays.copyOf(values, count);
        Arrays.sort(sorted);
        return sorted[Math.min(count - 1, (int) (count * fraction))];
    }

    private static float smooth(float from, float to, float value) {
        float t = Mth.clamp((value - from) / (to - from), 0.0F, 1.0F);
        return t * t * (3.0F - 2.0F * t);
    }

    private static float fbm(float x, float z, float t) {
        float sum = 0.0F, amplitude = 0.5F, norm = 0.0F;
        for (int octave = 0; octave < 3; octave++) {
            sum += amplitude * noise(x + t, z - t);
            norm += amplitude;
            x *= 2.03F;
            z *= 2.03F;
            amplitude *= 0.5F;
        }
        return sum / norm;
    }

    private static float noise(float x, float z) {
        int ix = Mth.floor(x), iz = Mth.floor(z);
        float fx = x - ix, fz = z - iz;
        float ux = fx * fx * (3.0F - 2.0F * fx), uz = fz * fz * (3.0F - 2.0F * fz);
        float a = hash(ix, iz), b = hash(ix + 1, iz), c = hash(ix, iz + 1), d = hash(ix + 1, iz + 1);
        return Mth.lerp(uz, Mth.lerp(ux, a, b), Mth.lerp(ux, c, d));
    }

    private static float hash(int x, int z) {
        int h = x * 374761393 + z * 668265263;
        h = (h ^ (h >>> 13)) * 1274126177;
        return ((h ^ (h >>> 16)) & 0xFFFFFF) / (float) 0xFFFFFF;
    }
}
