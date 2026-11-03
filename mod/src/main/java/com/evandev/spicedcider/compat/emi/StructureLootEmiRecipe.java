package com.evandev.spicedcider.compat.emi;

import com.evandev.spicedcider.networking.LootDisplayPayload.Drop;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class StructureLootEmiRecipe extends LootEmiRecipe {
    public static final int COLUMNS = 7;
    private static final int TITLE = 12;
    private static final int ICON = 32;

    private final Component title;
    private final List<ItemStack> containers;

    public StructureLootEmiRecipe(EmiRecipeCategory category, ResourceLocation id, Component title, List<ItemStack> containers, List<Drop> drops) {
        super(category, id, List.of(), drops, ICON + 4, TITLE, COLUMNS, TITLE + ICON);
        this.title = title;
        this.containers = containers;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addText(title, 0, 0, 0x404040, false);
        if (!containers.isEmpty()) {
            int top = TITLE + (Math.min(outputRowsHeight(), getDisplayHeight() - TITLE) - ICON) / 2;
            widgets.addDrawable(0, Math.max(TITLE, top), ICON, ICON, (graphics, mouseX, mouseY, delta) -> drawContainer(graphics));
        }
        addOutputs(widgets);
    }

    private void drawContainer(GuiGraphics graphics) {
        ItemStack stack = containers.get((int) (EmiScenes.millis() / 2500 % containers.size()));
        graphics.pose().pushPose();
        graphics.pose().scale(2, 2, 1);
        graphics.renderItem(stack, 0, 0);
        graphics.pose().popPose();
    }
}
