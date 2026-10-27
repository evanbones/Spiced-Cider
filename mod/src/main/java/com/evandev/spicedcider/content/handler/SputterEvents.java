package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.entities.sputter.SputterEntity;
import com.evandev.spicedcider.registry.ModEntityTypes;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class SputterEvents {
    private static BlockPattern pattern;

    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.SPUTTER.get(), SputterEntity.createAttributes().build());
    }

    public static void addToCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            Item egg = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "sputter_spawn_egg"));
            event.insertAfter(new ItemStack(Items.SPIDER_SPAWN_EGG), new ItemStack(egg), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }

    @SubscribeEvent
    public static void onPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        BlockPattern.BlockPatternMatch match = pattern().find(level, event.getPos());
        if (match == null) return;
        SputterEntity sputter = ModEntityTypes.SPUTTER.get().create(level);
        if (sputter == null) return;

        CarvedPumpkinBlock.clearPatternBlocks(level, match);
        BlockPos pos = match.getBlock(0, 1, 0).getPos();
        sputter.moveTo(pos.getX() + 0.5, pos.getY() + 0.05, pos.getZ() + 0.5, 0.0F, 0.0F);
        level.addFreshEntity(sputter);
        for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, sputter.getBoundingBox().inflate(5.0))) {
            CriteriaTriggers.SUMMONED_ENTITY.trigger(player, sputter);
        }
        CarvedPumpkinBlock.updatePatternBlocks(level, match);
    }

    private static BlockPattern pattern() {
        if (pattern == null) {
            pattern = BlockPatternBuilder.start()
                    .aisle("^", "#")
                    .where('^', BlockInWorld.hasState(state -> state != null && (state.is(Blocks.CARVED_PUMPKIN) || state.is(Blocks.JACK_O_LANTERN))))
                    .where('#', BlockInWorld.hasState(state -> state != null && state.is(Tags.Blocks.BARRELS)))
                    .build();
        }
        return pattern;
    }
}
