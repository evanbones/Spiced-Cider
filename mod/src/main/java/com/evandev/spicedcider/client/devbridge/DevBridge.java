package com.evandev.spicedcider.client.devbridge;

import com.evandev.spicedcider.SpicedCider;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.NativeImage;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import jdk.jfr.Configuration;
import jdk.jfr.Recording;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.screens.GenericMessageScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.sound.PlaySoundSourceEvent;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.event.GameShuttingDownEvent;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.io.OutputStream;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, value = Dist.CLIENT)
public final class DevBridge {
    private static final List<JsonObject> SOUNDS = new ArrayList<>();
    private static final String PROPERTY = "spicedcider.devbridge";
    private static final int DEFAULT_PORT = 25599;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss");
    private static final FrameStats FRAMES = new FrameStats();
    private static final GameplayDriver DRIVER = new GameplayDriver();
    private static final Aim AIM = new Aim();

    private static HttpServer server;
    private static ExecutorService executor;
    private static Recording recording;

    private DevBridge() {
    }

    private record Request(String method, String path, Map<String, String> query, String body) {
        String get(String key, String fallback) {
            return query.getOrDefault(key, fallback);
        }

        int getInt(String key, int fallback) {
            String value = query.get(key);
            return value == null ? fallback : Integer.parseInt(value);
        }
    }

    @FunctionalInterface
    private interface Route {
        JsonElement handle(Request request) throws Exception;
    }

    private static final class HttpError extends Exception {
        final int status;

        HttpError(int status, String message) {
            super(message);
            this.status = status;
        }
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        String value = System.getProperty(PROPERTY);
        if (value == null) return;
        int port = value.isBlank() || value.equalsIgnoreCase("true") ? DEFAULT_PORT : Integer.parseInt(value.trim());
        try {
            start(port);
        } catch (IOException e) {
            SpicedCider.LOGGER.error("Dev bridge failed to bind 127.0.0.1:{}", port, e);
            return;
        }
        event.enqueueWork(() -> Minecraft.getInstance().options.pauseOnLostFocus = false);
    }

    @SubscribeEvent
    public static void onFrame(RenderFrameEvent.Pre event) {
        if (server != null) FRAMES.onFrameStart(Minecraft.getInstance().screen != null);
    }

    @SubscribeEvent
    public static void onTickPre(ClientTickEvent.Pre event) {
        if (server != null) {
            DRIVER.onClientTick();
            AIM.onClientTick();
        }
    }

    @SubscribeEvent
    public static void onTick(ClientTickEvent.Post event) {
        if (server != null) FRAMES.onClientTick();
    }

    @SubscribeEvent
    public static void onSound(PlaySoundSourceEvent event) {
        if (server == null) return;
        SoundInstance sound = event.getSound();
        if (sound == null || sound.getSound() == null) return;
        JsonObject entry = new JsonObject();
        entry.addProperty("sound", sound.getLocation().toString());
        entry.addProperty("source", sound.getSource().getName());
        entry.addProperty("pitch", sound.getPitch());
        entry.addProperty("volume", sound.getVolume());
        entry.addProperty("x", sound.getX());
        entry.addProperty("y", sound.getY());
        entry.addProperty("z", sound.getZ());
        entry.addProperty("epochMs", System.currentTimeMillis());
        synchronized (SOUNDS) {
            SOUNDS.add(entry);
            if (SOUNDS.size() > 200) SOUNDS.remove(0);
        }
    }

    @SubscribeEvent
    public static void onShutdown(GameShuttingDownEvent event) {
        if (server == null) return;
        server.stop(0);
        executor.shutdownNow();
        server = null;
    }

