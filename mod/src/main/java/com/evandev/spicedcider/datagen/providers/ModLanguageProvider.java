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

        addItem(ModItems.FIRE_STRIKER, "Fire Striker");

        addItem(ModItems.GRAPPLING_HOOK, "Grappling Hook");
        addItem(ModItems.STICKY_GRAPPLING_HOOK, "Sticky Grappling Hook");
        addItem(ModItems.RUBBER_CABLE, "Rubber Cable");
        add("item.spicedcider.grappling_hook.desc", "Good for climbing, not fishing");
        add("item.spicedcider.sticky_grappling_hook.desc", "Good for climbing, not fishing. Adheres to walls rather than bouncing off them.");

        addItem(ModItems.BLAST_PROOF_PLATING, "Blast-Proof Plating");
        addItem(ModItems.MISCHIEF_HELMET, "Mischief Helmet");
        addItem(ModItems.MISCHIEF_CHESTPLATE, "Mischief Chestplate");
        addItem(ModItems.MISCHIEF_LEGGINGS, "Mischief Leggings");
        addItem(ModItems.MISCHIEF_BOOTS, "Mischief Boots");
        addItem(ModItems.CLEAVER_OF_BEHEADING, "Cleaver of Beheading");
        add("attribute.name.spicedcider.explosive_damage_reduction", "Explosive Damage Reduction");

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
        add("category.spicedcider.vista", "Vista Mirrors");
        add("category.spicedcider.vista.tooltip", "Performance tuning for Vista's mirror/TV reflection rendering. Only shown if Vista is installed.");

        add("option.spicedcider.cooksCollectionDripstoneFix", "Fix Salted Dripstone Occlusion");
        add("option.spicedcider.cooksCollectionDripstoneFix.tooltip", "Fixes Cooks' Collection's salted dripstone block causing occlusion/culling issues by basing it on vanilla dripstone block. Requires a restart to take effect.");
        add("option.spicedcider.wisteriaLeafDensityFix", "Fix Wisteria Leaf Density");
        add("option.spicedcider.wisteriaLeafDensityFix.tooltip", "Fixes Environmental's wisteria trees generating with sparse/patchy leaves.");
        add("option.spicedcider.blockBoxWoodVariants", "Block Box Wood Variants");
        add("option.spicedcider.blockBoxWoodVariants.tooltip", "Registers Every Compat wood-type variants (seats, palisades) for The Block Box. Requires a restart to take effect.");
        add("option.spicedcider.sodiumLightingParityFix", "Sodium Lighting Parity Fix");
        add("option.spicedcider.sodiumLightingParityFix.tooltip", "Restores vanilla parity for Sodium's Smooth Lighting corner blending, which stretches light further out than vanilla.");
        add("option.spicedcider.slimeTimeDisableItemMerging", "Disable Slime Time Slimeball Merging");
        add("option.spicedcider.slimeTimeDisableItemMerging.tooltip", "Disables Slime Time's feature where clicking slimeballs together in inventory merges them.");

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
        add("option.spicedcider.removeQuasiConnectivity", "Remove Quasi-Connectivity");
        add("option.spicedcider.removeQuasiConnectivity.tooltip", "Removes quasi-connectivity from pistons, dispensers, and droppers.");

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
        add("option.spicedcider.cloudFarPlaneFix", "Cloud Far Plane Fix");
        add("option.spicedcider.cloudFarPlaneFix.tooltip", "Gives clouds their own extended far plane instead of sharing terrain's (which is capped at render distance * 4 blocks), so far away clouds don't get hard-clipped.");
        add("option.spicedcider.cloudFarPlaneDistance", "Cloud Far Plane Distance");
        add("option.spicedcider.cloudFarPlaneDistance.tooltip", "Far clip plane distance in blocks used for clouds.");

        add("option.spicedcider.vistaMirrorAlwaysConnect", "Always Connect Mirrors");
        add("option.spicedcider.vistaMirrorAlwaysConnect.tooltip", "Ignores Vista's square-aspect-ratio restriction when growing connected mirrors, so adjacent mirrors always merge into one group regardless of shape.");
        add("option.spicedcider.vistaMirrorPerfFixes", "Mirror Performance Fixes");
        add("option.spicedcider.vistaMirrorPerfFixes.tooltip", "Applies performance fixes to Vista's mirror/TV reflection rendering (tightened culling, shared-state thrash suppression, throttled re-renders).");
        add("option.spicedcider.vistaMirrorReflectionDistance", "Mirror Reflection Distance");
        add("option.spicedcider.vistaMirrorReflectionDistance.tooltip", "Max distance in blocks that terrain and entities render to inside a mirror/TV reflection.");
        add("option.spicedcider.vistaMirrorUpdateFps", "Mirror Update Rate");
        add("option.spicedcider.vistaMirrorUpdateFps.tooltip", "How many times per second each mirror re-renders its reflection while the viewer is moving.");
        add("option.spicedcider.vistaMirrorMinUpdateFps", "Mirror Minimum Update Rate");
        add("option.spicedcider.vistaMirrorMinUpdateFps.tooltip", "The floor that the mirror update rate will never drop below.");
        add("option.spicedcider.vistaMirrorIdleUpdateFps", "Mirror Idle Update Rate");
        add("option.spicedcider.vistaMirrorIdleUpdateFps.tooltip", "Reflection update rate used while the viewer's eye is stationary. Set equal to update rate to disable the idle slowdown.");
        add("option.spicedcider.vistaMirrorThrottleBudgetMs", "Mirror Frame Budget (ms)");
        add("option.spicedcider.vistaMirrorThrottleBudgetMs.tooltip", "Max milliseconds per frame all mirror reflection re-renders together may take before update-rate throttling kicks in.");

        add("item.spicedcider.broken", "Broken");

        add("tooltip.spicedcider.angling_slot.line.title", "Fishing Line");
        add("tooltip.spicedcider.angling_slot.bobber.title", "Fishing Bobber");
        add("tooltip.spicedcider.angling_slot.hook.title", "Fishing Hook");
    }
}