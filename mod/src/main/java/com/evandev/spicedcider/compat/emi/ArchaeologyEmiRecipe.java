package com.evandev.spicedcider.compat.emi;

import com.evandev.spicedcider.networking.LootDisplayPayload.Drop;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.List;

public class ArchaeologyEmiRecipe extends LootEmiRecipe {
    public static final int COLUMNS = 5;
    private static final int TITLE = 12;
    private static final int SCENE_X = 20;
    private static final int SCENE_W = 44;
    private static final int SCENE_H = 46;

    private final Component title;
    private final List<EmiStack> blocks;

    public ArchaeologyEmiRecipe(EmiRecipeCategory category, ResourceLocation id, Component title, List<EmiStack> blocks, List<Drop> drops) {
        super(category, id, List.of(EmiIngredient.of(blocks)), drops, SCENE_X + SCENE_W + 4, TITLE, COLUMNS, TITLE + SCENE_H);
        this.title = title;
        this.blocks = blocks;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        int body = getDisplayHeight() - TITLE;
        widgets.addText(title, 0, 0, 0x404040, false);
        widgets.addSlot(inputs.getFirst(), 0, TITLE + (body - SLOT) / 2);
        widgets.addDrawable(SCENE_X, TITLE + (body - SCENE_H) / 2, SCENE_W, SCENE_H, (graphics, mouseX, mouseY, delta) -> drawScene(graphics));
        addOutputs(widgets);
    }

    private void drawScene(GuiGraphics graphics) {
        long time = EmiScenes.millis();
        ItemStack stack = blocks.get((int) (time / 3000 % blocks.size())).getItemStack();
        if (!(stack.getItem() instanceof BlockItem blockItem)) return;
        BlockState state = blockItem.getBlock().defaultBlockState();
        int phase = (int) (time / 450 % 6);
        if (state.hasProperty(BlockStateProperties.DUSTED)) state = state.setValue(BlockStateProperties.DUSTED, Math.min(phase, 3));
        float swing = Mth.sin(time / 120f) * 18;
        EmiScenes.beginIso(graphics, SCENE_W / 2f - 2, SCENE_H / 2f + 4, 20);
        EmiScenes.block(graphics, state, 0, 0, 0);
        EmiScenes.item(graphics, new ItemStack(Items.BRUSH), 0.5f, 1.35f, 1.05f, 0.75f, 90, -35 + swing);
        EmiScenes.endIso(graphics);
    }
}
