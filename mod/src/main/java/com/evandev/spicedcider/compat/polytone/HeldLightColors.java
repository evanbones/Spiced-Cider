package com.evandev.spicedcider.compat.polytone;

import net.mehvahdjukaar.polytone.api.ResolvedPointLight;
import net.mehvahdjukaar.polytone.content.shaders.light.ColoredLightsManager;
import net.mehvahdjukaar.polytone.content.shaders.light.PointLightProvider;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class HeldLightColors {
    private static final RandomSource RANDOM = RandomSource.create(0L);
    private static final boolean LDL = ModList.get().isLoaded("lambdynlights");

    @Nullable
    public static PointLightProvider<Entity> blockColored(ColoredLightsManager manager, ItemStack stack) {
        if (!(stack.getItem() instanceof BlockItem blockItem)) return null;
        BlockState state = blockItem.getBlock().defaultBlockState();
        BlockState ruleState = state;
        List<ColoredLightsManager.BlockRule> rules = manager.getBlockLights(blockItem.getBlock());
        if (rules == null || rules.isEmpty()) {
            ruleState = manager.lightStateOf(state);
            if (ruleState == state) return null;
            rules = manager.getBlockLights(ruleState.getBlock());
            if (rules == null || rules.isEmpty()) return null;
        }
        if (luminance(stack, state, false) <= 0) return null;
        List<ColoredLightsManager.BlockRule> matched = rules;
        BlockState matchState = ruleState;
        return (entity, level) -> {
            ResolvedPointLight source = blockLight(manager, matched, matchState, entity, level);
            if (source == null) return new ResolvedPointLight(0xFFFFFF, 0F, 0F);
            int radius = luminance(stack, state, entity.isUnderWater());
            float brightness = source.brightness() > 0F ? source.brightness() : 1F;
            return new ResolvedPointLight(source.color(), radius, radius > 0 ? brightness : 0F);
        };
    }

    @Nullable
    private static ResolvedPointLight blockLight(ColoredLightsManager manager, List<ColoredLightsManager.BlockRule> rules,
                                                 BlockState state, Entity entity, ClientLevel level) {
        BlockState lightState = manager.lightStateOf(state);
        ResolvedPointLight fallback = null;
        for (ColoredLightsManager.BlockRule rule : rules) {
            if (!rule.matches(state, RANDOM)) continue;
            ResolvedPointLight light = rule.resolve(state, lightState, entity.position(), level);
            if (light == null) continue;
            if (light.brightness() > 0F) return light;
            if (fallback == null) fallback = light;
        }
        return fallback;
    }

    private static int luminance(ItemStack stack, BlockState state, boolean submerged) {
        return LDL ? LdlLuminance.of(stack, submerged) : state.getLightEmission();
    }
}
