package com.evandev.spicedcider.compat.sodium;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.FogType;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL13C;
import org.lwjgl.opengl.GL30C;

public final class WaterDepthFog {
    public static final String FOG_UNIFORM = "u_SpicedCiderWaterFog";
    public static final String DEPTH_UNIFORM = "u_SpicedCiderSceneDepth";
    public static final String UV_UNIFORM = "u_SpicedCiderWaterUV";
    public static final int SPRITE_SLOTS = 4;
    public static final int DEPTH_UNIT = 4;

    public static final float DEEP_BRIGHTNESS = 0.35F;
    public static final float SURFACE_BLEND = 0.6F;
    public static final float SHALLOW_ALPHA = 0.55F;
    public static final float DENSITY = 0.12F;
    public static final float[] DISABLED = new float[4];
    private static final float[] NO_RECT = {2.0F, 2.0F, -1.0F, -1.0F};
    private static final ResourceLocation[] WATER_SPRITES = {
            ResourceLocation.fromNamespaceAndPath("bigwater", "block/water_still"),
            ResourceLocation.fromNamespaceAndPath("bigwater", "block/water_flow"),
            ResourceLocation.withDefaultNamespace("block/water_still"),
            ResourceLocation.withDefaultNamespace("block/water_flow")
    };

    private static final String VERTEX_DECLARATION = "uniform vec2 u_TexCoordShrink;";
    private static final String VERTEX_END = "v_Material = _material_params;";
    private static final String FRAGMENT_DECLARATION = "uniform float u_FogEnd;";
    private static final String FRAGMENT_OUTPUT = "fragColor = _linearFog(color, v_FragDistance, u_FogColor, u_FogStart, u_FogEnd);";

    private static final String VERTEX_HEADER = """
            uniform vec2 u_TexCoordShrink;
            uniform vec4 u_SpicedCiderWaterUV[%d];
            flat out float v_SpicedCiderWater;
            out float v_SpicedCiderViewDistance;

            bool spicedciderInRect(vec2 uv, vec4 rect) {
                return all(greaterThanEqual(uv, rect.xy - 0.0001)) && all(lessThanEqual(uv, rect.zw + 0.0001));
            }""".formatted(SPRITE_SLOTS);
    private static final String VERTEX_BODY = """
            v_Material = _material_params;
                v_SpicedCiderWater = 0.0;
                for (int i = 0; i < %d; i++) {
                    if (spicedciderInRect(_vert_tex_diffuse_coord, u_SpicedCiderWaterUV[i])) v_SpicedCiderWater = 1.0;
                }
                v_SpicedCiderViewDistance = length(position);""".formatted(SPRITE_SLOTS);
    private static final String FRAGMENT_HEADER = """
            uniform float u_FogEnd;
            uniform vec4 u_SpicedCiderWaterFog;
            uniform sampler2D u_SpicedCiderSceneDepth;
            uniform mat4 u_ProjectionMatrix;
            flat in float v_SpicedCiderWater;
            in float v_SpicedCiderViewDistance;

            float spicedciderEyeDepth(float depth) {
                return u_ProjectionMatrix[3][2] / (depth * 2.0 - 1.0 + u_ProjectionMatrix[2][2]);
            }""";
    private static final String FRAGMENT_BODY = """
            if (u_SpicedCiderWaterFog.a > 0.0 && v_SpicedCiderWater > 0.5) {
                    float spicedciderSurface = spicedciderEyeDepth(gl_FragCoord.z);
                    float spicedciderScene = spicedciderEyeDepth(texelFetch(u_SpicedCiderSceneDepth, ivec2(gl_FragCoord.xy), 0).r);
                    float spicedciderThickness = max(spicedciderScene - spicedciderSurface, 0.0) * v_SpicedCiderViewDistance / max(spicedciderSurface, 0.0001);
                    float spicedciderMurk = 1.0 - exp(-spicedciderThickness * u_SpicedCiderWaterFog.a);
                    color.a *= mix(u_SpicedCiderWaterFog.b, 1.0, spicedciderMurk);
                    vec3 spicedciderDeep = color.rgb * u_SpicedCiderWaterFog.r;
                    color.rgb = mix(color.rgb, spicedciderDeep, spicedciderMurk * u_SpicedCiderWaterFog.g);
                    float spicedciderAlpha = color.a + (1.0 - color.a) * spicedciderMurk;
                    color.rgb = (color.rgb * color.a + spicedciderDeep * (1.0 - color.a) * spicedciderMurk) / max(spicedciderAlpha, 0.0001);
                    color.a = spicedciderAlpha;
                }
                fragColor = _linearFog(color, v_FragDistance, u_FogColor, u_FogStart, u_FogEnd);""";

