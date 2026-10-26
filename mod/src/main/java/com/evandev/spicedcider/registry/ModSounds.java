package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.neoforge.common.util.DeferredSoundType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, SpicedCider.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> PLAYER_DEATH = SOUNDS.register("music.death.death1",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "music.death.death1")));

    public static final DeferredHolder<SoundEvent, SoundEvent> SPIDER_PREPARE_SHOOT = SOUNDS.register("entity.spider.prepare_shoot",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "entity.spider.prepare_shoot")));

    public static final DeferredHolder<SoundEvent, SoundEvent> SPIDER_SHOOT = SOUNDS.register("entity.spider.shoot",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "entity.spider.shoot")));

    public static final DeferredHolder<SoundEvent, SoundEvent> SPIDER_WEB_IMPACT = SOUNDS.register("entity.spider.web_impact",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "entity.spider.web_impact")));

    public static final DeferredHolder<SoundEvent, SoundEvent> GRAPPLING_HOOK_TIGHTEN = SOUNDS.register("entity.player.grappling_hook.tighten",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "entity.player.grappling_hook.tighten")));

    public static final DeferredHolder<SoundEvent, SoundEvent> BALLOON_TIE = SOUNDS.register("entity.balloon.tie",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "entity.balloon.tie")));

    public static final DeferredHolder<SoundEvent, SoundEvent> BALLOON_POP = SOUNDS.register("entity.balloon.pop",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "entity.balloon.pop")));

    public static final DeferredHolder<SoundEvent, SoundEvent> ALGAE_BREAK = block("algae.break");
    public static final DeferredHolder<SoundEvent, SoundEvent> ALGAE_STEP = block("algae.step");
    public static final DeferredHolder<SoundEvent, SoundEvent> ALGAL_GRAVEL_BREAK = block("algal_gravel.break");
    public static final DeferredHolder<SoundEvent, SoundEvent> ALGAL_GRAVEL_STEP = block("algal_gravel.step");
    public static final DeferredHolder<SoundEvent, SoundEvent> ALGAL_SAND_BREAK = block("algal_sand.break");
    public static final DeferredHolder<SoundEvent, SoundEvent> ALGAL_SAND_STEP = block("algal_sand.step");
    public static final DeferredHolder<SoundEvent, SoundEvent> CHROMARINE_BREAK = block("chromarine.break");
    public static final DeferredHolder<SoundEvent, SoundEvent> CHROMARINE_STEP = block("chromarine.step");
    public static final DeferredHolder<SoundEvent, SoundEvent> PRISMARINE_CLUSTER_BREAK = block("prismarine_cluster.break");
    public static final DeferredHolder<SoundEvent, SoundEvent> SILT_BREAK = block("silt.break");
    public static final DeferredHolder<SoundEvent, SoundEvent> SILT_STEP = block("silt.step");
    public static final DeferredHolder<SoundEvent, SoundEvent> DRIED_SILT_BREAK = block("dried_silt.break");
    public static final DeferredHolder<SoundEvent, SoundEvent> DRIED_SILT_STEP = block("dried_silt.step");

    public static final SoundType ALGAE = new DeferredSoundType(1.0F, 1.0F, ALGAE_BREAK, ALGAE_STEP, ALGAE_BREAK, ALGAE_BREAK, ALGAE_STEP);
    public static final SoundType ALGAL_GRAVEL = new DeferredSoundType(1.0F, 1.0F, ALGAL_GRAVEL_BREAK, ALGAL_GRAVEL_STEP, ALGAL_GRAVEL_BREAK, ALGAL_GRAVEL_BREAK, ALGAL_GRAVEL_STEP);
    public static final SoundType ALGAL_SAND = new DeferredSoundType(1.0F, 1.0F, ALGAL_SAND_BREAK, ALGAL_SAND_STEP, ALGAL_SAND_BREAK, ALGAL_SAND_BREAK, ALGAL_SAND_STEP);
    public static final SoundType CHROMARINE = new DeferredSoundType(1.0F, 1.0F, CHROMARINE_BREAK, CHROMARINE_STEP, CHROMARINE_BREAK, CHROMARINE_BREAK, CHROMARINE_STEP);
    public static final SoundType SILT = new DeferredSoundType(1.0F, 1.0F, SILT_BREAK, SILT_STEP, SILT_BREAK, SILT_BREAK, SILT_STEP);
    public static final SoundType DRIED_SILT = new DeferredSoundType(1.0F, 1.0F, DRIED_SILT_BREAK, DRIED_SILT_STEP, DRIED_SILT_BREAK, DRIED_SILT_BREAK, DRIED_SILT_STEP);
    public static final SoundType PRISMARINE_CLUSTER = new DeferredSoundType(1.0F, 1.0F, PRISMARINE_CLUSTER_BREAK, SoundType.GLASS::getStepSound, SoundType.GLASS::getPlaceSound, SoundType.GLASS::getHitSound, SoundType.GLASS::getFallSound);

    private static DeferredHolder<SoundEvent, SoundEvent> block(String name) {
        String id = "block." + SpicedCider.MOD_ID + "." + name;
        return SOUNDS.register(id, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, id)));
    }
}
