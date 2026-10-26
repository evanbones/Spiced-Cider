package com.evandev.spicedcider.compat.iteminteractions;

import com.evandev.spicedcider.content.item.PocketJukeboxContents;
import com.evandev.spicedcider.content.item.PocketJukeboxItem;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.iteminteractions.api.v1.DyeBackedColor;
import fuzs.iteminteractions.api.v1.provider.ItemContentsProvider;
import fuzs.iteminteractions.api.v1.provider.impl.BundleProvider;
import fuzs.iteminteractions.api.v1.tooltip.BundleContentsTooltip;
import fuzs.puzzleslib.api.container.v1.ContainerMenuHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class PocketJukeboxProvider extends BundleProvider {
    public static final MapCodec<PocketJukeboxProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            backgroundColorCodec()
    ).apply(instance, color -> new PocketJukeboxProvider(color.orElse(null))));

    public PocketJukeboxProvider(@Nullable DyeBackedColor color) {
        super(PocketJukeboxContents.CAPACITY, color);
    }

    @Override
    public boolean hasContents(ItemStack containerStack) {
        return !PocketJukeboxItem.contents(containerStack).isEmpty();
    }

    @Override
    public SimpleContainer getItemContainer(ItemStack containerStack, Player player, boolean allowSaving) {
        PocketJukeboxContents contents = PocketJukeboxItem.contents(containerStack);
        ItemStack[] stacks = Stream.concat(contents.discs().stream().map(ItemStack::copy), Stream.of(ItemStack.EMPTY)).toArray(ItemStack[]::new);
        NonNullList<ItemStack> items = NonNullList.of(ItemStack.EMPTY, stacks);
        return ContainerMenuHelper.createListBackedContainer(items, allowSaving ? container -> {
            PocketJukeboxContents current = PocketJukeboxItem.contents(containerStack);
            List<ItemStack> discs = new ArrayList<>();
            for (ItemStack stack : items) {
                if (!stack.isEmpty()) discs.add(stack.copy());
            }
            ItemStack playing = current.currentDisc();
            int index = -1;
            for (int i = 0; i < discs.size() && !playing.isEmpty(); i++) {
                if (ItemStack.matches(discs.get(i), playing)) {
                    index = i;
                    break;
                }
            }
            PocketJukeboxContents updated = index >= 0
                    ? new PocketJukeboxContents(discs, index, current.progress(), current.playing(), current.session(), current.started())
                    : new PocketJukeboxContents(discs, -1, 0, false, current.session(), current.started());
            PocketJukeboxItem.setContents(containerStack, updated);
        } : null);
    }

    @Override
    public boolean isItemAllowedInContainer(ItemStack stackToAdd) {
        return PocketJukeboxContents.accepts(stackToAdd) && super.isItemAllowedInContainer(stackToAdd);
    }

    @Override
    public int getMaxAmountToAdd(ItemStack containerStack, ItemStack stackToAdd, Player player) {
        if (!isItemAllowedInContainer(stackToAdd)) return 0;
        return Math.max(0, PocketJukeboxContents.CAPACITY - PocketJukeboxItem.contents(containerStack).size());
    }

    @Override
    public Fraction computeContentWeight(ItemStack containerStack, Player player) {
        return Fraction.getFraction(PocketJukeboxItem.contents(containerStack).size(), 1);
    }

    @Override
    public TooltipComponent createTooltipImageComponent(ItemStack containerStack, Player player, NonNullList<ItemStack> items) {
        return new BundleContentsTooltip(items, PocketJukeboxItem.contents(containerStack).size() >= PocketJukeboxContents.CAPACITY, getBackgroundColor());
    }

    @Override
    public ItemContentsProvider.Type getType() {
        return ItemInteractionsCompat.POCKET_JUKEBOX.value();
    }
}
