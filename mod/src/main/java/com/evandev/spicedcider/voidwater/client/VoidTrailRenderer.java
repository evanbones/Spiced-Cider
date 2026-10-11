package com.evandev.spicedcider.voidwater.client;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.voidwater.VoidTrailColumns;
import com.evandev.spicedcider.voidwater.VoidTrails;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.textures.FluidSpriteCache;
import org.joml.Matrix4f;

import java.util.IdentityHashMap;
import java.util.Map;

public class VoidTrailRenderer {
    private static final boolean SMOOTH_LIGHTING = ModList.get().isLoaded("sodium");
    private static final float INSET = 0.001F;

    public static void render(RenderLevelStageEvent event, ClientLevel level, VoidTrailColumns columns, Vec3 camera) {
        int max = VoidTrails.maxLength();
        if (max == 0) return;

        double decay = SpicedCiderConfig.COMMON.voidWaterTrailDecay.get();
        float[] fade = new float[max + 1];
        for (int depth = 1; depth <= max; depth++) {
            fade[depth] = (float) Math.pow(1.0 - (double) depth / max, decay);
        }
        float[] alphas = new float[max + 1];
        Map<Fluid, TextureAtlasSprite[]> sprites = new IdentityHashMap<>();

        int minY = level.getMinBuildHeight();
        Frustum frustum = event.getFrustum();
        Matrix4f pose = event.getPoseStack().last().pose();
        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer buffer = null;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (Map.Entry<Long, byte[]> entry : columns.chunks().entrySet()) {
            int baseX = ChunkPos.getX(entry.getKey()) << 4;
            int baseZ = ChunkPos.getZ(entry.getKey()) << 4;
            if (frustum != null && !frustum.isVisible(new AABB(baseX, minY - max, baseZ, baseX + 16, minY + 1, baseZ + 16))) continue;

            byte[] lengths = entry.getValue();
            for (int i = 0; i < 256; i++) {
                int length = Math.min(lengths[i] & 0xFF, max);
                if (length == 0) continue;
                pos.set(baseX + (i & 15), minY, baseZ + (i >> 4));
                FluidState fluid = level.getFluidState(pos);
                if (fluid.isEmpty()) continue;
                if (buffer == null) buffer = buffers.getBuffer(RenderType.translucent());
                BlockPos column = pos.immutable();
                TextureAtlasSprite[] fluidSprites = sprites.get(fluid.getType());
                if (fluidSprites == null) {
                    fluidSprites = FluidSpriteCache.getFluidSprites(level, column, fluid);
                    sprites.put(fluid.getType(), fluidSprites);
                }
                renderTrail(buffer, pose, level, columns, lengths, i, column, fluid, fluidSprites, length, decay, fade, alphas, camera);
            }
        }

        if (buffer != null) buffers.endBatch(RenderType.translucent());
    }

