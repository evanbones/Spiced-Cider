package com.evandev.spicedcider.client.devbridge;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

final class RecipeBench {
    private static final Map<String, String[]> GRIDS = new LinkedHashMap<>();

    static {
        GRIDS.put("planks", new String[]{"minecraft:oak_log", "", "", "", "", "", "", "", ""});
        GRIDS.put("sticks", new String[]{"minecraft:oak_planks", "", "", "minecraft:oak_planks", "", "", "", "", ""});
        GRIDS.put("torch", new String[]{"minecraft:coal", "", "", "minecraft:stick", "", "", "", "", ""});
        GRIDS.put("chest", new String[]{"minecraft:oak_planks", "minecraft:oak_planks", "minecraft:oak_planks", "minecraft:oak_planks", "", "minecraft:oak_planks", "minecraft:oak_planks", "minecraft:oak_planks", "minecraft:oak_planks"});
        GRIDS.put("nomatch", new String[]{"minecraft:dirt", "minecraft:gravel", "", "", "", "", "", "", ""});
    }

    private RecipeBench() {
    }

    static JsonObject run(int rounds, int lookups) throws Exception {
        IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();
        if (server == null) throw new IllegalStateException("Not in a world");
        JsonObject out = new JsonObject();
        out.add("lookups", onServer(server, () -> lookups(server, lookups)));
        out.add("shiftCraft", shiftCraft(server, rounds));
        return out;
    }

    private static JsonObject lookups(IntegratedServer server, int n) {
        ServerLevel level = server.overworld();
        RecipeManager manager = server.getRecipeManager();
        JsonObject out = new JsonObject();
        out.addProperty("manager", manager.getClass().getName());
        JsonObject counts = new JsonObject();
        counts.addProperty("crafting", manager.getAllRecipesFor(RecipeType.CRAFTING).size());
        counts.addProperty("smelting", manager.getAllRecipesFor(RecipeType.SMELTING).size());
        counts.addProperty("all", manager.getRecipes().size());
        out.add("counts", counts);
        JsonObject crafting = new JsonObject();
        for (Map.Entry<String, String[]> grid : GRIDS.entrySet()) {
            List<ItemStack> items = new ArrayList<>();
            for (String id : grid.getValue()) items.add(stack(id, 1));
            CraftingInput input = CraftingInput.of(3, 3, items);
            crafting.add(grid.getKey(), time(n, () -> manager.getRecipeFor(RecipeType.CRAFTING, input, level).map(h -> h.id().toString()).orElse("none")));
        }
        out.add("crafting", crafting);
        JsonObject smelting = new JsonObject();
        for (String id : List.of("minecraft:raw_iron", "minecraft:oak_log", "minecraft:dirt")) {
            SingleRecipeInput input = new SingleRecipeInput(stack(id, 1));
            smelting.add(id, time(n, () -> manager.getRecipeFor(RecipeType.SMELTING, input, level).map(h -> h.id().toString()).orElse("none")));
        }
        out.add("smelting", smelting);
        return out;
    }

    private static JsonObject time(int n, Callable<String> lookup) {
        try {
            String result = null;
            for (int i = 0; i < Math.max(5, n / 10); i++) result = lookup.call();
            long[] samples = new long[n];
            for (int i = 0; i < n; i++) {
                long start = System.nanoTime();
                lookup.call();
                samples[i] = System.nanoTime() - start;
            }
            JsonObject out = new JsonObject();
            out.addProperty("result", result);
            out.add("us", summary(samples));
            return out;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static JsonObject summary(long[] nanos) {
        long[] sorted = nanos.clone();
        Arrays.sort(sorted);
        long sum = 0;
        for (long v : sorted) sum += v;
        JsonObject out = new JsonObject();
        out.addProperty("mean", sum / (double) sorted.length / 1000.0);
        out.addProperty("p50", sorted[sorted.length / 2] / 1000.0);
        out.addProperty("p90", sorted[(int) (sorted.length * 0.9)] / 1000.0);
        out.addProperty("max", sorted[sorted.length - 1] / 1000.0);
        return out;
    }

    private static JsonObject shiftCraft(IntegratedServer server, int rounds) throws Exception {
        JsonObject out = new JsonObject();
        for (Map.Entry<String, String[]> grid : GRIDS.entrySet()) {
            if (grid.getKey().equals("nomatch")) continue;
            long[] samples = new long[rounds];
            int crafted = 0;
            String menuClass = null;
            String result = null;
            for (int round = 0; round < rounds; round++) {
                String[] layout = grid.getValue();
                String[] opened = onServer(server, () -> {
                    ServerPlayer player = server.getPlayerList().getPlayers().getFirst();
                    ServerLevel level = player.serverLevel();
                    BlockPos pos = player.blockPosition().above(2);
                    level.setBlockAndUpdate(pos, Blocks.CRAFTING_TABLE.defaultBlockState());
                    player.getInventory().clearContent();
                    MenuProvider provider = level.getBlockState(pos).getMenuProvider(level, pos);
                    player.openMenu(provider);
                    AbstractContainerMenu menu = player.containerMenu;
                    for (int i = 0; i < 9; i++) {
                        if (!layout[i].isEmpty()) menu.getSlot(i + 1).set(stack(layout[i], 64));
                    }
                    menu.slotsChanged(menu.getSlot(1).container);
                    return new String[]{menu.getClass().getName()};
                });
                menuClass = opened[0];
                Thread.sleep(250);
                Object[] clicked = onServer(server, () -> {
                    ServerPlayer player = server.getPlayerList().getPlayers().getFirst();
                    AbstractContainerMenu menu = player.containerMenu;
                    String preview = BuiltInRegistries.ITEM.getKey(menu.getSlot(0).getItem().getItem()).toString();
                    long start = System.nanoTime();
                    menu.clicked(0, 0, ClickType.QUICK_MOVE, player);
                    long elapsed = System.nanoTime() - start;
                    int count = 0;
                    for (ItemStack s : player.getInventory().items) {
                        if (!s.isEmpty() && BuiltInRegistries.ITEM.getKey(s.getItem()).toString().equals(preview)) count += s.getCount();
                    }
                    player.closeContainer();
                    player.getInventory().clearContent();
                    return new Object[]{elapsed, count, preview};
                });
                samples[round] = (Long) clicked[0];
                crafted = (Integer) clicked[1];
                result = (String) clicked[2];
                Thread.sleep(100);
            }
            JsonObject entry = new JsonObject();
            entry.addProperty("menu", menuClass);
            entry.addProperty("result", result);
            entry.addProperty("crafted", crafted);
            JsonObject ms = summary(samples);
            JsonObject converted = new JsonObject();
            for (String key : ms.keySet()) converted.addProperty(key, ms.get(key).getAsDouble() / 1000.0);
            entry.add("ms", converted);
            JsonArray raw = new JsonArray();
            for (long v : samples) raw.add(v / 1e6);
            entry.add("raw", raw);
            out.add(grid.getKey(), entry);
        }
        return out;
    }

    private static ItemStack stack(String id, int count) {
        if (id.isEmpty()) return ItemStack.EMPTY;
        return new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(id)), count);
    }

    private static <T> T onServer(IntegratedServer server, Callable<T> task) throws Exception {
        return server.submit(() -> {
            try {
                return task.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).get(120, TimeUnit.SECONDS);
    }
}
