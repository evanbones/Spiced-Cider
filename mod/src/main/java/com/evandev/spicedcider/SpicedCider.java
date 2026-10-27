package com.evandev.spicedcider;

import com.evandev.spicedcider.compat.continuity.ContinuityLazyModelWrapping;
import com.evandev.spicedcider.compat.voxy.VoxyFog;
import com.evandev.spicedcider.compat.wherewindsblow.WhereWindsBlowLazyModelWrapping;
import com.evandev.spicedcider.compat.everycompat.BlockBoxEveryCompatLoader;
import com.evandev.spicedcider.compat.infernalexp.InfernalExpansionCompat;
import com.evandev.spicedcider.compat.embersandicicles.IceCubes;
import com.evandev.spicedcider.compat.iteminteractions.ItemInteractionsCompat;
import com.evandev.spicedcider.compat.tide.TideCompat;
import com.evandev.spicedcider.compat.yacl.ConfigScreenLoader;
import com.evandev.spicedcider.config.LogReconfiguration;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.content.block.LatexCauldronBlock;
import com.evandev.spicedcider.content.item.BalloonItem;
import com.evandev.spicedcider.content.handler.SpawnControlEvents;
import com.evandev.spicedcider.content.handler.SputterEvents;
import com.evandev.spicedcider.namingunconvention.RandomNameGenerator;
import com.evandev.spicedcider.networking.CloudCoverPayload;
import com.evandev.spicedcider.networking.GenerationEdgePayload;
import com.evandev.spicedcider.networking.LodChunkPayload;
import com.evandev.spicedcider.networking.LodHelloPayload;
import com.evandev.spicedcider.lodgen.LodGen;
import com.evandev.spicedcider.lodgen.client.LodClient;
import com.evandev.spicedcider.networking.VoidTrailPayload;
import com.evandev.spicedcider.registry.*;
import com.evandev.spicedcider.util.ItemAbilityItemsCache;
import com.evandev.spicedcider.voidwater.client.ClientVoidTrails;
import com.evandev.spicedcider.weather.client.CloudCoverClient;
import net.minecraft.core.dispenser.BoatDispenseItemBehavior;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DyeColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.stream.Stream;