    private static void start(int port) throws IOException {
        Map<String, Route> routes = new HashMap<>();
        routes.put("GET /status", r -> onClient(DevBridge::status));
        routes.put("GET /stats", DevBridge::stats);
        routes.put("POST /stats/reset", r -> {
            FRAMES.reset();
            return ok();
        });
        routes.put("POST /command", DevBridge::command);
        routes.put("POST /screenshot", DevBridge::screenshot);
        routes.put("POST /wait", DevBridge::await);
        routes.put("POST /jfr/start", DevBridge::jfrStart);
        routes.put("POST /jfr/stop", DevBridge::jfrStop);
        routes.put("POST /drive", DevBridge::drive);
        routes.put("POST /drive/stop", r -> {
            DRIVER.stop();
            return ok();
        });
        routes.put("GET /drive", r -> DRIVER.describe());
        routes.put("POST /lookat", r -> onClient(() -> AIM.lookAt(Aim.parse(r.query()), r.query().containsKey("lock"))));
        routes.put("POST /lookat/clear", r -> {
            AIM.clear();
            return ok();
        });
        routes.put("POST /interact", DevBridge::interact);
        routes.put("POST /camera", r -> onClient(() -> {
            String mode = r.query().getOrDefault("mode", "first");
            CameraType type = switch (mode) {
                case "back" -> CameraType.THIRD_PERSON_BACK;
                case "front" -> CameraType.THIRD_PERSON_FRONT;
                default -> CameraType.FIRST_PERSON;
            };
            Minecraft.getInstance().options.setCameraType(type);
            return ok();
        }));
        routes.put("GET /items", r -> onClient(DevBridge::items));
        routes.put("GET /itemdata", r -> onClient(DevBridge::itemData));
        routes.put("GET /tabs", r -> onClient(DevBridge::tabContents));
        routes.put("GET /tabcheck", r -> onClient(DevBridge::tabCheck));
        routes.put("GET /emi", r -> DevBridge.emiIndex());
        routes.put("GET /recipes",r -> onClient(() -> recipes(r.query().get("type"))));
        routes.put("GET /tooltip", r -> onClient(() -> tooltip(r.query().get("advanced") != null)));
        routes.put("GET /sounds", r -> {
            JsonArray out = new JsonArray();
            synchronized (SOUNDS) {
                SOUNDS.forEach(out::add);
            }
            JsonObject result = new JsonObject();
            result.add("sounds", out);
            return result;
        });
        routes.put("POST /sounds/reset", r -> {
            synchronized (SOUNDS) {
                SOUNDS.clear();
            }
            return ok();
        });
        routes.put("GET /blocktexture", r -> onClient(() -> blockTexture(r.query().get("id"))));
        routes.put("POST /join", DevBridge::join);
        routes.put("POST /leave", r -> leave());
        routes.put("POST /quit", r -> {
            Minecraft mc = Minecraft.getInstance();
            CompletableFuture.delayedExecutor(250, TimeUnit.MILLISECONDS).execute(() -> mc.execute(mc::stop));
            return ok();
        });

        executor = Executors.newCachedThreadPool(runnable -> {
            Thread thread = new Thread(runnable, "Spiced Cider Dev Bridge");
            thread.setDaemon(true);
            return thread;
        });
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
        server.setExecutor(executor);
        server.createContext("/", exchange -> dispatch(routes, exchange));
        server.start();
        SpicedCider.LOGGER.info("Dev bridge listening on http://127.0.0.1:{}", port);
    }

