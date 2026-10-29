package com.evandev.spicedcider.content.item;

import com.evandev.spicedcider.client.page.RuinedPageScreen;
import com.evandev.spicedcider.registry.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RuinedPageItem extends Item {
    public static final int PAGES = 31;

    public RuinedPageItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot, boolean selected) {
        if (!level.isClientSide() && !stack.has(ModDataComponents.RUINED_PAGE)) {
            stack.set(ModDataComponents.RUINED_PAGE, 1 + level.getRandom().nextInt(PAGES));
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        Integer page = stack.get(ModDataComponents.RUINED_PAGE);
        if (page == null) return InteractionResultHolder.pass(stack);
        level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 1.0F, 0.8F);
        if (level.isClientSide()) RuinedPageScreen.open(page);
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        Integer page = stack.get(ModDataComponents.RUINED_PAGE);
        if (page != null) tooltip.add(Component.translatable("item.spicedcider.ruined_page.page", page, PAGES).withStyle(ChatFormatting.GRAY));
    }
}
