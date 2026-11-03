package com.evandev.spicedcider.compat.emi;

import com.evandev.spicedcider.client.SceneEntities;
import com.evandev.spicedcider.networking.LootDisplayPayload.Drop;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.sniffer.Sniffer;

import java.util.List;

public class SnifferEmiRecipe extends LootEmiRecipe {
    public static final int COLUMNS = 4;
    private static final int SCENE_X = 20;
    private static final int SCENE_W = 62;
    private static final int SCENE_H = 50;
    private static final int DIG_TICKS = 160;

    private int digStart = Integer.MIN_VALUE;

    public SnifferEmiRecipe(EmiRecipeCategory category, ResourceLocation id, EmiIngredient diggable, List<Drop> drops) {
        super(category, id, List.of(diggable), drops, SCENE_X + SCENE_W + 4, 0, COLUMNS, SCENE_H);
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        int height = getDisplayHeight();
        widgets.addSlot(inputs.getFirst(), 0, (height - SLOT) / 2);
        widgets.addDrawable(SCENE_X, (height - SCENE_H) / 2, SCENE_W, SCENE_H, (graphics, mouseX, mouseY, delta) -> drawScene(graphics));
        addOutputs(widgets);
    }

    private void drawScene(GuiGraphics graphics) {
        Sniffer sniffer = SceneEntities.get("sniffer", EntityType.SNIFFER, entity -> {
        });
        if (sniffer == null) return;
        int ticks = (int) (EmiScenes.millis() / 50);
        sniffer.tickCount = ticks;
        if (ticks - digStart >= DIG_TICKS || ticks < digStart) {
            digStart = ticks;
            sniffer.diggingAnimationState.stop();
            sniffer.diggingAnimationState.start(ticks);
        }
        EmiScenes.entity(graphics, sniffer, SCENE_W / 2f, SCENE_H / 2f + 4, 17, -40, 10);
    }
}