    private static void renderTrail(VertexConsumer buffer, Matrix4f pose, ClientLevel level, VoidTrailColumns columns, byte[] lengths, int index, BlockPos pos,
                                    FluidState fluid, TextureAtlasSprite[] sprites, int length, double decay, float[] fade, float[] alphas, Vec3 camera) {
        int tint = IClientFluidTypeExtensions.of(fluid).getTintColor(fluid, level, pos);
        float alpha = (tint >> 24 & 0xFF) / 255.0F;
        float red = (tint >> 16 & 0xFF) / 255.0F;
        float green = (tint >> 8 & 0xFF) / 255.0F;
        float blue = (tint & 0xFF) / 255.0F;

        float fadedAlpha = (float) Math.pow(alpha, decay);
        alphas[0] = alpha;
        for (int depth = 1; depth <= length; depth++) {
            alphas[depth] = fadedAlpha * fade[depth];
        }

        float wx = (float) (pos.getX() - camera.x);
        float wy = (float) (pos.getY() - camera.y);
        float wz = (float) (pos.getZ() - camera.z);
        int flatLight = SMOOTH_LIGHTING ? LevelRenderer.getLightColor(level, pos) : sideFaceLight(level, pos);
        BlockState state = level.getBlockState(pos);

        TextureAtlasSprite side = sprites[1];
        float u0 = side.getU(0.0F);
        float u1 = side.getU(0.5F);
        float v0 = side.getV(0.0F);
        float v1 = side.getV(0.5F);

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos neighborPos = pos.relative(direction);
            int localX = (index & 15) + direction.getStepX();
            int localZ = (index >> 4) + direction.getStepZ();
            int neighborLength = localX >= 0 && localX < 16 && localZ >= 0 && localZ < 16
                    ? lengths[localZ << 4 | localX] & 0xFF
                    : columns.get(neighborPos.getX(), neighborPos.getZ());
            FluidState neighborFluid = level.getBlockState(neighborPos).getFluidState();
            if (neighborLength >= length && neighborFluid.getType().isSame(fluid.getType())) continue;
            boolean renderFace = LiquidBlockRenderer.shouldRenderFace(level, pos, fluid, state, direction, neighborFluid);
            if (!renderFace && neighborLength >= length) continue;
            int firstDepth = renderFace ? 1 : neighborLength + 1;

            float x1, x2, z1, z2;
            switch (direction) {
                case NORTH -> {
                    x1 = wx;
                    x2 = wx + 1.0F;
                    z1 = z2 = wz + INSET;
                }
                case SOUTH -> {
                    x1 = wx + 1.0F;
                    x2 = wx;
                    z1 = z2 = wz + 1.0F - INSET;
                }
                case WEST -> {
                    x1 = x2 = wx + INSET;
                    z1 = wz + 1.0F;
                    z2 = wz;
                }
                default -> {
                    x1 = x2 = wx + 1.0F - INSET;
                    z1 = wz;
                    z2 = wz + 1.0F;
                }
            }

            boolean zAxis = direction.getAxis() == Direction.Axis.Z;
            float shade = SMOOTH_LIGHTING ? (zAxis ? 0.8F : 0.6F) : level.getShade(Direction.UP, true) * level.getShade(zAxis ? Direction.NORTH : Direction.WEST, true);
            float flatR = shade * red;
            float flatG = shade * green;
            float flatB = shade * blue;

            int topLightL = flatLight;
            int topLightR = flatLight;
            float aoL = 1.0F;
            float aoR = 1.0F;
            if (SMOOTH_LIGHTING && firstDepth == 1) {
                long left = cornerLight(level, pos, direction, direction.getCounterClockWise());
                long right = cornerLight(level, pos, direction, direction.getClockWise());
                topLightL = (int) (left >>> 32);
                topLightR = (int) (right >>> 32);
                aoL = Float.intBitsToFloat((int) left);
                aoR = Float.intBitsToFloat((int) right);
            }

            for (int depth = firstDepth; depth <= length; depth++) {
                boolean first = depth == 1;
                float topAlpha = alphas[depth - 1];
                float bottomAlpha = alphas[depth];
                float yTop = wy + 1.0F - depth + INSET;
                float yBottom = wy - depth + INSET;
                int light1 = first ? topLightL : flatLight;
                int light2 = first ? topLightR : flatLight;
                float ao1 = first ? aoL : 1.0F;
                float ao2 = first ? aoR : 1.0F;

                vertex(buffer, pose, x1, yTop, z1, flatR * ao1, flatG * ao1, flatB * ao1, topAlpha, u0, v0, light1);
                vertex(buffer, pose, x2, yTop, z2, flatR * ao2, flatG * ao2, flatB * ao2, topAlpha, u1, v0, light2);
                vertex(buffer, pose, x2, yBottom, z2, flatR, flatG, flatB, bottomAlpha, u1, v1, flatLight);
                vertex(buffer, pose, x1, yBottom, z1, flatR, flatG, flatB, bottomAlpha, u0, v1, flatLight);

                vertex(buffer, pose, x1, yBottom, z1, flatR, flatG, flatB, bottomAlpha, u0, v1, flatLight);
                vertex(buffer, pose, x2, yBottom, z2, flatR, flatG, flatB, bottomAlpha, u1, v1, flatLight);
                vertex(buffer, pose, x2, yTop, z2, flatR * ao2, flatG * ao2, flatB * ao2, topAlpha, u1, v0, light2);
                vertex(buffer, pose, x1, yTop, z1, flatR * ao1, flatG * ao1, flatB * ao1, topAlpha, u0, v0, light1);
            }
        }