    private static TextureTarget sceneDepth;
    private static boolean active;
    private static final float[] FOG = {DEEP_BRIGHTNESS, SURFACE_BLEND, SHALLOW_ALPHA, DENSITY};
    private static final float[][] rects = new float[SPRITE_SLOTS][];

    public static String patchVertex(String source) {
        if (source == null || !source.contains(VERTEX_DECLARATION) || !source.contains(VERTEX_END)) return source;
        return source.replace(VERTEX_DECLARATION, VERTEX_HEADER).replace(VERTEX_END, VERTEX_BODY);
    }

    public static String patchFragment(String source) {
        if (source == null || !source.contains(FRAGMENT_DECLARATION) || !source.contains(FRAGMENT_OUTPUT)) return source;
        return source.replace(FRAGMENT_DECLARATION, FRAGMENT_HEADER).replace(FRAGMENT_OUTPUT, FRAGMENT_BODY);
    }

    public static boolean enabled() {
        if (!SpicedCiderConfig.clientOr(SpicedCiderConfig.CLIENT.waterDepthFog, false)) return false;
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.level != null && minecraft.gameRenderer.getMainCamera().getFluidInCamera() == FogType.NONE;
    }

    public static void beforeTranslucent() {
        active = false;
        if (!enabled()) return;
        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget main = minecraft.getMainRenderTarget();
        if (GL11C.glGetInteger(GL30C.GL_DRAW_FRAMEBUFFER_BINDING) != main.frameBufferId) return;

        if (sceneDepth == null) {
            sceneDepth = new TextureTarget(main.width, main.height, true, Minecraft.ON_OSX);
        }
        if (main.isStencilEnabled() && !sceneDepth.isStencilEnabled()) {
            sceneDepth.enableStencil();
        } else if (sceneDepth.width != main.width || sceneDepth.height != main.height) {
            sceneDepth.resize(main.width, main.height, Minecraft.ON_OSX);
        }
        sceneDepth.copyDepthFrom(main);
        main.bindWrite(false);

        updateSprites(minecraft);
        active = true;
    }

    public static boolean bind() {
        if (!active || sceneDepth == null) return false;
        GlStateManager._activeTexture(GL13C.GL_TEXTURE0 + DEPTH_UNIT);
        GlStateManager._bindTexture(sceneDepth.getDepthTextureId());
        GlStateManager._activeTexture(GL13C.GL_TEXTURE0);
        return true;
    }

    public static float[] fog() {
        return active ? FOG : DISABLED;
    }

    public static float[] rect(int slot) {
        float[] rect = rects[slot];
        return rect == null ? NO_RECT : rect;
    }

    private static void updateSprites(Minecraft minecraft) {
        TextureAtlas atlas = minecraft.getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS);
        for (int i = 0; i < SPRITE_SLOTS; i++) {
            TextureAtlasSprite sprite = atlas.getSprite(WATER_SPRITES[i]);
            rects[i] = sprite.contents().name().equals(MissingTextureAtlasSprite.getLocation())
                    ? null
                    : new float[]{sprite.getU0(), sprite.getV0(), sprite.getU1(), sprite.getV1()};
        }
    }
}
