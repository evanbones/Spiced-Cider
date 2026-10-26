package com.evandev.spicedcider.content.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxSong;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class PocketJukeboxContents {
    public static final int CAPACITY = 20;
    public static final PocketJukeboxContents EMPTY = new PocketJukeboxContents(List.of(), -1, 0, false, 0, 0L);

    public static final Codec<PocketJukeboxContents> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.CODEC.listOf().optionalFieldOf("discs", List.of()).forGetter(PocketJukeboxContents::discs),
            Codec.INT.optionalFieldOf("current", -1).forGetter(PocketJukeboxContents::current),
            Codec.INT.optionalFieldOf("progress", 0).forGetter(PocketJukeboxContents::progress),
            Codec.BOOL.optionalFieldOf("playing", false).forGetter(PocketJukeboxContents::playing),
            Codec.INT.optionalFieldOf("session", 0).forGetter(PocketJukeboxContents::session),
            Codec.LONG.optionalFieldOf("started", 0L).forGetter(PocketJukeboxContents::started)
    ).apply(instance, PocketJukeboxContents::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PocketJukeboxContents> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()), PocketJukeboxContents::discs,
            ByteBufCodecs.VAR_INT, PocketJukeboxContents::current,
            ByteBufCodecs.VAR_INT, PocketJukeboxContents::progress,
            ByteBufCodecs.BOOL, PocketJukeboxContents::playing,
            ByteBufCodecs.INT, PocketJukeboxContents::session,
            ByteBufCodecs.VAR_LONG, PocketJukeboxContents::started,
            PocketJukeboxContents::new
    );

    private final List<ItemStack> discs;
    private final int current;
    private final int progress;
    private final boolean playing;
    private final int session;
    private final long started;

    public PocketJukeboxContents(List<ItemStack> discs, int current, int progress, boolean playing, int session, long started) {
        this.discs = List.copyOf(discs);
        this.current = current >= 0 && current < this.discs.size() ? current : -1;
        this.progress = Math.max(0, progress);
        this.playing = playing && this.current >= 0;
        this.session = session;
        this.started = started;
    }

    public List<ItemStack> discs() {
        return discs;
    }

    public int current() {
        return current;
    }

    public int progress() {
        return progress;
    }

    public boolean playing() {
        return playing;
    }

    public int session() {
        return session;
    }

    public long started() {
        return started;
    }

    public boolean isEmpty() {
        return discs.isEmpty();
    }

    public int size() {
        return discs.size();
    }

    public ItemStack currentDisc() {
        return current >= 0 ? discs.get(current) : ItemStack.EMPTY;
    }

    public int elapsed(long gameTime) {
        return playing ? progress + (int) Math.max(0L, gameTime - started) : progress;
    }

    public Optional<JukeboxSong> currentSong(HolderLookup.Provider registries) {
        return current >= 0 ? JukeboxSong.fromStack(registries, discs.get(current)).map(holder -> holder.value()) : Optional.empty();
    }

    public static boolean accepts(ItemStack stack) {
        return !stack.isEmpty() && stack.has(DataComponents.JUKEBOX_PLAYABLE);
    }

    public PocketJukeboxContents insert(ItemStack disc) {
        List<ItemStack> list = new ArrayList<>(discs);
        list.addFirst(disc);
        return new PocketJukeboxContents(list, current >= 0 ? current + 1 : -1, progress, playing, session, started);
    }

    public PocketJukeboxContents removeFirst() {
        List<ItemStack> list = new ArrayList<>(discs);
        list.removeFirst();
        if (current == 0) return new PocketJukeboxContents(list, -1, 0, false, session, started);
        return new PocketJukeboxContents(list, current > 0 ? current - 1 : -1, progress, playing, session, started);
    }

    public PocketJukeboxContents start(int disc, int fromProgress, long gameTime, RandomSource random) {
        return new PocketJukeboxContents(discs, disc, fromProgress, true, random.nextInt(), gameTime);
    }

    public PocketJukeboxContents pause(long gameTime) {
        return new PocketJukeboxContents(discs, current, elapsed(gameTime), false, session, started);
    }

    public PocketJukeboxContents stop() {
        return new PocketJukeboxContents(discs, current, 0, false, session, started);
    }

    public int randomDisc(RandomSource random) {
        if (discs.size() <= 1) return discs.isEmpty() ? -1 : 0;
        if (current < 0) return random.nextInt(discs.size());
        int next = random.nextInt(discs.size() - 1);
        return next >= current ? next + 1 : next;
    }

    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof PocketJukeboxContents that
                && current == that.current && progress == that.progress && playing == that.playing
                && session == that.session && started == that.started && ItemStack.listMatches(discs, that.discs);
    }

    @Override
    public int hashCode() {
        int hash = ItemStack.hashStackList(discs);
        hash = 31 * hash + current;
        hash = 31 * hash + progress;
        hash = 31 * hash + Boolean.hashCode(playing);
        hash = 31 * hash + session;
        return 31 * hash + Long.hashCode(started);
    }
}
