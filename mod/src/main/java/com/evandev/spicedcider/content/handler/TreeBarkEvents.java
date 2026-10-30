package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class TreeBarkEvents {

    private static final ResourceLocation TREE_BARK = ResourceLocation.fromNamespaceAndPath("farmersdelight", "tree_bark");

    @Nullable
    private static Map<Block, Block> unstripped;

    public static Optional<Item> treeBark() {
        return BuiltInRegistries.ITEM.getOptional(TREE_BARK);
    }

    public static void dropBark(Level level, BlockPos pos, Direction face, BlockState original) {
        if (level.isClientSide() || !original.is(BlockTags.LOGS)) return;
        treeBark().ifPresent(bark -> Block.popResourceFromFace(level, pos, face, new ItemStack(bark)));
    }

    @SubscribeEvent
    public static void onTagsUpdated(TagsUpdatedEvent event) {
        unstripped = null;
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack = event.getItemStack();
        Optional<Item> bark = treeBark();
        if (bark.isEmpty() || !stack.is(bark.get())) return;

        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        Block log = unstrippedMap(level).get(state.getBlock());
        if (log == null) return;

        Player player = event.getEntity();
        if (!level.isClientSide()) {
            BlockState restored = log.withPropertiesOf(state);
            level.setBlock(pos, restored, Block.UPDATE_ALL_IMMEDIATE);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, restored));
            level.playSound(null, pos, restored.getSoundType(level, pos, player).getPlaceSound(), SoundSource.BLOCKS, 1.0F, 0.8F);
            stack.consume(1, player);
        }
        event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide()));
        event.setCanceled(true);
    }

    private static Map<Block, Block> unstrippedMap(Level level) {
        if (unstripped != null) return unstripped;
        Map<Block, Block> map = new HashMap<>();
        UseOnContext context = new UseOnContext(level, null, InteractionHand.MAIN_HAND, new ItemStack(Items.IRON_AXE), new BlockHitResult(Vec3.ZERO, Direction.UP, BlockPos.ZERO, false)) {
        };
        for (Block block : BuiltInRegistries.BLOCK) {
            BlockState state = block.defaultBlockState();
            if (!state.is(BlockTags.LOGS)) continue;
            try {
                BlockState stripped = state.getToolModifiedState(context, ItemAbilities.AXE_STRIP, true);
                if (stripped != null && stripped.getBlock() != block) map.putIfAbsent(stripped.getBlock(), block);
            } catch (RuntimeException ignored) {
            }
        }
        unstripped = map;
        return map;
    }
}
