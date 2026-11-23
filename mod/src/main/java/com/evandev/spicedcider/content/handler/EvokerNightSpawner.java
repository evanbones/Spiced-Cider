package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class EvokerNightSpawner {
    private static final int INTERVAL = 1200;
    private static final float CHANCE = 0.5F;
    private static final int MAX_NEARBY = 2;
    private static final double NEARBY_RADIUS = 64.0;
    private static final int MIN_DISTANCE = 24;
    private static final int MAX_DISTANCE = 48;
    private static final int ATTEMPTS = 12;

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level) || level.dimension() != Level.OVERWORLD) return;
        if (level.getGameTime() % INTERVAL != 0 || !SpicedCiderConfig.COMMON.darkForestEvokers.get()) return;
        if (level.getDifficulty() == Difficulty.PEACEFUL || !level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING) || !level.isNight()) return;
        RandomSource random = level.random;
        for (ServerPlayer player : level.players()) {
            if (player.isSpectator() || random.nextFloat() >= CHANCE || !onForestSurface(level, player.blockPosition())) continue;
            AABB area = player.getBoundingBox().inflate(NEARBY_RADIUS);
            if (level.getEntitiesOfClass(Evoker.class, area).size() >= MAX_NEARBY) continue;
            trySpawn(level, player, random);
        }
    }

    private static boolean onForestSurface(ServerLevel level, BlockPos pos) {
        return level.getBiome(pos).is(ModTags.Biomes.EVOKER_NIGHT_SPAWNS)
                && pos.getY() >= level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ()) - 4;
    }

    private static void trySpawn(ServerLevel level, ServerPlayer player, RandomSource random) {
        for (int i = 0; i < ATTEMPTS; i++) {
            float angle = random.nextFloat() * Mth.TWO_PI;
            int distance = Mth.nextInt(random, MIN_DISTANCE, MAX_DISTANCE);
            int x = Mth.floor(player.getX() + Mth.cos(angle) * distance);
            int z = Mth.floor(player.getZ() + Mth.sin(angle) * distance);
            if (!level.hasChunkAt(new BlockPos(x, 0, z))) continue;
            BlockPos pos = new BlockPos(x, level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z), z);
            if (!level.getBiome(pos).is(ModTags.Biomes.EVOKER_NIGHT_SPAWNS)) continue;
            if (!SpawnPlacements.isSpawnPositionOk(EntityType.EVOKER, level, pos)) continue;
            if (!Monster.checkMonsterSpawnRules(EntityType.EVOKER, level, MobSpawnType.NATURAL, pos, random)) continue;
            if (!level.noCollision(EntityType.EVOKER.getSpawnAABB(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5))) continue;
            Evoker evoker = EntityType.EVOKER.create(level);
            if (evoker == null) return;
            evoker.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, random.nextFloat() * 360.0F, 0.0F);
            evoker.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.NATURAL, null);
            level.addFreshEntityWithPassengers(evoker);
            return;
        }
    }
}
