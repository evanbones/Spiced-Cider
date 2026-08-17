package com.evandev.spicedcider;

import com.evandev.spicedcider.blockgrid.ClientOffsetCache;
import com.evandev.spicedcider.blockgrid.SupportOffsets;
import com.evandev.spicedcider.compat.everycompat.BlockBoxEveryCompatLoader;
import com.evandev.spicedcider.compat.yacl.SpicedCiderConfigScreen;
import com.evandev.spicedcider.config.ConfigFileHandler;
import com.evandev.spicedcider.config.LoggerNamePatternSelector;
import com.evandev.spicedcider.config.Reconfigurator;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.namingunconvention.RandomNameGenerator;
import com.evandev.spicedcider.networking.ChunkOffsetsPayload;
import com.evandev.spicedcider.registry.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.util.PluginRegistry;

import java.io.IOException;
import java.net.URI;

@Mod(SpicedCider.MOD_ID)
@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class SpicedCider {
    public static final String MOD_ID = "spicedcider";
    public static final Logger LOGGER = LogManager.getLogger("Spiced Cider");
    public static final RandomNameGenerator RANDOM_NAME_GENERATOR = new RandomNameGenerator();

    /**
     * An arbitrary unique identifier to be passed to Log4j when loading our
     * {@link LoggerNamePatternSelector} plugin
     */
    public static final long BUNDLE_ID = 54321;
    public static ClassLoader CLASSLOADER;

    public SpicedCider(IEventBus modEventBus, ModContainer modContainer) {
        CLASSLOADER = SpicedCider.class.getClassLoader();

        modContainer.registerConfig(ModConfig.Type.STARTUP, SpicedCiderConfig.STARTUP_SPEC);
        modContainer.registerConfig(ModConfig.Type.COMMON, SpicedCiderConfig.COMMON_SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, SpicedCiderConfig.CLIENT_SPEC);

        modContainer.registerExtensionPoint(IConfigScreenFactory.class, (mc, screen) -> SpicedCiderConfigScreen.create(screen));

        ModEntityTypes.ENTITY_TYPES.register(modEventBus);
        ModEntityTypes.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModAttachments.ATTACHMENT_TYPES.register(modEventBus);
        ModAttributes.ATTRIBUTES.register(modEventBus);
        ModArmorMaterials.ARMOR_MATERIALS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
        ModSounds.SOUNDS.register(modEventBus);
        ModParticleTypes.PARTICLE_TYPES.register(modEventBus);
        ModLootModifiers.LOOT_MODIFIER_SERIALIZERS.register(modEventBus);

        modEventBus.addListener(this::registerPayloads);
        NeoForge.EVENT_BUS.addListener(this::onTagsUpdated);

        if (SpicedCiderConfig.STARTUP.blockBoxWoodVariants.get()
                && ModList.get().isLoaded("blockbox")
                && ModList.get().isLoaded("everycomp")
                && ModList.get().isLoaded("moonlight")) {
            BlockBoxEveryCompatLoader.register(MOD_ID);
        }

        LOGGER.info("Starting Log4j reconfiguration.");
        loadPlugin();
        URI newConfigUri = ConfigFileHandler.getOrCreateDefaultConfigFile();

        try {
            Reconfigurator.reconfigureWithUri(newConfigUri);
        } catch (UnsupportedOperationException | IOException e) {
            LOGGER.error("Failed to reconfigure Log4j:", e);
        }
        LOGGER.info("Finished Log4j reconfiguration.");
    }

    /**
     * Prompts Log4j to scan for our {@link LoggerNamePatternSelector} plugin.
     */
    public static void loadPlugin() {
        PluginRegistry.getInstance().loadFromBundle(BUNDLE_ID, CLASSLOADER);
    }

    @SubscribeEvent
    public static void modifyVanillaAttributes(EntityAttributeModificationEvent event) {
        if (!SpicedCiderConfig.STARTUP.skeletonHealthNerf.get()) return;

        event.add(EntityType.SKELETON, Attributes.MAX_HEALTH, 12.0D);
        event.add(EntityType.STRAY, Attributes.MAX_HEALTH, 12.0D);
    }

    @SubscribeEvent
    public static void buildContents(BuildCreativeModeTabContentsEvent event) {
        CreativeModeTab.TabVisibility vis = CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS;
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.insertAfter(new ItemStack(Items.FISHING_ROD), new ItemStack(ModItems.GRAPPLING_HOOK.get()), vis);
            event.insertAfter(new ItemStack(ModItems.GRAPPLING_HOOK.get()), new ItemStack(ModItems.STICKY_GRAPPLING_HOOK.get()), vis);
            event.insertAfter(new ItemStack(Items.FLINT_AND_STEEL), new ItemStack(ModItems.FIRE_STRIKER.get()), vis);
            event.insertAfter(new ItemStack(Items.LEAD), new ItemStack(ModItems.RUBBER_CABLE.get()), vis);
        }
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.insertAfter(new ItemStack(Items.IRON_BLOCK), new ItemStack(ModItems.CAST_IRON_BLOCK.get()), vis);
        }
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.insertAfter(new ItemStack(Items.GOLD_NUGGET), new ItemStack(ModItems.CAST_IRON_NUGGET.get()), vis);
            event.insertAfter(new ItemStack(Items.IRON_INGOT), new ItemStack(ModItems.CAST_IRON_INGOT.get()), vis);
            event.insertAfter(new ItemStack(ModItems.CAST_IRON_INGOT.get()), new ItemStack(ModItems.CAST_IRON_SHEET.get()), vis);
            event.insertAfter(new ItemStack(ModItems.CAST_IRON_SHEET.get()), new ItemStack(ModItems.BLAST_PROOF_PLATING.get()), vis);
        }
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.insertAfter(new ItemStack(Items.NETHERITE_AXE), new ItemStack(ModItems.INFERNITE_CLEAVER.get()), vis);
            event.insertAfter(new ItemStack(Items.IRON_BOOTS), new ItemStack(ModItems.MISCHIEF_HELMET.get()), vis);
            event.insertAfter(new ItemStack(ModItems.MISCHIEF_HELMET.get()), new ItemStack(ModItems.MISCHIEF_CHESTPLATE.get()), vis);
            event.insertAfter(new ItemStack(ModItems.MISCHIEF_CHESTPLATE.get()), new ItemStack(ModItems.MISCHIEF_LEGGINGS.get()), vis);
            event.insertAfter(new ItemStack(ModItems.MISCHIEF_LEGGINGS.get()), new ItemStack(ModItems.MISCHIEF_BOOTS.get()), vis);
        }
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(MOD_ID);

        registrar.playToClient(
                ChunkOffsetsPayload.TYPE,
                ChunkOffsetsPayload.CODEC,
                (payload, context) -> context.enqueueWork(() -> ClientOffsetCache.receive(payload.chunk(), payload.entries()))
        );
    }

    private void onTagsUpdated(TagsUpdatedEvent event) {
        SupportOffsets.onTagsUpdated();
    }
}