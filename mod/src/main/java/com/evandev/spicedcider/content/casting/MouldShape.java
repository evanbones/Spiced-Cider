package com.evandev.spicedcider.content.casting;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

import java.util.List;

public enum MouldShape implements StringRepresentable {
    INGOT("ingot", List.of(new Cavity(4, 5, 12, 11))),
    PLATE("plate", List.of(new Cavity(3, 3, 13, 13))),
    VESSEL("vessel", List.of(new Cavity(5, 4, 11, 12), new Cavity(4, 5, 5, 11), new Cavity(11, 5, 12, 11))),
    FRAME("frame", List.of(new Cavity(3, 3, 13, 5), new Cavity(3, 11, 13, 13), new Cavity(3, 5, 5, 11), new Cavity(11, 5, 13, 11))),
    PIPE("pipe", List.of(new Cavity(2, 6, 14, 10)));

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

    public record Cavity(int minX, int minZ, int maxX, int maxZ) {
    }
}
