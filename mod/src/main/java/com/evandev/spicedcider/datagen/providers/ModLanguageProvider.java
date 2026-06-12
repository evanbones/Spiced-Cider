package com.evandev.spicedcider.datagen.providers;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.registry.ModBlocks;
import com.evandev.spicedcider.registry.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ModLanguageProvider extends LanguageProvider {

    public ModLanguageProvider(PackOutput output) {
        super(output, SpicedCider.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        addBlock(ModBlocks.WORKSTONE, "Workstone");

        addItem(ModItems.FLINT_HAMMER, "Flint Hammer");
        addItem(ModItems.IRON_HAMMER, "Iron Hammer");
        addItem(ModItems.GOLDEN_HAMMER, "Golden Hammer");
        addItem(ModItems.DIAMOND_HAMMER, "Diamond Hammer");
        addItem(ModItems.NETHERITE_HAMMER, "Netherite Hammer");

        add("spicedcider.respawn_blocked", "The Respawn Anchor is unresponsive in this dimension");

        add("key.category.naming_unconvention.naming_unconvention", "Naming Unconvention");
        add("key.naming_unconvention.reroll", "Reroll World Name");
        add("subtitles.spicedcider.block.workstone.hammer", "Workstone hammered");
        add("emi.category.spicedcider.workstone", "Workstone");
        add("subtitles.spicedcider.music.death.death1", "Player died");

        add("title.spicedcider.config", "Spiced Cider Configuration");

        add("category.spicedcider.client", "Client Settings");
        add("category.spicedcider.client.tooltip", "Settings that only apply to your game client.");
        add("option.spicedcider.exportDirectory", "Export Directory Path");
        add("option.spicedcider.exportDirectory.desc", "The path where textures and models will be exported. Supports absolute paths (e.g. C:/Downloads) or relative to the game directory.");

        add("category.spicedcider.structures", "Structure Settings");

        add("group.spicedcider.search_settings", "Search Settings");
        add("option.spicedcider.useFastStructureLookup", "Use Fast Structure Lookup");
        add("option.spicedcider.structureSearchTimeout", "Search Timeout (Seconds)");
        add("option.spicedcider.mapSearchRadius", "Map Search Radius");
        add("option.spicedcider.globalSearchRadius", "Global Search Radius");
        add("option.spicedcider.locateSearchRadius", "Locate Command Radius");

        add("group.spicedcider.generation_settings", "Generation Settings");
        add("option.spicedcider.spacingSeparationModifier", "Spacing / Separation Modifier");
        add("option.spicedcider.minimumStructureDistanceEnabled", "Enable Minimum Distance Limit");
        add("option.spicedcider.minimumStructureDistance", "Minimum Distance Between Structures");
        add("option.spicedcider.logDuplicatedSalt", "Log Duplicated Generation Salt");
    }
}