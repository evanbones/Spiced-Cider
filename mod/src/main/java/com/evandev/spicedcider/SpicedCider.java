package com.evandev.spicedcider;

import com.evandev.spicedcider.blockgrid.ClientOffsetCache;
import com.evandev.spicedcider.blockgrid.SupportOffsets;
import com.evandev.spicedcider.compat.continuity.ContinuityLazyModelWrapping;
import com.evandev.spicedcider.compat.everycompat.BlockBoxEveryCompatLoader;
import com.evandev.spicedcider.compat.infernalexp.InfernalExpansionCompat;
import com.evandev.spicedcider.compat.tide.TideCompat;
import com.evandev.spicedcider.compat.yacl.ConfigScreenLoader;
import com.evandev.spicedcider.config.LogReconfiguration;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.namingunconvention.RandomNameGenerator;
import com.evandev.spicedcider.networking.ChunkOffsetsPayload;
import com.evandev.spicedcider.networking.VoidTrailPayload;
import com.evandev.spicedcider.registry.*;
import com.evandev.spicedcider.voidwater.client.ClientVoidTrails;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(SpicedCider.MOD_ID)
@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class SpicedCider {
    public static final String MOD_ID = "spicedcider";
    public static final Logger LOGGER = LogManager.getLogger("Spiced Cider");
    public static final RandomNameGenerator RANDOM_NAME_GENERATOR = new RandomNameGenerator();

    public SpicedCider(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.STARTUP, SpicedCiderConfig.STARTUP_SPEC);
        modContainer.registerConfig(ModConfig.Type.COMMON, SpicedCiderConfig.COMMON_SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, SpicedCiderConfig.CLIENT_SPEC);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            ConfigScreenLoader.register(modContainer);
            if (ModList.get().isLoaded("continuity") && ModList.get().isLoaded("modernfix")) {
                ContinuityLazyModelWrapping.register();
            }
        }

        ModEntityTypes.ENTITY_TYPES.register(modEventBus);
        ModEntityTypes.ITEMS.register(modEventBus);
        ModBuildingBlocks.register();
        ModBlocks.BLOCKS.register(modEventBus);
        ModAttachments.ATTACHMENT_TYPES.register(modEventBus);
        ModAttributes.ATTRIBUTES.register(modEventBus);
        ModArmorMaterials.ARMOR_MATERIALS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
        ModSounds.SOUNDS.register(modEventBus);
        ModParticleTypes.PARTICLE_TYPES.register(modEventBus);
        ModLootModifiers.LOOT_MODIFIER_SERIALIZERS.register(modEventBus);
        ModDataComponents.DATA_COMPONENTS.register(modEventBus);

        modEventBus.addListener(this::registerPayloads);
        NeoForge.EVENT_BUS.addListener(this::onTagsUpdated);

        if (SpicedCiderConfig.STARTUP.blockBoxWoodVariants.get()
                && ModList.get().isLoaded("blockbox")
                && ModList.get().isLoaded("everycomp")
                && ModList.get().isLoaded("moonlight")) {
            BlockBoxEveryCompatLoader.register(MOD_ID);
        }

        LogReconfiguration.run();
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
            if (TideCompat.isLoaded()) {
                event.insertAfter(new ItemStack(Items.FISHING_ROD), new ItemStack(ModItems.GRAPPLING_HOOK.get()), vis);
                event.insertAfter(new ItemStack(ModItems.GRAPPLING_HOOK.get()), new ItemStack(ModItems.STICKY_GRAPPLING_HOOK.get()), vis);
            }
            event.insertAfter(new ItemStack(Items.FLINT_AND_STEEL), new ItemStack(ModItems.FIRE_STRIKER.get()), vis);
            event.insertAfter(new ItemStack(Items.LEAD), new ItemStack(ModItems.RUBBER_CABLE.get()), vis);
        }
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.insertAfter(new ItemStack(Items.IRON_BLOCK), new ItemStack(ModItems.CAST_IRON_BLOCK.get()), vis);
            event.insertAfter(new ItemStack(Items.HONEYCOMB_BLOCK), new ItemStack(ModItems.TALLOW_BLOCK.get()), vis);
            ModBuildingBlocks.ITEMS.values().forEach(item -> event.accept(item.get(), vis));
            event.accept(ModItems.GLOWSTONE_BRICKS.get(), vis);
            event.accept(ModItems.GLOWSTONE_BRICK_STAIRS.get(), vis);
            event.accept(ModItems.GLOWSTONE_BRICK_SLAB.get(), vis);
            event.accept(ModItems.GLOWSTONE_BRICK_WALL.get(), vis);
            event.accept(ModItems.CHISELED_GLOWSTONE_BRICKS.get(), vis);
            event.accept(ModItems.POLISHED_GLOWSTONE_PILLAR.get(), vis);
        }
        if (event.getTabKey() == CreativeModeTabs.COLORED_BLOCKS) {
            for (DyeColor color : DyeColor.values()) {
                ItemStack stairs = new ItemStack(ModItems.WOOL_STAIRS.get(color).get());
                event.insertAfter(new ItemStack(ModBlocks.wool(color)), stairs, vis);
                event.insertAfter(stairs, new ItemStack(ModItems.WOOL_SLABS.get(color).get()), vis);
            }
        }
        if (ModItems.POLISHED_GLOWSTONE_STAIRS != null && event.getTabKey().location().equals(InfernalExpansionCompat.TAB)) {
            ItemStack stairs = new ItemStack(ModItems.POLISHED_GLOWSTONE_STAIRS.get());
            ItemStack slab = new ItemStack(ModItems.POLISHED_GLOWSTONE_SLAB.get());
            event.insertAfter(new ItemStack(InfernalExpansionCompat.polishedGlowstone()), stairs, vis);
            event.insertAfter(stairs, slab, vis);
            event.insertAfter(slab, new ItemStack(ModItems.POLISHED_GLOWSTONE_WALL.get()), vis);
        }
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.insertAfter(new ItemStack(Items.GOLD_NUGGET), new ItemStack(ModItems.CAST_IRON_NUGGET.get()), vis);
            event.insertAfter(new ItemStack(Items.IRON_INGOT), new ItemStack(ModItems.CAST_IRON_INGOT.get()), vis);
            event.insertAfter(new ItemStack(ModItems.CAST_IRON_INGOT.get()), new ItemStack(ModItems.CAST_IRON_SHEET.get()), vis);
            event.insertAfter(new ItemStack(ModItems.CAST_IRON_SHEET.get()), new ItemStack(ModItems.BLAST_PROOF_PLATING.get()), vis);
            event.insertAfter(new ItemStack(Items.HONEYCOMB), new ItemStack(ModItems.TALLOW.get()), vis);
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

        registrar.playToClient(
                VoidTrailPayload.TYPE,
                VoidTrailPayload.CODEC,
                (payload, context) -> context.enqueueWork(() -> ClientVoidTrails.receive(payload))
        );
    }

    private void onTagsUpdated(TagsUpdatedEvent event) {
        SupportOffsets.onTagsUpdated();
    }
}