    private static void dispatch(Map<String, Route> routes, HttpExchange exchange) throws IOException {
        int status = 200;
        JsonElement body;
        try {
            Request request = new Request(
                    exchange.getRequestMethod(),
                    exchange.getRequestURI().getPath(),
                    parseQuery(exchange.getRequestURI().getRawQuery()),
                    new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8)
            );
            Route route = routes.get(request.method() + " " + request.path());
            if (route == null) throw new HttpError(404, "No route for " + request.method() + " " + request.path());
            body = route.handle(request);
        } catch (HttpError e) {
            status = e.status;
            body = error(e.getMessage());
        } catch (Exception e) {
            status = 500;
            body = error(e.toString());
            SpicedCider.LOGGER.warn("Dev bridge request failed", e);
        }
        byte[] bytes = GSON.toJson(body).getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }

    private static Map<String, String> parseQuery(String raw) {
        Map<String, String> query = new HashMap<>();
        if (raw == null || raw.isEmpty()) return query;
        for (String pair : raw.split("&")) {
            int eq = pair.indexOf('=');
            String key = URLDecoder.decode(eq < 0 ? pair : pair.substring(0, eq), StandardCharsets.UTF_8);
            String value = eq < 0 ? "" : URLDecoder.decode(pair.substring(eq + 1), StandardCharsets.UTF_8);
            query.put(key, value);
        }
        return query;
    }

    private static <T> T onClient(Supplier<T> task) throws Exception {
        return Minecraft.getInstance().submit(task).get(60, TimeUnit.SECONDS);
    }

    private static JsonObject interact(Request request) throws Exception {
        Map<String, String> query = request.query();
        boolean sneak = query.containsKey("sneak");
        String action = query.getOrDefault("action", "use");
        InteractionHand hand = "off".equals(query.get("hand")) ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        boolean air = !query.containsKey("block") && !query.containsKey("entity") && !query.containsKey("pos");
        if (sneak) {
            onClient(() -> {
                Minecraft.getInstance().options.keyShift.setDown(true);
                return null;
            });
            Thread.sleep(200);
        }
        try {
            String result = onClient(() -> Aim.interact(air ? null : Aim.parse(query), action, hand));
            JsonObject out = ok();
            out.addProperty("result", result);
            return out;
        } finally {
            if (sneak) {
                Thread.sleep(150);
                onClient(() -> {
                    Minecraft.getInstance().options.keyShift.setDown(false);
                    return null;
                });
            }
        }
    }

    private static JsonObject ok() {
        JsonObject out = new JsonObject();
        out.addProperty("ok", true);
        return out;
    }

    private static JsonObject error(String message) {
        JsonObject out = new JsonObject();
        out.addProperty("ok", false);
        out.addProperty("error", message);
        return out;
    }

    private static boolean inWorld() {
        Minecraft mc = Minecraft.getInstance();
        return mc.level != null && mc.player != null && mc.screen == null && mc.getOverlay() == null;
    }

    private static JsonObject items() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) throw new IllegalStateException("Not in a world");
        boolean permissions = mc.player.canUseGameMasterBlocks() && mc.options.operatorItemsTab().get();
        Map<Item, List<String>> cachedTabs = tabMembership();
        CreativeModeTabs.tryRebuildTabContents(mc.level.enabledFeatures(), !permissions, mc.level.registryAccess());
        CreativeModeTabs.tryRebuildTabContents(mc.level.enabledFeatures(), permissions, mc.level.registryAccess());
        Map<Item, List<String>> tabs = tabMembership();
        java.util.Set<Item> searchable = new java.util.HashSet<>();
        for (ItemStack stack : CreativeModeTabs.searchTab().getDisplayItems()) searchable.add(stack.getItem());
        Map<?, ?> shapeParents = Map.of();
        try {
            shapeParents = (Map<?, ?>) Class.forName("dev.tazer.clutternomore.shape.ShapeMap").getMethod("getParentByShape").invoke(null);
        } catch (ReflectiveOperationException | LinkageError ignored) {
        }
        JsonArray out = new JsonArray();
        for (Item item : BuiltInRegistries.ITEM) {
            JsonObject entry = new JsonObject();
            entry.addProperty("id", BuiltInRegistries.ITEM.getKey(item).toString());
            ItemStack stack = new ItemStack(item);
            entry.addProperty("name", stack.getHoverName().getString());
            entry.addProperty("key", stack.getDescriptionId());
            entry.addProperty("search", searchable.contains(item));
            JsonArray tabList = new JsonArray();
            tabs.getOrDefault(item, List.of()).forEach(tabList::add);
            entry.add("tabs", tabList);
            entry.addProperty("inCachedTabs", cachedTabs.containsKey(item));
            if (shapeParents.get(item) instanceof Item parent) {
                entry.addProperty("shapeOf", BuiltInRegistries.ITEM.getKey(parent).toString());
            }
            out.add(entry);
        }
        JsonObject result = new JsonObject();
        result.add("items", out);
        return result;
    }

    private static JsonObject recipes(String type) {
        IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();
        if (server == null) throw new IllegalStateException("Not in a world");
        var ops = server.registryAccess().createSerializationContext(JsonOps.INSTANCE);
        JsonArray list = new JsonArray();
        for (RecipeHolder<?> holder : server.getRecipeManager().getRecipes()) {
            String recipeType = String.valueOf(BuiltInRegistries.RECIPE_TYPE.getKey(holder.value().getType()));
            if (type != null && !type.equals(recipeType)) continue;
            JsonObject entry = new JsonObject();
            entry.addProperty("id", holder.id().toString());
            entry.addProperty("type", recipeType);
            Recipe.CODEC.encodeStart(ops, holder.value()).result().ifPresent(json -> entry.add("recipe", json));
            list.add(entry);
        }
        JsonObject out = new JsonObject();
        out.add("recipes", list);
        return out;
    }

    private static JsonObject tooltip(boolean advanced) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) throw new IllegalStateException("Not in a world");
        ItemStack stack = mc.player.getMainHandItem();
        JsonArray lines = new JsonArray();
        for (Component line : stack.getTooltipLines(net.minecraft.world.item.Item.TooltipContext.of(mc.level), mc.player, advanced ? TooltipFlag.ADVANCED : TooltipFlag.NORMAL)) {
            lines.add(line.getString());
        }
        JsonObject result = new JsonObject();
        result.addProperty("item", BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
        result.add("lines", lines);
        return result;
    }

    private static JsonObject blockTexture(String id) {
        Minecraft mc = Minecraft.getInstance();
        Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(id));
        JsonObject out = new JsonObject();
        JsonObject faces = new JsonObject();
        BakedModel model = mc.getBlockRenderer().getBlockModel(block.defaultBlockState());
        RandomSource random = RandomSource.create(0);
        for (Direction direction : new Direction[]{Direction.UP, Direction.NORTH, Direction.DOWN}) {
            List<BakedQuad> quads = model.getQuads(block.defaultBlockState(), direction, random);
            if (!quads.isEmpty()) faces.addProperty(direction.getName(), quads.get(0).getSprite().contents().name().toString());
        }
        faces.addProperty("particle", model.getParticleIcon().contents().name().toString());
        out.add("sprites", faces);
        JsonObject pngs = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : faces.entrySet()) {
            ResourceLocation sprite = ResourceLocation.parse(entry.getValue().getAsString());
            ResourceLocation file = sprite.withPath(path -> "textures/" + path + ".png");
            mc.getResourceManager().getResource(file).ifPresent(resource -> {
                try (InputStream in = resource.open()) {
                    pngs.addProperty(entry.getKey(), Base64.getEncoder().encodeToString(in.readAllBytes()));
                } catch (IOException ignored) {
                }
            });
        }
        out.add("png", pngs);
        return out;
    }

    private static JsonObject itemData() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) throw new IllegalStateException("Not in a world");
        JsonArray out = new JsonArray();
        for (Item item : BuiltInRegistries.ITEM) {
            JsonObject entry = new JsonObject();
            entry.addProperty("id", BuiltInRegistries.ITEM.getKey(item).toString());
            ItemStack stack = new ItemStack(item);
            entry.addProperty("name", stack.getHoverName().getString());
            entry.add("itemClasses", classChain(item.getClass(), Item.class));
            JsonArray itemTags = new JsonArray();
            item.builtInRegistryHolder().tags().map(tag -> tag.location().toString()).sorted().forEach(itemTags::add);
            entry.add("itemTags", itemTags);
            if (item instanceof BlockItem blockItem) {
                Block block = blockItem.getBlock();
                entry.addProperty("block", BuiltInRegistries.BLOCK.getKey(block).toString());
                entry.add("blockClasses", classChain(block.getClass(), Block.class));
                JsonArray blockTags = new JsonArray();
                block.builtInRegistryHolder().tags().map(tag -> tag.location().toString()).sorted().forEach(blockTags::add);
                entry.add("blockTags", blockTags);
                entry.addProperty("soundType", String.valueOf(block.defaultBlockState().getSoundType().getPlaceSound().getLocation()));
                entry.addProperty("destroyTime", block.defaultDestroyTime());
                entry.addProperty("requiresTool", block.defaultBlockState().requiresCorrectToolForDrops());
            }
            entry.addProperty("maxStack", stack.getMaxStackSize());
            entry.addProperty("maxDamage", stack.getMaxDamage());
            FoodProperties food = stack.get(DataComponents.FOOD);
            if (food != null) {
                entry.addProperty("nutrition", food.nutrition());
                entry.addProperty("saturation", food.saturation());
                entry.addProperty("alwaysEdible", food.canAlwaysEat());
                entry.addProperty("useRemainder", food.usingConvertsTo().map(r -> BuiltInRegistries.ITEM.getKey(r.getItem()).toString()).orElse(null));
            }
            entry.addProperty("useAnim", stack.getUseAnimation().name());
            JsonArray components = new JsonArray();
            stack.getComponents().keySet().forEach(type -> components.add(String.valueOf(BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(type))));
            entry.add("components", components);
            out.add(entry);
        }
        JsonObject result = new JsonObject();
        result.add("items", out);
        return result;
    }

    private static JsonArray classChain(Class<?> type, Class<?> stop) {
        JsonArray chain = new JsonArray();
        for (Class<?> c = type; c != null && c != Object.class; c = c.getSuperclass()) {
            chain.add(c.getName());
            if (c == stop) break;
        }
        return chain;
    }

    private static JsonObject tabContents() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) throw new IllegalStateException("Not in a world");
        boolean permissions = mc.player.canUseGameMasterBlocks() && mc.options.operatorItemsTab().get();
        CreativeModeTabs.tryRebuildTabContents(mc.level.enabledFeatures(), !permissions, mc.level.registryAccess());
        CreativeModeTabs.tryRebuildTabContents(mc.level.enabledFeatures(), permissions, mc.level.registryAccess());
        List<CreativeModeTab> visible = CreativeModeTabs.tabs();
        JsonArray out = new JsonArray();
        for (CreativeModeTab tab : BuiltInRegistries.CREATIVE_MODE_TAB) {
            JsonObject entry = new JsonObject();
            entry.addProperty("id", String.valueOf(BuiltInRegistries.CREATIVE_MODE_TAB.getKey(tab)));
            entry.addProperty("name", tab.getDisplayName().getString());
            entry.addProperty("type", tab.getType().name());
            entry.addProperty("visibleIndex", visible.indexOf(tab));
            entry.add("items", stackList(tab.getDisplayItems()));
            out.add(entry);
        }
        JsonObject result = new JsonObject();
        result.add("tabs", out);
        return result;
    }

    private static JsonObject tabCheck() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) throw new IllegalStateException("Not in a world");
        CreativeModeTab.ItemDisplayParameters params = new CreativeModeTab.ItemDisplayParameters(mc.level.enabledFeatures(), false, mc.level.registryAccess());
        JsonObject out = new JsonObject();
        for (CreativeModeTab tab : BuiltInRegistries.CREATIVE_MODE_TAB) {
            String id = String.valueOf(BuiltInRegistries.CREATIVE_MODE_TAB.getKey(tab));
            try {
                tab.buildContents(params);
                out.addProperty(id, "ok " + tab.getDisplayItems().size());
            } catch (Throwable t) {
                Throwable root = t;
                while (root.getCause() != null) root = root.getCause();
                out.addProperty(id, "FAIL " + root);
            }
        }
        return out;
    }

    private static JsonArray stackList(java.util.Collection<ItemStack> stacks) {
        Minecraft mc = Minecraft.getInstance();
        var ops = mc.level.registryAccess().createSerializationContext(JsonOps.INSTANCE);
        JsonArray items = new JsonArray();
        for (ItemStack stack : stacks) {
            String id = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
            if (stack.getComponentsPatch().isEmpty()) {
                items.add(id);
                continue;
            }
            JsonObject entry = new JsonObject();
            entry.addProperty("item", id);
            net.minecraft.core.component.DataComponentPatch.CODEC.encodeStart(ops, stack.getComponentsPatch()).result().ifPresent(json -> entry.add("components", json));
            items.add(entry);
        }
        return items;
    }

    private static JsonObject emiIndex() throws ReflectiveOperationException {
        Class<?> manager = Class.forName("com.evandev.remi.integration.emi.StackManager");
        List<?> stacks = (List<?>) manager.getField("unsearchedStacks").get(null);
        JsonArray out = new JsonArray();
        for (Object stack : stacks) {
            if (stack.getClass().getSimpleName().equals("EmiGroupStack")) {
                Object group = stack.getClass().getField("group").get(stack);
                JsonObject entry = new JsonObject();
                entry.addProperty("group", String.valueOf(group.getClass().getMethod("getId").invoke(group)));
                JsonArray members = new JsonArray();
                for (Object grouped : (List<?>) stack.getClass().getField("itemsNew").get(stack)) {
                    members.add(stackId(grouped.getClass().getField("realStack").get(grouped)));
                }
                entry.add("members", members);
                out.add(entry);
            } else {
                out.add(stackId(stack));
            }
        }
        JsonArray raw = new JsonArray();
        for (Object stack : (List<?>) Class.forName("dev.emi.emi.registry.EmiStackList").getField("filteredStacks").get(null)) {
            raw.add(stackId(stack));
        }
        JsonObject result = new JsonObject();
        result.add("index", out);
        result.add("raw", raw);
        return result;
    }

    private static String stackId(Object emiStack) throws ReflectiveOperationException {
        String id = String.valueOf(emiStack.getClass().getMethod("getId").invoke(emiStack));
        Object patch = emiStack.getClass().getMethod("getComponentChanges").invoke(emiStack);
        return patch instanceof net.minecraft.core.component.DataComponentPatch p && !p.isEmpty() ? id + "+" : id;
    }

    private static Map<Item, List<String>> tabMembership() {
        Map<Item, List<String>> tabs = new HashMap<>();
        for (CreativeModeTab tab : CreativeModeTabs.allTabs()) {
            if (tab.getType() != CreativeModeTab.Type.CATEGORY) continue;
            String tabId = String.valueOf(BuiltInRegistries.CREATIVE_MODE_TAB.getKey(tab));
            for (ItemStack stack : tab.getDisplayItems()) {
                List<String> list = tabs.computeIfAbsent(stack.getItem(), k -> new ArrayList<>());
                if (!list.contains(tabId)) list.add(tabId);
            }
        }
        return tabs;
    }

    private static JsonObject status() {
        Minecraft mc = Minecraft.getInstance();
        JsonObject out = new JsonObject();
        String state;
        if (mc.getOverlay() != null) state = "loading";
        else if (mc.level == null) state = "menu";
        else state = mc.screen == null ? "world" : "world_screen";
        out.addProperty("state", state);
        out.addProperty("screen", mc.screen == null ? null : mc.screen.getClass().getName());
        out.addProperty("fps", mc.getFps());
        out.addProperty("focused", mc.isWindowActive());
        out.addProperty("paused", mc.isPaused());
        out.addProperty("window", mc.getWindow().getWidth() + "x" + mc.getWindow().getHeight());
        IntegratedServer integrated = mc.getSingleplayerServer();
        out.addProperty("world", integrated == null ? null : integrated.getWorldData().getLevelName());
        if (mc.level != null && mc.player != null) {
            out.addProperty("dimension", mc.level.dimension().location().toString());
            JsonArray pos = new JsonArray();
            pos.add(mc.player.getX());
            pos.add(mc.player.getY());
            pos.add(mc.player.getZ());
            out.add("pos", pos);
            out.addProperty("yaw", mc.player.getYRot());
            out.addProperty("pitch", mc.player.getXRot());
            out.addProperty("gameTime", mc.level.getGameTime());
            out.addProperty("dayTime", mc.level.getDayTime());
        }
        return out;
    }

    private static JsonElement stats(Request request) throws Exception {
        JsonObject out = new JsonObject();
        out.add("frames", FRAMES.snapshot());
        out.add("client", onClient(() -> {
            Minecraft mc = Minecraft.getInstance();
            JsonObject client = new JsonObject();
            client.addProperty("fps", mc.getFps());
            client.addProperty("renderDistance", mc.options.getEffectiveRenderDistance());
            if (mc.level != null) {
                client.addProperty("entities", mc.level.getEntityCount());
                client.addProperty("loadedChunks", mc.level.getChunkSource().getLoadedChunksCount());
                client.addProperty("sections", mc.levelRenderer.getSectionStatistics());
                client.addProperty("entityRender", mc.levelRenderer.getEntityStatistics());
            }
            return client;
        }));

        IntegratedServer integrated = Minecraft.getInstance().getSingleplayerServer();
        if (integrated != null) {
            out.add("server", integrated.submit(() -> {
                JsonObject srv = new JsonObject();
                long[] ticks = integrated.getTickTimesNanos();
                long max = 0;
                for (long t : ticks) max = Math.max(max, t);
                srv.addProperty("avgMspt", integrated.getAverageTickTimeNanos() / 1e6);
                srv.addProperty("maxMsptLast100", max / 1e6);
                srv.addProperty("tickCount", integrated.getTickCount());
                JsonObject levels = new JsonObject();
                for (ServerLevel level : integrated.getAllLevels()) {
                    JsonObject l = new JsonObject();
                    int entities = 0;
                    for (Entity ignored : level.getAllEntities()) entities++;
                    l.addProperty("entities", entities);
                    l.addProperty("loadedChunks", level.getChunkSource().getLoadedChunksCount());
                    levels.add(level.dimension().location().toString(), l);
                }
                srv.add("levels", levels);
                return srv;
            }).get(60, TimeUnit.SECONDS));
        }

        JsonObject jvm = new JsonObject();
        Runtime runtime = Runtime.getRuntime();
        jvm.addProperty("heapUsedMb", (runtime.totalMemory() - runtime.freeMemory()) >> 20);
        jvm.addProperty("heapMaxMb", runtime.maxMemory() >> 20);
        JsonObject gcs = new JsonObject();
        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            JsonObject g = new JsonObject();
            g.addProperty("count", gc.getCollectionCount());
            g.addProperty("timeMs", gc.getCollectionTime());
            gcs.add(gc.getName(), g);
        }
        jvm.add("gc", gcs);
        out.add("jvm", jvm);
        return out;
    }

    private static JsonElement command(Request request) throws Exception {
        List<String> commands = new ArrayList<>();
        for (String line : request.body().split("\\R")) {
            String trimmed = line.strip();
            if (trimmed.isEmpty()) continue;
            commands.add(trimmed.startsWith("/") ? trimmed.substring(1) : trimmed);
        }
        if (commands.isEmpty()) throw new HttpError(400, "Send one command per line in the request body");

        Minecraft mc = Minecraft.getInstance();
        if (request.get("side", "server").equals("client")) {
            onClient(() -> {
                if (mc.player == null) return null;
                commands.forEach(mc.player.connection::sendCommand);
                return null;
            });
            if (mc.player == null) throw new IllegalStateException("Not in a world");
            JsonObject out = ok();
            out.addProperty("note", "Client commands report to chat only");
            return out;
        }

        IntegratedServer integrated = mc.getSingleplayerServer();
        if (integrated == null) throw new HttpError(409, "No integrated server running");
        UUID playerId = mc.player == null ? null : mc.player.getUUID();

        JsonArray results = new JsonArray();
        for (String cmd : commands) {
            List<String> output = Collections.synchronizedList(new ArrayList<>());
            CommandSource capture = new CommandSource() {
                @Override
                public void sendSystemMessage(Component component) {
                    output.add(component.getString());
                }

                @Override
                public boolean acceptsSuccess() {
                    return true;
                }

                @Override
                public boolean acceptsFailure() {
                    return true;
                }

                @Override
                public boolean shouldInformAdmins() {
                    return false;
                }
            };
            integrated.submit(() -> {
                ServerPlayer player = playerId == null ? null : integrated.getPlayerList().getPlayer(playerId);
                CommandSourceStack source = (player != null ? player.createCommandSourceStack() : integrated.createCommandSourceStack())
                        .withSource(capture)
                        .withPermission(4);
                integrated.getCommands().performPrefixedCommand(source, cmd);
                return null;
            }).get(60, TimeUnit.SECONDS);
            JsonObject result = new JsonObject();
            result.addProperty("command", cmd);
            JsonArray lines = new JsonArray();
            output.forEach(lines::add);
            result.add("output", lines);
            results.add(result);
        }
        JsonObject out = ok();
        out.add("results", results);
        return out;
    }

    private static JsonElement screenshot(Request request) throws Exception {
        Minecraft mc = Minecraft.getInstance();
        boolean hud = request.get("hud", "0").equals("1");
        String name = request.get("name", STAMP.format(LocalDateTime.now()));
        Path out = FMLPaths.GAMEDIR.get().resolve("devbridge").resolve("screenshots").resolve(name + ".png").toAbsolutePath();
        Files.createDirectories(out.getParent());

        boolean previous = onClient(() -> {
            boolean was = mc.options.hideGui;
            mc.options.hideGui = !hud;
            return was;
        });
        try {
            long target = FRAMES.totalFrames() + 3;
            waitUntil(() -> FRAMES.totalFrames() >= target, 10_000);
            onClient(() -> {
                try (NativeImage image = Screenshot.takeScreenshot(mc.getMainRenderTarget())) {
                    image.writeToFile(out);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return null;
            });
        } finally {
            onClient(() -> {
                mc.options.hideGui = previous;
                return null;
            });
        }
        JsonObject result = ok();
        result.addProperty("path", out.toString());
        return result;
    }

    private static JsonElement await(Request request) throws Exception {
        String condition = request.get("for", "world");
        int n = request.getInt("n", 1);
        long timeoutMs = request.getInt("timeout", 600) * 1000L;
        long started = System.nanoTime();
        boolean reached = switch (condition) {
            case "world" -> waitUntil(() -> call(DevBridge::inWorld), timeoutMs);
            case "menu" -> waitUntil(() -> call(() -> {
                Minecraft mc = Minecraft.getInstance();
                return mc.getOverlay() == null && mc.level == null && mc.screen instanceof TitleScreen;
            }), timeoutMs);
            case "ticks" -> {
                long target = FRAMES.clientTicks() + n;
                yield waitUntil(() -> FRAMES.clientTicks() >= target, timeoutMs);
            }
            case "frames" -> {
                long target = FRAMES.totalFrames() + n;
                yield waitUntil(() -> FRAMES.totalFrames() >= target, timeoutMs);
            }
            case "seconds" -> waitUntil(() -> System.nanoTime() - started >= n * 1_000_000_000L, timeoutMs);
            case "stable" -> waitForStableChunks(n, timeoutMs);
            default -> throw new HttpError(400, "Unknown wait condition " + condition);
        };
        JsonObject out = new JsonObject();
        out.addProperty("ok", reached);
        out.addProperty("condition", condition);
        out.addProperty("waitedSeconds", (System.nanoTime() - started) / 1e9);
        return out;
    }

    private static boolean waitForStableChunks(int seconds, long timeoutMs) throws InterruptedException {
        long deadline = System.currentTimeMillis() + timeoutMs;
        int last = -1;
        long stableSince = System.currentTimeMillis();
        while (System.currentTimeMillis() < deadline) {
            int count = call(() -> {
                Minecraft mc = Minecraft.getInstance();
                return mc.level == null ? -1 : mc.level.getChunkSource().getLoadedChunksCount();
            });
            if (count != last) {
                last = count;
                stableSince = System.currentTimeMillis();
            } else if (count >= 0 && System.currentTimeMillis() - stableSince >= seconds * 1000L) {
                return true;
            }
            Thread.sleep(250);
        }
        return false;
    }

    private static <T> T call(Supplier<T> task) {
        try {
            return onClient(task);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean waitUntil(BooleanSupplier condition, long timeoutMs) throws InterruptedException {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (condition.getAsBoolean()) return true;
            Thread.sleep(100);
        }
        return condition.getAsBoolean();
    }

    private static synchronized JsonElement jfrStart(Request request) throws Exception {
        if (recording != null) throw new HttpError(409, "A recording is already running");
        Configuration configuration = Configuration.getConfiguration(request.get("settings", "profile"));
        recording = new Recording(configuration);
        recording.setName("spicedcider-devbridge");
        recording.setToDisk(true);
        recording.start();
        return ok();
    }

    private static synchronized JsonElement jfrStop(Request request) throws Exception {
        if (recording == null) throw new HttpError(409, "No recording is running");
        String name = request.get("name", STAMP.format(LocalDateTime.now()));
        Path out = FMLPaths.GAMEDIR.get().resolve("devbridge").resolve("jfr").resolve(name + ".jfr").toAbsolutePath();
        Files.createDirectories(out.getParent());
        try {
            recording.stop();
            recording.dump(out);
        } finally {
            recording.close();
            recording = null;
        }
        JsonObject result = ok();
        result.addProperty("path", out.toString());
        return result;
    }

    private static JsonElement drive(Request request) throws Exception {
        if (!call(DevBridge::inWorld)) throw new HttpError(409, "Not in a world, or a screen is open");
        GameplayDriver.Plan plan = new GameplayDriver.Plan(
                flag(request, "forward"), flag(request, "back"), flag(request, "left"), flag(request, "right"),
                flag(request, "sprint"), flag(request, "jump"), flag(request, "sneak"),
                flag(request, "attack"), flag(request, "use"), flag(request, "fly"),
                Float.parseFloat(request.get("yaw", "0")), Float.parseFloat(request.get("pitch", "NaN")),
                request.getInt("swayTicks", 0), Float.parseFloat(request.get("sway", "0")),
                Math.round(Double.parseDouble(request.get("seconds", "10")) * 20));
        DRIVER.start(plan);
        if (flag(request, "wait")) {
            waitUntil(() -> !DRIVER.active(), plan.ticks() * 50L + 60_000L);
        }
        JsonObject out = ok();
        out.add("driver", DRIVER.describe());
        return out;
    }

    private static boolean flag(Request request, String key) {
        String value = request.query().get(key);
        return value != null && !value.equals("0") && !value.equalsIgnoreCase("false");
    }

    private static JsonElement join(Request request) throws Exception {
        String world = request.query().get("world");
        if (world == null) throw new HttpError(400, "Pass ?world=<save folder name>");
        Minecraft mc = Minecraft.getInstance();
        String problem = onClient(() -> {
            if (mc.getOverlay() != null) return "Still loading resources";
            if (mc.level != null) return "Already in a world";
            if (!mc.getLevelSource().levelExists(world)) return "No save folder named " + world;
            mc.createWorldOpenFlows().openWorld(world, () -> mc.setScreen(new TitleScreen()));
            return null;
        });
        if (problem != null) throw new HttpError(409, problem);
        return ok();
    }

    private static JsonElement leave() throws Exception {
        Minecraft mc = Minecraft.getInstance();
        boolean left = onClient(() -> {
            if (mc.level == null) return false;
            boolean local = mc.isLocalServer();
            mc.level.disconnect();
            if (local) mc.disconnect(new GenericMessageScreen(Component.translatable("menu.savingLevel")));
            else mc.disconnect();
            mc.setScreen(new TitleScreen());
            return true;
        });
        if (!left) throw new IllegalStateException("Not in a world");
        return ok();
    }
}
