package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.registry.ModDataMaps;
import com.evandev.spicedcider.registry.ModTags;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class SpawnControlEvents {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;

        Entity entity = event.getEntity();
        if (entity instanceof Player) return;

        Holder<EntityType<?>> type = entity.getType().builtInRegistryHolder();

        if (type.is(ModTags.EntityTypes.DENY_SPAWN)
                || (type.is(ModTags.EntityTypes.DENY_NATURAL_SPAWN) && !isPlayerPlaced(entity))) {
            event.setCanceled(true);
            return;
        }

        if (!event.loadedFromDisk()) {
            applySpawnEquipment(entity, type);
        }
    }

    private static boolean isPlayerPlaced(Entity entity) {
        if (!(entity instanceof Mob mob)) return false;
        MobSpawnType spawnType = mob.getSpawnType();
        return spawnType == MobSpawnType.MOB_SUMMONED
                || spawnType == MobSpawnType.SPAWN_EGG
                || spawnType == MobSpawnType.COMMAND;
    }

    private static void applySpawnEquipment(Entity entity, Holder<EntityType<?>> type) {
        if (!(entity instanceof LivingEntity living)) return;

        ModDataMaps.SpawnEquipment equipment = type.getData(ModDataMaps.SPAWN_EQUIPMENT);
        if (equipment == null || living.getRandom().nextFloat() >= equipment.chance()) return;

        equipment.items().forEach((slot, item) -> living.setItemSlot(slot, new ItemStack(item)));
    }
}
