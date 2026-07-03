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
        add("block_type.blockbox.seat", "%s Seat");
        add("block_type.blockbox.palisade", "%s Palisade");
        add("block_type.blockbox.stripped_palisade", "Stripped %s Palisade");
        add("block_type.blockbox.spiked_palisade", "Spiked %s Palisade");
        add("block_type.blockbox.stripped_spiked_palisade", "Stripped Spiked %s Palisade");

        add("category.spicedcider.client", "Client Settings");
        add("category.spicedcider.client.tooltip", "Settings that only apply to your game client.");
        add("option.spicedcider.exportDirectory", "Export Directory Path");
        add("option.spicedcider.exportDirectory.desc", "The path where textures and models will be exported. Supports absolute paths (e.g. C:/Downloads) or relative to the game directory.");

        add("item.spicedcider.broken", "Broken");
    }
}