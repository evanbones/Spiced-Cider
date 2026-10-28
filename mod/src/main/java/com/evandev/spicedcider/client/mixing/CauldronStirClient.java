package com.evandev.spicedcider.client.mixing;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.compat.manuallabour.SpoonStirRender;
import com.evandev.spicedcider.content.mixing.CauldronMixing;
import com.evandev.spicedcider.networking.CauldronStirPayload;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, value = Dist.CLIENT)
public class CauldronStirClient {
    private static final float SPEED = 12.0F;
    private static final Map<BlockPos, Stir> STIRS = new ConcurrentHashMap<>();

    private static final class Stir {
        ItemStack spoon;
        boolean active = true;
        float angle;
        float velocity;

        Stir(ItemStack spoon) {
            this.spoon = spoon;
        }

        void tick() {
            if (this.active) {
                this.velocity += (SPEED - this.velocity) * 0.25F;
            } else {
                this.velocity -= this.velocity * 0.3F;
            }
            this.angle += this.velocity;
        }
    }

    public static void receive(CauldronStirPayload payload) {
        if (payload.spoon().isEmpty()) {
            Stir stir = STIRS.get(payload.pos());
            if (stir != null) {
                stir.active = false;
            }
            return;
        }
        Stir stir = STIRS.computeIfAbsent(payload.pos().immutable(), pos -> new Stir(payload.spoon()));
        stir.spoon = payload.spoon();
        stir.active = true;
    }

    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event) {
        STIRS.values().removeIf(stir -> {
            stir.tick();
            return !stir.active && Math.abs(stir.velocity) < 0.2F;
        });
    }

    @SubscribeEvent
    public static void unload(LevelEvent.Unload event) {
        STIRS.clear();
    }

    @SubscribeEvent
    public static void render(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS || STIRS.isEmpty() || !ModList.get().isLoaded("manual_labour")) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) {
            return;
        }
        PoseStack poseStack = event.getPoseStack();
        Vec3 camera = event.getCamera().getPosition();
        MultiBufferSource.BufferSource buffer = minecraft.renderBuffers().bufferSource();
        float partial = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        for (Map.Entry<BlockPos, Stir> entry : STIRS.entrySet()) {
            BlockPos pos = entry.getKey();
            Stir stir = entry.getValue();
            poseStack.pushPose();
            poseStack.translate(pos.getX() - camera.x, pos.getY() - camera.y, pos.getZ() - camera.z);
            SpoonStirRender.render(stir.spoon, stir.angle + partial * stir.velocity, poseStack, buffer,
                    LevelRenderer.getLightColor(level, pos), OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }
        buffer.endBatch();
    }

    @SubscribeEvent
    public static void hideHand(RenderHandEvent event) {
        if (!event.getItemStack().is(CauldronMixing.SPOONS)) {
            return;
        }
        HitResult hit = Minecraft.getInstance().hitResult;
        if (hit instanceof BlockHitResult blockHit && hit.getType() == HitResult.Type.BLOCK) {
            Stir stir = STIRS.get(blockHit.getBlockPos());
            if (stir != null && stir.active) {
                event.setCanceled(true);
            }
        }
    }
}
