package com.evandev.spicedcider.client.options;

import com.evandev.spicedcider.client.options.entry.ButtonEntry;
import com.evandev.spicedcider.client.options.entry.CategoryEntry;
import com.evandev.spicedcider.client.options.entry.Entry;
import com.evandev.spicedcider.client.options.entry.KeyEntry;
import com.evandev.spicedcider.client.options.entry.LanguageEntry;
import com.evandev.spicedcider.client.options.entry.PackEntry;
import com.evandev.spicedcider.client.options.entry.SettingEntry;
import com.evandev.spicedcider.client.options.entry.TextEntry;
import com.evandev.spicedcider.client.options.setting.CycleSetting;
import com.evandev.spicedcider.client.options.setting.GuiScaleSetting;
import com.evandev.spicedcider.client.options.setting.Setting;
import com.evandev.spicedcider.client.options.setting.VanillaSetting;
import com.evandev.spicedcider.compat.controlify.ControlifySettings;
import com.evandev.spicedcider.compat.lambdynlights.DynamicLightsSetting;
import com.evandev.spicedcider.compat.musictweaks.MusicTweaksSettings;
import com.evandev.spicedcider.compat.sodium.SodiumSetting;
import com.evandev.spicedcider.client.nether.NetherSettings;
import com.evandev.spicedcider.compat.voxy.VoxyFogSettings;
import com.evandev.spicedcider.compat.voxy.VoxyIngest;
import com.evandev.spicedcider.lodgen.client.LodSettings;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundLockDifficultyPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class SettingsPages {

    private static final Minecraft MC = Minecraft.getInstance();
    private static final int VOXY_DEFAULT_DISTANCE = 32;

    public static List<SettingsPage> create() {
        return List.of(
                new SettingsPage("general", icon(Items.GRASS_BLOCK), true, SettingsPages::general),
                new SettingsPage("player", icon(Items.PLAYER_HEAD), true, SettingsPages::player),
                new SettingsPage("audio", icon(Items.MUSIC_DISC_CAT), true, SettingsPages::audio),
                new SettingsPage("video", icon(Items.SPYGLASS), true, SettingsPages::video),
                new SettingsPage("controls", icon(Items.OAK_BOAT), true, SettingsPages::controls),
                new SettingsPage("accessibility", icon(Items.LANTERN), true, SettingsPages::accessibility),
                new SettingsPage("resource_packs", icon(Items.PAINTING), false, SettingsPages::resourcePacks),
                new SettingsPage("language", icon(Items.WRITABLE_BOOK), false, SettingsPages::language),
                new SettingsPage("search", icon(Items.GLASS), false, screen -> List.of())
        );
    }

    private static ItemStack icon(ItemLike item) {
        return new ItemStack(item);
    }

    private static Options options() {
        return MC.options;
    }

    private static List<Entry> general(SettingsScreen screen) {
        Options o = options();
        List<Entry> entries = new ArrayList<>();
        add(entries, category(screen, "gameplay",
                vanilla(o.fov()),
                difficulty(),
                lockDifficulty(screen)));
        add(entries, category(screen, "interface",
                setting(new GuiScaleSetting()),
                sodium("sodium:general.attack_indicator", o.attackIndicator()),
                sodium("sodium:general.autosave_indicator", o.showAutosaveIndicator()),
                vanilla(o.notificationDisplayTime())));
        add(entries, category(screen, "chat",
                vanilla(o.chatVisibility()),
                vanilla(o.chatColors()),
                vanilla(o.chatLinks()),
                vanilla(o.chatLinksPrompt()),
                vanilla(o.autoSuggestions()),
                vanilla(o.chatOpacity()),
                vanilla(o.textBackgroundOpacity()),
                vanilla(o.backgroundForChatOnly()),
                vanilla(o.chatScale()),
                vanilla(o.chatWidth()),
                vanilla(o.chatHeightFocused()),
                vanilla(o.chatHeightUnfocused()),
                vanilla(o.chatLineSpacing()),
                vanilla(o.chatDelay())));
        add(entries, category(screen, "folders",
                new ButtonEntry(Component.translatable("spicedcider.options.open_screenshots"), () -> openFolder(new File(MC.gameDirectory, "screenshots"))),
                new ButtonEntry(Component.translatable("spicedcider.options.open_saves"), () -> openFolder(MC.getLevelSource().getBaseDir().toFile()))));
        return entries;
    }

    private static List<Entry> player(SettingsScreen screen) {
        Options o = options();
        List<Entry> parts = new ArrayList<>();
        for (PlayerModelPart part : PlayerModelPart.values()) {
            parts.add(setting(CycleSetting.toggle(part.getName(), () -> o.isModelPartEnabled(part), value -> o.toggleModelPart(part, value)).defaultValue(PackDefaults.raw("modelPart_" + part.getId()).map(Boolean::parseBoolean).orElse(true))));
        }
        List<Entry> entries = new ArrayList<>();
        add(entries, category(screen, "skin", parts.toArray(Entry[]::new)));
        add(entries, category(screen, "hands", vanilla(o.mainHand())));
        return entries;
    }

    private static List<Entry> audio(SettingsScreen screen) {
        Options o = options();
        List<Entry> volumes = new ArrayList<>();
        for (SoundSource source : SoundSource.values()) {
            volumes.add(vanilla(o.getSoundSourceOptionInstance(source)));
        }
        List<Entry> entries = new ArrayList<>();
        add(entries, category(screen, "volume", volumes.toArray(Entry[]::new)));
        if (ModList.get().isLoaded("music_tweaks")) {
            entries.add(MusicTweaksSettings.category(screen));
        }
        add(entries, category(screen, "sound",
                vanilla(o.showSubtitles()),
                vanilla(o.directionalAudio()),
                vanilla(o.soundDevice())));
        return entries;
    }

    private static List<Entry> video(SettingsScreen screen) {
        Options o = options();
        List<Entry> entries = new ArrayList<>();
        add(entries, category(screen, "display",
                sodium("sodium:general.fullscreen", o.fullscreen()),
                sodium("sodium:general.fullscreen_resolution", null),
                sodium("sodium:general.vsync", o.enableVsync()),
                sodium("sodium:general.framerate_limit", o.framerateLimit()),
                sodium("sodium:general.gamma", o.gamma())));
        add(entries, category(screen, "distance",
                sodium("sodium:general.render_distance", o.renderDistance()),
                sodium("sodium:general.simulation_distance", o.simulationDistance()),
                sodium("sodium:quality.entity_distance", o.entityDistanceScaling()),
                named(sodiumOnly("voxy:enabled"), "spicedcider.options.lod_terrain"),
                named(sodiumOnly("voxy:render_distance", VOXY_DEFAULT_DISTANCE), "spicedcider.options.lod_distance"),
                VoxyIngest.available() ? LodSettings.generate() : null,
                VoxyIngest.available() ? LodSettings.threads() : null,
                ModList.get().isLoaded("roxy") ? VoxyFogSettings.start() : null,
                ModList.get().isLoaded("roxy") ? VoxyFogSettings.end() : null));
        add(entries, category(screen, "quality",
                sodium("sodium:quality.graphics", o.graphicsMode()),
                sodium("sodium:quality.ao", o.ambientOcclusion()),
                sodium("sodium:quality.clouds", o.cloudStatus()),
                sodium("sodium:quality.weather", null),
                sodium("sodium:quality.leaves", null),
                sodium("sodium:quality.particles", o.particles()),
                ModList.get().isLoaded("betterbiomeblend")
                        ? named(sodiumOnly("betterbiomeblend:biome_blend_radius", PackDefaults.raw("betterBiomeBlendRadius").map(Integer::valueOf).orElse(null)), "options.biomeBlendRadius")
                        : sodium("sodium:quality.biome_blend", o.biomeBlendRadius()),
                sodium("sodium:quality.entity_shadows", o.entityShadows()),
                sodium("sodium:quality.vignette", null),
                ModList.get().isLoaded("lambdynlights") ? optional(DynamicLightsSetting.create()) : null,
                sodium("sodium:quality.mipmap_levels", o.mipmapLevels())));
        return entries;
    }

    private static List<Entry> controls(SettingsScreen screen) {
        Options o = options();
        List<Entry> entries = new ArrayList<>();
        add(entries, category(screen, "mouse",
                vanilla(o.sensitivity()),
                vanilla(o.invertYMouse()),
                vanilla(o.mouseWheelSensitivity()),
                vanilla(o.discreteMouseScroll()),
                vanilla(o.rawMouseInput())));
        add(entries, category(screen, "movement_options",
                vanilla(o.toggleCrouch()),
                vanilla(o.toggleSprint()),
                vanilla(o.autoJump())));
        if (ModList.get().isLoaded("controlify")) {
            entries.add(ControlifySettings.settingsCategory(screen));
        }

        Map<String, List<KeyMapping>> groups = new LinkedHashMap<>();
        for (String group : KeyLayout.GROUPS) groups.put(group, new ArrayList<>());
        for (KeyMapping mapping : o.keyMappings) {
            String group = KeyLayout.groupOf(mapping.getName());
            if (group != null) groups.get(group).add(mapping);
        }
        for (Map.Entry<String, List<KeyMapping>> group : groups.entrySet()) {
            List<KeyMapping> mappings = group.getValue();
            mappings.sort(Comparator.comparingInt(mapping -> KeyLayout.order(mapping.getName())));
            add(entries, category(screen, "keys." + group.getKey(), mappings.stream().map(mapping -> (Entry) new KeyEntry(screen, mapping)).toArray(Entry[]::new)));
        }
        entries.add(new ButtonEntry(Component.translatable("controls.resetAll"), () -> {
            for (KeyMapping mapping : o.keyMappings) {
                PackDefaults.resetKey(mapping);
            }
            KeyMapping.resetMapping();
        }));
        if (ModList.get().isLoaded("controlify")) {
            entries.addAll(ControlifySettings.bindingCategories(screen));
        }
        return entries;
    }

    private static List<Entry> accessibility(SettingsScreen screen) {
        Options o = options();
        List<Entry> entries = new ArrayList<>();
        add(entries, category(screen, "accessibility",
                vanilla(o.narrator()),
                vanilla(o.narratorHotkey()),
                vanilla(o.showSubtitles()),
                vanilla(o.highContrast()),
                vanilla(o.textBackgroundOpacity()),
                vanilla(o.notificationDisplayTime())));
        add(entries, category(screen, "motion",
                sodium("sodium:general.view_bobbing", o.bobView()),
                vanilla(o.screenEffectScale()),
                NetherSettings.heatHaze(),
                NetherSettings.mist(),
                vanilla(o.fovEffectScale()),
                vanilla(o.darknessEffectScale()),
                vanilla(o.damageTiltStrength()),
                vanilla(o.hideLightningFlash()),
                vanilla(o.glintSpeed()),
                vanilla(o.glintStrength())));
        return entries;
    }

    private static List<Entry> resourcePacks(SettingsScreen screen) {
        PackSelectionModel model = screen.packModel();
        List<Entry> entries = new ArrayList<>();
        entries.add(new ButtonEntry(Component.translatable("pack.openFolder"), () -> openFolder(MC.getResourcePackDirectory().toFile())));
        List<Entry> selected = model.getSelected().filter(pack -> screen.isPackVisible(pack.getId())).map(pack -> (Entry) new PackEntry(pack)).toList();
        List<Entry> available = model.getUnselected().filter(pack -> screen.isPackVisible(pack.getId())).map(pack -> (Entry) new PackEntry(pack)).toList();
        entries.add(new CategoryEntry(Component.translatable("pack.selected.title"), new ArrayList<>(selected.isEmpty() ? List.of(new TextEntry(Component.translatable("spicedcider.options.packs.none"))) : selected), screen.collapsed()));
        entries.add(new CategoryEntry(Component.translatable("pack.available.title"), new ArrayList<>(available.isEmpty() ? List.of(new TextEntry(Component.translatable("spicedcider.options.packs.none"))) : available), screen.collapsed()));
        return entries;
    }

    private static List<Entry> language(SettingsScreen screen) {
        List<Entry> entries = new ArrayList<>();
        add(entries, category(screen, "text", vanilla(options().forceUnicodeFont())));
        List<Entry> languages = new ArrayList<>();
        MC.getLanguageManager().getLanguages().forEach((code, info) -> languages.add(new LanguageEntry(code, info, screen::selectedLanguage, screen::selectLanguage)));
        entries.add(new CategoryEntry(Component.translatable("spicedcider.options.category.languages"), languages, screen.collapsed()));
        return entries;
    }

    @Nullable
    private static Entry difficulty() {
        if (MC.level == null || !MC.hasSingleplayerServer()) return null;
        return setting(new CycleSetting<>(
                Component.translatable("options.difficulty"),
                () -> Arrays.asList(Difficulty.values()),
                () -> MC.level.getDifficulty(),
                value -> MC.getConnection().send(new ServerboundChangeDifficultyPacket(value)),
                Difficulty::getDisplayName
        ).active(() -> MC.level != null && !MC.level.getLevelData().isHardcore() && !MC.level.getLevelData().isDifficultyLocked()));
    }

    @Nullable
    private static Entry lockDifficulty(SettingsScreen screen) {
        if (MC.level == null || !MC.hasSingleplayerServer() || MC.level.getLevelData().isHardcore()) return null;
        return new ButtonEntry(Component.translatable("spicedcider.options.lock_difficulty"), () -> MC.setScreen(new ConfirmScreen(
                confirmed -> {
                    if (confirmed && MC.getConnection() != null) {
                        MC.getConnection().send(new ServerboundLockDifficultyPacket(true));
                    }
                    MC.setScreen(screen);
                },
                Component.translatable("difficulty.lock.title"),
                Component.translatable("difficulty.lock.question", MC.level.getLevelData().getDifficulty().getDisplayName())
        )), () -> MC.level != null && !MC.level.getLevelData().isDifficultyLocked());
    }

    private static void openFolder(File folder) {
        folder.mkdirs();
        Util.getPlatform().openFile(folder);
    }

    @Nullable
    private static CategoryEntry category(SettingsScreen screen, String key, @Nullable Entry... children) {
        List<Entry> list = new ArrayList<>(Arrays.stream(children).filter(Objects::nonNull).toList());
        if (list.isEmpty()) return null;
        return new CategoryEntry(Component.translatable("spicedcider.options.category." + key), list, screen.collapsed());
    }

    private static void add(List<Entry> entries, @Nullable Entry entry) {
        if (entry != null) entries.add(entry);
    }

    private static Entry setting(Setting setting) {
        return new SettingEntry(setting);
    }

    @Nullable
    private static Entry optional(@Nullable Setting setting) {
        return setting == null ? null : new SettingEntry(setting);
    }

    private static Entry vanilla(OptionInstance<?> option) {
        return new SettingEntry(new VanillaSetting<>(option));
    }

    @Nullable
    private static Entry sodium(String id, @Nullable OptionInstance<?> fallback) {
        if (SettingsScreen.useSodium()) {
            Setting setting = SodiumSetting.of(id, fallback == null ? null : PackDefaults.value(fallback).orElse(null));
            if (setting != null) return new SettingEntry(setting);
        }
        return fallback == null ? null : vanilla(fallback);
    }

    @Nullable
    private static Setting sodiumOnly(String id) {
        return sodiumOnly(id, null);
    }

    @Nullable
    private static Setting sodiumOnly(String id, @Nullable Object packDefault) {
        return SettingsScreen.useSodium() ? SodiumSetting.of(id, packDefault) : null;
    }

    @Nullable
    private static Entry named(@Nullable Setting setting, String key) {
        return setting == null ? null : new SettingEntry(setting.named(Component.translatable(key)));
    }
}
