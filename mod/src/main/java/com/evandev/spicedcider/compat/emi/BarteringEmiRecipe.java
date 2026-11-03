package com.evandev.spicedcider.compat.emi;

import com.evandev.spicedcider.client.SceneEntities;
import com.evandev.spicedcider.networking.LootDisplayPayload.Drop;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class BarteringEmiRecipe extends LootEmiRecipe {
    public static final int COLUMNS = 5;
    private static final int SCENE_X = 20;
    private static final int SCENE_W = 40;
    private static final int SCENE_H = 62;

    public BarteringEmiRecipe(EmiRecipeCategory category, ResourceLocation id, List<Drop> drops) {
        super(category, id, List.of(EmiStack.of(Items.GOLD_INGOT)), drops, SCENE_X + SCENE_W + 4, 0, COLUMNS, SCENE_H);
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        int height = getDisplayHeight();
        widgets.addSlot(inputs.getFirst(), 0, (height - SLOT) / 2);
        widgets.addDrawable(SCENE_X, (height - SCENE_H) / 2, SCENE_W, SCENE_H, (graphics, mouseX, mouseY, delta) -> drawScene(graphics));
        addOutputs(widgets);
    }

    private void drawScene(GuiGraphics graphics) {
        Piglin piglin = SceneEntities.get("bartering_piglin", EntityType.PIGLIN, entity -> {
            entity.setImmuneToZombification(true);
            entity.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.GOLD_INGOT));
        });
        if (piglin == null) return;
        long time = EmiScenes.millis();
        piglin.tickCount = (int) (time / 50);
        float sway = Mth.sin(time / 900f) * 8;
        EmiScenes.entity(graphics, piglin, SCENE_W / 2f, SCENE_H / 2f + 1, 27, -25 + sway, 8);
    }
}
