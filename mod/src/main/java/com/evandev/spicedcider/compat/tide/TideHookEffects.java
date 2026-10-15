package com.evandev.spicedcider.compat.tide;

import com.evandev.spicedcider.registry.ModItems;
import com.evandev.spicedcider.registry.ModMobEffects;
import com.li64.tide.data.fishing.FishData;
import com.li64.tide.data.fishing.mediums.FishingMedium;
import com.li64.tide.data.rods.CustomRodManager;
import com.li64.tide.registries.entities.misc.fishing.TideFishingHook;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.Nullable;

public final class TideHookEffects {
    private static final ResourceKey<LootTable> PERFECT_CATCH_TREASURE = ResourceKey.create(Registries.LOOT_TABLE,
            ResourceLocation.fromNamespaceAndPath("spicedcider", "gameplay/fishing/perfect_catch"));
    private static final int LURE_DURATION = 1200;
    private static final float PERFECT_TREASURE_CHANCE = 0.25f;

    private TideHookEffects() {
    }

    public static boolean uses(@Nullable TideFishingHook hook, @Nullable DeferredItem<Item> item) {
        return hook != null && item != null && hook.getHook().is(item.get());
    }

    public static float minigameArea(@Nullable TideFishingHook hook, float area) {
        if (uses(hook, ModItems.FLUX_HOOK)) return Math.min(1.0f, area * 1.25f);
        return area;
    }

    public static float minigameSpeed(@Nullable TideFishingHook hook, float speed) {
        if (uses(hook, ModItems.LIGHT_HOOK)) return speed * 0.85f;
        if (uses(hook, ModItems.HEAVY_HOOK)) return speed * 0.9f;
        return speed;
    }

    public static float castCharge(ItemStack rod, float charge) {
        return ModItems.LIGHT_HOOK != null && CustomRodManager.getHook(rod).is(ModItems.LIGHT_HOOK.get()) ? charge * 1.35f : charge;
    }

    public static int luck(TideFishingHook hook, int luck) {
        if (uses(hook, ModItems.GOLD_HOOK)) luck += 1;
        if (uses(hook, ModItems.PRISMARITE_HOOK) && hook.getCurrentMedium() == FishingMedium.WATER
                && hook.isOpenWaterFishing() && hook.getBiome().is(BiomeTags.IS_OCEAN)) luck += 2;
        return luck;
    }

    public static double weight(@Nullable TideFishingHook hook, FishData fish, double weight) {
        if (!uses(hook, ModItems.HEAVY_HOOK) || fish.size().isEmpty()) return weight;
        return weight * Mth.clamp(fish.getAverageLength() / 50.0, 0.5, 3.0);
    }

    public static boolean doubleCatch(@Nullable TideFishingHook hook) {
        return uses(hook, ModItems.DOUBLE_HOOK) && hook.getRandom().nextFloat() < 0.1f;
    }

    public static boolean keepsFish(@Nullable TideFishingHook hook) {
        return uses(hook, ModItems.FEATHERLIGHT_HOOK);
    }

    public static boolean keepsBait(@Nullable TideFishingHook hook) {
        return uses(hook, ModItems.NECROMIUM_HOOK);
    }

    public static int lureSpeed(Player player, int lureSpeed) {
        MobEffectInstance lure = player.getEffect(ModMobEffects.LURE);
        return lure == null ? lureSpeed : lureSpeed + 2 * (lure.getAmplifier() + 1);
    }

    public static void rewardPerfectCatch(TideFishingHook hook) {
        Player player = hook.getPlayerOwner();
        if (player == null || !(hook.level() instanceof ServerLevel level)) return;

        MobEffectInstance current = player.getEffect(ModMobEffects.LURE);
        int amplifier = current == null ? 0 : Math.min(current.getAmplifier() + 1, 2);
        player.addEffect(new MobEffectInstance(ModMobEffects.LURE, LURE_DURATION, amplifier));
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.NOTE_BLOCK_CHIME.value(), SoundSource.PLAYERS,
                0.8f, (float) Math.pow(2.0, (amplifier * 4) / 12.0));

        if (level.random.nextFloat() >= PERFECT_TREASURE_CHANCE) return;
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.NOTE_BLOCK_BELL.value(), SoundSource.PLAYERS, 0.8f, 1.5f);
        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, hook.position())
                .withParameter(LootContextParams.TOOL, hook.getRod())
                .withParameter(LootContextParams.THIS_ENTITY, hook)
                .withLuck(player.getLuck())
                .create(LootContextParamSets.FISHING);
        for (ItemStack stack : level.getServer().reloadableRegistries().getLootTable(PERFECT_CATCH_TREASURE).getRandomItems(params)) {
            ItemEntity entity = new ItemEntity(level, player.getX(), player.getY() + 0.5, player.getZ(), stack);
            entity.setNoPickUpDelay();
            level.addFreshEntity(entity);
        }
    }

    public static boolean canFishTears(TideFishingHook hook) {
        return uses(hook, ModItems.ADAMANTITE_HOOK)
                || (ModItems.ADAMANTITE_FISHING_ROD != null && hook.getRod().is(ModItems.ADAMANTITE_FISHING_ROD.get()));
    }
}
