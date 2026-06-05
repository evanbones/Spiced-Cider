package com.evandev.spicedcider.client.events;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.client.music.DeathSoundInstance;
import com.evandev.spicedcider.client.renderer.TextureCatcher;
import com.evandev.spicedcider.client.screens.AssetSelectionScreen;
import com.evandev.spicedcider.registry.ModSounds;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.EmiStackInteraction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;

import static com.evandev.spicedcider.client.events.ClientModEvents.ModEvents.OPEN_TEXTURE_FOLDER;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ClientGameEvents {

    private static boolean wasDead = false;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();

        while (OPEN_TEXTURE_FOLDER.consumeClick()) {
            HitResult hit = mc.hitResult;
            if (hit != null && mc.level != null && mc.screen == null) {
                Set<ResourceLocation> assetsToCopy = new HashSet<>();

                if (hit.getType() == HitResult.Type.BLOCK && hit instanceof BlockHitResult blockHit) {
                    BlockPos pos = blockHit.getBlockPos();
                    BlockState state = mc.level.getBlockState(pos);

                    BakedModel model = mc.getBlockRenderer().getBlockModel(state);
                    assetsToCopy.addAll(getSpritesFromModel(model, state));

                    BlockEntity blockEntity = mc.level.getBlockEntity(pos);
                    if (blockEntity != null) {
                        BlockEntityRenderer<BlockEntity> renderer = mc.getBlockEntityRenderDispatcher().getRenderer(blockEntity);
                        if (renderer != null) {
                            TextureCatcher.start();

                            try {
                                PoseStack poseStack = new PoseStack();
                                MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

                                renderer.render(blockEntity, mc.getTimer().getGameTimeDeltaTicks(), poseStack, bufferSource, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
                                bufferSource.endBatch();
                            } catch (Exception e) {
                                SpicedCider.LOGGER.error("Failed to capture BER textures for block: {}", state.getBlock(), e);
                            }

                            Set<ResourceLocation> captured = TextureCatcher.stop();

                            for (ResourceLocation loc : captured) {
                                assetsToCopy.add(normalizeTextureLocation(loc));
                            }
                        }
                    }

                    ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
                    assetsToCopy.add(ResourceLocation.fromNamespaceAndPath(blockId.getNamespace(), "blockstates/" + blockId.getPath() + ".json"));
                    assetsToCopy.add(ResourceLocation.fromNamespaceAndPath(blockId.getNamespace(), "models/block/" + blockId.getPath() + ".json"));

                    mc.setScreen(new AssetSelectionScreen(assetsToCopy));

                } else if (hit.getType() == HitResult.Type.ENTITY && hit instanceof EntityHitResult entityHit) {
                    Entity entity = entityHit.getEntity();
                    @SuppressWarnings("unchecked")
                    EntityRenderer<Entity> renderer = (EntityRenderer<Entity>) mc.getEntityRenderDispatcher().getRenderer(entity);

                    ResourceLocation mainTexture = renderer.getTextureLocation(entity);
                    assetsToCopy.add(normalizeTextureLocation(mainTexture));

                    mc.setScreen(new AssetSelectionScreen(assetsToCopy));
                }
            }
        }

        if (mc.player != null) {
            boolean isDead = mc.player.isDeadOrDying();

            if (isDead && !wasDead) {
                DeathSoundInstance deathSound = new DeathSoundInstance(ModSounds.PLAYER_DEATH.get(), 200);
                mc.getSoundManager().play(deathSound);
            }
            wasDead = isDead;
        } else {
            wasDead = false;
        }
    }

    @SubscribeEvent
    public static void onKeyPressed(ScreenEvent.KeyPressed.Pre event) {
        int keyCode = event.getKeyCode();

        if (keyCode == OPEN_TEXTURE_FOLDER.getKey().getValue()) {
            EmiStackInteraction interaction = EmiApi.getHoveredStack(true);
            if (interaction != null && !interaction.isEmpty()) {
                EmiIngredient ingredient = interaction.getStack();
                if (!ingredient.isEmpty() && !ingredient.getEmiStacks().isEmpty()) {
                    ItemStack stack = ingredient.getEmiStacks().getFirst().getItemStack();
                    if (stack != null && !stack.isEmpty()) {
                        Minecraft mc = Minecraft.getInstance();
                        BakedModel model = mc.getItemRenderer().getModel(stack, mc.level, mc.player, 0);

                        Set<ResourceLocation> assetsToCopy = new HashSet<>(getSpritesFromModel(model, null));

                        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
                        assetsToCopy.add(ResourceLocation.fromNamespaceAndPath(itemId.getNamespace(), "models/item/" + itemId.getPath() + ".json"));

                        mc.execute(() -> mc.setScreen(new AssetSelectionScreen(assetsToCopy)));
                        return;
                    }
                }
            }
        }

        if (!FMLLoader.isProduction() && keyCode == GLFW.GLFW_KEY_C) {
            EmiStackInteraction interaction = EmiApi.getHoveredStack(true);

            if (interaction != null && !interaction.isEmpty()) {
                EmiIngredient ingredient = interaction.getStack();

                if (!ingredient.isEmpty() && !ingredient.getEmiStacks().isEmpty()) {
                    EmiStack emiStack = ingredient.getEmiStacks().getFirst();
                    ItemStack stack = emiStack.getItemStack();

                    if (stack != null && !stack.isEmpty()) {
                        String descriptionId = stack.getItem().getDescriptionId();
                        String clipboardData = "\"%s\": \"%s\"".formatted(descriptionId, I18n.get(descriptionId));

                        Minecraft.getInstance().keyboardHandler.setClipboard(clipboardData);
                    }
                }
            }
        }
    }

    /**
     * Iterates through all quads of a BakedModel to extract every distinct texture sprite used.
     */
    private static Set<ResourceLocation> getSpritesFromModel(BakedModel model, @Nullable BlockState state) {
        Set<ResourceLocation> sprites = new HashSet<>();
        sprites.add(normalizeTextureLocation(ResourceLocation.parse(model.getParticleIcon().contents().name().toString())));

        RandomSource random = RandomSource.create();

        for (Direction dir : Direction.values()) {
            for (BakedQuad quad : model.getQuads(state, dir, random, ModelData.EMPTY, null)) {
                sprites.add(normalizeTextureLocation(ResourceLocation.parse(quad.getSprite().contents().name().toString())));
            }
        }

        for (BakedQuad quad : model.getQuads(state, null, random, ModelData.EMPTY, null)) {
            sprites.add(normalizeTextureLocation(ResourceLocation.parse(quad.getSprite().contents().name().toString())));
        }

        return sprites;
    }

    /**
     * Converts a raw texture namespace (e.g. "minecraft:block/stone") into a concrete
     * file asset path (e.g. "minecraft:textures/block/stone.png") so the Resource Manager finds it.
     */
    private static ResourceLocation normalizeTextureLocation(ResourceLocation rawLoc) {
        if (rawLoc.getPath().endsWith(".png")) {
            return rawLoc;
        } else {
            return ResourceLocation.fromNamespaceAndPath(rawLoc.getNamespace(), "textures/" + rawLoc.getPath() + ".png");
        }
    }
}