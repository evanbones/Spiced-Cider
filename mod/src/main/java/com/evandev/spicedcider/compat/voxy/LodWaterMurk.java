package com.evandev.spicedcider.compat.voxy;

import com.evandev.spicedcider.compat.sodium.WaterDepthFog;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL20C;
import org.lwjgl.opengl.GL33C;
import org.lwjgl.opengl.GL43C;
import org.lwjgl.opengl.GL45C;

public final class LodWaterMurk {
    private static final int LOCATION = 22;
    private static final int DEPTH_UNIT = 7;
    private static final String DECLARATION = "layout(location = 41) uniform vec4 roxyTerrainFogColour;";
    private static final String COLOUR = "colour = computeColour(texPos, colour);";
    private static final String MURK = """
            colour = computeColour(texPos, colour);
                #ifdef TRANSLUCENT
                if (spicedciderWaterMurk.x > 0.0 && tintingState() != 0u) {
                    vec2 spicedciderNdc = gl_FragCoord.xy / vec2(textureSize(spicedciderLodDepth, 0)) * 2.0 - 1.0;
                    mat4 spicedciderInverse = inverse(MVP);
                    vec4 spicedciderSurface = spicedciderInverse * vec4(spicedciderNdc, SCREEN2NDC_DEPTH(gl_FragCoord.z), 1.0);
                    vec4 spicedciderFloor = spicedciderInverse * vec4(spicedciderNdc, SCREEN2NDC_DEPTH(texelFetch(spicedciderLodDepth, ivec2(gl_FragCoord.xy), 0).r), 1.0);
                    float spicedciderThickness = abs(spicedciderFloor.w) < 0.000001 ? 100000.0
                            : distance(spicedciderSurface.xyz / spicedciderSurface.w, spicedciderFloor.xyz / spicedciderFloor.w);
                    float spicedciderMurk = 1.0 - exp(-spicedciderThickness * spicedciderWaterMurk.w);
                    colour.a *= mix(spicedciderWaterShallow.x, 1.0, spicedciderMurk);
                    vec3 spicedciderDeep = colour.rgb * spicedciderWaterMurk.y;
                    colour.rgb = mix(colour.rgb, spicedciderDeep, spicedciderMurk * spicedciderWaterMurk.z);
                    float spicedciderAlpha = colour.a + (1.0 - colour.a) * spicedciderMurk;
                    colour.rgb = (colour.rgb * colour.a + spicedciderDeep * (1.0 - colour.a) * spicedciderMurk) / max(spicedciderAlpha, 0.0001);
                    colour.a = spicedciderAlpha;
                }
                #endif""";

    private static volatile boolean patched;
    private static int depthCopy;
    private static int copyWidth;
    private static int copyHeight;
    private static int copyFormat;

    public static String patch(String source) {
        if (source == null || !source.contains(DECLARATION) || source.indexOf(COLOUR) < 0
                || source.indexOf(COLOUR) != source.lastIndexOf(COLOUR)) return source;
        patched = true;
        return source
                .replace(DECLARATION, DECLARATION + "\nlayout(location = " + LOCATION + ") uniform vec4 spicedciderWaterMurk;"
                        + "\nlayout(location = " + (LOCATION + 1) + ") uniform vec4 spicedciderWaterShallow;"
                        + "\nlayout(binding = " + DEPTH_UNIT + ") uniform sampler2D spicedciderLodDepth;")
                .replace(COLOUR, MURK);
    }

    public static void beforeTranslucent(int depthTexture, int width, int height, int format) {
        if (!patched) return;
        if (!WaterDepthFog.enabled() || width <= 0 || height <= 0) {
            GL20C.glUniform4f(LOCATION, 0.0F, 0.0F, 0.0F, 0.0F);
            return;
        }
        if (depthCopy == 0 || copyWidth != width || copyHeight != height || copyFormat != format) {
            if (depthCopy != 0) GL11C.glDeleteTextures(depthCopy);
            depthCopy = GL45C.glCreateTextures(GL11C.GL_TEXTURE_2D);
            GL45C.glTextureStorage2D(depthCopy, 1, format, width, height);
            GL45C.glTextureParameteri(depthCopy, GL11C.GL_TEXTURE_MIN_FILTER, GL11C.GL_NEAREST);
            GL45C.glTextureParameteri(depthCopy, GL11C.GL_TEXTURE_MAG_FILTER, GL11C.GL_NEAREST);
            copyWidth = width;
            copyHeight = height;
            copyFormat = format;
        }
        GL43C.glCopyImageSubData(depthTexture, GL11C.GL_TEXTURE_2D, 0, 0, 0, 0, depthCopy, GL11C.GL_TEXTURE_2D, 0, 0, 0, 0, width, height, 1);
        GL33C.glBindSampler(DEPTH_UNIT, 0);
        GL45C.glBindTextureUnit(DEPTH_UNIT, depthCopy);
        GL20C.glUniform4f(LOCATION, 1.0F, WaterDepthFog.DEEP_BRIGHTNESS, WaterDepthFog.SURFACE_BLEND, WaterDepthFog.DENSITY);
        GL20C.glUniform4f(LOCATION + 1, WaterDepthFog.SHALLOW_ALPHA, 0.0F, 0.0F, 0.0F);
    }
}
