package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, SpicedCider.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> HAMMER1 = SOUNDS.register("block.workstone.hammer1",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "block.workstone.hammer1")));

    public static final DeferredHolder<SoundEvent, SoundEvent> HAMMER2 = SOUNDS.register("block.workstone.hammer2",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "block.workstone.hammer2")));

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

}