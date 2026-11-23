package com.evandev.spicedcider.content.casting;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

import java.util.List;

public enum MouldShape implements StringRepresentable {
    INGOT("ingot", List.of(
            new Cavity(9.5F, 3.5F, 11F, 4.25F),
            new Cavity(7.25F, 4.25F, 11.75F, 5F),
            new Cavity(5F, 5F, 12.5F, 5.75F),
            new Cavity(2.75F, 5.75F, 13.25F, 6.5F),
            new Cavity(2F, 6.5F, 14F, 9.5F),
            new Cavity(2F, 9.5F, 13.25F, 10.25F),
            new Cavity(2.75F, 10.25F, 11.75F, 11F),
            new Cavity(3.5F, 11F, 9.5F, 11.75F),
            new Cavity(4.25F, 11.75F, 7.25F, 12.5F))),
    PLATE("plate", List.of(
            new Cavity(8F, 4.25F, 9.5F, 5F),
            new Cavity(6.5F, 5F, 10.25F, 5.75F),
            new Cavity(5F, 5.75F, 11F, 6.5F),
            new Cavity(3.5F, 6.5F, 11.75F, 7.25F),
            new Cavity(2.75F, 7.25F, 12.5F, 8F),
            new Cavity(2.75F, 8F, 13.25F, 9.5F),
            new Cavity(3.5F, 9.5F, 13.25F, 10.25F),
            new Cavity(4.25F, 10.25F, 12.5F, 11F),
            new Cavity(5F, 11F, 11F, 11.75F),
            new Cavity(5.75F, 11.75F, 9.5F, 12.5F),
            new Cavity(6.5F, 12.5F, 8F, 13.25F)));

    public static final Codec<MouldShape> CODEC = StringRepresentable.fromEnum(MouldShape::values);
    public static final StreamCodec<ByteBuf, MouldShape> STREAM_CODEC = ByteBufCodecs.idMapper(i -> values()[i], MouldShape::ordinal);

    private final String name;
    private final List<Cavity> cavities;

    MouldShape(String name, List<Cavity> cavities) {
        this.name = name;
        this.cavities = cavities;
    }

    public List<Cavity> cavities() {
        return this.cavities;
    }

    public Component displayName() {
        return Component.translatable("spicedcider.mould_shape." + this.name);
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public record Cavity(float minX, float minZ, float maxX, float maxZ) {
    }
}
