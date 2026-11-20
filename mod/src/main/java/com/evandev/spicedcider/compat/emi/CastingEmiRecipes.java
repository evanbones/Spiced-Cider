package com.evandev.spicedcider.compat.emi;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.content.casting.CastingHeat;
import com.evandev.spicedcider.content.casting.CrucibleMeltingRecipe;
import com.evandev.spicedcider.content.casting.MouldCastingRecipe;
import com.evandev.spicedcider.content.casting.MouldShape;
import com.evandev.spicedcider.content.casting.smeltery.SmelteryAlloyingRecipe;
import com.evandev.spicedcider.content.casting.smeltery.SmelteryBlockEntity;
import com.evandev.spicedcider.registry.ModItems;
import com.evandev.spicedcider.registry.ModRecipeSerializers;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.neoforge.NeoForgeEmiIngredient;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.TextWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;

public class CastingEmiRecipes {
    public static void register(EmiRegistry registry) {
        EmiStack crucible = EmiStack.of(ModItems.CRUCIBLE.get());
        EmiStack smeltery = EmiStack.of(ModItems.SMELTERY.get());
        EmiStack mould = EmiStack.of(ModItems.MOULDS.get(MouldShape.INGOT).get());
        EmiRecipeCategory melting = new EmiRecipeCategory(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "crucible_melting"), crucible);
        EmiRecipeCategory smelteryMelting = new EmiRecipeCategory(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "smeltery_melting"), smeltery);
        EmiRecipeCategory alloying = new EmiRecipeCategory(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "smeltery_alloying"), smeltery);
        EmiRecipeCategory casting = new EmiRecipeCategory(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "mould_casting"), mould);
        registry.addCategory(melting);
        registry.addCategory(smelteryMelting);
        registry.addCategory(alloying);
        registry.addCategory(casting);
        registry.addWorkstation(melting, crucible);
        registry.addWorkstation(smelteryMelting, smeltery);
        registry.addWorkstation(alloying, smeltery);
        for (MouldShape shape : MouldShape.values()) {
            registry.addWorkstation(casting, EmiStack.of(ModItems.MOULDS.get(shape).get()));
        }
        for (RecipeHolder<CrucibleMeltingRecipe> holder : registry.getRecipeManager().getAllRecipesFor(ModRecipeSerializers.CRUCIBLE_MELTING_TYPE.get())) {
            registry.addRecipe(new Melting(melting, holder.id(), holder.value(), true));
            if (holder.value().ingredients().size() == 1) {
                registry.addRecipe(new Melting(smelteryMelting, holder.id().withSuffix("/smeltery"), holder.value(), false));
            }
        }
        for (RecipeHolder<CrucibleMeltingRecipe> holder : registry.getRecipeManager().getAllRecipesFor(ModRecipeSerializers.SMELTERY_MELTING_TYPE.get())) {
            registry.addRecipe(new Melting(smelteryMelting, holder.id(), holder.value(), false));
        }
        for (RecipeHolder<SmelteryAlloyingRecipe> holder : registry.getRecipeManager().getAllRecipesFor(ModRecipeSerializers.SMELTERY_ALLOYING_TYPE.get())) {
            registry.addRecipe(new Alloying(alloying, holder.id(), holder.value()));
        }
        for (RecipeHolder<MouldCastingRecipe> holder : registry.getRecipeManager().getAllRecipesFor(ModRecipeSerializers.MOULD_CASTING_TYPE.get())) {
            registry.addRecipe(new Casting(casting, holder.id(), holder.value()));
        }
    }

    private static class Melting extends BasicEmiRecipe {
        private static final int WIDTH = 108;
        private static final int ARROW_X = (WIDTH - 24) / 2;
        private final CastingHeat heat;
        private final boolean showHeat;
        private final int ticks;
        private final int contentHeight;

        Melting(EmiRecipeCategory category, ResourceLocation id, CrucibleMeltingRecipe recipe, boolean showHeat) {
            super(category, id, WIDTH, height(recipe, showHeat));
            this.inputs = recipe.ingredients().stream().map(EmiIngredient::of).toList();
            this.outputs = List.of(EmiStack.of(recipe.result().getFluid(), recipe.result().getAmount()));
            this.heat = recipe.heat();
            this.showHeat = showHeat;
            this.ticks = showHeat ? recipe.time() : SmelteryBlockEntity.meltTime(recipe);
            this.contentHeight = contentHeight(recipe.ingredients().size());
        }

        private static int contentHeight(int inputs) {
            return Math.max(((inputs + 1) / 2) * 18, 26);
        }

        private static int height(CrucibleMeltingRecipe recipe, boolean showHeat) {
            return contentHeight(recipe.ingredients().size()) + (showHeat ? 13 : 0);
        }

        @Override
        public void addWidgets(WidgetHolder widgets) {
            int count = this.inputs.size();
            int rows = (count + 1) / 2;
            int top = (this.contentHeight - rows * 18) / 2;
            for (int i = 0; i < count; i++) {
                int column = count == 1 ? 1 : i % 2;
                widgets.addSlot(this.inputs.get(i), ARROW_X - 42 + column * 18, top + (i / 2) * 18);
            }
            widgets.addFillingArrow(ARROW_X, (this.contentHeight - 17) / 2, this.ticks * 50);
            widgets.addSlot(this.outputs.getFirst(), ARROW_X + 30, (this.contentHeight - 26) / 2).large(true).recipeContext(this);
            if (this.showHeat) {
                widgets.addText(this.heat.displayName(), WIDTH / 2, this.contentHeight + 4, 0xFF404040, false)
                        .horizontalAlign(TextWidget.Alignment.CENTER);
            }
        }
    }

    private static class Alloying extends BasicEmiRecipe {
        private static final int WIDTH = 140;
        private static final int COLUMNS = 4;
        private static final int ARROW_X = COLUMNS * 18 + 8;
        private final int fluidCount;
        private final int itemCount;
        private final int contentHeight;

        Alloying(EmiRecipeCategory category, ResourceLocation id, SmelteryAlloyingRecipe recipe) {
            super(category, id, WIDTH, contentHeight(recipe.items().size()));
            List<EmiIngredient> list = new ArrayList<>();
            recipe.fluids().forEach(fluid -> list.add(NeoForgeEmiIngredient.of(fluid)));
            recipe.items().forEach(item -> list.add(EmiIngredient.of(item)));
            this.inputs = list;
            this.outputs = List.of(EmiStack.of(recipe.result().getFluid(), recipe.result().getAmount()));
            this.fluidCount = recipe.fluids().size();
            this.itemCount = recipe.items().size();
            this.contentHeight = contentHeight(this.itemCount);
        }

        private static int inputHeight(int items) {
            return items == 0 ? 18 : 18 + 6 + ((items + COLUMNS - 1) / COLUMNS) * 18;
        }

        private static int contentHeight(int items) {
            return Math.max(inputHeight(items), 36);
        }

        @Override
        public void addWidgets(WidgetHolder widgets) {
            int top = (this.contentHeight - inputHeight(this.itemCount)) / 2;
            for (int i = 0; i < this.fluidCount; i++) {
                widgets.addSlot(this.inputs.get(i), i * 18, top);
            }
            if (this.itemCount > 0) {
                int dividerY = top + 20;
                widgets.addDrawable(1, dividerY, COLUMNS * 18 - 2, 1, (graphics, mouseX, mouseY, delta) -> graphics.fill(0, 0, COLUMNS * 18 - 2, 1, 0xFF8B8B8B));
                for (int i = 0; i < this.itemCount; i++) {
                    widgets.addSlot(this.inputs.get(this.fluidCount + i), (i % COLUMNS) * 18, top + 24 + (i / COLUMNS) * 18);
                }
            }
            int arrowY = (this.contentHeight - 33) / 2;
            widgets.addFillingArrow(ARROW_X, arrowY, 2000);
            widgets.addTexture(EmiTexture.EMPTY_FLAME, ARROW_X + 5, arrowY + 19);
            widgets.addAnimatedTexture(EmiTexture.FULL_FLAME, ARROW_X + 5, arrowY + 19, 4000, false, true, true);
            widgets.addSlot(this.outputs.getFirst(), ARROW_X + 32, (this.contentHeight - 26) / 2).large(true).recipeContext(this);
        }
    }

    private static class Casting extends BasicEmiRecipe {
        private static final int WIDTH = 96;

        Casting(EmiRecipeCategory category, ResourceLocation id, MouldCastingRecipe recipe) {
            super(category, id, WIDTH, 18);
            this.inputs = List.of(NeoForgeEmiIngredient.of(recipe.fluid()));
            this.catalysts = List.of(EmiStack.of(ModItems.MOULDS.get(recipe.shape()).get()));
            this.outputs = List.of(EmiStack.of(recipe.result()));
        }

        @Override
        public void addWidgets(WidgetHolder widgets) {
            widgets.addSlot(this.inputs.getFirst(), 0, 0);
            widgets.addSlot(this.catalysts.getFirst(), 20, 0).catalyst(true);
            widgets.addTexture(EmiTexture.EMPTY_ARROW, 44, 1);
            widgets.addSlot(this.outputs.getFirst(), WIDTH - 18, 0).recipeContext(this);
        }
    }
}
