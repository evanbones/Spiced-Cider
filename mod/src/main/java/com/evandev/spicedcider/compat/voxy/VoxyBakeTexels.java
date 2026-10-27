package com.evandev.spicedcider.compat.voxy;

import org.lwjgl.system.MemoryUtil;

public final class VoxyBakeTexels {
    private static final int FACES = 6;
    private static final int PIXELS = 256;
    private static final float LIGHT_FLOOR = 0.3F;
    private static final float LIGHT_GAIN = 1.08F;

    public static void flatten(long output, float contrast) {
        for (int face = 0; face < FACES; face++) {
            long base = output + face * PIXELS * 8L;
            long red = 0;
            long green = 0;
            long blue = 0;
            int count = 0;
            for (int i = 0; i < PIXELS; i++) {
                int pixel = MemoryUtil.memGetInt(base + i * 8L);
                if ((pixel >>> 24) == 0) continue;
                red += pixel & 255;
                green += pixel >> 8 & 255;
                blue += pixel >> 16 & 255;
                count++;
            }
            if (count == 0) continue;
            float meanRed = (float) red / count;
            float meanGreen = (float) green / count;
            float meanBlue = (float) blue / count;
            for (int i = 0; i < PIXELS; i++) {
                long address = base + i * 8L;
                int pixel = MemoryUtil.memGetInt(address);
                if ((pixel >>> 24) == 0) continue;
                int r = channel(meanRed + ((pixel & 255) - meanRed) * contrast);
                int g = channel(meanGreen + ((pixel >> 8 & 255) - meanGreen) * contrast);
                int b = channel(meanBlue + ((pixel >> 16 & 255) - meanBlue) * contrast);
                MemoryUtil.memPutInt(address, pixel & 0xFF000000 | b << 16 | g << 8 | r);
            }
        }
    }

    public static void tintByLight(long output, int color) {
        float red = (color >> 16 & 255) / 255.0F;
        float green = (color >> 8 & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;
        float max = Math.max(red, Math.max(green, blue));
        if (max <= 0.0F) return;
        red = LIGHT_GAIN * (LIGHT_FLOOR + (1.0F - LIGHT_FLOOR) * red / max);
        green = LIGHT_GAIN * (LIGHT_FLOOR + (1.0F - LIGHT_FLOOR) * green / max);
        blue = LIGHT_GAIN * (LIGHT_FLOOR + (1.0F - LIGHT_FLOOR) * blue / max);
        for (int i = 0; i < FACES * PIXELS; i++) {
            long address = output + i * 8L;
            int pixel = MemoryUtil.memGetInt(address);
            if ((pixel >>> 24) == 0) continue;
            int r = channel((pixel & 255) * red);
            int g = channel((pixel >> 8 & 255) * green);
            int b = channel((pixel >> 16 & 255) * blue);
            MemoryUtil.memPutInt(address, pixel & 0xFF000000 | b << 16 | g << 8 | r);
        }
    }

    private static int channel(float value) {
        return Math.clamp(Math.round(value), 0, 255);
    }
}
