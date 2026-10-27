package com.evandev.spicedcider.client.nether;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.joml.Vector3f;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, value = Dist.CLIENT)
public final class NetherAmbience {
    private static final float TINT_BLEND = 0.03F;
    private static final float TINT_FLOOR = 0.35F;

    private static final Vector3f hue = new Vector3f(1.0F);
    private static float strength;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null || minecraft.isPaused()) {
            if (level == null) strength = 0.0F;
            return;
        }
        boolean nether = level.dimension() == Level.NETHER && SpicedCiderConfig.CLIENT_SPEC.isLoaded();
        Vec3 camera = minecraft.gameRenderer.getMainCamera().getPosition();

        float targetStrength = nether ? SpicedCiderConfig.CLIENT.netherAmbientTint.get() / 100.0F : 0.0F;
        if (nether) {
            int fog = level.getBiome(BlockPos.containing(camera)).value().getFogColor();
            float red = (fog >> 16 & 255) / 255.0F;
            float green = (fog >> 8 & 255) / 255.0F;
            float blue = (fog & 255) / 255.0F;
            float max = Math.max(red, Math.max(green, blue));
            if (max > 0.0F) {
                hue.lerp(new Vector3f(red / max, green / max, blue / max), TINT_BLEND);
            }
        }
        strength += (targetStrength - strength) * TINT_BLEND;
        if (targetStrength == 0.0F && strength < 0.002F) strength = 0.0F;

    }

    public static void tintLightmap(Vector3f colors, int blockLight) {
        if (strength <= 0.0F) return;
        float ambient = 1.0F - blockLight / 15.0F;
        float weight = strength * ambient * ambient;
        if (weight <= 0.0F) return;
        float luminance = luminance(colors.x, colors.y, colors.z);
        float red = Mth.lerp(TINT_FLOOR, hue.x, 1.0F);
        float green = Mth.lerp(TINT_FLOOR, hue.y, 1.0F);
        float blue = Mth.lerp(TINT_FLOOR, hue.z, 1.0F);
        float tinted = luminance(colors.x * red, colors.y * green, colors.z * blue);
        float scale = tinted > 0.0F ? luminance / tinted : 1.0F;
        colors.lerp(new Vector3f(colors.x * red * scale, colors.y * green * scale, colors.z * blue * scale), weight);
        colors.set(Mth.clamp(colors.x, 0.0F, 1.0F), Mth.clamp(colors.y, 0.0F, 1.0F), Mth.clamp(colors.z, 0.0F, 1.0F));
    }

    private static float luminance(float red, float green, float blue) {
        return 0.2126F * red + 0.7152F * green + 0.0722F * blue;
    }
}
