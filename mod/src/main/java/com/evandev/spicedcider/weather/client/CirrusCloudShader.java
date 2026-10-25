package com.evandev.spicedcider.weather.client;

import com.evandev.spicedcider.SpicedCider;

public final class CirrusCloudShader {
    private static final String CLOUDS = "cirrus:cirrus_clouds";
    private static final String CLOUD_MASK = "cirrus:cirrus_cloud_mask";

    private static final String CLOUDS_DISCARD = """
                if (color.a < 0.1) {
                    discard;
                }
            """;
    private static final String CLOUDS_FADED_DISCARD = """
                float spicedciderCover = cloudSample.a;
                if (spicedciderCover < 0.004 || color.a < 0.1 * spicedciderCover) {
                    discard;
                }
                gl_FragDepth = spicedciderCover > 0.999 ? gl_FragCoord.z : 1.0 - 0.000001 * spicedciderCover;
            """;
    private static final String SUN_BOOST = "color.a = mix(color.a, 1.0, sunIllumination * CirrusSunWeight);";
    private static final String FADED_SUN_BOOST = "color.a = mix(color.a, 1.0, sunIllumination * CirrusSunWeight * spicedciderCover);";

    private static final String MASK_DISCARD = "if (cloudAlpha * CirrusLayerOpacity < 0.1) {";
    private static final String FADED_MASK_DISCARD = """
            vec2 spicedciderCell = abs(fract(texCoord0 * vec2(textureSize(Sampler0, 0))) - 0.5) * 2.0;
                if (CirrusLayerOpacity < 0.1 || cloudAlpha < 0.004 || max(spicedciderCell.x, spicedciderCell.y) > cloudAlpha) {""";

    public static String patch(String name, String source) {
        if (CLOUDS.equals(name)) {
            return replace(name, source, CLOUDS_DISCARD, CLOUDS_FADED_DISCARD, SUN_BOOST, FADED_SUN_BOOST);
        }
        if (CLOUD_MASK.equals(name)) {
            return replace(name, source, MASK_DISCARD, FADED_MASK_DISCARD);
        }
        return source;
    }

    private static String replace(String name, String source, String... pairs) {
        String normalized = source.replace("\r\n", "\n");
        for (int i = 0; i < pairs.length; i += 2) {
            if (!normalized.contains(pairs[i])) {
                SpicedCider.LOGGER.warn("Cirrus shader {} changed, cloud cover fading disabled for it", name);
                return source;
            }
            normalized = normalized.replace(pairs[i], pairs[i + 1]);
        }
        return normalized;
    }
}
