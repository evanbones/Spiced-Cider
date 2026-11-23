package com.evandev.spicedcider.client.models;

import com.evandev.spicedcider.content.casting.CrucibleBlock;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CrucibleCampfireModel extends BakedModelWrapper<BakedModel> {
    static final ModelProperty<Boolean> RING = new ModelProperty<>();
    private static final float LOW = 2.0F / 16.0F;
    private static final float HIGH = 14.0F / 16.0F;
    private static final float NEAR = 3.0F / 16.0F;
    private static final float FAR = 13.0F / 16.0F;
    private static final float CAMPFIRE_BOTTOM = 4.0F / 16.0F;
    private static final float TOP = 1.0F;

    private final float bottom;
    private volatile TextureAtlasSprite ringSprite;
    private volatile List<BakedQuad> ring = List.of();

    public CrucibleCampfireModel(BakedModel original) {
        this(original, CAMPFIRE_BOTTOM);
    }

    public CrucibleCampfireModel(BakedModel original, float bottom) {
        super(original);
        this.bottom = bottom;
    }

    @Override
    public @NotNull ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData data) {
        ModelData base = super.getModelData(level, pos, state, data);
        BlockState above = level.getBlockState(pos.above());
        if (!(above.getBlock() instanceof CrucibleBlock) || CrucibleBlock.drop(above) == 0.0F) {
            return base;
        }
        return base.derive().with(RING, Boolean.TRUE).build();
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType) {
        List<BakedQuad> quads = super.getQuads(state, side, rand, data, renderType);
        if (side != null || !Boolean.TRUE.equals(data.get(RING))) {
            return quads;
        }
        List<BakedQuad> kept = new ArrayList<>(quads.size());
        TextureAtlasSprite fire = null;
        for (BakedQuad quad : quads) {
            if (isFlame(quad)) {
                fire = quad.getSprite();
            } else {
                kept.add(quad);
            }
        }
        if (fire == null) {
            return quads;
        }
        kept.addAll(ring(fire));
        return kept;
    }

    private static boolean isFlame(BakedQuad quad) {
        int[] vertices = quad.getVertices();
        float minX = Float.MAX_VALUE, maxX = -Float.MAX_VALUE, minY = Float.MAX_VALUE, maxY = -Float.MAX_VALUE, minZ = Float.MAX_VALUE, maxZ = -Float.MAX_VALUE;
        for (int i = 0; i < 4; i++) {
            int offset = i * IQuadTransformer.STRIDE + IQuadTransformer.POSITION;
            float x = Float.intBitsToFloat(vertices[offset]);
            float y = Float.intBitsToFloat(vertices[offset + 1]);
            float z = Float.intBitsToFloat(vertices[offset + 2]);
            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);
            minZ = Math.min(minZ, z);
            maxZ = Math.max(maxZ, z);
        }
        float epsilon = 1.0E-3F;
        return maxX - minX > epsilon && maxY - minY > epsilon && maxZ - minZ > epsilon && quad.getSprite().contents().name().getPath().contains("fire");
    }

    private List<BakedQuad> ring(TextureAtlasSprite sprite) {
        if (sprite == this.ringSprite) {
            return this.ring;
        }
        QuadBakingVertexConsumer baker = new QuadBakingVertexConsumer();
        List<BakedQuad> built = new ArrayList<>(8);
        for (float z : new float[]{NEAR, FAR}) {
            built.add(quad(baker, sprite, Direction.NORTH, HIGH, z, LOW, z));
            built.add(quad(baker, sprite, Direction.SOUTH, LOW, z, HIGH, z));
        }
        for (float x : new float[]{NEAR, FAR}) {
            built.add(quad(baker, sprite, Direction.WEST, x, LOW, x, HIGH));
            built.add(quad(baker, sprite, Direction.EAST, x, HIGH, x, LOW));
        }
        List<BakedQuad> result = List.copyOf(built);
        this.ring = result;
        this.ringSprite = sprite;
        return result;
    }

    private BakedQuad quad(QuadBakingVertexConsumer baker, TextureAtlasSprite sprite, Direction direction, float leftX, float leftZ, float rightX, float rightZ) {
        baker.setSprite(sprite);
        baker.setDirection(direction);
        baker.setShade(false);
        baker.setHasAmbientOcclusion(false);
        baker.setTintIndex(-1);
        float u0 = sprite.getU(LOW);
        float u1 = sprite.getU(HIGH);
        float v0 = sprite.getV(0.0F);
        float v1 = sprite.getV(TOP - this.bottom);
        vertex(baker, direction, leftX, TOP, leftZ, u0, v0);
        vertex(baker, direction, leftX, this.bottom, leftZ, u0, v1);
        vertex(baker, direction, rightX, this.bottom, rightZ, u1, v1);
        vertex(baker, direction, rightX, TOP, rightZ, u1, v0);
        return baker.bakeQuad();
    }

    private static void vertex(QuadBakingVertexConsumer baker, Direction direction, float x, float y, float z, float u, float v) {
        baker.addVertex(x, y, z).setColor(-1).setUv(u, v).setUv2(0, 0).setNormal(direction.getStepX(), direction.getStepY(), direction.getStepZ());
    }
}
