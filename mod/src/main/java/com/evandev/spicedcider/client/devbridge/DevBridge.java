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
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.screens.GenericMessageScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.event.GameShuttingDownEvent;

import java.io.IOException;
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
    private static final String PROPERTY = "spicedcider.devbridge";
    private static final int DEFAULT_PORT = 25599;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss");
    private static final FrameStats FRAMES = new FrameStats();

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
    public static void onTick(ClientTickEvent.Post event) {
        if (server != null) FRAMES.onClientTick();
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
            if (mc.player == null) throw new HttpError(409, "Not in a world");
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

    private static JsonElement join(Request request) throws Exception {
        String world = request.query().get("world");
        if (world == null) throw new HttpError(400, "Pass ?world=<save folder name>");
        Minecraft mc = Minecraft.getInstance();
        String problem = onClient(() -> {
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
        if (!left) throw new HttpError(409, "Not in a world");
        return ok();
    }
}
