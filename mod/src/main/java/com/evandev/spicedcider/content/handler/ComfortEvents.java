package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.registry.ModMobEffects;
import com.evandev.spicedcider.registry.ModTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class ComfortEvents {
    private static final int IN_BED_TICKS = 100;
    private static final int REFRESH_BELOW_TICKS = 60;

    @SubscribeEvent
    public static void onFinishEating(LivingEntityUseItemEvent.Finish event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) return;
        ItemStack stack = event.getItem();
        if (!stack.is(ModTags.Items.COMFORT_FOODS) || stack.is(ModTags.Items.NOT_COMFORT_FOODS)) return;
        int perNutrition = SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.comfortSoupSecondsPerNutrition, 0);
        if (perNutrition <= 0) return;
        FoodProperties food = stack.getFoodProperties(entity);
        if (food == null || food.nutrition() <= 0) return;
        int maxSeconds = SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.comfortSoupMaxSeconds, 180);
        grant(entity, Math.min(maxSeconds, food.nutrition() * perNutrition) * 20);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !player.isSleeping() || player.tickCount % 20 != 0) return;
        if (sleepSeconds() <= 0) return;
        MobEffectInstance current = player.getEffect(ModMobEffects.COMFORT);
        if (current != null && current.getDuration() >= REFRESH_BELOW_TICKS) return;
        player.addEffect(new MobEffectInstance(ModMobEffects.COMFORT, IN_BED_TICKS, 0, true, false, true));
    }

    @SubscribeEvent
    public static void onWakeUp(PlayerWakeUpEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (event.wakeImmediately() || event.updateLevel() || !player.isSleepingLongEnough()) return;
        int seconds = sleepSeconds();
        if (seconds > 0) grant(player, seconds * 20);
    }

    private static int sleepSeconds() {
        return SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.comfortSleepSeconds, 0);
    }

    private static void grant(LivingEntity entity, int ticks) {
        if (ticks <= 0) return;
        entity.addEffect(new MobEffectInstance(ModMobEffects.COMFORT, ticks, 0, false, false, true));
    }
}
