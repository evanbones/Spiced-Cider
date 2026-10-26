package com.evandev.spicedcider.lodgen;

import com.evandev.spicedcider.mixin.minecraft.ChunkMapLodAccessor;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.PalettedContainerRO;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.ticks.ProtoChunkTicks;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class LodDisk {
    private static final long HEADER_LIFETIME_MS = 10_000L;
    private static final Codec<PalettedContainer<BlockState>> BLOCK_STATES = PalettedContainer.codecRW(
            Block.BLOCK_STATE_REGISTRY, BlockState.CODEC, PalettedContainer.Strategy.SECTION_STATES, Blocks.AIR.defaultBlockState());

    private final ServerLevel level;
    private final Registry<Biome> biomes;
    private final Codec<PalettedContainerRO<Holder<Biome>>> biomeCodec;
    private final Path regions;
    private final Long2ObjectOpenHashMap<Header> headers = new Long2ObjectOpenHashMap<>();

    LodDisk(ServerLevel level, Registry<Biome> biomes) {
        this.level = level;
        this.biomes = biomes;
        this.biomeCodec = PalettedContainer.codecRO(biomes.asHolderIdMap(), biomes.holderByNameCodec(),
                PalettedContainer.Strategy.SECTION_BIOMES, biomes.getHolderOrThrow(Biomes.PLAINS));
        this.regions = DimensionType.getStorageFolder(level.dimension(), level.getServer().getWorldPath(LevelResource.ROOT)).resolve("region");
    }

    boolean exists(int x, int z) {
        long key = ChunkPos.asLong(x >> 5, z >> 5);
        Header header = headers.get(key);
        long now = System.currentTimeMillis();
        if (header == null || now - header.read > HEADER_LIFETIME_MS) {
            header = new Header(readHeader(x >> 5, z >> 5), now);
            headers.put(key, header);
        }
        return header.offsets != null && header.offsets[(x & 31) + (z & 31) * 32] != 0;
    }

    @Nullable
    LodColumn read(int x, int z, LodLight light) {
        ChunkMap chunkMap = level.getChunkSource().chunkMap;
        CompoundTag tag = chunkMap.read(new ChunkPos(x, z)).join().orElse(null);
        if (tag == null) return null;
        tag = ((ChunkMapLodAccessor) chunkMap).spicedcider$upgradeChunkTag(tag);
        if (ChunkStatus.byName(tag.getString("Status")) != ChunkStatus.FULL) return null;

        int count = level.getSectionsCount();
        LevelChunkSection[] sections = new LevelChunkSection[count];
        DataLayer[] blockLight = new DataLayer[count];
        DataLayer[] skyLight = new DataLayer[count];
        boolean lit = tag.getBoolean("isLightOn");
        boolean hasSky = level.dimensionType().hasSkyLight();
        ListTag list = tag.getList("sections", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag section = list.getCompound(i);
            int index = level.getSectionIndexFromSectionY(section.getByte("Y"));
            if (index < 0 || index >= count) continue;
            PalettedContainer<BlockState> states = section.contains("block_states", Tag.TAG_COMPOUND)
                    ? BLOCK_STATES.parse(NbtOps.INSTANCE, section.getCompound("block_states")).result().orElse(null) : null;
            if (states == null) {
                states = new PalettedContainer<>(Block.BLOCK_STATE_REGISTRY, Blocks.AIR.defaultBlockState(), PalettedContainer.Strategy.SECTION_STATES);
            }
            PalettedContainerRO<Holder<Biome>> sectionBiomes = section.contains("biomes", Tag.TAG_COMPOUND)
                    ? biomeCodec.parse(NbtOps.INSTANCE, section.getCompound("biomes")).result().orElse(null) : null;
            if (sectionBiomes == null) {
                sectionBiomes = new PalettedContainer<>(biomes.asHolderIdMap(), biomes.getHolderOrThrow(Biomes.PLAINS), PalettedContainer.Strategy.SECTION_BIOMES);
            }
            sections[index] = new LevelChunkSection(states, sectionBiomes);
            if (lit && section.contains("BlockLight", Tag.TAG_BYTE_ARRAY)) blockLight[index] = new DataLayer(section.getByteArray("BlockLight"));
            if (lit && hasSky && section.contains("SkyLight", Tag.TAG_BYTE_ARRAY)) skyLight[index] = new DataLayer(section.getByteArray("SkyLight"));
        }

        ProtoChunk chunk = new ProtoChunk(new ChunkPos(x, z), UpgradeData.EMPTY, sections, new ProtoChunkTicks<>(), new ProtoChunkTicks<>(), level, biomes, null);
        if (!lit) {
            light.solve(new ProtoChunk[]{chunk}, 1, 1);
            return new LodColumn(x, z, light.sections(chunk, 0, 0));
        }

        int top = chunk.getHighestFilledSectionIndex();
        if (top < 0) return new LodColumn(x, z, List.of());
        int last = Math.min(top + 1, count - 1);
        List<LodColumn.Section> out = new ArrayList<>(last + 1);
        for (int i = 0; i <= last; i++) {
            DataLayer sky = skyLight[i];
            if (sky == null && hasSky && i > top) sky = new DataLayer(15);
            out.add(new LodColumn.Section(level.getSectionYFromSectionIndex(i), chunk.getSection(i), blockLight[i], sky));
        }
        return new LodColumn(x, z, out);
    }

    void clear() {
        headers.clear();
    }

    @Nullable
    private int[] readHeader(int regionX, int regionZ) {
        Path file = regions.resolve("r." + regionX + "." + regionZ + ".mca");
        if (!Files.isRegularFile(file)) return null;
        try (FileChannel channel = FileChannel.open(file, StandardOpenOption.READ)) {
            ByteBuffer buffer = ByteBuffer.allocate(4096);
            while (buffer.hasRemaining() && channel.read(buffer) >= 0) {
            }
            buffer.flip();
            int[] offsets = new int[1024];
            for (int i = 0; i < 1024 && buffer.remaining() >= 4; i++) offsets[i] = buffer.getInt();
            return offsets;
        } catch (NoSuchFileException exception) {
            return null;
        } catch (IOException exception) {
            int[] unknown = new int[1024];
            Arrays.fill(unknown, 1);
            return unknown;
        }
    }

    private record Header(@Nullable int[] offsets, long read) {
    }
}
