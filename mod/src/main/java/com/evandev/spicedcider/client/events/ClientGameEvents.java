package com.evandev.spicedcider.client.events;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.client.music.DeathSoundInstance;
import com.evandev.spicedcider.client.renderer.TextureCatcher;
import com.evandev.spicedcider.client.screens.AssetSelectionScreen;
import com.evandev.spicedcider.registry.ModSounds;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.EmiStackInteraction;
import dev.emi.emi.api.widget.Widget;
import net.minecraft.Util;
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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
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

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.evandev.spicedcider.client.events.ClientModEvents.ModEvents.OPEN_TEXTURE_FOLDER;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ClientGameEvents {

    private static final Path DATAPACK_DIR = Path.of("C:\\Users\\evan\\Documents\\GitHub\\Spiced-Cider\\pack\\config\\openloader\\packs\\spicedcider_data\\data");
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
        Minecraft mc = Minecraft.getInstance();

        if (keyCode == OPEN_TEXTURE_FOLDER.getKey().getValue()) {
            EmiRecipe hoveredRecipe = getHoveredEmiRecipe(mc);

            if (hoveredRecipe != null) {
                ResourceLocation recipeId = hoveredRecipe.getId();

                if (recipeId != null && mc.level != null) {
                    try {
                        RecipeHolder<?> serverRecipe;
                        RegistryOps<JsonElement> ops;

                        if (mc.hasSingleplayerServer() && mc.getSingleplayerServer() != null) {
                            serverRecipe = mc.getSingleplayerServer().getRecipeManager().byKey(recipeId).orElse(null);
                            ops = mc.getSingleplayerServer().registryAccess().createSerializationContext(JsonOps.INSTANCE);
                        } else {
                            if (mc.player != null) {
                                mc.player.displayClientMessage(Component.literal("§cWarning: §fExporting shaped recipes requires Singleplayer!"), false);
                            }
                            serverRecipe = mc.level.getRecipeManager().byKey(recipeId).orElse(null);
                            ops = mc.level.registryAccess().createSerializationContext(JsonOps.INSTANCE);
                        }

                        if (serverRecipe != null) {
                            Codec<Recipe<?>> recipeCodec =
                                    BuiltInRegistries.RECIPE_SERIALIZER.byNameCodec()
                                            .dispatch(Recipe::getSerializer, RecipeSerializer::codec);

                            JsonElement json = recipeCodec.encodeStart(ops, serverRecipe.value()).getOrThrow();
                            String jsonStr = new GsonBuilder().setPrettyPrinting().create().toJson(json);

                            Path recipeFile = DATAPACK_DIR.resolve(recipeId.getNamespace()).resolve("recipe").resolve(recipeId.getPath() + ".json");

                            Files.createDirectories(recipeFile.getParent());
                            Files.writeString(recipeFile, jsonStr);

                            if (mc.player != null) {
                                mc.player.displayClientMessage(Component.literal("Exported recipe to: " + recipeFile), false);
                            }

                            Util.getPlatform().openUri(recipeFile.getParent().toUri());
                            event.setCanceled(true);
                            return;
                        }
                    } catch (Exception e) {
                        SpicedCider.LOGGER.error("Failed to serialize or save recipe for export", e);
                        if (mc.player != null) {
                            mc.player.displayClientMessage(Component.literal("§cExport Failed: §f" + e.getMessage()), false);
                        }
                        event.setCanceled(true);
                        return;
                    }
                } else if (recipeId != null && mc.player != null) {
                    mc.player.displayClientMessage(Component.literal("Hovered recipe lacks a backing JSON. ID: " + recipeId), false);
                    event.setCanceled(true);
                    return;
                }
            }

            EmiStackInteraction interaction = EmiApi.getHoveredStack(true);
            if (interaction != null && !interaction.isEmpty()) {
                EmiIngredient ingredient = interaction.getStack();
                if (!ingredient.isEmpty() && !ingredient.getEmiStacks().isEmpty()) {
                    ItemStack stack = ingredient.getEmiStacks().getFirst().getItemStack();
                    if (stack != null && !stack.isEmpty()) {
                        BakedModel model = mc.getItemRenderer().getModel(stack, mc.level, mc.player, 0);

                        Set<ResourceLocation> assetsToCopy = new HashSet<>(getSpritesFromModel(model, null));

                        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
                        assetsToCopy.add(ResourceLocation.fromNamespaceAndPath(itemId.getNamespace(), "models/item/" + itemId.getPath() + ".json"));

                        mc.execute(() -> mc.setScreen(new AssetSelectionScreen(assetsToCopy)));
                        event.setCanceled(true);
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

                        mc.keyboardHandler.setClipboard(clipboardData);
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    /**
     * Extracts the hovered EmiRecipe from the RecipeScreen via reflection.
     */
    @SuppressWarnings("unchecked")
    private static EmiRecipe getHoveredEmiRecipe(Minecraft mc) {
        if (mc.screen == null || !mc.screen.getClass().getName().equals("dev.emi.emi.screen.RecipeScreen")) {
            return null;
        }

        try {
            double mouseX = mc.mouseHandler.xpos() * (double) mc.getWindow().getGuiScaledWidth() / (double) mc.getWindow().getScreenWidth();
            double mouseY = mc.mouseHandler.ypos() * (double) mc.getWindow().getGuiScaledHeight() / (double) mc.getWindow().getScreenHeight();

            Field currentPageField = mc.screen.getClass().getDeclaredField("currentPage");
            currentPageField.setAccessible(true);
            List<?> currentPage = (List<?>) currentPageField.get(mc.screen);

            if (currentPage == null) return null;

            for (Object group : currentPage) {
                Class<?> groupClass = group.getClass();

                int groupX = (int) groupClass.getMethod("x").invoke(group);
                int groupY = (int) groupClass.getMethod("y").invoke(group);

                int relX = (int) mouseX - groupX;
                int relY = (int) mouseY - groupY;

                List<Widget> widgets = (List<Widget>) groupClass.getField("widgets").get(group);
                for (Widget widget : widgets) {
                    if (widget.getBounds().contains(relX, relY)) {
                        return (EmiRecipe) groupClass.getField("recipe").get(group);
                    }
                }
            }
        } catch (Exception e) {
            SpicedCider.LOGGER.error("Failed to extract recipe via reflection", e);
        }
        return null;
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