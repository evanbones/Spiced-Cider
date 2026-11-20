package com.evandev.spicedcider.compat.create;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public class BasinWashing {
    private static final int WATER_PER_WASH = 250;
    private static final String[] LABELS = {"Label1", "Label2", "Label3", "Label4"};

    public static boolean tryWash(Level level, BasinBlockEntity basin, @Nullable Player player, InteractionHand hand, ItemStack held) {
        ItemStack clean = washed(held);
        if (clean == null || water(basin) < WATER_PER_WASH) return false;
        if (level.isClientSide()) return true;
        basin.inputTank.getCapability().drain(new FluidStack(Fluids.WATER, WATER_PER_WASH), IFluidHandler.FluidAction.EXECUTE);
        if (player != null) {
            player.setItemInHand(hand, clean);
        }
        level.playSound(null, basin.getBlockPos(), SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 0.4F, 1.2F);
        basin.notifyChangeOfContents();
        basin.notifyUpdate();
        return true;
    }

    private static int water(BasinBlockEntity basin) {
        IFluidHandler tank = basin.inputTank.getCapability();
        int total = 0;
        for (int i = 0; i < tank.getTanks(); i++) {
            FluidStack stack = tank.getFluidInTank(i);
            if (stack.getFluid().isSame(Fluids.WATER)) total += stack.getAmount();
        }
        return total;
    }

    @Nullable
    public static ItemStack washed(ItemStack stack) {
        if (stack.isEmpty()) return null;
        boolean changed = false;
        ItemStack result = stack.copy();
        if (result.is(ItemTags.DYEABLE) && result.has(DataComponents.DYED_COLOR)) {
            result.remove(DataComponents.DYED_COLOR);
            changed = true;
        }
        BannerPatternLayers patterns = result.get(DataComponents.BANNER_PATTERNS);
        if (result.is(ItemTags.BANNERS) && patterns != null && !patterns.layers().isEmpty()) {
            result.set(DataComponents.BANNER_PATTERNS, patterns.removeLast());
            changed = true;
        }
        if (result.getItem() instanceof BlockItem block && block.getBlock() instanceof ShulkerBoxBlock && !result.is(Items.SHULKER_BOX)) {
            result = result.transmuteCopy(Items.SHULKER_BOX, result.getCount());
            changed = true;
        }
        CustomData data = result.get(DataComponents.CUSTOM_DATA);
        if (data != null) {
            CompoundTag tag = data.copyTag();
            boolean labelled = false;
            for (String label : LABELS) {
                if (tag.contains(label)) {
                    tag.remove(label);
                    labelled = true;
                }
            }
            if (labelled) {
                if (tag.isEmpty()) result.remove(DataComponents.CUSTOM_DATA);
                else result.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
                changed = true;
            }
        }
        return changed ? result : null;
    }
}
