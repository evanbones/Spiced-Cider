package com.evandev.spicedcider.compat.wherewindsblow;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FoliageShaderTime {
    private static final Pattern TEMPO = Pattern.compile("t \\* ([0-9.]+) \\* tempoDrift");
    private static final String RIPPLE = "t * 3.6 * (1.0 + phaseDrift * 0.035)";
    private static final String LOCAL_PHASE = "float localPhase = plantWind * ((gustSeed - 0.5) * 1.7 + (bladeSeed - 0.5) * 0.16);";
    private static final String GUST_SEED = "float gustSeed = wwb_grass_variation_seed(gustCell, vec2(127.1, 311.7));";

    public static String bound(String source) {
        if (source == null) {
            return null;
        }
        String fixed = TEMPO.matcher(source).replaceAll(m -> Matcher.quoteReplacement(
                "(t * " + m.group(1) + " + (tempoDrift / localTempo - 1.0) * " + m.group(1) + " * 11.1)"));
        return fixed.replace(RIPPLE, "(t * 3.6 + phaseDrift * 1.2)")
                .replace(LOCAL_PHASE, "float localPhase = plantWind * ((gustSeed - 0.5) * 0.35 + (bladeSeed - 0.5) * 0.16);")
                .replace(GUST_SEED, "float gustSeed = wwb_foliage_value_noise(motionPosition * 0.58, vec2(127.1, 311.7));");
    }
}
