package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.registry.ModDataMaps;
import com.evandev.spicedcider.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

import java.util.List;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class SpawnControlEvents {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;

        Entity entity = event.getEntity();
        if (entity instanceof Player) return;

        Holder<EntityType<?>> type = entity.getType().builtInRegistryHolder();

        if (isDenied(entity, event.getLevel())) {
            entity.ejectPassengers();
            event.setCanceled(true);
            return;
        }
        Entity vehicle = entity.getVehicle();
        if (vehicle != null && !vehicle.isAddedToLevel() && isDenied(vehicle, event.getLevel())) {
            entity.stopRiding();
        }

        if (!event.loadedFromDisk()) {
            applySpawnEquipment(entity, type);
            if (entity instanceof Cat cat && isNaturalSpawn(cat) && SpicedCiderConfig.feature("persistentWildCats")) {
                cat.setPersistenceRequired();
            }
        }
    }

    @SubscribeEvent
    public static void onPotentialSpawns(LevelEvent.PotentialSpawns event) {
        if (!event.getLevel().getBiome(event.getPos()).is(ModTags.Biomes.RESTRICTED_SPAWNS)) return;

        for (MobSpawnSettings.SpawnerData data : List.copyOf(event.getSpawnerDataList())) {
            if (!data.type.builtInRegistryHolder().is(ModTags.EntityTypes.ALLOWED_IN_RESTRICTED_BIOMES)) {
                event.removeSpawnerData(data);
            }
        }
    }

    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(EntityType.EVOKER, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                SpawnControlEvents::checkVampireSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(EntityType.ALLAY, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                SpawnControlEvents::checkAllaySpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    private static boolean checkAllaySpawnRules(EntityType<Allay> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (spawnType != MobSpawnType.NATURAL && spawnType != MobSpawnType.CHUNK_GENERATION) return true;
        return level.getBlockState(pos.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON) && level.getRawBrightness(pos, 0) > 8;
    }

    private static boolean checkVampireSpawnRules(EntityType<Evoker> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (spawnType == MobSpawnType.NATURAL) {
            if (!level.getLevel().isNight()) return false;
            if (pos.getY() < level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ())) return false;
        }
        return Monster.checkMonsterSpawnRules(type, level, spawnType, pos, random);
    }

    private static boolean isDenied(Entity entity, Level level) {
        Holder<EntityType<?>> type = entity.getType().builtInRegistryHolder();
        return type.is(ModTags.EntityTypes.DENY_SPAWN)
                || (type.is(ModTags.EntityTypes.DENY_NATURAL_SPAWN) && isNaturalSpawn(entity))
                || (isNaturalSpawn(entity) && !type.is(ModTags.EntityTypes.ALLOWED_IN_RESTRICTED_BIOMES)
                && level.getBiome(entity.blockPosition()).is(ModTags.Biomes.RESTRICTED_SPAWNS));
    }

    private static boolean isNaturalSpawn(Entity entity) {
        if (!(entity instanceof Mob mob)) return false;
        MobSpawnType spawnType = mob.getSpawnType();
        return spawnType == MobSpawnType.NATURAL
                || spawnType == MobSpawnType.CHUNK_GENERATION;
    }

    private static void applySpawnEquipment(Entity entity, Holder<EntityType<?>> type) {
        if (!(entity instanceof LivingEntity living)) return;

        ModDataMaps.SpawnEquipment equipment = type.getData(ModDataMaps.SPAWN_EQUIPMENT);
        if (equipment == null || living.getRandom().nextFloat() >= equipment.chance()) return;

        equipment.items().forEach((slot, item) -> living.setItemSlot(slot, new ItemStack(item)));
    }
}
