package com.evandev.spicedcider.weather.client;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.mixin.minecraft.client.BlockColorsAccessor;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, value = Dist.CLIENT)
public final class FrostedLeaves {
    public static final TagKey<Block> WHITENS_IN_SNOWFALL = BlockTags.create(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "whitens_in_snowfall"));

    private static final int STAGES = 6;
    private static final int SAMPLES_PER_SECTION = 24;
    private static final int CHUNKS_PER_TICK = 64;
    private static final int REMESH_INTERVAL = 40;
    private static final int PRUNE_INTERVAL = 600;
    private static final int FROST_RED = 0xF2;
    private static final int FROST_GREEN = 0xF6;
    private static final int FROST_BLUE = 0xFA;
    private static final byte UNKNOWN = 0;
    private static final byte SNOWING = 1;
    private static final byte DRY = 2;

    private static final Predicate<BlockState> WHITENS = state -> state.is(WHITENS_IN_SNOWFALL);
    private static final Map<Long, byte[]> SECTIONS = new ConcurrentHashMap<>();
    private static final LongOpenHashSet DIRTY = new LongOpenHashSet();
    private static final byte[] CELLS = new byte[64];
    private static final BlockPos.MutableBlockPos CELL_POS = new BlockPos.MutableBlockPos();
    private static final RandomSource RANDOM = RandomSource.create();

    private static ClientLevel trackedLevel;
    private static boolean wrapped;
    private static int ticks;

    public static int stageAt(BlockPos pos) {
        if (SECTIONS.isEmpty()) return 0;
        byte[] stages = SECTIONS.get(SectionPos.asLong(pos.getX() >> 4, pos.getY() >> 4, pos.getZ() >> 4));
        return stages == null ? 0 : stages[index(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15)];
    }

    public static int frost(int color, int stage) {
        float amount = Math.min(stage, STAGES) / (float) STAGES;
        int red = blend(color >> 16 & 255, FROST_RED, amount);
        int green = blend(color >> 8 & 255, FROST_GREEN, amount);
        int blue = blend(color & 255, FROST_BLUE, amount);
        return color & 0xFF000000 | red << 16 | green << 8 | blue;
    }

    @SubscribeEvent
    public static void onTagsUpdated(TagsUpdatedEvent event) {
        if (event.getUpdateCause() != TagsUpdatedEvent.UpdateCause.CLIENT_PACKET_RECEIVED) return;
        LeafTints.apply();
        wrapped = false;
        if (enabled()) wrapColors();
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level != trackedLevel) {
            SECTIONS.clear();
            DIRTY.clear();
            trackedLevel = level;
        }
        if (level == null || minecraft.player == null) return;
        if (!enabled()) {
            if (!SECTIONS.isEmpty()) {
                SECTIONS.keySet().forEach(DIRTY::add);
                SECTIONS.clear();
                flush(minecraft);
            }
            return;
        }
        if (!wrapped) wrapColors();
        if (minecraft.isPaused()) return;

        ticks++;
        boolean raining = level.isRaining();
        if (!SECTIONS.isEmpty()) visitFrosted(level, raining);
        if (raining) scanAroundPlayer(minecraft, level);
        if (ticks % REMESH_INTERVAL == 0) flush(minecraft);
        if (ticks % PRUNE_INTERVAL == 0) prune(level);
    }

    private static boolean enabled() {
        return SpicedCiderConfig.clientOr(SpicedCiderConfig.CLIENT.snowfallLeafWhitening, false);
    }

    private static void wrapColors() {
        wrapped = true;
        BlockColors colors = Minecraft.getInstance().getBlockColors();
        Map<Block, BlockColor> providers = ((BlockColorsAccessor) colors).spicedcider$blockColors();
        java.util.List<String> missing = new java.util.ArrayList<>();
        for (Holder<Block> holder : BuiltInRegistries.BLOCK.getTagOrEmpty(WHITENS_IN_SNOWFALL)) {
            Block block = holder.value();
            BlockColor provider = providers.get(block);
            if (provider == null) {
                missing.add(BuiltInRegistries.BLOCK.getKey(block).toString());
            } else if (!(provider instanceof FrostedBlockColor)) {
                colors.register(new FrostedBlockColor(provider), block);
            }
        }
        if (!missing.isEmpty()) {
            SpicedCider.LOGGER.info("Frosted Leaves can't tint these leaves because they have no colour provider: {}", missing);
        }
    }

    private static void visitFrosted(ClientLevel level, boolean raining) {
        for (Map.Entry<Long, byte[]> entry : SECTIONS.entrySet()) {
            long key = entry.getKey();
            LevelChunkSection section = section(level, SectionPos.x(key), SectionPos.y(key), SectionPos.z(key));
            if (section == null) continue;
            visit(level, section, key, entry.getValue(), SAMPLES_PER_SECTION, raining);
        }
    }

    private static void scanAroundPlayer(Minecraft minecraft, ClientLevel level) {
        int radius = Math.min(SpicedCiderConfig.CLIENT.snowfallLeafWhiteningRadius.get(), minecraft.options.getEffectiveRenderDistance());
        int side = radius * 2 + 1;
        int slices = Math.max(1, (side * side + CHUNKS_PER_TICK - 1) / CHUNKS_PER_TICK);
        int phase = ticks % slices;
        int playerX = minecraft.player.getBlockX() >> 4;
        int playerZ = minecraft.player.getBlockZ() >> 4;
        for (int chunkX = playerX - radius; chunkX <= playerX + radius; chunkX++) {
            for (int chunkZ = playerZ - radius; chunkZ <= playerZ + radius; chunkZ++) {
                if (slices > 1 && Math.floorMod(HashCommon.mix(ChunkPos.asLong(chunkX, chunkZ)), slices) != phase) continue;
                LevelChunk chunk = level.getChunkSource().getChunk(chunkX, chunkZ, false);
                if (chunk == null) continue;
                LevelChunkSection[] sections = chunk.getSections();
                for (int i = 0; i < sections.length; i++) {
                    LevelChunkSection section = sections[i];
                    if (section == null || section.hasOnlyAir() || !section.maybeHas(WHITENS)) continue;
                    long key = SectionPos.asLong(chunkX, level.getSectionYFromSectionIndex(i), chunkZ);
                    if (SECTIONS.containsKey(key)) continue;
                    visit(level, section, key, null, SAMPLES_PER_SECTION * slices, true);
                }
            }
        }
    }

    private static void visit(ClientLevel level, LevelChunkSection section, long key, byte[] stages, int samples, boolean raining) {
        Arrays.fill(CELLS, UNKNOWN);
        int baseX = SectionPos.sectionToBlockCoord(SectionPos.x(key));
        int baseY = SectionPos.sectionToBlockCoord(SectionPos.y(key));
        int baseZ = SectionPos.sectionToBlockCoord(SectionPos.z(key));
        for (int n = 0; n < samples; n++) {
            int index = RANDOM.nextInt(4096);
            int x = index & 15;
            int z = index >> 4 & 15;
            int y = index >> 8;
            int stage = stages == null ? 0 : stages[index];
            if (!section.getBlockState(x, y, z).is(WHITENS_IN_SNOWFALL)) {
                if (stage != 0) stages[index] = 0;
                continue;
            }
            boolean snowing = raining && snowingAt(level, baseX, baseY, baseZ, x, y, z);
            int next = snowing ? Math.min(STAGES, stage + 1) : Math.max(0, stage - 1);
            if (next == stage) continue;
            if (stages == null) {
                stages = new byte[4096];
                SECTIONS.put(key, stages);
            }
            stages[index] = (byte) next;
            DIRTY.add(key);
        }
    }

    private static boolean snowingAt(ClientLevel level, int baseX, int baseY, int baseZ, int x, int y, int z) {
        int cell = (y >> 2) << 4 | (z >> 2) << 2 | x >> 2;
        byte cached = CELLS[cell];
        if (cached == UNKNOWN) {
            int blockX = baseX + (x & ~3);
            int blockY = baseY + (y & ~3);
            int blockZ = baseZ + (z & ~3);
            Holder<Biome> biome = level.getNoiseBiome(blockX >> 2, blockY >> 2, blockZ >> 2);
            CELL_POS.set(blockX + 2, blockY + 2, blockZ + 2);
            cached = biome.value().getPrecipitationAt(CELL_POS) == Biome.Precipitation.SNOW ? SNOWING : DRY;
            CELLS[cell] = cached;
        }
        return cached == SNOWING;
    }

    private static LevelChunkSection section(ClientLevel level, int sectionX, int sectionY, int sectionZ) {
        LevelChunk chunk = level.getChunkSource().getChunk(sectionX, sectionZ, false);
        if (chunk == null) return null;
        int index = level.getSectionIndexFromSectionY(sectionY);
        LevelChunkSection[] sections = chunk.getSections();
        return index < 0 || index >= sections.length ? null : sections[index];
    }

    private static void flush(Minecraft minecraft) {
        if (DIRTY.isEmpty()) return;
        int range = minecraft.options.getEffectiveRenderDistance() + 1;
        SectionPos camera = SectionPos.of(minecraft.gameRenderer.getMainCamera().getPosition());
        LongIterator iterator = DIRTY.iterator();
        while (iterator.hasNext()) {
            long key = iterator.nextLong();
            int sectionX = SectionPos.x(key);
            int sectionZ = SectionPos.z(key);
            if (Math.abs(sectionX - camera.x()) > range || Math.abs(sectionZ - camera.z()) > range) continue;
            minecraft.levelRenderer.setSectionDirty(sectionX, SectionPos.y(key), sectionZ);
        }
        DIRTY.clear();
    }

    private static void prune(ClientLevel level) {
        Iterator<Map.Entry<Long, byte[]>> iterator = SECTIONS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, byte[]> entry = iterator.next();
            long key = entry.getKey();
            if (!level.hasChunk(SectionPos.x(key), SectionPos.z(key)) || isClear(entry.getValue())) {
                iterator.remove();
            }
        }
    }

    private static boolean isClear(byte[] stages) {
        for (byte stage : stages) {
            if (stage != 0) return false;
        }
        return true;
    }

    private static int index(int x, int y, int z) {
        return y << 8 | z << 4 | x;
    }

    private static int blend(int from, int to, float amount) {
        return Math.round(from + (to - from) * amount);
    }
}
