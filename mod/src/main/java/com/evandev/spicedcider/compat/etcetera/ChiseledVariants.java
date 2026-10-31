package com.evandev.spicedcider.compat.etcetera;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.compat.reliableremover.ReliableRemoverCompat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class ChiseledVariants {
    private static final Map<String, String> EXTRAS = loadExtras();
    private static final Cache SERVER = new Cache();
    private static final Cache CLIENT = new Cache();

    private static class Cache {
        @Nullable
        private Collection<RecipeHolder<?>> recipes;
        private Map<Block, List<Block>> map = Map.of();
    }

    @SubscribeEvent
    public static void onTagsUpdated(TagsUpdatedEvent event) {
        Cache cache = event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.CLIENT_PACKET_RECEIVED ? CLIENT : SERVER;
        synchronized (cache) {
            cache.recipes = null;
        }
    }

    public static List<Block> targets(BlockState state) {
        return map().getOrDefault(state.getBlock(), List.of());
    }

    @Nullable
    public static Block target(BlockState state, int slot) {
        List<Block> targets = targets(state);
        return slot < targets.size() ? targets.get(slot) : null;
    }

    public static Map<Block, List<Block>> map() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        boolean client = server == null || !server.isSameThread();
        Level level = client ? ChiseledVariantsClient.level() : server.overworld();
        if (level == null) return Map.of();
        Cache cache = client ? CLIENT : SERVER;
        synchronized (cache) {
            RecipeManager manager = level.getRecipeManager();
            if (cache.recipes != manager.getRecipes()) {
                cache.recipes = manager.getRecipes();
                cache.map = build(manager, level.registryAccess());
            }
            return cache.map;
        }
    }

    private static Map<Block, List<Block>> build(RecipeManager manager, RegistryAccess registries) {
        Map<Block, Set<Block>> found = new HashMap<>();
        for (RecipeHolder<?> holder : manager.getAllRecipesFor(RecipeType.STONECUTTING)) {
            collect(holder.value(), registries, found, false);
        }
        for (RecipeHolder<?> holder : manager.getAllRecipesFor(RecipeType.CRAFTING)) {
            collect(holder.value(), registries, found, true);
        }
        EXTRAS.forEach((from, to) -> {
            Block source = BuiltInRegistries.BLOCK.getOptional(ResourceLocation.parse(from)).orElse(null);
            Block target = BuiltInRegistries.BLOCK.getOptional(ResourceLocation.parse(to)).orElse(null);
            if (source != null && target != null && !ReliableRemoverCompat.isRemoved(source.asItem()) && !ReliableRemoverCompat.isRemoved(target.asItem())) found.computeIfAbsent(source, key -> new HashSet<>()).add(target);
        });

        Comparator<Block> byId = Comparator.comparing(block -> BuiltInRegistries.BLOCK.getKey(block).toString());
        Map<Block, List<Block>> result = new HashMap<>();
        found.forEach((source, targets) -> result.put(source, targets.stream().sorted(byId).toList()));
        return result;
    }

    private static void collect(Recipe<?> recipe, RegistryAccess registries, Map<Block, Set<Block>> found, boolean uniform) {
        ItemStack output;
        try {
            output = recipe.getResultItem(registries);
        } catch (RuntimeException e) {
            return;
        }
        if (!(output.getItem() instanceof BlockItem resultItem) || !isChiseled(resultItem.getBlock())) return;
        Block target = resultItem.getBlock();

        Set<Block> sources = null;
        for (Ingredient ingredient : recipe.getIngredients()) {
            if (ingredient.isEmpty()) continue;
            Set<Block> blocks = new HashSet<>();
            for (ItemStack stack : ingredient.getItems()) {
                if (stack.getItem() instanceof BlockItem item && isSource(item.getBlock(), target)) blocks.add(item.getBlock());
            }
            if (sources == null) {
                sources = blocks;
            } else if (!uniform) {
                sources.addAll(blocks);
            } else if (!sources.equals(blocks)) {
                return;
            }
        }
        if (sources == null) return;
        for (Block source : sources) found.computeIfAbsent(source, key -> new HashSet<>()).add(target);
    }

    private static boolean isChiseled(Block block) {
        return !(block instanceof EntityBlock)
                && Arrays.asList(BuiltInRegistries.BLOCK.getKey(block).getPath().split("_")).contains("chiseled");
    }

    private static boolean isSource(Block block, Block target) {
        if (block == target || block instanceof EntityBlock) return false;
        BlockState state = block.defaultBlockState();
        return Block.isShapeFullBlock(state.getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO));
    }

    private static Map<String, String> loadExtras() {
        try (InputStream in = ChiseledVariants.class.getResourceAsStream("/spicedcider/chisel_extras.json")) {
            return new Gson().fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), new TypeToken<Map<String, String>>() {
            }.getType());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to read chisel extras", e);
        }
    }
}
