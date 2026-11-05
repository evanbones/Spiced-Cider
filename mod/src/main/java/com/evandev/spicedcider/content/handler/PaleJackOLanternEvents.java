package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.registry.ModFireBlocks;
import com.evandev.spicedcider.registry.ModPaleGardenBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class PaleJackOLanternEvents {
    private static final TagKey<Item> CUPRIC_TORCHES = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "torches/cupric"));
    private static final ResourceLocation GLOWLIGHT_TORCH = ResourceLocation.fromNamespaceAndPath("infernalexp", "glowlight_torch");

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!ModList.get().isLoaded("autumnity")) return;
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        if (!state.is(ModPaleGardenBlocks.CARVED_PALE_PUMPKIN.get())) return;
        ItemStack stack = event.getItemStack();
        Block lantern = lanternFor(stack);
        if (lantern == null) return;
        if (!level.isClientSide) {
            level.setBlock(pos, lantern.withPropertiesOf(state), 3);
            level.playSound(null, pos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(event.getEntity(), GameEvent.BLOCK_CHANGE, pos);
            stack.consume(1, event.getEntity());
        }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
    }

    @Nullable
    private static Block lanternFor(ItemStack stack) {
        if (stack.is(Items.TORCH)) return ModPaleGardenBlocks.PALE_JACK_O_LANTERN.get();
        if (stack.is(Items.SOUL_TORCH)) return ModPaleGardenBlocks.PALE_SOUL_JACK_O_LANTERN.get();
        if (stack.is(Items.REDSTONE_TORCH)) return ModPaleGardenBlocks.PALE_REDSTONE_JACK_O_LANTERN.get();
        if (stack.is(ModFireBlocks.SULFUR_TORCH_ITEM.get())) return ModPaleGardenBlocks.PALE_SULFUR_JACK_O_LANTERN.get();
        if (stack.is(CUPRIC_TORCHES)) return get(ModPaleGardenBlocks.PALE_CUPRIC_JACK_O_LANTERN);
        if (BuiltInRegistries.ITEM.getKey(stack.getItem()).equals(GLOWLIGHT_TORCH)) return get(ModPaleGardenBlocks.PALE_GLOWLIGHT_JACK_O_LANTERN);
        return null;
    }

    @Nullable
    private static Block get(@Nullable DeferredBlock<Block> block) {
        return block == null ? null : block.get();
    }
}
