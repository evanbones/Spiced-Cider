package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.compat.reliableremover.ReliableRemoverCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class BlockDyeingEvents {
    private static Map<Block, Map<DyeColor, Block>> families;

    @SubscribeEvent
    public static void use(UseItemOnBlockEvent event) {
        if (event.getUsePhase() != UseItemOnBlockEvent.UsePhase.ITEM_BEFORE_BLOCK) return;
        ItemStack held = event.getItemStack();
        DyeColor color = DyeColor.getColor(held);
        if (color == null) return;

        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        Block target = dyed(state.getBlock(), color);
        if (target == null || ReliableRemoverCompat.isRemoved(target.asItem())) return;

        event.cancelWithResult(ItemInteractionResult.sidedSuccess(level.isClientSide()));
        if (level.isClientSide()) return;

        Player player = event.getPlayer();
        level.setBlock(pos, target.withPropertiesOf(state), Block.UPDATE_ALL);
        level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        if (player != null) {
            player.awardStat(Stats.ITEM_USED.get(held.getItem()));
            if (!player.getAbilities().instabuild) held.shrink(1);
        }
    }

    @Nullable
    public static Block dyed(Block block, DyeColor color) {
        Map<DyeColor, Block> family = families().get(block);
        if (family == null) return null;
        Block target = family.get(color);
        return target == block ? null : target;
    }

    private static Map<Block, Map<DyeColor, Block>> families() {
        if (families == null) families = buildFamilies();
        return families;
    }

    private static Map<Block, Map<DyeColor, Block>> buildFamilies() {
        Map<String, Map<DyeColor, Block>> byKey = new HashMap<>();
        for (Block block : BuiltInRegistries.BLOCK) {
            if (!dyeable(block)) continue;
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
            List<String> tokens = new ArrayList<>(Arrays.asList(id.getPath().split("_")));
            DyeColor color = stripColor(tokens);
            if (color == null) continue;
            tokens.remove("stained");
            byKey.computeIfAbsent(id.getNamespace() + ":" + String.join("_", tokens), key -> new EnumMap<>(DyeColor.class)).put(color, block);
        }

        Map<Block, Map<DyeColor, Block>> result = new HashMap<>();
        byKey.forEach((key, family) -> {
            if (family.size() < DyeColor.values().length) return;
            family.values().forEach(block -> result.put(block, family));
            Block base = BuiltInRegistries.BLOCK.getOptional(ResourceLocation.parse(key)).orElse(null);
            if (base != null && dyeable(base)) result.put(base, family);
        });
        return result;
    }

    private static boolean dyeable(Block block) {
        BlockState state = block.defaultBlockState();
        return !(block instanceof EntityBlock)
                && !state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)
                && !state.hasProperty(BlockStateProperties.BED_PART);
    }

    @Nullable
    private static DyeColor stripColor(List<String> tokens) {
        for (int i = 0; i < tokens.size(); i++) {
            if (i + 1 < tokens.size()) {
                DyeColor two = DyeColor.byName(tokens.get(i) + "_" + tokens.get(i + 1), null);
                if (two != null) {
                    tokens.subList(i, i + 2).clear();
                    return two;
                }
            }
            DyeColor one = DyeColor.byName(tokens.get(i), null);
            if (one != null) {
                tokens.remove(i);
                return one;
            }
        }
        return null;
    }
}
