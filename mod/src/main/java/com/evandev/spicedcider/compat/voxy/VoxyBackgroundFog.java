package com.evandev.spicedcider.compat.voxy;

import com.evandev.spicedcider.compat.modulation.ModulationFogBridge;
import net.neoforged.fml.ModList;
import org.lwjgl.opengl.GL20C;

public final class VoxyBackgroundFog {
    private static final boolean MODULATION = ModList.get().isLoaded("modulation");
    private static final int FLAG_LOCATION = 20;
    private static final String FOG_MIX = "colour.rgb = mix(colour.rgb, fogColour.rgb, fogLerp*fogColour.a);";
    private static final String VERSION = "#version";
    private static final String DECLARATIONS = """

            layout(binding = 9) uniform sampler2D spicedciderSky;
            layout(binding = 10) uniform sampler2D spicedciderClouds;
            layout(binding = 11) uniform sampler2D spicedciderCloudsDepth;
            layout(location = 20) uniform float spicedciderBackground;

            vec3 spicedcider_background(float fragmentDepth) {
                ivec2 pixel = ivec2(gl_FragCoord.xy);
                vec3 sky = texelFetch(spicedciderSky, pixel, 0).rgb;
                vec4 clouds = texelFetch(spicedciderClouds, pixel, 0);
                float behind = step(fragmentDepth, texelFetch(spicedciderCloudsDepth, pixel, 0).r);
                return mix(sky, clouds.rgb / (clouds.a > 0.0 ? clouds.a : 1.0), clouds.a * behind);
            }
            """;
    private static final String BACKGROUND_MIX = """
            vec3 spicedciderTarget = fogColour.rgb;
                    if (spicedciderBackground > 0.0) {
                        spicedciderTarget = mix(fogColour.rgb, spicedcider_background(depth), smoothstep(0.85, 1.0, fogLerp));
                    }
                    colour.rgb = mix(colour.rgb, spicedciderTarget, fogLerp*fogColour.a);""";

    private static volatile boolean patched;

    public static String patchShader(String source) {
        if (!MODULATION || source == null || !source.startsWith(VERSION) || !source.contains(FOG_MIX)) return source;
        patched = true;
        int firstLine = source.indexOf('\n');
        String declarations = source.contains("local_size_x") ? DECLARATIONS.replace("ivec2(gl_FragCoord.xy)", "ivec2(gl_GlobalInvocationID.xy)") : DECLARATIONS;
        return source.substring(0, firstLine) + declarations + source.substring(firstLine).replace(FOG_MIX, BACKGROUND_MIX);
    }

    public static void bind() {
        if (!patched) return;
        GL20C.glUniform1f(FLAG_LOCATION, ModulationFogBridge.bindBackground() ? 1.0F : 0.0F);
    }
}
