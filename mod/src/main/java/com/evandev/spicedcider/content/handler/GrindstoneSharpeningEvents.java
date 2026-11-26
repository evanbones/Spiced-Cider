package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.registry.ModTags;
import com.evandev.spicedcider.util.BrokenItemUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class GrindstoneSharpeningEvents {
    private static final float MAX_SHARPENED = 0.7F;

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.grindstoneSharpening, false)) return;
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        if (!level.getBlockState(pos).is(Blocks.GRINDSTONE)) return;
        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();
        if (player.isSecondaryUseActive() || !stack.isDamageableItem() || !stack.isDamaged() || !stack.is(ModTags.Items.GRINDSTONE_SHARPENABLE)) return;
        int max = stack.getMaxDamage();
        int target = max - (int) Math.floor(max * MAX_SHARPENED);
        if (stack.getDamageValue() <= target) return;

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
        if (level.isClientSide) return;
        if (BrokenItemUtil.isBroken(stack)) {
            player.displayClientMessage(Component.translatable("message.spicedcider.grindstone_too_broken"), true);
            level.playSound(null, pos, SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 0.6F, 0.8F);
            return;
        }
        stack.setDamageValue(target);
        level.playSound(null, pos, SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
        if (level instanceof ServerLevel server) {
            server.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, stack), pos.getX() + 0.5, pos.getY() + 0.9, pos.getZ() + 0.5, 8, 0.2, 0.1, 0.2, 0.05);
            server.sendParticles(ParticleTypes.CRIT, pos.getX() + 0.5, pos.getY() + 0.9, pos.getZ() + 0.5, 6, 0.2, 0.1, 0.2, 0.1);
        }
    }
}
