package com.evandev.spicedcider.client.progress;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.progress.StoringChunkProgressListener;

public class OldLoadingScreen extends Screen {

    private final Component header;
    private final Component stage;
    private final StoringChunkProgressListener progressListener;

    public OldLoadingScreen(StoringChunkProgressListener progressListener, Component header, Component stage) {
        super(Component.empty());
        this.progressListener = progressListener;
        this.header = header;
        this.stage = stage;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    protected boolean shouldNarrateNavigation() {
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (this.minecraft == null) return;

        this.renderBackground(graphics, mouseX, mouseY, partialTick);

        ProgressRenderer.drawHeaderText(graphics, this.header, this.width);
        ProgressRenderer.drawStageText(graphics, this.stage, this.width);
        ProgressRenderer.renderProgressWithInt(this.progressListener.getProgress());
    }
}
