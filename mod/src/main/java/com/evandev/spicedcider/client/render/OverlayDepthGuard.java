package com.evandev.spicedcider.client.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import net.minecraft.client.Minecraft;

public class OverlayDepthGuard {
    private static TextureTarget saved;
    private static boolean holding;

    public static void saveOnce() {
        if (holding) return;
        RenderTarget main = Minecraft.getInstance().getMainRenderTarget();
        if (saved == null) {
            saved = new TextureTarget(main.width, main.height, true, Minecraft.ON_OSX);
        } else if (saved.width != main.width || saved.height != main.height) {
            saved.resize(main.width, main.height, Minecraft.ON_OSX);
        }
        saved.copyDepthFrom(main);
        main.bindWrite(false);
        holding = true;
    }

    public static void restore() {
        if (!holding) return;
        holding = false;
        RenderTarget main = Minecraft.getInstance().getMainRenderTarget();
        main.copyDepthFrom(saved);
        main.bindWrite(false);
    }
}
