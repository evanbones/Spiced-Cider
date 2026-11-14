package com.evandev.spicedcider.wind;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.networking.WindPayload;
import com.evandev.spicedcider.weather.CloudCover;
import com.evandev.spicedcider.weather.CloudCoverData;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.ServerLevelData;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class WindData extends SavedData {
    public static final String DATA_NAME = "spicedcider_wind";
    private static final int SYNC_INTERVAL = 40;
    private static final int FRONT_LEAD_TICKS = 2400;
    private static final float FRONT_STRENGTH = 0.3F;
    private static final float RAIN_STRENGTH = 0.3F;
    private static final float THUNDER_STRENGTH = 0.45F;

    private float heading;
    private float headingTarget;
    private int headingHold;
    private float base = 0.2F;
    private float baseTarget = 0.2F;
    private int baseHold;
    private Mood mood = Mood.NORMAL;
    private long day = Long.MIN_VALUE;
    private float front;
    private float overcast;
    private int gustAge;
    private int gustLength;
    private float gustPeak;
    private int gustWait = 400;
    private int gustChain;
    private float forcedStrength = -1.0F;
    private int forcedTicks;
    private float steady;

    public static WindData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(WindData::new, WindData::load, null), DATA_NAME);
    }

    public WindSample sample() {
        float gust = WindSample.gustEnvelope(gustAge, gustLength) * gustPeak;
        return new WindSample(heading, Math.min(WindSample.MAX_STRENGTH, steady + gust), gust);
    }

    public Mood mood() {
        return mood;
    }

    public void tick(ServerLevel level) {
        Profile profile = Profile.of(level);
        if (profile == Profile.STILL) {
            if (steady != 0.0F || gustLength != 0) {
                steady = 0.0F;
                gustLength = 0;
                sync(level);
            }
            return;
        }

        RandomSource random = level.random;
        long today = level.getDayTime() / 24000L;
        if (today != day) {
            day = today;
            mood = Mood.roll(random);
            baseHold = 0;
        }
        if (--baseHold <= 0) {
            baseTarget = mood.sample(random);
            baseHold = 600 + random.nextInt(1800);
        }
        base += Mth.clamp(baseTarget - base, -0.0008F, 0.0008F);

        float rain = profile.weather ? level.getRainLevel(1.0F) : 0.0F;
        float thunder = profile.weather ? level.getThunderLevel(1.0F) : 0.0F;
        float frontTarget = profile.weather ? frontTarget(level) : 0.0F;
        front += Mth.clamp(frontTarget - front, -0.0005F, 0.0005F);
        float overcastTarget = profile.weather ? overcastTarget(level) : 0.0F;
        overcast += Mth.clamp(overcastTarget - overcast, -0.0005F, 0.0005F);

        if (--headingHold <= 0) {
            float swing = random.nextFloat() < 0.12F
                    ? (random.nextFloat() * 2.0F - 1.0F) * Mth.PI
                    : (float) random.nextGaussian() * 0.6F;
            heading = Mth.wrapDegrees(heading * Mth.RAD_TO_DEG) * Mth.DEG_TO_RAD;
            headingTarget = heading + swing;
            headingHold = (int) ((1200 + random.nextInt(4800)) / (1.0F + 2.0F * thunder));
        }
        float turn = 0.0009F * (1.0F + rain + 3.0F * thunder);
        heading += Mth.clamp(headingTarget - heading, -turn, turn);

        float natural = Math.max(0.0F, base + RAIN_STRENGTH * rain + THUNDER_STRENGTH * thunder + front + overcast) * profile.strength;
        if (forcedStrength >= 0.0F) {
            steady = forcedStrength;
            if (forcedTicks > 0 && --forcedTicks == 0) forcedStrength = -1.0F;
        } else {
            steady = natural;
        }

        boolean gustStarted = false;
        if (gustLength > 0) {
            if (++gustAge >= gustLength) {
                gustLength = 0;
                gustAge = 0;
                gustWait = gustChain > 0
                        ? 20 + random.nextInt(60)
                        : (int) ((300 + random.nextInt(1500)) / ((0.6F + steady) * profile.gustRate));
            }
        } else if (--gustWait <= 0) {
            startGust(random, (0.12F + 0.35F * random.nextFloat()) * (0.4F + steady) * profile.gustStrength);
            gustStarted = true;
        }

        setDirty();
        if (gustStarted || level.getGameTime() % SYNC_INTERVAL == 0) sync(level);
    }

    public void force(ServerLevel level, float strength, int ticks) {
        forcedStrength = strength;
        forcedTicks = ticks;
        steady = strength;
        setDirty();
        sync(level);
    }

    public void turn(ServerLevel level, float degrees) {
        heading = degrees * Mth.DEG_TO_RAD;
        headingTarget = heading;
        headingHold = 2400;
        setDirty();
        sync(level);
    }

    public void gust(ServerLevel level, float peak) {
        gustChain = 0;
        startGust(level.random, peak);
        setDirty();
        sync(level);
    }

    public void release(ServerLevel level) {
        forcedStrength = -1.0F;
        forcedTicks = 0;
        headingHold = 0;
        setDirty();
        sync(level);
    }

    public WindPayload payload() {
        return new WindPayload(heading, steady, gustPeak, gustAge, gustLength);
    }

    private void sync(ServerLevel level) {
        PacketDistributor.sendToPlayersInDimension(level, payload());
    }

    private void startGust(RandomSource random, float peak) {
        gustAge = 0;
        gustLength = 60 + random.nextInt(160);
        gustPeak = peak;
        if (gustChain > 0) gustChain--;
        else if (random.nextFloat() < 0.2F) gustChain = 1 + random.nextInt(3);
    }

    private static float frontTarget(ServerLevel level) {
        if (level.isRaining() || !level.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE)) return 0.0F;
        ServerLevelData data = (ServerLevelData) level.getLevelData();
        if (data.getClearWeatherTime() > 0) return 0.0F;
        int untilRain = data.getRainTime();
        if (untilRain <= 0 || untilRain > FRONT_LEAD_TICKS) return 0.0F;
        return FRONT_STRENGTH * (1.0F - (float) untilRain / FRONT_LEAD_TICKS);
    }

    private static float overcastTarget(ServerLevel level) {
        if (level.dimension() != Level.OVERWORLD || !SpicedCiderConfig.feature("cloudCover")) return 0.0F;
        CloudCover cover = CloudCoverData.get(level).cover();
        return switch (cover) {
            case CLEAR -> -0.06F;
            case MOSTLY_CLEAR -> -0.03F;
            case PARTLY_CLOUDY -> 0.0F;
            case CLOUDY -> 0.12F;
        };
    }

    private static WindData load(CompoundTag tag, HolderLookup.Provider registries) {
        WindData data = new WindData();
        data.heading = tag.getFloat("heading");
        data.headingTarget = tag.getFloat("headingTarget");
        data.headingHold = tag.getInt("headingHold");
        data.base = tag.getFloat("base");
        data.baseTarget = tag.getFloat("baseTarget");
        data.baseHold = tag.getInt("baseHold");
        data.mood = Mood.byId(tag.getInt("mood"));
        data.day = tag.getLong("day");
        data.front = tag.getFloat("front");
        data.overcast = tag.getFloat("overcast");
        data.gustWait = tag.getInt("gustWait");
        data.forcedStrength = tag.contains("forcedStrength") ? tag.getFloat("forcedStrength") : -1.0F;
        data.forcedTicks = tag.getInt("forcedTicks");
        return data;
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        tag.putFloat("heading", heading);
        tag.putFloat("headingTarget", headingTarget);
        tag.putInt("headingHold", headingHold);
        tag.putFloat("base", base);
        tag.putFloat("baseTarget", baseTarget);
        tag.putInt("baseHold", baseHold);
        tag.putInt("mood", mood.ordinal());
        tag.putLong("day", day);
        tag.putFloat("front", front);
        tag.putFloat("overcast", overcast);
        tag.putInt("gustWait", gustWait);
        tag.putFloat("forcedStrength", forcedStrength);
        tag.putInt("forcedTicks", forcedTicks);
        return tag;
    }

    public enum Mood {
        CALM(10, 0.02F, 0.12F),
        NORMAL(45, 0.08F, 0.4F),
        BREEZY(25, 0.3F, 0.6F),
        WINDY(20, 0.45F, 0.8F);

        private final int weight;
        private final float min;
        private final float max;

        Mood(int weight, float min, float max) {
            this.weight = weight;
            this.min = min;
            this.max = max;
        }

        static Mood roll(RandomSource random) {
            int total = 0;
            for (Mood mood : values()) total += mood.weight;
            int pick = random.nextInt(total);
            for (Mood mood : values()) {
                pick -= mood.weight;
                if (pick < 0) return mood;
            }
            return NORMAL;
        }

        static Mood byId(int id) {
            return id >= 0 && id < values().length ? values()[id] : NORMAL;
        }

        float sample(RandomSource random) {
            return Mth.lerp(random.nextFloat(), min, max);
        }
    }

    public enum Profile {
        OPEN(true, 1.0F, 1.0F, 1.0F),
        ENCLOSED(false, 0.5F, 1.4F, 1.6F),
        STILL(false, 0.0F, 0.0F, 0.0F);

        private final boolean weather;
        private final float strength;
        private final float gustStrength;
        private final float gustRate;

        Profile(boolean weather, float strength, float gustStrength, float gustRate) {
            this.weather = weather;
            this.strength = strength;
            this.gustStrength = gustStrength;
            this.gustRate = gustRate;
        }

        public static Profile of(Level level) {
            if (level.dimension() == Level.END) return STILL;
            return level.dimensionType().hasCeiling() ? ENCLOSED : OPEN;
        }
    }
}
