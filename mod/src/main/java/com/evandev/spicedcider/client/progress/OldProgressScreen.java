package com.evandev.spicedcider.client.progress;

import com.evandev.spicedcider.mixin.minecraft.client.ProgressScreenAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class OldProgressScreen extends ProgressScreen implements ProgressListener {

    public static @Nullable ResourceKey<Level> PREVIOUS_DIMENSION = null;
    public static @Nullable ResourceKey<Level> CURRENT_DIMENSION = null;
    private final ProgressScreenAccessor progressScreenAccess;

    @Nullable
    private Component header;

    @Nullable
    private Component stage;
    private int progress = -1;
    private boolean stop = false;

    public OldProgressScreen(ProgressScreen originalScreen) {
        super(((ProgressScreenAccessor) originalScreen).spicedcider$clearScreenAfterStop());
        this.progressScreenAccess = (ProgressScreenAccessor) originalScreen;
    }

    public void setHeader(@Nullable Component header) {
        this.header = header;
    }

    public void setStage(@Nullable Component stage) {
        this.stage = stage;
    }

    public boolean hasStage() {
        return this.stage != null;
    }

    private void setHeaderAndStage() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null) return;

        if (this.header != null && (this.header.getString().equals("Saving level") || this.header.getString().equals(Component.translatable("menu.savingLevel").getString()) || this.header.getString().equals(Component.translatable("selectWorld.savingLevel").getString()))) {
            this.setHeader(null);
            this.setStage(Component.translatable("gui.spicedcider.screen.level.saving"));
        }

        ResourceKey<Level> currentDimension = CURRENT_DIMENSION;
        ResourceKey<Level> previousDimension = PREVIOUS_DIMENSION;

        boolean isTextNeeded = this.header == null && this.stage == null;
        boolean isMultiplayer = minecraft.getConnection() != null;
        boolean isConnectedLevel = minecraft.level != null && isMultiplayer;
        boolean isChangingLevel = minecraft.player != null && currentDimension != null && previousDimension != null;

        if (isTextNeeded && (!isMultiplayer || isConnectedLevel) && isChangingLevel) {
            if (currentDimension == Level.NETHER) {
                this.setHeader(Component.translatable("gui.spicedcider.screen.level.enterNether"));
                this.setStage(Component.translatable("gui.spicedcider.screen.level.building"));
            } else if (currentDimension == Level.END) {
                this.setHeader(Component.translatable("gui.spicedcider.screen.level.enterEnd"));
                this.setStage(Component.translatable("gui.spicedcider.screen.level.building"));
            } else if (currentDimension == Level.OVERWORLD) {
                if (previousDimension == Level.NETHER) {
                    this.setHeader(Component.translatable("gui.spicedcider.screen.level.leaveNether"));
                    this.setStage(Component.translatable("gui.spicedcider.screen.level.building"));
                } else if (previousDimension == Level.END) {
                    this.setHeader(Component.translatable("gui.spicedcider.screen.level.leaveEnd"));
                    this.setStage(Component.translatable("gui.spicedcider.screen.level.building"));
                }
            }

            if (this.stage == null) {
                this.setHeader(Component.translatable("gui.spicedcider.screen.level.loading"));
                this.setStage(Component.translatable("gui.spicedcider.screen.level.building"));
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void stop() {
        this.stop = true;
    }

    @Override
    public void removed() {
        this.stop();
    }

    @Override
    public void tick() {
        if (this.progress < 100) {
            this.progress++;
        }
        super.tick();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null) return;

        if (this.stop || this.progressScreenAccess.spicedcider$stop()) {
            if (this.progressScreenAccess.spicedcider$clearScreenAfterStop()) {
                minecraft.setScreen(null);
            }
            return;
        }

        this.setHeaderAndStage();

        if (this.header == null && this.stage == null) return;

        this.renderBackground(graphics, mouseX, mouseY, partialTick);

        ProgressRenderer.renderProgressWithInt(this.progress);

        if (this.header != null) {
            ProgressRenderer.drawHeaderText(graphics, this.header, this.width);
        }

        if (this.stage != null) {
            ProgressRenderer.drawStageText(graphics, this.stage, this.width);
        }
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderPanorama(graphics, partialTick);
        this.renderBlurredBackground(partialTick);
        renderMenuBackgroundTexture(graphics, MENU_BACKGROUND, 0, 0, 0.0F, 0.0F, this.width, this.height);
    }

    @Override
    public void progressStartNoAbort(Component component) {
        this.progressStart(component);
    }

    @Override
    public void progressStart(Component header) {
        this.setHeader(header);
        this.progressStage(Component.translatable("menu.working"));
    }

    @Override
    public void progressStage(Component stage) {
        this.setStage(stage);
        this.progressStagePercentage(0);
    }

    @Override
    public void progressStagePercentage(int progress) {
        this.progress = progress;
    }
}
