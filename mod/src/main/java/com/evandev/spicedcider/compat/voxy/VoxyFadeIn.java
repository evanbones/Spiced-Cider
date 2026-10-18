package com.evandev.spicedcider.compat.voxy;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL45;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, value = Dist.CLIENT)
public final class VoxyFadeIn {
    private static final int SIZE = 1024;
    private static final int MASK = SIZE - 1;
    private static final int BINDING = 13;
    private static final float FADE_SECONDS = 0.75F;
    private static final long SWEEP_INTERVAL_NANOS = 2_000_000_000L;
    private static final int SWEEP_MARGIN_CHUNKS = 32;
    private static final long START_NANOS = System.nanoTime();

    private static final String VERTEX_OUTPUT = "layout(location = 0) out flat uvec4 interData;";
    private static final String VERTEX_ASSIGN = "interData = quad.attributeData;";
    private static final String VERTEX_IDENTITY = "setupQuad(quad, quadData[";
    private static final String FRAGMENT_INPUT = "layout(location = 0) in flat uvec4 interData;";
    private static final String FRAGMENT_MAIN = "void main() {";
    private static final String FRAGMENT_COLOUR = "colour = computeColour(texPos, colour);";

    private static final String VERTEX_FADE = """

            layout(location = 6) out vec2 spicedciderFadePos;""";
    private static final String VERTEX_FADE_ASSIGN = """

                spicedciderFadePos = (quad.basePoint + swizzelDataAxis(quad.axis, vec3(quad.quadSizeAddin * vec2((cornerId >> 1) & 1u, cornerId & 1u) * quad.lodScale, 0.0))).xz;""";
    private static final String FRAGMENT_FADE = """
            layout(location = 6) in vec2 spicedciderFadePos;
            layout(binding = 13) uniform sampler2D spicedciderFadeTimes;
            layout(binding = 13, std140) uniform SpicedciderFade {
                vec4 spicedciderFadeColour;
                vec4 spicedciderFadeParams;
            };

            float spicedciderFade() {
                if (spicedciderFadeParams.z == 0.0) return 1.0;
                ivec2 chunk = baseSectionPos.xz * 2 + ivec2(floor(spicedciderFadePos / 16.0));
                float revealed = texelFetch(spicedciderFadeTimes, chunk & ivec2(1023), 0).r;
                if (revealed == 0.0) return 1.0;
                float t = clamp((spicedciderFadeParams.x - revealed) * spicedciderFadeParams.y, 0.0, 1.0);
                return t * t * t * (t * (t * 6.0 - 15.0) + 10.0);
            }

            """;
    private static final String FRAGMENT_FADE_APPLY = """

                colour.rgb = mix(spicedciderFadeColour.rgb, colour.rgb, spicedciderFade());""";

    private static final ConcurrentLinkedQueue<long[]> PENDING = new ConcurrentLinkedQueue<>();

    private static boolean enabled = true;
    private static volatile boolean resetRequested;
    private static volatile boolean vertexPatched;

    private static int texture;
    private static int uniformBuffer;
    private static FloatBuffer times;
    private static FloatBuffer uniforms;
    private static long[] owners;
    private static final LongOpenHashSet REVEALED = new LongOpenHashSet();
    private static int dirtyMin = Integer.MAX_VALUE;
    private static int dirtyMax = -1;
    private static long lastSweep;

    public static String patchShader(String source) {
        if (source == null) return null;
        if (source.contains(VERTEX_IDENTITY) && once(source, VERTEX_OUTPUT) && once(source, VERTEX_ASSIGN)) {
            vertexPatched = true;
            return source.replace(VERTEX_OUTPUT, VERTEX_OUTPUT + VERTEX_FADE)
                    .replace(VERTEX_ASSIGN, VERTEX_ASSIGN + VERTEX_FADE_ASSIGN);
        }
        if (vertexPatched && once(source, FRAGMENT_INPUT) && once(source, FRAGMENT_MAIN) && once(source, FRAGMENT_COLOUR)) {
            return source.replace(FRAGMENT_MAIN, FRAGMENT_FADE + FRAGMENT_MAIN)
                    .replace(FRAGMENT_COLOUR, FRAGMENT_COLOUR + FRAGMENT_FADE_APPLY);
        }
        return source;
    }

