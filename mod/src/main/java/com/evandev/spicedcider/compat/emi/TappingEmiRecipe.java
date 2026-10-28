package com.evandev.spicedcider.compat.emi;

import com.evandev.spicedcider.SpicedCider;
import com.farcr.nomansland.common.block.tap.TapBlock;
import com.farcr.nomansland.common.block.tap.TapInteraction;
import com.farcr.nomansland.common.registry.NMLRegistries;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.fluids.CauldronFluidContent;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TappingEmiRecipe extends BasicEmiRecipe {
    private static final ResourceLocation TAP_ID = ResourceLocation.fromNamespaceAndPath("nomansland", "tap");
    private static final int WIDTH = 134;
    private static final int SCENE_HEIGHT = 58;

    private final List<BlockState> sources;
    private final boolean tree;
    private final BlockState receiver;
    private final IntegerProperty receiverLevel;

    private TappingEmiRecipe(EmiRecipeCategory category, ResourceLocation id, List<BlockState> sources, boolean tree,
                             EmiStack output, BlockState receiver) {
        super(category, id, WIDTH, SCENE_HEIGHT);
        this.sources = sources;
        this.tree = tree;
        this.receiver = receiver;
        this.receiverLevel = receiver != null && receiver.getBlock().getStateDefinition().getProperty("level") instanceof IntegerProperty level ? level : null;
        this.inputs = List.of(EmiIngredient.of(sources.stream().map(state -> EmiStack.of(state.getBlock())).distinct().toList()));
        this.outputs = List.of(output);
    }

    public static void register(EmiRegistry registry) {
        Block tap = BuiltInRegistries.BLOCK.get(TAP_ID);
        if (tap == Blocks.AIR) return;
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection == null) return;
        Registry<TapInteraction> interactions = connection.registryAccess().registry(NMLRegistries.TAP_INTERACTION_KEY).orElse(null);
        if (interactions == null) return;

        EmiStack tapStack = EmiStack.of(tap);
        EmiRecipeCategory category = new EmiRecipeCategory(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "tapping"), tapStack);
        registry.addCategory(category);
        registry.addWorkstation(category, tapStack);

        for (Map.Entry<ResourceKey<TapInteraction>, TapInteraction> entry : interactions.entrySet()) {
            TapInteraction interaction = entry.getValue();
            if (interaction.output().isEmpty()) continue;
            List<BlockState> sources = sourceStates(interaction.source());
            if (sources.isEmpty()) continue;

            TapInteraction.Output out = interaction.output().get();
            EmiStack output;
            BlockState receiver = null;
            if (out.cauldron().isPresent()) {
                Block cauldron = out.cauldron().get();
                receiver = cauldron.defaultBlockState();
                CauldronFluidContent content = CauldronFluidContent.getForBlock(cauldron);
                if (content != null && content.fluid != Fluids.EMPTY) {
                    output = EmiStack.of(content.fluid, content.totalAmount / content.maxLevel);
                } else if (cauldron.asItem() != Items.AIR) {
                    output = EmiStack.of(cauldron);
                } else {
                    continue;
                }
            } else {
                FluidStack fluid = out.fluid().get();
                output = EmiStack.of(fluid.getFluid(), fluid.getComponentsPatch(), fluid.getAmount());
            }

            ResourceLocation key = entry.getKey().location();
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "/tapping/" + key.getNamespace() + "/" + key.getPath());
            registry.addRecipe(new TappingEmiRecipe(category, id, sources, interaction.source().tree().isPresent(), output, receiver));
        }
    }

    private static List<BlockState> sourceStates(TapInteraction.Source source) {
        if (!source.states().isEmpty()) return source.states();
        List<BlockState> states = new ArrayList<>();
        for (Holder<Block> holder : source.blocks()) {
            Block block = holder.value();
            if (block.asItem() == Items.AIR) continue;
            BlockState state = block.defaultBlockState();
            if (state.hasProperty(RotatedPillarBlock.AXIS)) state = state.setValue(RotatedPillarBlock.AXIS, Direction.Axis.Y);
            states.add(state);
        }
        return states;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        int slotY = 20;
        widgets.addSlot(inputs.getFirst(), 4, slotY);
        widgets.addSlot(outputs.getFirst(), WIDTH - 22, slotY).recipeContext(this);
        widgets.addDrawable(26, 0, WIDTH - 52, SCENE_HEIGHT, (graphics, mouseX, mouseY, delta) -> drawScene(graphics));
    }

    private void drawScene(GuiGraphics graphics) {
        long time = System.currentTimeMillis();
        BlockState source = sources.get((int) (time / 2000 % sources.size()));
        Block tapBlock = BuiltInRegistries.BLOCK.get(TAP_ID);
        BlockState tap = tapBlock.defaultBlockState().setValue(TapBlock.FACING, Direction.SOUTH).setValue(TapBlock.CLOSED, false);

        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate((WIDTH - 52) / 2f, 31, 100);
        pose.scale(18, -18, 18);
        pose.mulPose(Axis.XP.rotationDegrees(30));
        pose.mulPose(Axis.YP.rotationDegrees(45));
        pose.translate(-0.5f, -1f, -1f);

        Lighting.setupFor3DItems();
        if (tree) renderBlock(graphics, source, 0, 0, 0);
        renderBlock(graphics, source, 0, 1, 0);
        renderBlock(graphics, tap, 0, 1, 1);
        if (receiver != null) renderBlock(graphics, receiverState(time), 0, 0, 1);
        graphics.flush();
        pose.popPose();
    }

    private BlockState receiverState(long time) {
        if (receiverLevel == null) return receiver;
        List<Integer> levels = new ArrayList<>(receiverLevel.getPossibleValues());
        int step = (int) (time / 1000 % (levels.size() + 1));
        return step == 0 ? Blocks.CAULDRON.defaultBlockState() : receiver.setValue(receiverLevel, levels.get(step - 1));
    }

    private static void renderBlock(GuiGraphics graphics, BlockState state, int x, int y, int z) {
        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(x, y, z);
        dispatcher.renderSingleBlock(state, pose, graphics.bufferSource(), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);
        pose.popPose();
    }
}
