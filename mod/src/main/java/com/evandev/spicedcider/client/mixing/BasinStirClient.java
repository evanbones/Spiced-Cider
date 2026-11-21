package com.evandev.spicedcider.client.mixing;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.compat.manuallabour.SpoonStirRender;
import com.evandev.spicedcider.networking.BasinStirPayload;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
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
public class BasinStirClient {
    private static final TagKey<Item> SPOONS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("manual_labour", "ladles"));
    private static final float SPEED = 12.0F;
    private static final Map<BlockPos, Stir> STIRS = new ConcurrentHashMap<>();

    private static final class Stir {
        ItemStack spoon;
        float angle;
        float velocity;

        Stir(ItemStack spoon) {
            this.spoon = spoon;
        }

        void tick() {
            this.velocity += (SPEED - this.velocity) * 0.25F;
            this.angle += this.velocity;
        }
    }

    public static void receive(BasinStirPayload payload) {
        if (payload.spoon().isEmpty()) {
            STIRS.remove(payload.pos());
            return;
        }
        Stir stir = STIRS.computeIfAbsent(payload.pos().immutable(), pos -> new Stir(payload.spoon()));
        stir.spoon = payload.spoon();
    }

    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!STIRS.isEmpty() && !minecraft.options.keyUse.isDown() && minecraft.hitResult instanceof BlockHitResult blockHit
                && blockHit.getType() == HitResult.Type.BLOCK && minecraft.player != null && minecraft.player.isHolding(stack -> stack.is(SPOONS))) {
            STIRS.remove(blockHit.getBlockPos());
        }
        STIRS.values().forEach(Stir::tick);
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
        if (!event.getItemStack().is(SPOONS)) {
            return;
        }
        HitResult hit = Minecraft.getInstance().hitResult;
        if (hit instanceof BlockHitResult blockHit && hit.getType() == HitResult.Type.BLOCK) {
            Stir stir = STIRS.get(blockHit.getBlockPos());
            if (stir != null) {
                event.setCanceled(true);
            }
        }
    }
}
