package com.evandev.spicedcider.weather;

import com.evandev.spicedcider.networking.CloudCoverPayload;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.ServerLevelData;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class CloudCoverData extends SavedData {
    public static final String DATA_NAME = "spicedcider_cloud_cover";
    private static final int RAIN_LEAD_TICKS = 2400;

    private CloudCover cover = CloudCover.PARTLY_CLOUDY;
    private int ticksLeft = -1;
    private boolean commanded;
    private boolean stormy;

    public static CloudCoverData get(ServerLevel overworld) {
        return overworld.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(CloudCoverData::new, CloudCoverData::load, null), DATA_NAME);
    }

    public CloudCover cover() {
        return cover;
    }

    public void tick(ServerLevel level) {
        ServerLevelData data = (ServerLevelData) level.getLevelData();
        boolean cycle = level.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE);
        if (ticksLeft < 0) {
            ticksLeft = cover.sampleDuration(level.random);
        }

        if (data.isRaining() || cycle && !commanded && ticksUntilRain(data) <= RAIN_LEAD_TICKS) {
            stormy = true;
            change(level, CloudCover.CLOUDY, ticksLeft, false);
            return;
        }
        if (stormy) {
            stormy = false;
            commanded = false;
            change(level, CloudCover.PARTLY_CLOUDY, CloudCover.PARTLY_CLOUDY.sampleDuration(level.random), false);
            return;
        }
        if (!cycle) return;

        if (--ticksLeft <= 0) {
            commanded = false;
            CloudCover next = cover.next(level.random);
            change(level, next, next.sampleDuration(level.random), false);
        }
        setDirty();
    }

    public void command(ServerLevel level, CloudCover cover, int duration) {
        commanded = true;
        stormy = false;
        change(level, cover, duration, true);
    }

    public void fastFade() {
        PacketDistributor.sendToAllPlayers(new CloudCoverPayload(cover, true));
    }

    private void change(ServerLevel level, CloudCover next, int duration, boolean fast) {
        ticksLeft = duration;
        setDirty();
        if (next == cover && !fast) return;
        cover = next;
        PacketDistributor.sendToAllPlayers(new CloudCoverPayload(cover, fast));
    }

    private static int ticksUntilRain(ServerLevelData data) {
        if (data.getClearWeatherTime() > 0) return data.getClearWeatherTime() + 1;
        return data.getRainTime() > 0 ? data.getRainTime() : Integer.MAX_VALUE;
    }

    private static CloudCoverData load(CompoundTag tag, HolderLookup.Provider registries) {
        CloudCoverData data = new CloudCoverData();
        data.cover = CloudCover.byId(tag.getInt("cover"));
        data.ticksLeft = tag.getInt("ticksLeft");
        data.commanded = tag.getBoolean("commanded");
        data.stormy = tag.getBoolean("stormy");
        return data;
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        tag.putInt("cover", cover.ordinal());
        tag.putInt("ticksLeft", ticksLeft);
        tag.putBoolean("commanded", commanded);
        tag.putBoolean("stormy", stormy);
        return tag;
    }
}