@Mod(SpicedCider.MOD_ID)
@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class SpicedCider {
    public static final String MOD_ID = "spicedcider";
    public static final Logger LOGGER = LogManager.getLogger("Spiced Cider");
    public static final RandomNameGenerator RANDOM_NAME_GENERATOR = new RandomNameGenerator();
    private static final List<DyeColor> BALLOON_ORDER = List.of(
            DyeColor.WHITE, DyeColor.LIGHT_GRAY, DyeColor.GRAY, DyeColor.BLACK, DyeColor.BROWN, DyeColor.RED,
            DyeColor.ORANGE, DyeColor.YELLOW, DyeColor.LIME, DyeColor.GREEN, DyeColor.CYAN, DyeColor.LIGHT_BLUE,
            DyeColor.BLUE, DyeColor.PURPLE, DyeColor.MAGENTA, DyeColor.PINK);

    public SpicedCider(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.STARTUP, SpicedCiderConfig.STARTUP_SPEC);
        modContainer.registerConfig(ModConfig.Type.COMMON, SpicedCiderConfig.COMMON_SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, SpicedCiderConfig.CLIENT_SPEC);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            ConfigScreenLoader.register(modContainer);
            if (ModList.get().isLoaded("continuity") && ModList.get().isLoaded("modernfix")) {
                ContinuityLazyModelWrapping.register();
            }
            if (ModList.get().isLoaded("where_winds_blow") && ModList.get().isLoaded("modernfix")) {
                WhereWindsBlowLazyModelWrapping.register();
            }
        }

        ModEntityTypes.ENTITY_TYPES.register(modEventBus);
        ModEntityTypes.ITEMS.register(modEventBus);
        ModBuildingBlocks.register();
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModTreeFeatures.TRUNK_PLACERS.register(modEventBus);
        ModTreeFeatures.TREE_DECORATORS.register(modEventBus);
        ModTreeFeatures.FEATURES.register(modEventBus);
        ModTreeFeatures.PLACEMENT_MODIFIERS.register(modEventBus);
        ModAttachments.ATTACHMENT_TYPES.register(modEventBus);
        ModAttributes.ATTRIBUTES.register(modEventBus);
        ModArmorMaterials.ARMOR_MATERIALS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModDrinkware.register(modEventBus);
        ModTierItems.register(modEventBus);
        ModRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
        ModSounds.SOUNDS.register(modEventBus);
        ModMobEffects.MOB_EFFECTS.register(modEventBus);
        ModParticleTypes.PARTICLE_TYPES.register(modEventBus);
        ModLootModifiers.LOOT_MODIFIER_SERIALIZERS.register(modEventBus);
        ModDataComponents.DATA_COMPONENTS.register(modEventBus);
        if (ModList.get().isLoaded("iteminteractions")) ItemInteractionsCompat.register(modEventBus);
        ModFluidTypes.FLUID_TYPES.register(modEventBus);
        ModFluids.FLUIDS.register(modEventBus);

        modEventBus.addListener(this::registerPayloads);
        modEventBus.addListener(SpicedCider::addSignBlocks);
        modEventBus.addListener(SpicedCider::commonSetup);
        modEventBus.addListener(SpawnControlEvents::registerSpawnPlacements);
        modEventBus.addListener(SputterEvents::registerAttributes);
        modEventBus.addListener(SputterEvents::addToCreativeTab);
        NeoForge.EVENT_BUS.addListener(this::onTagsUpdated);

        if (SpicedCiderConfig.STARTUP.blockBoxWoodVariants.get()
                && ModList.get().isLoaded("blockbox")
                && ModList.get().isLoaded("everycomp")
                && ModList.get().isLoaded("moonlight")) {
            BlockBoxEveryCompatLoader.register(MOD_ID);
        }

        LogReconfiguration.run();
    }

    private static void addSignBlocks(BlockEntityTypeAddBlocksEvent event) {
        if (ModBlocks.TWISTED_SIGN == null) return;
        event.modify(BlockEntityType.SIGN, ModBlocks.TWISTED_SIGN.get(), ModBlocks.TWISTED_WALL_SIGN.get());
        event.modify(BlockEntityType.HANGING_SIGN, ModBlocks.TWISTED_HANGING_SIGN.get(), ModBlocks.TWISTED_WALL_HANGING_SIGN.get());
    }

    private static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(TideCompat::registerFishingMediums);
        event.enqueueWork(IceCubes::registerDispenserBehavior);
        event.enqueueWork(LatexCauldronBlock::registerInteractions);
        event.enqueueWork(() -> ModItems.BALLOONS.values().forEach(balloon -> DispenserBlock.registerBehavior(balloon.get(), new BalloonItem.DispenseBehavior())));
        event.enqueueWork(() -> ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.GLOAMWOOD_SAPLING.getId(), ModBlocks.POTTED_GLOAMWOOD_SAPLING));
        event.enqueueWork(() -> ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.CACAO_SAPLING.getId(), ModBlocks.POTTED_CACAO_SAPLING));
        if (ModItems.TWISTED_BOAT == null) return;
        event.enqueueWork(() -> {
            Boat.Type twisted = ModBoatTypes.TWISTED.getValue();
            DispenserBlock.registerBehavior(ModItems.TWISTED_BOAT.get(), new BoatDispenseItemBehavior(twisted));
            DispenserBlock.registerBehavior(ModItems.TWISTED_CHEST_BOAT.get(), new BoatDispenseItemBehavior(twisted, true));
        });
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
            ItemStack previous = new ItemStack(ModItems.RUBBER_CABLE.get());
            for (DyeColor color : BALLOON_ORDER) {
                ItemStack balloon = new ItemStack(ModItems.BALLOONS.get(color).get());
                event.insertAfter(previous, balloon, vis);
                previous = balloon;
            }
            ItemStack jukebox = new ItemStack(ModItems.POCKET_JUKEBOX.get());
            event.insertBefore(new ItemStack(Items.MUSIC_DISC_13), jukebox, vis);
            for (DyeColor color : BALLOON_ORDER) {
                ItemStack dyed = new ItemStack(ModItems.POCKET_JUKEBOXES.get(color).get());
                event.insertBefore(new ItemStack(Items.MUSIC_DISC_13), dyed, vis);
            }
            if (ModItems.TWISTED_BOAT != null) {
                event.insertAfter(new ItemStack(Items.BAMBOO_CHEST_RAFT), new ItemStack(ModItems.TWISTED_BOAT.get()), vis);
                event.insertAfter(new ItemStack(ModItems.TWISTED_BOAT.get()), new ItemStack(ModItems.TWISTED_CHEST_BOAT.get()), vis);
            }
        }
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.WORM.get(), vis);
            event.accept(ModItems.GOLDEN_WORM.get(), vis);
            event.accept(ModItems.GEODE.get(), vis);
            event.accept(ModItems.DEEPSLATE_GEODE.get(), vis);
            event.accept(ModItems.BLACKSTONE_GEODE.get(), vis);
            event.accept(ModItems.LATEX_BOTTLE.get(), vis);
        }
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.insertAfter(new ItemStack(Items.MILK_BUCKET), new ItemStack(ModItems.LATEX_BUCKET.get()), vis);
        }
        if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
            event.insertAfter(new ItemStack(Items.ROOTED_DIRT), new ItemStack(ModItems.WORMY_DIRT.get()), vis);
            event.insertAfter(new ItemStack(Items.FLOWERING_AZALEA), new ItemStack(ModItems.GLOAMWOOD_SAPLING.get()), vis);
            event.accept(ModItems.DEPOSIT.get(), vis);
            event.accept(ModItems.DEEPSLATE_DEPOSIT.get(), vis);
            event.accept(ModItems.BLACKSTONE_DEPOSIT.get(), vis);
            event.accept(ModItems.WORM_CRATE.get(), vis);
            event.accept(ModItems.GOLDEN_WORM_CRATE.get(), vis);
            Stream.of(ModItems.CACAO_LOG, ModItems.CACAO_WOOD, ModItems.STRIPPED_CACAO_LOG, ModItems.STRIPPED_CACAO_WOOD).forEach(item -> event.accept(item.get(), vis));
            event.accept(ModItems.CACAO_LEAVES.get(), vis);
            event.accept(ModItems.CACAO_SAPLING.get(), vis);
            Stream.of(ModItems.DEEPSILT, ModItems.ALGAE, ModItems.ALGAL_SAND, ModItems.ALGAL_GRAVEL, ModItems.CHROMARINE, ModItems.ACHROMARINE, ModItems.SULFIDE,
                    ModItems.SULFIDE_VENT, ModItems.CALCITE_VENT, ModItems.TUBEWORM, ModItems.BUDDING_PRISMARINE, ModItems.SMALL_PRISMARINE_BUD,
                    ModItems.MEDIUM_PRISMARINE_BUD, ModItems.LARGE_PRISMARINE_BUD, ModItems.PRISMARINE_CLUSTER, ModItems.CHROMATIC_BRAMBLE,
                    ModItems.TALL_CHROMATIC_BRAMBLE, ModItems.ACHROMATIC_BRAMBLE, ModItems.TALL_ACHROMATIC_BRAMBLE).forEach(item -> event.accept(item.get(), vis));
        }
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS && ModItems.TWISTED_SIGN != null) {
            event.insertAfter(new ItemStack(Items.WARPED_HANGING_SIGN), new ItemStack(ModItems.TWISTED_SIGN.get()), vis);
            event.insertAfter(new ItemStack(ModItems.TWISTED_SIGN.get()), new ItemStack(ModItems.TWISTED_HANGING_SIGN.get()), vis);
        }
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.insertAfter(new ItemStack(Items.IRON_BLOCK), new ItemStack(ModItems.CAST_IRON_BLOCK.get()), vis);
            event.insertAfter(new ItemStack(Items.HONEYCOMB_BLOCK), new ItemStack(ModItems.TALLOW_BLOCK.get()), vis);
            Stream.of(ModItems.DRIED_DEEPSILT, ModItems.CUT_DRIED_DEEPSILT, ModItems.POLISHED_DRIED_DEEPSILT, ModItems.CHISELED_DRIED_DEEPSILT).forEach(item -> event.accept(item.get(), vis));
            ModBuildingBlocks.ITEMS.values().forEach(item -> event.accept(item.get(), vis));
            event.accept(ModItems.GLOWSTONE_BRICKS.get(), vis);
            event.accept(ModItems.GLOWSTONE_BRICK_STAIRS.get(), vis);
            event.accept(ModItems.GLOWSTONE_BRICK_SLAB.get(), vis);
            event.accept(ModItems.GLOWSTONE_BRICK_WALL.get(), vis);
            event.accept(ModItems.CHISELED_GLOWSTONE_BRICKS.get(), vis);
            event.accept(ModItems.POLISHED_GLOWSTONE_PILLAR.get(), vis);
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
                VoidTrailPayload.TYPE,
                VoidTrailPayload.CODEC,
                (payload, context) -> context.enqueueWork(() -> ClientVoidTrails.receive(payload))
        );
        registrar.playToClient(
                CloudCoverPayload.TYPE,
                CloudCoverPayload.CODEC,
                (payload, context) -> context.enqueueWork(() -> CloudCoverClient.receive(payload.cover(), payload.fast()))
        );
        registrar.playToClient(
                GenerationEdgePayload.TYPE,
                GenerationEdgePayload.CODEC,
                (payload, context) -> context.enqueueWork(() -> VoxyFog.receiveEdge(payload.distance()))
        );
        registrar.playToClient(
                LodChunkPayload.TYPE,
                LodChunkPayload.CODEC,
                (payload, context) -> context.enqueueWork(() -> LodClient.receive(payload))
        );
        registrar.playToServer(
                LodHelloPayload.TYPE,
                LodHelloPayload.CODEC,
                (payload, context) -> context.enqueueWork(() -> LodGen.hello((ServerPlayer) context.player(), payload.radius()))
        );
    }

    private void onTagsUpdated(TagsUpdatedEvent event) {
        ItemAbilityItemsCache.clear();
    }
}