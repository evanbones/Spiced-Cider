package com.evandev.spicedcider.compat.etcetera;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class ChiselEvents {
    public static final ResourceLocation CHISEL = ResourceLocation.fromNamespaceAndPath("etcetera", "chisel");
    private static final ResourceLocation CHISEL_SOUND = ResourceLocation.fromNamespaceAndPath("etcetera", "item.chisel.use");

    public static boolean isChisel(ItemStack stack) {
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).equals(CHISEL);
    }

    public static Item chisel() {
        return BuiltInRegistries.ITEM.get(CHISEL);
    }

    @SubscribeEvent
    public static void use(UseItemOnBlockEvent event) {
        if (event.getUsePhase() != UseItemOnBlockEvent.UsePhase.ITEM_BEFORE_BLOCK) return;
        ItemStack held = event.getItemStack();
        if (!isChisel(held)) return;

        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        Block target = ChiseledVariants.target(state, 0);
        if (target == null) return;

        event.cancelWithResult(ItemInteractionResult.sidedSuccess(level.isClientSide()));
        if (level.isClientSide()) return;

        BlockState chiseled = target.withPropertiesOf(state);
        level.setBlock(pos, chiseled, Block.UPDATE_ALL);
        level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(state));
        BuiltInRegistries.SOUND_EVENT.getOptional(CHISEL_SOUND)
                .ifPresent(sound -> level.playSound(null, pos, sound, SoundSource.PLAYERS, 0.75F, 1.0F));
        level.playSound(null, pos, chiseled.getSoundType(level, pos, null).getPlaceSound(), SoundSource.BLOCKS, 1.0F, 1.0F);

        Player player = event.getPlayer();
        if (player != null) {
            player.awardStat(Stats.ITEM_USED.get(held.getItem()));
            if (!player.getAbilities().instabuild) held.hurtAndBreak(1, player, LivingEntity.getSlotForHand(event.getHand()));
        }
    }
}
