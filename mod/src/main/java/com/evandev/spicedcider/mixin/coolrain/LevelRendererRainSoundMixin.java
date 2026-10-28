package com.evandev.spicedcider.mixin.coolrain;

import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Set;

@IfModLoaded("coolrain")
@Mixin(value = LevelRenderer.class, priority = 900)
public class LevelRendererRainSoundMixin {
    @Unique
    private static final Set<String> METAL = Set.of("metal", "iron", "copper", "netherite", "chain", "anvil", "lantern", "gold", "bulb", "grate", "bell");
    @Unique
    private static final Set<String> GLASS = Set.of("glass", "ice");
    @Unique
    private static final Set<String> FABRIC = Set.of("wool", "cloth", "clothwood");
    @Unique
    private static final Set<String> FOLIAGE = Set.of("leaves");

    @Final
    @Shadow
    private Minecraft minecraft;

    @Inject(method = "tickRain", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;playLocalSound(Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V", ordinal = 0), cancellable = true)
    private void spicedcider$rainSoundAbove(Camera camera, CallbackInfo ci, @Local(ordinal = 1) BlockPos pos) {
        if (spicedcider$playRain(pos, true)) ci.cancel();
    }

    @Inject(method = "tickRain", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;playLocalSound(Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V", ordinal = 1), cancellable = true)
    private void spicedcider$rainSound(Camera camera, CallbackInfo ci, @Local(ordinal = 1) BlockPos pos) {
        if (spicedcider$playRain(pos, false)) ci.cancel();
    }

    @Unique
    private boolean spicedcider$playRain(BlockPos pos, boolean muffled) {
        ClientLevel level = minecraft.level;
        if (level == null || pos == null) return false;
        BlockState state = level.getBlockState(pos);
        String category = spicedcider$category(state);
        RandomSource random = level.random;

        if (category == null) {
            level.playLocalSound(pos, SoundEvents.WEATHER_RAIN, SoundSource.WEATHER, muffled ? 0.1F : 0.2F, 1.0F, false);
            return true;
        }
        SoundEvent sound = BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath("coolrain", "rain_sounds_" + category));
        if (sound == null) return false;

        float pitch = category.equals("foliage") || category.equals("lava") ? 1.0F : random.nextIntBetweenInclusive(8, 12) * 0.1F;
        float volume = switch (category) {
            case "fabric" -> 0.75F;
            case "water" -> 0.175F;
            case "amethyst" -> 0.3F;
            case "noteblock", "lava" -> 0.2F;
            default -> muffled ? 0.1F : 0.5F;
        };
        boolean chime = category.equals("amethyst") || category.equals("noteblock");
        if (chime) level.playLocalSound(pos, SoundEvents.WEATHER_RAIN, SoundSource.WEATHER, 0.2F, 1.0F, false);
        if (category.equals("lava")) level.playLocalSound(pos, SoundEvents.WEATHER_RAIN, SoundSource.WEATHER, 0.1F, pitch, false);
        level.playLocalSound(pos, sound, SoundSource.WEATHER, volume, pitch, !chime);
        return true;
    }

    @Unique
    private static String spicedcider$category(BlockState state) {
        if (state.getFluidState().is(FluidTags.LAVA) || state.is(Blocks.MAGMA_BLOCK) || CampfireBlock.isLitCampfire(state)
                || state.is(spicedcider$tag("lava"))) return "lava";
        if (state.is(Blocks.WATER) || state.is(Blocks.BUBBLE_COLUMN) || state.is(spicedcider$tag("water"))) return "water";
        if (state.is(Blocks.NOTE_BLOCK) || state.is(spicedcider$tag("noteblock"))) return "noteblock";
        if (state.is(spicedcider$tag("amethyst"))) return "amethyst";
        if (state.is(spicedcider$tag("metal"))) return "metal";
        if (state.is(spicedcider$tag("glass"))) return "glass";
        if (state.is(spicedcider$tag("fabric")) || state.is(BlockTags.BEDS)) return "fabric";
        if (state.is(spicedcider$tag("foliage")) || state.is(BlockTags.LEAVES)) return "foliage";

        List<String> tokens = List.of(state.getSoundType().getBreakSound().getLocation().getPath().split("[._/]"));
        if (tokens.contains("amethyst")) return "amethyst";
        if (tokens.stream().anyMatch(METAL::contains)) return "metal";
        if (tokens.stream().anyMatch(GLASS::contains)) return "glass";
        if (tokens.stream().anyMatch(FABRIC::contains)) return "fabric";
        if (tokens.stream().anyMatch(FOLIAGE::contains)) return "foliage";
        return null;
    }

    @Unique
    private static TagKey<Block> spicedcider$tag(String name) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("coolrain", name + "_rain_blocks"));
    }
}
