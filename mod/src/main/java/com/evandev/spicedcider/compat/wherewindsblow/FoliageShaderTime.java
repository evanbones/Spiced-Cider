package com.evandev.spicedcider.compat.wherewindsblow;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FoliageShaderTime {
    private static final Pattern TEMPO = Pattern.compile("t \\* ([0-9.]+) \\* tempoDrift");
    private static final String RIPPLE = "t * 3.6 * (1.0 + phaseDrift * 0.035)";

    public static String bound(String source) {
        if (source == null) {
            return null;
        }
        String fixed = TEMPO.matcher(source).replaceAll(m -> Matcher.quoteReplacement(
                "(t * " + m.group(1) + " * localTempo + (tempoDrift - localTempo) * " + m.group(1) + " * 11.1)"));
        return fixed.replace(RIPPLE, "(t * 3.6 + phaseDrift * 1.2)");
    }
}
