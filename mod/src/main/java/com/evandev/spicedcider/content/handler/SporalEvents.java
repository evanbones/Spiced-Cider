package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.entities.sporal.Sporal;
import com.evandev.spicedcider.registry.ModEntityTypes;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

public class SporalEvents {

    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.SPORAL.get(), Sporal.createAttributes().build());
    }

    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(ModEntityTypes.SPORAL.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    public static void addToCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS && SpicedCiderConfig.feature("sporals")) {
            SpawnEggItem egg = SpawnEggItem.byId(ModEntityTypes.SPORAL.get());
            if (egg != null) {
                event.insertAfter(new ItemStack(Items.SPIDER_SPAWN_EGG), new ItemStack(egg), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            }
        }
    }
}
