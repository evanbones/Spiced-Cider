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

        addItem(ModItems.GRAPPLING_HOOK, "Grappling Hook");
        addItem(ModItems.STICKY_GRAPPLING_HOOK, "Sticky Grappling Hook");
        add("item.spicedcider.grappling_hook.desc", "Good for climbing, not fishing");
        add("item.spicedcider.sticky_grappling_hook.desc", "Good for climbing, not fishing. Adheres to walls rather than bouncing off them.");

        add("spicedcider.respawn_blocked", "The Respawn Anchor is unresponsive in this dimension");

        add("key.category.naming_unconvention.naming_unconvention", "Naming Unconvention");
        add("key.naming_unconvention.reroll", "Reroll World Name");
        add("subtitles.spicedcider.block.workstone.hammer", "Workstone hammered");
        add("subtitles.spicedcider.entity.player.grappling_hook.tighten", "Rope tightens");
        add("emi.category.spicedcider.workstone", "Workstone");
        add("subtitles.spicedcider.music.death.death1", "Player died");

        add("block_type.blockbox.seat", "%s Seat");
        add("block_type.blockbox.palisade", "%s Palisade");
        add("block_type.blockbox.stripped_palisade", "Stripped %s Palisade");
        add("block_type.blockbox.spiked_palisade", "Spiked %s Palisade");
        add("block_type.blockbox.stripped_spiked_palisade", "Stripped Spiked %s Palisade");

        add("title.spicedcider.config", "Spiced Cider Configuration");

        add("category.spicedcider.gameplay", "Gameplay");
        add("category.spicedcider.gameplay.tooltip", "Gameplay tweaks and fixes.");
        add("category.spicedcider.compat", "Compatibility");
        add("category.spicedcider.compat.tooltip", "Fixes and integrations for other mods. Only shown if the relevant mod is installed.");
        add("category.spicedcider.performance", "Performance");
        add("category.spicedcider.performance.tooltip", "Performance optimizations.");
        add("category.spicedcider.client", "Client Settings");
        add("category.spicedcider.client.tooltip", "Settings that only apply to your game client.");

        add("option.spicedcider.cooksCollectionDripstoneFix", "Fix Salted Dripstone Occlusion");
        add("option.spicedcider.cooksCollectionDripstoneFix.tooltip", "Fixes Cooks' Collection's salted dripstone block causing occlusion/culling issues by basing it on vanilla dripstone block. Requires a restart to take effect.");
        add("option.spicedcider.wisteriaLeafDensityFix", "Fix Wisteria Leaf Density");
        add("option.spicedcider.wisteriaLeafDensityFix.tooltip", "Fixes Environmental's wisteria trees generating with sparse/patchy leaves.");
        add("option.spicedcider.blockBoxWoodVariants", "Block Box Wood Variants");
        add("option.spicedcider.blockBoxWoodVariants.tooltip", "Registers Every Compat wood-type variants (seats, palisades) for The Block Box. Requires a restart to take effect.");

        add("option.spicedcider.bedExplosionPrevention", "Prevent Bed Explosions");
        add("option.spicedcider.bedExplosionPrevention.tooltip", "Beds don't explode outside the Overworld; instead they show a message and wake sleeping villagers.");
        add("option.spicedcider.respawnAnchorExplosionPrevention", "Prevent Respawn Anchor Explosions");
        add("option.spicedcider.respawnAnchorExplosionPrevention.tooltip", "Respawn anchors don't explode outside dimensions where they work; instead they show a message.");
        add("option.spicedcider.endCrystalPlaceAnywhere", "End Crystals Placeable Anywhere");
        add("option.spicedcider.endCrystalPlaceAnywhere.tooltip", "End crystals can be placed on any block, not just obsidian/bedrock.");
        add("option.spicedcider.endCrystalHealing", "End Crystal Healing");
        add("option.spicedcider.endCrystalHealing.tooltip", "End crystals with no beam target heal nearby damaged entities and render a beam to their heal target.");
        add("option.spicedcider.keepBrokenItems", "Keep Broken Items");
        add("option.spicedcider.keepBrokenItems.tooltip", "Enchanted, named, or elytra items become \"Broken\" instead of being destroyed when they run out of durability.");
        add("option.spicedcider.wolvesWearAnyArmor", "Wolves Wear Any Armor");
        add("option.spicedcider.wolvesWearAnyArmor.tooltip", "Tamed wolves can be equipped with horse/animal armor as body armor.");
        add("option.spicedcider.spiderRangedWebAttacks", "Spider Ranged Web Attacks");
        add("option.spicedcider.spiderRangedWebAttacks.tooltip", "Spiders switch to shooting cobweb projectiles at range when their target is trapped.");
        add("option.spicedcider.skeletonHealthNerf", "Skeleton Health Nerf");
        add("option.spicedcider.skeletonHealthNerf.tooltip", "Skeletons and strays get 12 max health. Requires a restart to take effect.");

        add("option.spicedcider.disableRecipeBookTracking", "Disable Recipe Book Tracking");
        add("option.spicedcider.disableRecipeBookTracking.tooltip", "Disables recipe book unlock tracking/saving/syncing entirely. Saves memory and disk I/O.");
        add("option.spicedcider.skipRedundantBlockCacheRebuild", "Skip Redundant Block Cache Rebuild");
        add("option.spicedcider.skipRedundantBlockCacheRebuild.tooltip", "Skips the redundant block shape cache rebuild that happens on every tag reload.");
        add("option.spicedcider.filterUnusedResourcePackNamespaces", "Filter Unused Resource Pack Namespaces");
        add("option.spicedcider.filterUnusedResourcePackNamespaces.tooltip", "Filters out resource pack namespaces that aren't used by any loaded mod, speeding up client asset loading. Requires a restart to take effect.");

        add("option.spicedcider.randomWorldNaming", "Random World Naming");
        add("option.spicedcider.randomWorldNaming.tooltip", "Auto-generates a random world name on the world creation screen, with a reroll button if Modern World Creation is present.");
        add("option.spicedcider.customDeathSound", "Custom Death Sound");
        add("option.spicedcider.customDeathSound.tooltip", "Plays a custom sound when your player character dies.");
        add("option.spicedcider.hideMelancholicHungerTooltip", "Hide Melancholic Hunger Tooltip");
        add("option.spicedcider.hideMelancholicHungerTooltip.tooltip", "Hides Melancholic Hunger's regeneration tooltip lines from item tooltips.");

        add("item.spicedcider.broken", "Broken");

        add("tooltip.spicedcider.angling_slot.line.title", "Fishing Line");
        add("tooltip.spicedcider.angling_slot.bobber.title", "Fishing Bobber");
        add("tooltip.spicedcider.angling_slot.hook.title", "Fishing Hook");
    }
}