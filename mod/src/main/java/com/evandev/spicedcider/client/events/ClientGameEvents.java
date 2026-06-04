package com.evandev.spicedcider.client.events;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.client.music.DeathSoundInstance;
import com.evandev.spicedcider.registry.ModSounds;
import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.EmiStackInteraction;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.evandev.spicedcider.client.events.ClientModEvents.ModEvents.OPEN_TEXTURE_FOLDER;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ClientGameEvents {

    private static boolean wasDead = false;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();

        while (OPEN_TEXTURE_FOLDER.consumeClick()) {
            HitResult hit = mc.hitResult;
            if (hit != null && mc.level != null) {
                if (hit.getType() == HitResult.Type.BLOCK && hit instanceof BlockHitResult blockHit) {
                    BlockState state = mc.level.getBlockState(blockHit.getBlockPos());
                    TextureAtlasSprite sprite = mc.getBlockRenderer().getBlockModel(state).getParticleIcon();
                    ResourceLocation spriteLoc = sprite.contents().name();

                    ResourceLocation fileResource = ResourceLocation.fromNamespaceAndPath(
                            spriteLoc.getNamespace(), "textures/" + spriteLoc.getPath() + ".png"
                    );
                    copyAndOpenTexture(fileResource);

                } else if (hit.getType() == HitResult.Type.ENTITY && hit instanceof EntityHitResult entityHit) {
                    Entity entity = entityHit.getEntity();
                    @SuppressWarnings("unchecked")
                    EntityRenderer<Entity> renderer = (EntityRenderer<Entity>) mc.getEntityRenderDispatcher().getRenderer(entity);
                    ResourceLocation fileResource = renderer.getTextureLocation(entity);
                    copyAndOpenTexture(fileResource);
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
                        TextureAtlasSprite sprite = Minecraft.getInstance().getItemRenderer()
                                .getModel(stack, null, null, 0).getParticleIcon();
                        ResourceLocation spriteLoc = sprite.contents().name();

                        ResourceLocation fileResource = ResourceLocation.fromNamespaceAndPath(
                                spriteLoc.getNamespace(), "textures/" + spriteLoc.getPath() + ".png"
                        );
                        copyAndOpenTexture(fileResource);
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

    private static void copyAndOpenTexture(ResourceLocation fileResource) {
        Path targetFile = getTargetFile(fileResource);
        Path targetDir = targetFile.getParent();

        try {
            Files.createDirectories(targetDir);

            var resourceManager = Minecraft.getInstance().getResourceManager();
            var resource = resourceManager.getResource(fileResource);

            if (resource.isPresent()) {
                if (!Files.exists(targetFile)) {
                    try (InputStream in = resource.get().open()) {
                        Files.copy(in, targetFile);
                        SpicedCider.LOGGER.info("Successfully extracted texture to {}", targetFile);
                    }
                }
            } else {
                SpicedCider.LOGGER.warn("Could not find the texture resource for {}", fileResource);
            }

            Util.getPlatform().openUri(targetDir.toUri());
        } catch (Exception e) {
            SpicedCider.LOGGER.error("Failed to extract or open texture directory for: {}", targetFile, e);
        }
    }

    private static @NotNull Path getTargetFile(ResourceLocation fileResource) {
        String namespace = fileResource.getNamespace();
        String subPath = fileResource.getPath();
        if (subPath.startsWith("textures/")) {
            subPath = subPath.substring(9);
        }

        Path basePath = FMLPaths.GAMEDIR.get()
                .resolve("resourcepacks")
                .resolve("spicedcider_resources")
                .resolve("assets")
                .resolve(namespace)
                .resolve("textures");

        return basePath.resolve(subPath);
    }
}