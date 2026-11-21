package com.evandev.spicedcider.compat.emi;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.content.casting.CastingHeat;
import com.evandev.spicedcider.content.casting.CrucibleBlockEntity;
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
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.TankWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
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

    private static final int TANK = 34;

    private static TankWidget tank(WidgetHolder widgets, EmiIngredient stack, int x, int y, int width, int height, int capacity, int marks) {
        widgets.addDrawable(x, y, width, height, (graphics, mouseX, mouseY, delta) -> {
            graphics.fill(0, 0, width - 1, 1, 0xFF373737);
            graphics.fill(0, 0, 1, height - 1, 0xFF373737);
            graphics.fill(1, height - 1, width, height, 0xFFFFFFFF);
            graphics.fill(width - 1, 1, width, height, 0xFFFFFFFF);
            graphics.fill(1, 1, width - 1, height - 1, 0xFF8B8B8B);
        });
        TankWidget widget = widgets.addTank(stack, x, y, width, height, capacity);
        widget.drawBack(false);
        if (marks > 1) {
            widgets.addDrawable(x, y, width, height, (graphics, mouseX, mouseY, delta) -> {
                for (int i = 1; i < marks; i++) {
                    int lineY = height - 1 - (height - 2) * i / marks;
                    graphics.fill(1, lineY, i % 2 == 0 ? width / 2 : width / 3, lineY + 1, 0x80373737);
                }
            });
        }
        return widget;
    }

    private static String seconds(int ticks) {
        float value = ticks / 20.0F;
        return value == Math.floor(value) ? String.valueOf((int) value) : String.format("%.1f", value);
    }

    private static EmiIngredient heatSources(CastingHeat heat) {
        List<EmiStack> stacks = new ArrayList<>();
        List<TagKey<Block>> tags = heat == CastingHeat.HIGH ? List.of(CastingHeat.HIGH_HEAT) : List.of(CastingHeat.LOW_HEAT, CastingHeat.HIGH_HEAT);
        for (TagKey<Block> tag : tags) {
            BuiltInRegistries.BLOCK.getTag(tag).ifPresent(set -> set.forEach(holder -> {
                Item item = holder.value().asItem();
                if (item != Items.AIR) {
                    EmiStack stack = EmiStack.of(item);
                    if (!stacks.contains(stack)) {
                        stacks.add(stack);
                    }
                }
            }));
        }
        BuiltInRegistries.ITEM.getOptional(ResourceLocation.fromNamespaceAndPath("create", "blaze_burner")).ifPresent(item -> stacks.add(EmiStack.of(item)));
        stacks.add(EmiStack.of(Fluids.LAVA, 1000));
        return EmiIngredient.of(stacks);
    }

    private static class Melting extends BasicEmiRecipe {
        private static final int WIDTH = 128;
        private static final int HEIGHT = TANK;
        private static final int ARROW_X = 42;
        private static final int TANK_X = 72;
        private final CastingHeat heat;
        private final boolean crucible;
        private final int ticks;
        private final int capacity;
        private final int fuel;

        Melting(EmiRecipeCategory category, ResourceLocation id, CrucibleMeltingRecipe recipe, boolean crucible) {
            super(category, id, WIDTH, HEIGHT);
            this.inputs = recipe.ingredients().stream().map(EmiIngredient::of).toList();
            this.outputs = List.of(EmiStack.of(recipe.result().getFluid(), recipe.result().getAmount()));
            this.heat = recipe.heat();
            this.crucible = crucible;
            this.ticks = crucible ? recipe.time() : SmelteryBlockEntity.meltTime(recipe);
            this.capacity = crucible ? CrucibleBlockEntity.CAPACITY : Math.max(1000, recipe.result().getAmount());
            this.fuel = SmelteryBlockEntity.fuelCost(recipe.result());
            if (crucible) {
                this.catalysts = List.of(heatSources(this.heat));
            }
        }

        @Override
        public void addWidgets(WidgetHolder widgets) {
            int top = 0;
            int count = this.inputs.size();
            int rows = (count + 1) / 2;
            int inputY = top + (TANK - rows * 18) / 2;
            for (int i = 0; i < count; i++) {
                int x = count == 1 ? 9 : (i % 2) * 18;
                widgets.addSlot(this.inputs.get(i), x, inputY + (i / 2) * 18);
            }
            int arrowY = top + (TANK - 17) / 2;
            widgets.addFillingArrow(ARROW_X, arrowY, this.ticks * 50);
            widgets.addTooltipText(List.of(Component.translatable("spicedcider.emi.melt_time", seconds(this.ticks))), ARROW_X, arrowY, 24, 17);
            tank(widgets, this.outputs.getFirst(), TANK_X, top, TANK, TANK, this.capacity, this.capacity / 250).recipeContext(this);
            if (this.crucible) {
                widgets.addSlot(this.catalysts.getFirst(), TANK_X + TANK + 4, top + (TANK - 18) / 2).catalyst(true)
                        .appendTooltip(this.heat.displayName().copy().withStyle(ChatFormatting.GOLD));
            } else {
                tank(widgets, EmiIngredient.of(SmelteryBlockEntity.FUELS, this.fuel), TANK_X + TANK + 4, top, 14, TANK, Math.max(this.fuel, 100), 0)
                        .appendTooltip(Component.translatable("spicedcider.emi.fuel", this.fuel).withStyle(ChatFormatting.GOLD));
            }
        }
    }

    private static class Alloying extends BasicEmiRecipe {
        private static final int HEIGHT = TANK;
        private final int fluidCount;
        private final int itemCount;
        private final int capacity;

        Alloying(EmiRecipeCategory category, ResourceLocation id, SmelteryAlloyingRecipe recipe) {
            super(category, id, width(recipe), HEIGHT);
            List<EmiIngredient> list = new ArrayList<>();
            recipe.fluids().forEach(fluid -> list.add(NeoForgeEmiIngredient.of(fluid)));
            recipe.items().forEach(item -> list.add(EmiIngredient.of(item)));
            this.inputs = list;
            this.outputs = List.of(EmiStack.of(recipe.result().getFluid(), recipe.result().getAmount()));
            this.fluidCount = recipe.fluids().size();
            this.itemCount = recipe.items().size();
            int max = recipe.result().getAmount();
            for (SizedFluidIngredient fluid : recipe.fluids()) {
                max = Math.max(max, fluid.amount());
            }
            this.capacity = max;
        }

        private static int itemColumns(int items) {
            return (items + 1) / 2;
        }

        private static int width(SmelteryAlloyingRecipe recipe) {
            return recipe.fluids().size() * 18 + itemColumns(recipe.items().size()) * 18 + 4 + 24 + 6 + TANK;
        }

        @Override
        public void addWidgets(WidgetHolder widgets) {
            int top = 0;
            for (int i = 0; i < this.fluidCount; i++) {
                tank(widgets, this.inputs.get(i), i * 18, top, 16, TANK, this.capacity, 0);
            }
            int x = this.fluidCount * 18;
            int rows = Math.min(2, this.itemCount);
            int itemY = top + (TANK - rows * 18) / 2;
            for (int i = 0; i < this.itemCount; i++) {
                widgets.addSlot(this.inputs.get(this.fluidCount + i), x + (i / 2) * 18, itemY + (i % 2) * 18);
            }
            int arrowX = x + itemColumns(this.itemCount) * 18 + 4;
            int arrowY = top + (TANK - 17) / 2;
            widgets.addFillingArrow(arrowX, arrowY, 2000);
            tank(widgets, this.outputs.getFirst(), arrowX + 30, top, TANK, TANK, this.capacity, 0).recipeContext(this);
        }
    }

    private static class Casting extends BasicEmiRecipe {
        private static final int WIDTH = 104;
        private static final int HEIGHT = 34;
        private final int ticks;

        Casting(EmiRecipeCategory category, ResourceLocation id, MouldCastingRecipe recipe) {
            super(category, id, WIDTH, HEIGHT);
            this.inputs = List.of(NeoForgeEmiIngredient.of(recipe.fluid()));
            this.catalysts = List.of(EmiStack.of(ModItems.MOULDS.get(recipe.shape()).get()));
            this.outputs = List.of(EmiStack.of(recipe.result()));
            this.ticks = recipe.time();
        }

        @Override
        public void addWidgets(WidgetHolder widgets) {
            tank(widgets, this.inputs.getFirst(), 0, 0, 16, TANK, Math.max(1, (int) this.inputs.getFirst().getAmount()), 0);
            widgets.addSlot(this.catalysts.getFirst(), 22, 8).catalyst(true);
            widgets.addFillingArrow(44, 9, this.ticks * 50);
            widgets.addTooltipText(List.of(Component.translatable("spicedcider.emi.cool_time", seconds(this.ticks))), 44, 9, 24, 17);
            widgets.addSlot(this.outputs.getFirst(), 74, 4).large(true).recipeContext(this);
        }
    }
}
