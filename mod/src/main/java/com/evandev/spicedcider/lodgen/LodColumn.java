package com.evandev.spicedcider.lodgen;

import io.netty.buffer.Unpooled;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public record LodColumn(int x, int z, List<Section> sections) {
    private static final int BLOCK_LIGHT = 1;
    private static final int SKY_LIGHT = 2;

    public byte[] encode() {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeVarInt(sections.size());
        for (Section section : sections) {
            buf.writeByte(section.y);
            buf.writeByte((section.blockLight != null ? BLOCK_LIGHT : 0) | (section.skyLight != null ? SKY_LIGHT : 0));
            if (section.blockLight != null) buf.writeBytes(section.blockLight.getData());
            if (section.skyLight != null) buf.writeBytes(section.skyLight.getData());
            section.section.write(buf);
        }
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        buf.release();
        return bytes;
    }

    public static LodColumn decode(int x, int z, byte[] bytes, Registry<Biome> biomes) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.wrappedBuffer(bytes));
        int count = buf.readVarInt();
        List<Section> sections = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int y = buf.readByte();
            int flags = buf.readByte();
            DataLayer blockLight = (flags & BLOCK_LIGHT) != 0 ? readLayer(buf) : null;
            DataLayer skyLight = (flags & SKY_LIGHT) != 0 ? readLayer(buf) : null;
            LevelChunkSection section = new LevelChunkSection(biomes);
            section.read(buf);
            sections.add(new Section(y, section, blockLight, skyLight));
        }
        return new LodColumn(x, z, sections);
    }

    private static DataLayer readLayer(FriendlyByteBuf buf) {
        byte[] data = new byte[DataLayer.SIZE];
        buf.readBytes(data);
        return new DataLayer(data);
    }

    public record Section(int y, LevelChunkSection section, @Nullable DataLayer blockLight, @Nullable DataLayer skyLight) {
    }
}
