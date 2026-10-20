package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.compat.farmersdelight.FarmersDelightCompat;
import com.evandev.spicedcider.compat.vanillabackport.VanillaBackportCompat;
import com.evandev.spicedcider.content.item.ElectrumArmorItem;
import com.evandev.spicedcider.content.item.MetalShieldItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class ModTierItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpicedCider.MOD_ID);
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, SpicedCider.MOD_ID);

    public static final TagKey<Item> STEEL_INGOTS = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "ingots/steel"));
    public static final TagKey<Item> ELECTRUM_INGOTS = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "ingots/electrum"));

    public static final Tier STEEL = tier(1000, 7.0F, 3.0F, BlockTags.INCORRECT_FOR_IRON_TOOL, 11, STEEL_INGOTS);
    public static final Tier ELECTRUM = tier(4000, 24.0F, 3.5F, BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 18, ELECTRUM_INGOTS);

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> ELECTRUM_ARMOR = ARMOR_MATERIALS.register("electrum", () -> new ArmorMaterial(
            Map.of(
                    ArmorItem.Type.BOOTS, 3,
                    ArmorItem.Type.LEGGINGS, 7,
                    ArmorItem.Type.CHESTPLATE, 8,
                    ArmorItem.Type.HELMET, 3
            ),
            18,
            SoundEvents.ARMOR_EQUIP_GOLD,
            () -> Ingredient.of(ELECTRUM_INGOTS),
            List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "electrum"))),
            2.0F,
            0.0F
    ));

    public static final DeferredItem<Item> STEEL_HAMMER = hammer("steel_hammer", STEEL, 6.0F, -3.1F);
    public static final DeferredItem<Item> STEEL_SHIELD = ITEMS.register("steel_shield",
            () -> new MetalShieldItem(STEEL_INGOTS, new Item.Properties().durability(1500)));

    public static final DeferredItem<Item> ELECTRUM_PICKAXE = ITEMS.register("electrum_pickaxe",
            () -> new PickaxeItem(ELECTRUM, new Item.Properties().attributes(PickaxeItem.createAttributes(ELECTRUM, 1.0F, -2.8F))));
    public static final DeferredItem<Item> ELECTRUM_AXE = ITEMS.register("electrum_axe",
            () -> new AxeItem(ELECTRUM, new Item.Properties().attributes(AxeItem.createAttributes(ELECTRUM, 5.0F, -3.0F))));
    public static final DeferredItem<Item> ELECTRUM_SHOVEL = ITEMS.register("electrum_shovel",
            () -> new ShovelItem(ELECTRUM, new Item.Properties().attributes(ShovelItem.createAttributes(ELECTRUM, 1.5F, -3.0F))));
    public static final DeferredItem<Item> ELECTRUM_HOE = ITEMS.register("electrum_hoe",
            () -> new HoeItem(ELECTRUM, new Item.Properties().attributes(HoeItem.createAttributes(ELECTRUM, -3.5F, 0.0F))));
    public static final DeferredItem<Item> ELECTRUM_KNIFE = ITEMS.register("electrum_knife",
            () -> FarmersDelightCompat.createKnife(ELECTRUM, new Item.Properties()));
    public static final DeferredItem<Item> ELECTRUM_HAMMER = hammer("electrum_hammer", ELECTRUM, 5.0F, -3.0F);
    public static final DeferredItem<Item> ELECTRUM_SHIELD = ITEMS.register("electrum_shield",
            () -> new MetalShieldItem(ELECTRUM_INGOTS, new Item.Properties().durability(2500)));
    public static final DeferredItem<Item> ELECTRUM_LANCE = ITEMS.register("electrum_lance",
            () -> VanillaBackportCompat.createLance(ELECTRUM,
                    ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "electrum_lance_reach"), new Item.Properties()));

    public static final DeferredItem<Item> ELECTRUM_HELMET = armor("electrum_helmet", ArmorItem.Type.HELMET);
    public static final DeferredItem<Item> ELECTRUM_CHESTPLATE = armor("electrum_chestplate", ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<Item> ELECTRUM_LEGGINGS = armor("electrum_leggings", ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<Item> ELECTRUM_BOOTS = armor("electrum_boots", ArmorItem.Type.BOOTS);

    public static final List<DeferredItem<Item>> TOOLS = List.of(STEEL_HAMMER, ELECTRUM_SHOVEL, ELECTRUM_PICKAXE,
            ELECTRUM_AXE, ELECTRUM_HOE, ELECTRUM_KNIFE, ELECTRUM_HAMMER);
    public static final List<DeferredItem<Item>> COMBAT = List.of(STEEL_SHIELD, ELECTRUM_SHIELD, ELECTRUM_LANCE,
            ELECTRUM_HELMET, ELECTRUM_CHESTPLATE, ELECTRUM_LEGGINGS, ELECTRUM_BOOTS);

    public static void register(IEventBus modEventBus) {
        ARMOR_MATERIALS.register(modEventBus);
        ITEMS.register(modEventBus);
        modEventBus.addListener(ModTierItems::buildContents);
    }

    private static void buildContents(BuildCreativeModeTabContentsEvent event) {
        CreativeModeTab.TabVisibility vis = CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS;
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            TOOLS.forEach(item -> event.accept(item.get(), vis));
        }
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            COMBAT.forEach(item -> event.accept(item.get(), vis));
        }
    }

    private static DeferredItem<Item> hammer(String name, Tier tier, float damage, float speed) {
        return ITEMS.register(name, () -> new DiggerItem(tier, BlockTags.MINEABLE_WITH_PICKAXE,
                new Item.Properties().attributes(DiggerItem.createAttributes(tier, damage, speed))));
    }

    private static DeferredItem<Item> armor(String name, ArmorItem.Type type) {
        return ITEMS.register(name, () -> new ElectrumArmorItem(ELECTRUM_ARMOR, type,
                new Item.Properties().durability(type.getDurability(35))));
    }

    private static Tier tier(int uses, float speed, float damage, TagKey<Block> incorrect, int enchantment, TagKey<Item> repair) {
        return new Tier() {
            @Override
            public int getUses() {
                return uses;
            }

            @Override
            public float getSpeed() {
                return speed;
            }

            @Override
            public float getAttackDamageBonus() {
                return damage;
            }

            @Override
            public @NotNull TagKey<Block> getIncorrectBlocksForDrops() {
                return incorrect;
            }

            @Override
            public int getEnchantmentValue() {
                return enchantment;
            }

            @Override
            public @NotNull Ingredient getRepairIngredient() {
                return Ingredient.of(repair);
            }
        };
    }
}
