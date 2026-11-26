package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.registry.ModTags;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class MossHarvestEvents {

    @SubscribeEvent
    public static void onBlockDrops(BlockDropsEvent event) {
        if (SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.mossNeedsHoe, false)
                && event.getBreaker() instanceof Player
                && event.getState().is(ModTags.Blocks.NEEDS_HOE_TO_HARVEST)
                && !event.getTool().isCorrectToolForDrops(event.getState())
                && !event.getTool().canPerformAction(ItemAbilities.SHEARS_DIG)
                && !hasSilkTouch(event.getLevel(), event.getTool())) {
            event.getDrops().clear();
        }
    }

    private static boolean hasSilkTouch(Level level, ItemStack tool) {
        return level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(Enchantments.SILK_TOUCH)
                .map(silkTouch -> EnchantmentHelper.getItemEnchantmentLevel(silkTouch, tool) > 0)
                .orElse(false);
    }
}
