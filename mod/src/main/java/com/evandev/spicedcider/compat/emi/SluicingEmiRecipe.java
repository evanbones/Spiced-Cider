package com.evandev.spicedcider.compat.emi;

import com.evandev.spicedcider.networking.LootDisplayPayload.Drop;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SluicingEmiRecipe extends LootEmiRecipe {
    public static final int COLUMNS = 5;
    private static final int SCENE_X = 20;
    private static final int SCENE_W = 52;
    private static final int SCENE_H = 54;

    private final Block sluice;
    private final List<EmiStack> fluids;
    @Nullable
    private final EmiIngredient item;

    public SluicingEmiRecipe(EmiRecipeCategory category, ResourceLocation id, Block sluice, List<EmiStack> fluids, @Nullable EmiIngredient item, List<Drop> drops) {
        super(category, id, item == null ? List.of(EmiIngredient.of(fluids)) : List.of(EmiIngredient.of(fluids), item), drops, SCENE_X + SCENE_W + 4, 0, COLUMNS, SCENE_H, true);
        this.sluice = sluice;
        this.fluids = fluids;
        this.item = item;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        int height = getDisplayHeight();
        int slotsTop = (height - inputs.size() * SLOT) / 2;
        for (int i = 0; i < inputs.size(); i++) widgets.addSlot(inputs.get(i), 0, slotsTop + i * SLOT);
        widgets.addDrawable(SCENE_X, (height - SCENE_H) / 2, SCENE_W, SCENE_H, (graphics, mouseX, mouseY, delta) -> drawScene(graphics));
        addOutputs(widgets);
    }

    private void drawScene(GuiGraphics graphics) {
        long time = EmiScenes.millis();
        Fluid fluid = fluids.get((int) (time / 2000 % fluids.size())).getKeyOfType(Fluid.class);
        EmiScenes.beginIso(graphics, SCENE_W / 2f, SCENE_H / 2f + 3, 17);
        EmiScenes.block(graphics, sluiceState(), 0, -0.5f, 0);
        if (fluid != null) EmiScenes.fluid(graphics, fluid, 0.5f, 0.5f + 0.75f);
        if (item != null && !item.getEmiStacks().isEmpty()) {
            List<EmiStack> stacks = item.getEmiStacks();
            ItemStack stack = stacks.get((int) (time / 2000 % stacks.size())).getItemStack();
            float bob = Mth.sin(time / 400f) * 0.06f;
            EmiScenes.item(graphics, stack, 0.5f, 1.3f + bob, 0.5f, 0.5f, time / 20f % 360, 0);
        }
        EmiScenes.endIso(graphics);
    }

    private BlockState sluiceState() {
        BlockState state = sluice.defaultBlockState();
        for (String name : List.of("north", "south")) {
            Property<?> property = sluice.getStateDefinition().getProperty(name);
            if (property instanceof BooleanProperty bool) state = state.setValue(bool, true);
        }
        return state;
    }
}
