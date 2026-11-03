package com.evandev.spicedcider.compat.dynamiccrosshair;

import com.evandev.spicedcider.SpicedCider;
import mod.crend.dynamiccrosshairapi.DynamicCrosshairApi;
import mod.crend.dynamiccrosshairapi.crosshair.Crosshair;
import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import java.util.List;

public class SpicedCiderCrosshairApi implements DynamicCrosshairApi {
    private static final TagKey<Block> MINEABLE_WITH_SHEARS = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "mineable/shears"));
    private static final TagKey<Block> DROPS_CAKE_SLICE = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("farmersdelight", "drops_cake_slice"));
    private static final TagKey<Item> KNIVES = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "tools/knife"));
    private static final List<ItemAbility> MODIFYING_ABILITIES = List.of(
            ItemAbilities.AXE_STRIP,
            ItemAbilities.AXE_SCRAPE,
            ItemAbilities.AXE_WAX_OFF,
            ItemAbilities.HOE_TILL,
            ItemAbilities.SHEARS_TRIM);

    @Override
    public String getNamespace() {
        return SpicedCider.MOD_ID;
    }

    @Override
    public boolean forceCheck() {
        return true;
    }

    @Override
    public Crosshair overrideFromItem(CrosshairContext context, InteractionType interactionType) {
        if (!context.isWithBlock() || !context.includeTool()) return null;
        ItemStack stack = context.getItemStack();
        boolean knife = stack.is(KNIVES);
        if (interactionType != InteractionType.TOOL && interactionType != InteractionType.USABLE_TOOL && !knife) return null;

        BlockState state = context.getBlockState();
        boolean alreadyUsable = interactionType == InteractionType.USABLE_TOOL;
        boolean newlyUsable = !alreadyUsable && isUsable(context, stack, state, knife);
        boolean shearable = stack.canPerformAction(ItemAbilities.SHEARS_DIG) && state.is(MINEABLE_WITH_SHEARS);
        if (!shearable && !newlyUsable) return null;

        InteractionType primary = alreadyUsable || newlyUsable ? InteractionType.USABLE_TOOL : InteractionType.TOOL;
        InteractionType secondary = shearable ? InteractionType.CORRECT_TOOL : context.checkToolWithBlock();
        return Crosshair.combine(new Crosshair(primary), new Crosshair(secondary));
    }

    private static boolean isUsable(CrosshairContext context, ItemStack stack, BlockState state, boolean knife) {
        if (context.getPlayer().isSecondaryUseActive()) return false;
        if (knife && (state.getBlock() instanceof CakeBlock || state.is(DROPS_CAKE_SLICE))) return true;
        if (state.is(Blocks.PUMPKIN) && stack.canPerformAction(ItemAbilities.SHEARS_CARVE)) return true;
        if (state.getBlock() instanceof BeehiveBlock && state.getValue(BeehiveBlock.HONEY_LEVEL) >= BeehiveBlock.MAX_HONEY_LEVELS
                && stack.canPerformAction(ItemAbilities.SHEARS_HARVEST)) return true;

        UseOnContext useContext = context.getItemUsageContext();
        for (ItemAbility ability : MODIFYING_ABILITIES) {
            if (stack.canPerformAction(ability) && state.getToolModifiedState(useContext, ability, true) != null) return true;
        }
        return stack.canPerformAction(ItemAbilities.SHOVEL_FLATTEN)
                && context.getBlockHitSide() != Direction.DOWN
                && context.getWorld().getBlockState(context.getBlockPos().above()).isAir()
                && state.getToolModifiedState(useContext, ItemAbilities.SHOVEL_FLATTEN, true) != null;
    }
}
