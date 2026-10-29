package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.entities.strawgolem.StrawGolem;
import com.evandev.spicedcider.entities.strawgolem.StrawGolemSenses;
import com.evandev.spicedcider.entities.strawgolem.goals.ConsumeGolemGoal;
import com.evandev.spicedcider.registry.ModEntityTypes;
import com.evandev.spicedcider.registry.ModItems;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class StrawGolemEvents {
    private static final Map<UUID, UUID> ORDERING = new HashMap<>();
    private static BlockPattern fullPattern;
    private static BlockPattern basePattern;

    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.STRAW_GOLEM.get(), StrawGolem.createAttributes().build());
    }

    public static void addToCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.insertAfter(new ItemStack(Items.STRAY_SPAWN_EGG), new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "straw_golem_spawn_egg"))), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        } else if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.STRAW_HAT.get());
        }
    }

    public static boolean trySpawn(Level level, BlockPos pos) {
        BlockPattern.BlockPatternMatch match = fullPattern().find(level, pos);
        if (match == null) return false;
        StrawGolem golem = ModEntityTypes.STRAW_GOLEM.get().create(level);
        if (golem == null) return false;

        CarvedPumpkinBlock.clearPatternBlocks(level, match);
        BlockPos spawn = match.getBlock(0, 1, 0).getPos();
        golem.moveTo(spawn.getX() + 0.5, spawn.getY() + 0.05, spawn.getZ() + 0.5, 0.0F, 0.0F);
        level.addFreshEntity(golem);
        for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, golem.getBoundingBox().inflate(5.0))) {
            CriteriaTriggers.SUMMONED_ENTITY.trigger(player, golem);
        }
        CarvedPumpkinBlock.updatePatternBlocks(level, match);
        return true;
    }

    public static boolean toggleOrdering(Player player, StrawGolem golem) {
        if (golem.getUUID().equals(ORDERING.get(player.getUUID()))) {
            ORDERING.remove(player.getUUID());
            return false;
        }
        ORDERING.put(player.getUUID(), golem.getUUID());
        return true;
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if (!(player.level() instanceof ServerLevel level) || event.getHand() != InteractionHand.MAIN_HAND
                || !player.isShiftKeyDown() || !player.getMainHandItem().isEmpty()) return;
        UUID golemId = ORDERING.get(player.getUUID());
        if (golemId == null || !StrawGolemSenses.isStorage(level, event.getPos())) return;

        ORDERING.remove(player.getUUID());
        if (level.getEntity(golemId) instanceof StrawGolem golem && golem.isAlive()) {
            golem.setPriorityPos(event.getPos().immutable());
            player.displayClientMessage(Component.translatable("entity.spicedcider.straw_golem.ordering.complete"), true);
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        } else {
            player.displayClientMessage(Component.translatable("entity.spicedcider.straw_golem.ordering.failure"), true);
        }
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        ORDERING.remove(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        Entity entity = event.getEntity();
        if (entity instanceof Cow || entity instanceof Pig || entity instanceof Sheep) {
            PathfinderMob mob = (PathfinderMob) entity;
            mob.goalSelector.addGoal(5, new ConsumeGolemGoal(mob));
            mob.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(mob, StrawGolem.class, true));
        }
    }

    public static BlockPattern basePattern() {
        if (basePattern == null) {
            basePattern = BlockPatternBuilder.start()
                    .aisle(" ", "#")
                    .where('#', BlockInWorld.hasState(state -> state != null && state.is(Blocks.HAY_BLOCK)))
                    .build();
        }
        return basePattern;
    }

    private static BlockPattern fullPattern() {
        if (fullPattern == null) {
            fullPattern = BlockPatternBuilder.start()
                    .aisle("^", "#")
                    .where('^', BlockInWorld.hasState(state -> state != null && (state.is(Blocks.CARVED_PUMPKIN) || state.is(Blocks.JACK_O_LANTERN))))
                    .where('#', BlockInWorld.hasState(state -> state != null && state.is(Blocks.HAY_BLOCK)))
                    .build();
        }
        return fullPattern;
    }
}
