package com.evandev.spicedcider.client.options;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class KeyLayout {

    public static final String OTHER = "misc";

    public static final List<String> GROUPS = List.of("movement", "gameplay", "hotbar", "interface", "camera", "voice_chat", "emotes", OTHER);

    private static final Map<String, String> GROUP_BY_KEY = new LinkedHashMap<>();

    private static final List<String> HIDDEN = List.of(
            "key.spectatorOutlines",
            "key.socialInteractions",
            "key.saveToolbarActivator",
            "key.loadToolbarActivator",
            "lambdynlights.key.toggle_fps_dynamic_lighting",
            "key.better_modlist.open_menu",
            "gnetum.config.keyMapping",
            "key.dungeonconfig.open",
            "key.modernfix.config",
            "quark.keybind.back",
            "quark.keybind.narrator_readout",
            "quark.keybind.narrator_full_readout",
            "chunksfadein.keybinds.toggleMod",
            "key.modulation.panorama_screenshot",
            "key.clickthrough.toggle",
            "create.keyinfo.shift_modifier",
            "create.keyinfo.ctrl_modifier",
            "create.keyinfo.alt_modifier",
            "key.raised.options",
            "key.presencefootsteps.settings",
            "key.presencefootsteps.toggle",
            "key.immersiveoverlays.settings",
            "key.entityculling.toggle",
            "key.toggleVisualItemContents",
            "key.toggleSelectedItemTooltips",
            "key.toggleCarriedItemTooltips",
            "key.map_atlases.increase_slice",
            "key.map_atlases.decrease_slice",
            "key.lodged.remove_shield_arrow"
    );

    private static final List<Set<String>> SHARED = List.of(
            Set.of("key.sneak", "key.jade.show_details", "create.keyinfo.shift_modifier"),
            Set.of("key.sprint", "create.keyinfo.ctrl_modifier"),
            Set.of("key.clutternomore.change_block_shape", "create.keyinfo.toolmenu", "create.keyinfo.alt_modifier"),
            Set.of("key.forward", "key.ponder.ponder"),
            Set.of("key.reliable_remover.emi_delete", "key.reliable_remover.jei_delete"),
            Set.of("quark.keybind.change_hotbar", "key.packdev_toolkit.export_description")
    );

    private static final List<String> HIDDEN_PREFIXES = List.of(
            "quark.keybind.patreon_emote.",
            "key.packdev_toolkit.",
            "placebo.",
            "key.jade.",
            "key.scholar.",
            "key.reliable_remover.",
            "key.modest_meals."
    );

    static {
        group("movement", "key.forward", "key.left", "key.back", "key.right", "key.jump", "key.sneak", "key.sprint",
                "key.personality.crawl", "key.personality.sit", "quark.keybind.autorun", "key.reliable_gliders.deploy");
        group("gameplay", "key.attack", "key.use", "key.pickItem", "key.swapOffhand", "key.drop",
                "key.clutternomore.change_block_shape", "quark.keybind.variant_selector", "supplementaries.keybind.quiver",
                "key.lodged.use_bandage", "treeliable.key.toggle_chopping",
                "treeliable.key.cycle_sneak_behavior", "create.keyinfo.toolmenu", "create.keyinfo.toolbelt",
                "create.keyinfo.rotate_menu", "key.exposure.camera_controls", "key.fieldguide.scan");
        group("hotbar", "key.hotbar.1", "key.hotbar.2", "key.hotbar.3", "key.hotbar.4", "key.hotbar.5", "key.hotbar.6",
                "key.hotbar.7", "key.hotbar.8", "key.hotbar.9", "quark.keybind.change_hotbar");
        group("interface", "key.inventory", "key.chat", "key.command", "key.playerlist", "key.advancements",
                "key.fieldguide.open", "key.ponder.ponder", "key.jade.show_details", "quark.keybind.sort_player",
                "quark.keybind.sort_container", "quark.keybind.transfer_insert", "quark.keybind.transfer_extract",
                "quark.keybind.shift_lock", "key.map_atlases.open_minimap", "key.map_atlases.zoom_in_minimap",
                "key.map_atlases.zoom_out_minimap", "key.map_atlases.place_pin", "key.immersiveoverlays.toggle");
        group("camera", "key.togglePerspective", "key.smoothCamera", "quark.keybind.lock_rotation",
                "quark.keybind.camera_mode", "key.picturemode.open", "key.screenshot",
                "key.screenshot_viewer.open_screenshots_screen", "key.fullscreen");
        group("voice_chat", "key.voice_chat", "key.push_to_talk", "key.whisper", "key.mute_microphone",
                "key.disable_voice_chat", "key.hide_icons", "key.voice_chat_settings", "key.voice_chat_group",
                "key.voice_chat_toggle_recording", "key.voice_chat_adjust_volumes");
    }

    private static void group(String group, String... keys) {
        for (String key : keys) GROUP_BY_KEY.put(key, group);
    }

    public static String groupOf(String name) {
        String group = GROUP_BY_KEY.get(name);
        if (group != null) return group;
        if (HIDDEN.contains(name)) return null;
        for (String prefix : HIDDEN_PREFIXES) {
            if (name.startsWith(prefix)) return null;
        }
        if (name.startsWith("quark.emote.")) return "emotes";
        return OTHER;
    }

    public static boolean sharesIntentionally(String a, String b) {
        for (Set<String> shared : SHARED) {
            if (shared.contains(a) && shared.contains(b)) return true;
        }
        return false;
    }

    public static int order(String name) {
        int index = 0;
        for (String key : GROUP_BY_KEY.keySet()) {
            if (key.equals(name)) return index;
            index++;
        }
        return Integer.MAX_VALUE;
    }
}