    private static boolean once(String source, String marker) {
        int first = source.indexOf(marker);
        return first >= 0 && source.indexOf(marker, first + marker.length()) < 0;
    }

    public static void collect(long position, long address, long bytes) {
        if (bytes < 8) return;
        int level = (int) ((position >>> 60) & 15);
        int baseX = ((int) ((position << 36) >> 40)) << 5;
        int baseZ = ((int) ((position << 12) >> 40)) << 5;

        boolean[] covered = new boolean[32 * 32];
        long end = address + (bytes & ~7L);
        for (long pointer = address; pointer < end; pointer += 8) {
            long quad = MemoryUtil.memGetLong(pointer);
            if (quad == 0) continue;
            int axis = (int) (quad & 7) >> 1;
            int width = (int) ((quad >>> 3) & 15) + 1;
            int height = (int) ((quad >>> 7) & 15) + 1;
            int x = (int) ((quad >>> 21) & 31);
            int z = (int) ((quad >>> 11) & 31);
            int maxX = Math.min(32, x + (axis == 2 ? 1 : width));
            int maxZ = Math.min(32, z + (axis == 1 ? 1 : height));
            for (int vz = z; vz < maxZ; vz++) {
                for (int vx = x; vx < maxX; vx++) {
                    covered[vz * 32 + vx] = true;
                }
            }
        }

        LongOpenHashSet columns = new LongOpenHashSet();
        for (int vz = 0; vz < 32; vz++) {
            for (int vx = 0; vx < 32; vx++) {
                if (!covered[vz * 32 + vx]) continue;
                int minChunkX = ((baseX + vx) << level) >> 4;
                int maxChunkX = (((baseX + vx + 1) << level) - 1) >> 4;
                int minChunkZ = ((baseZ + vz) << level) >> 4;
                int maxChunkZ = (((baseZ + vz + 1) << level) - 1) >> 4;
                for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                    for (int cx = minChunkX; cx <= maxChunkX; cx++) {
                        columns.add(ChunkPos.asLong(cx, cz));
                    }
                }
            }
        }
        if (!columns.isEmpty()) PENDING.add(columns.toLongArray());
    }

    public static void bind() {
        RenderSystem.assertOnRenderThread();
        if (texture == 0) create();
        enabled = !SpicedCiderConfig.CLIENT_SPEC.isLoaded() || SpicedCiderConfig.CLIENT.voxyFadeIn.get();

        if (resetRequested) {
            resetRequested = false;
            clear();
        }

        float now = now();
        long[] keys;
        while ((keys = PENDING.poll()) != null) {
            for (long key : keys) reveal(key, now);
        }

        long nanos = System.nanoTime();
        if (nanos - lastSweep > SWEEP_INTERVAL_NANOS) {
            lastSweep = nanos;
            sweep();
        }

        upload();

        float[] fog = RenderSystem.getShaderFogColor();
        uniforms.clear();
        uniforms.put(fog[0]).put(fog[1]).put(fog[2]).put(1.0F);
        uniforms.put(now).put(1.0F / FADE_SECONDS).put(enabled ? 1.0F : 0.0F).put(0.0F);
        uniforms.flip();
        GL45.glNamedBufferSubData(uniformBuffer, 0, uniforms);

        GL45.glBindTextureUnit(BINDING, texture);
        GL30.glBindBufferBase(GL31.GL_UNIFORM_BUFFER, BINDING, uniformBuffer);
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (!event.getLevel().isClientSide()) return;
        PENDING.clear();
        resetRequested = true;
    }

    public static boolean isRevealed(int chunkX, int chunkZ) {
        return owners != null && owners[index(chunkX, chunkZ)] == ChunkPos.asLong(chunkX, chunkZ);
    }

    private static float now() {
        return 1.0F + (System.nanoTime() - START_NANOS) / 1.0E9F;
    }

    private static void create() {
        times = MemoryUtil.memCallocFloat(SIZE * SIZE);
        uniforms = MemoryUtil.memAllocFloat(8);
        owners = new long[SIZE * SIZE];
        Arrays.fill(owners, Long.MIN_VALUE);

        texture = GL45.glCreateTextures(GL11.GL_TEXTURE_2D);
        GL45.glTextureStorage2D(texture, 1, GL30.GL_R32F, SIZE, SIZE);
        GL45.glTextureParameteri(texture, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL45.glTextureParameteri(texture, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        uniformBuffer = GL45.glCreateBuffers();
        GL45.glNamedBufferData(uniformBuffer, 32L, GL15.GL_DYNAMIC_DRAW);

        markDirty(0, SIZE - 1);
    }

    private static void clear() {
        MemoryUtil.memSet(MemoryUtil.memAddress(times), 0, (long) SIZE * SIZE * Float.BYTES);
        Arrays.fill(owners, Long.MIN_VALUE);
        REVEALED.clear();
        markDirty(0, SIZE - 1);
    }

    private static int index(int chunkX, int chunkZ) {
        return (chunkZ & MASK) * SIZE + (chunkX & MASK);
    }

    private static void reveal(long key, float now) {
        int x = ChunkPos.getX(key);
        int z = ChunkPos.getZ(key);
        int index = index(x, z);
        long owner = owners[index];
        if (owner == key) return;
        if (owner != Long.MIN_VALUE) REVEALED.remove(owner);
        owners[index] = key;
        times.put(index, now);
        REVEALED.add(key);
        markDirty(z & MASK, z & MASK);
    }

    private static void sweep() {
        float lodDistance = VoxyBridge.lodDistance();
        if (lodDistance <= 0 || REVEALED.isEmpty()) return;
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        int cameraX = camera.getBlockPosition().getX() >> 4;
        int cameraZ = camera.getBlockPosition().getZ() >> 4;
        int limit = Math.min((int) (lodDistance / 16.0F) + SWEEP_MARGIN_CHUNKS, SIZE / 2 - 1);

        LongIterator iterator = REVEALED.iterator();
        while (iterator.hasNext()) {
            long key = iterator.nextLong();
            int x = ChunkPos.getX(key);
            int z = ChunkPos.getZ(key);
            if (Math.abs(x - cameraX) <= limit && Math.abs(z - cameraZ) <= limit) continue;
            int index = index(x, z);
            owners[index] = Long.MIN_VALUE;
            times.put(index, 0.0F);
            iterator.remove();
            markDirty(z & MASK, z & MASK);
        }
    }

    private static void markDirty(int minRow, int maxRow) {
        dirtyMin = Math.min(dirtyMin, minRow);
        dirtyMax = Math.max(dirtyMax, maxRow);
    }

    private static void upload() {
        if (dirtyMax < dirtyMin) return;
        int unpackBuffer = GL11.glGetInteger(GL21.GL_PIXEL_UNPACK_BUFFER_BINDING);
        int alignment = GL11.glGetInteger(GL11.GL_UNPACK_ALIGNMENT);
        int rowLength = GL11.glGetInteger(GL11.GL_UNPACK_ROW_LENGTH);
        int skipRows = GL11.glGetInteger(GL11.GL_UNPACK_SKIP_ROWS);
        int skipPixels = GL11.glGetInteger(GL11.GL_UNPACK_SKIP_PIXELS);
        GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, 0);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 4);
        GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, 0);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_ROWS, 0);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, 0);

        int rows = dirtyMax - dirtyMin + 1;
        GL45.nglTextureSubImage2D(texture, 0, 0, dirtyMin, SIZE, rows, GL11.GL_RED, GL11.GL_FLOAT,
                MemoryUtil.memAddress(times) + (long) dirtyMin * SIZE * Float.BYTES);

        GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, unpackBuffer);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, alignment);
        GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, rowLength);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_ROWS, skipRows);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, skipPixels);
        dirtyMin = Integer.MAX_VALUE;
        dirtyMax = -1;
    }
}
