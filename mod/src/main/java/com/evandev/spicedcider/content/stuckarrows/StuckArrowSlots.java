package com.evandev.spicedcider.content.stuckarrows;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class StuckArrowSlots {
    public static final int MAX_SLOTS = 16;
    public static final int HIT_FLOATS = 6;

    public record Slot(int index, ItemStack stack, List<Float> hit) {
        public static final Codec<Slot> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("slot").forGetter(Slot::index),
                ItemStack.OPTIONAL_CODEC.optionalFieldOf("stack", ItemStack.EMPTY).forGetter(Slot::stack),
                Codec.FLOAT.listOf().optionalFieldOf("hit", List.of()).forGetter(Slot::hit)
        ).apply(instance, Slot::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Slot> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, Slot::index,
                ItemStack.OPTIONAL_STREAM_CODEC, Slot::stack,
                ByteBufCodecs.FLOAT.apply(ByteBufCodecs.list(HIT_FLOATS)), Slot::hit,
                Slot::new);

        public boolean recoverable() {
            return !stack.isEmpty();
        }

        public boolean hasHit() {
            return hit.size() == HIT_FLOATS;
        }
    }

    public static final Codec<StuckArrowSlots> CODEC = Slot.CODEC.listOf()
            .xmap(StuckArrowSlots::new, slots -> slots.slots);

    private final List<Slot> slots;

    public StuckArrowSlots() {
        this(List.of());
    }

    public StuckArrowSlots(List<Slot> slots) {
        this.slots = new ArrayList<>(slots);
    }

    public List<Slot> slots() {
        return slots;
    }

    public boolean isEmpty() {
        return slots.isEmpty();
    }

    public int size() {
        return slots.size();
    }

    public int renderCount() {
        int max = -1;
        for (Slot slot : slots) max = Math.max(max, slot.index());
        return max + 1;
    }

    @Nullable
    public Slot get(int index) {
        for (Slot slot : slots) {
            if (slot.index() == index) return slot;
        }
        return null;
    }

    public boolean add(ItemStack stack, List<Float> hit) {
        if (slots.size() >= MAX_SLOTS) return false;
        int index = 0;
        while (get(index) != null) index++;
        slots.add(new Slot(index, stack, List.copyOf(hit)));
        return true;
    }

    public boolean remove(int index) {
        return slots.removeIf(slot -> slot.index() == index);
    }

    public boolean removeLoose() {
        Slot loose = null;
        for (Slot slot : slots) {
            if (!slot.recoverable() && (loose == null || slot.index() > loose.index())) loose = slot;
        }
        return loose != null && slots.remove(loose);
    }
}