        TextureAtlasSprite cap = sprites[0];
        float cu0 = cap.getU(0.0F);
        float cu1 = cap.getU(1.0F);
        float cv0 = cap.getV(0.0F);
        float cv1 = cap.getV(1.0F);
        float capY = wy - length + INSET;
        float capShade = SMOOTH_LIGHTING ? 1.0F : level.getShade(Direction.DOWN, true);
        float capR = capShade * red;
        float capG = capShade * green;
        float capB = capShade * blue;
        float capAlpha = alphas[length];

        vertex(buffer, pose, wx, capY, wz + 1.0F, capR, capG, capB, capAlpha, cu0, cv1, flatLight);
        vertex(buffer, pose, wx, capY, wz, capR, capG, capB, capAlpha, cu0, cv0, flatLight);
        vertex(buffer, pose, wx + 1.0F, capY, wz, capR, capG, capB, capAlpha, cu1, cv0, flatLight);
        vertex(buffer, pose, wx + 1.0F, capY, wz + 1.0F, capR, capG, capB, capAlpha, cu1, cv1, flatLight);

        vertex(buffer, pose, wx + 1.0F, capY, wz + 1.0F, capR, capG, capB, capAlpha, cu1, cv1, flatLight);
        vertex(buffer, pose, wx + 1.0F, capY, wz, capR, capG, capB, capAlpha, cu1, cv0, flatLight);
        vertex(buffer, pose, wx, capY, wz, capR, capG, capB, capAlpha, cu0, cv0, flatLight);
        vertex(buffer, pose, wx, capY, wz + 1.0F, capR, capG, capB, capAlpha, cu0, cv1, flatLight);
    }

    private static long cornerLight(BlockAndTintGetter level, BlockPos pos, Direction face, Direction lateral) {
        BlockPos adjacent = pos.relative(face);
        BlockPos adjacentLateral = adjacent.relative(lateral);
        BlockPos adjacentDown = adjacent.below();
        BlockPos adjacentLateralDown = adjacentLateral.below();

        int l0 = sampleLightmap(level, adjacent);
        int l1 = sampleLightmap(level, adjacentLateral);
        int l2 = sampleLightmap(level, adjacentDown);
        int l3 = sampleLightmap(level, adjacentLateralDown);
        if (l0 == 0 || l1 == 0 || l2 == 0 || l3 == 0) {
            int min = minNonZero(minNonZero(l0, l1), minNonZero(l2, l3));
            l0 = Math.max(l0, min);
            l1 = Math.max(l1, min);
            l2 = Math.max(l2, min);
            l3 = Math.max(l3, min);
        }

        int blockSum = (l0 & 0xFF) + (l1 & 0xFF) + (l2 & 0xFF) + (l3 & 0xFF);
        int skySum = (l0 >>> 16 & 0xFF) + (l1 >>> 16 & 0xFF) + (l2 >>> 16 & 0xFF) + (l3 >>> 16 & 0xFF);
        int lightmap = blockSum >> 2 & 0xFF | (skySum >> 2 & 0xFF) << 16;
        float ao = (sampleAo(level, adjacent) + sampleAo(level, adjacentLateral) + sampleAo(level, adjacentDown) + sampleAo(level, adjacentLateralDown)) * 0.25F;
        return (long) lightmap << 32 | Float.floatToRawIntBits(ao) & 0xFFFFFFFFL;
    }

    private static int sampleLightmap(BlockAndTintGetter level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isSolidRender(level, pos) && state.getLightEmission(level, pos) == 0 ? 0 : LevelRenderer.getLightColor(level, state, pos);
    }

    private static float sampleAo(BlockAndTintGetter level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.getLightEmission(level, pos) != 0 ? 1.0F : state.getShadeBrightness(level, pos);
    }

    private static int minNonZero(int a, int b) {
        if (a == 0) return b;
        return b == 0 ? a : Math.min(a, b);
    }

    private static int sideFaceLight(BlockAndTintGetter level, BlockPos pos) {
        int here = LevelRenderer.getLightColor(level, pos);
        int above = LevelRenderer.getLightColor(level, pos.above());
        return Math.max(here & 0xFF, above & 0xFF) | Math.max(here >> 16 & 0xFF, above >> 16 & 0xFF) << 16;
    }

    private static void vertex(VertexConsumer buffer, Matrix4f pose, float x, float y, float z, float r, float g, float b, float a, float u, float v, int light) {
        buffer.addVertex(pose, x, y, z).setColor(r, g, b, a).setUv(u, v).setLight(light).setNormal(0.0F, 1.0F, 0.0F);
    }
}
