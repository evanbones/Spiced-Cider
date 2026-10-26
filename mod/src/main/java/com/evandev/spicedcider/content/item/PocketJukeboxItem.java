package com.evandev.spicedcider.content.item;

import com.evandev.spicedcider.content.handler.PocketJukeboxEvents;
import com.evandev.spicedcider.registry.ModDataComponents;
import com.evandev.spicedcider.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class PocketJukeboxItem extends Item {
    private static final int BAR_COLOR = Mth.color(0.4F, 0.4F, 1.0F);

    public PocketJukeboxItem(Properties properties) {
        super(properties);
    }

    public static Item byColor(@Nullable DyeColor color) {
        return color == null ? ModItems.POCKET_JUKEBOX.get() : ModItems.POCKET_JUKEBOXES.get(color).get();
    }

    public static PocketJukeboxContents contents(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.POCKET_JUKEBOX.get(), PocketJukeboxContents.EMPTY);
    }

    public static void setContents(ItemStack stack, PocketJukeboxContents contents) {
        stack.set(ModDataComponents.POCKET_JUKEBOX.get(), contents);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (hand == InteractionHand.OFF_HAND) return InteractionResultHolder.pass(stack);
        if (contents(stack).isEmpty()) {
            if (!level.isClientSide()) {
                player.displayClientMessage(Component.translatable("item.spicedcider.pocket_jukebox.empty"), true);
            }
            return InteractionResultHolder.fail(stack);
        }
        if (player instanceof ServerPlayer serverPlayer) {
            PocketJukeboxEvents.use(serverPlayer, stack, player.isSecondaryUseActive());
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public boolean overrideStackedOnOther(@NotNull ItemStack stack, @NotNull Slot slot, @NotNull ClickAction action, @NotNull Player player) {
        if (action != ClickAction.SECONDARY) return false;
        PocketJukeboxContents contents = contents(stack);
        ItemStack other = slot.getItem();
        if (other.isEmpty()) {
            if (contents.isEmpty()) return true;
            ItemStack disc = contents.discs().getFirst();
            ItemStack remainder = slot.safeInsert(disc.copy());
            if (remainder.isEmpty()) {
                setContents(stack, contents.removeFirst());
                playRemoveOneSound(player);
            }
        } else if (PocketJukeboxContents.accepts(other) && contents.size() < PocketJukeboxContents.CAPACITY) {
            ItemStack disc = slot.safeTake(other.getCount(), 1, player);
            if (!disc.isEmpty()) {
                setContents(stack, contents.insert(disc));
                playInsertSound(player);
            }
        }
        return true;
    }

    @Override
    public boolean overrideOtherStackedOnMe(@NotNull ItemStack stack, @NotNull ItemStack other, @NotNull Slot slot, @NotNull ClickAction action, @NotNull Player player, @NotNull SlotAccess access) {
        if (action != ClickAction.SECONDARY || !slot.allowModification(player)) return false;
        PocketJukeboxContents contents = contents(stack);
        if (other.isEmpty()) {
            if (!contents.isEmpty()) {
                access.set(contents.discs().getFirst().copy());
                setContents(stack, contents.removeFirst());
                playRemoveOneSound(player);
            }
        } else if (PocketJukeboxContents.accepts(other) && contents.size() < PocketJukeboxContents.CAPACITY) {
            setContents(stack, contents.insert(other.split(1)));
            playInsertSound(player);
        }
        return true;
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return !contents(stack).isEmpty();
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        return Math.min(1 + 12 * contents(stack).size() / PocketJukeboxContents.CAPACITY, 13);
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        return BAR_COLOR;
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack stack) {
        return Optional.of(new PocketJukeboxTooltip(contents(stack)));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> lines, @NotNull TooltipFlag flag) {
        PocketJukeboxContents contents = contents(stack);
        lines.add(Component.translatable("item.spicedcider.pocket_jukebox.fullness", contents.size(), PocketJukeboxContents.CAPACITY).withStyle(ChatFormatting.GRAY));
        if (context.registries() != null) {
            contents.currentSong(context.registries()).ifPresent(song -> lines.add(Component.translatable(
                    contents.playing() ? "item.spicedcider.pocket_jukebox.playing" : "item.spicedcider.pocket_jukebox.paused_on",
                    song.description()).withStyle(ChatFormatting.GRAY)));
        }
    }

    @Override
    public void onDestroyed(@NotNull ItemEntity itemEntity) {
        ItemUtils.onContainerDestroyed(itemEntity, contents(itemEntity.getItem()).discs().stream().map(ItemStack::copy).toList());
    }

    @Override
    public boolean canFitInsideContainerItems() {
        return false;
    }

    private static void playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private static void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }
}
