package com.evandev.spicedcider;

import com.evandev.spicedcider.command.SpicedCiderQueryCommand;
import com.evandev.spicedcider.command.SpicedCiderStructureCommand;
import com.evandev.spicedcider.config.ConfigFileHandler;
import com.evandev.spicedcider.config.LoggerNamePatternSelector;
import com.evandev.spicedcider.config.Reconfigurator;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.integration.yacl.SpicedCiderConfigScreen;
import com.evandev.spicedcider.namingunconvention.RandomNameGenerator;
import com.evandev.spicedcider.registry.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.util.PluginRegistry;

import java.io.IOException;
import java.net.URI;

@Mod(SpicedCider.MOD_ID)
@EventBusSubscriber(modid = SpicedCider.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
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

        modContainer.registerConfig(ModConfig.Type.COMMON, SpicedCiderConfig.COMMON_SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, SpicedCiderConfig.CLIENT_SPEC);

        modContainer.registerExtensionPoint(IConfigScreenFactory.class, (mc, screen) -> SpicedCiderConfigScreen.create(screen));

        NeoForge.EVENT_BUS.addListener(this::registerCommands);
        ModEntityTypes.ENTITY_TYPES.register(modEventBus);
        ModEntityTypes.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModAttachments.ATTACHMENT_TYPES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModRecipeTypes.RECIPE_TYPES.register(modEventBus);
        ModRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
        ModSounds.SOUNDS.register(modEventBus);

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
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.WORKSTONE.get(),
                (be, context) -> be.getInventory()
        );
    }

    @SubscribeEvent
    public static void modifyVanillaAttributes(EntityAttributeModificationEvent event) {
        event.add(EntityType.SKELETON, Attributes.MAX_HEALTH, 12.0D);
        event.add(EntityType.STRAY, Attributes.MAX_HEALTH, 12.0D);
    }

    @SubscribeEvent
    public static void buildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.FLINT_HAMMER);
            event.accept(ModItems.IRON_HAMMER);
            event.accept(ModItems.GOLDEN_HAMMER);
            event.accept(ModItems.DIAMOND_HAMMER);
            event.accept(ModItems.NETHERITE_HAMMER);
        }
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModItems.WORKSTONE_ITEM);
        }
    }

    private void registerCommands(RegisterCommandsEvent event) {
        SpicedCiderStructureCommand.register(event.getDispatcher(), event.getBuildContext());
        SpicedCiderQueryCommand.register(event.getDispatcher(), event.getBuildContext());
    }
}