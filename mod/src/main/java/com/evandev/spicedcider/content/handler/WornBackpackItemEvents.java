package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Predicate;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class WornBackpackItemEvents {

    public static final TagKey<Item> TICKS_IN_WORN_BACKPACK = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "ticks_in_worn_backpack"));

    private static final Map<ServerPlayer, Map<ItemStack, ItemStack>> LENT = new WeakHashMap<>();

    public static ItemStack find(Player player, Predicate<ItemStack> filter) {
        ItemContainerContents contents = player.getItemBySlot(EquipmentSlot.CHEST).get(DataComponents.CONTAINER);
        if (contents == null) return ItemStack.EMPTY;
        for (ItemStack stack : contents.nonEmptyItems()) {
            if (!filter.test(stack)) continue;
            if (player instanceof ServerPlayer serverPlayer) {
                LENT.computeIfAbsent(serverPlayer, p -> new IdentityHashMap<>()).computeIfAbsent(stack, ItemStack::copy);
            }
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        ItemStack backpack = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemContainerContents contents = backpack.get(DataComponents.CONTAINER);
        if (contents != null && !player.isSpectator()) {
            for (ItemStack stack : contents.nonEmptyItems()) {
                if (stack.is(TICKS_IN_WORN_BACKPACK)) {
                    stack.inventoryTick(player.level(), player, -1, false);
                }
            }
        }
        if (player instanceof ServerPlayer serverPlayer) {
            Map<ItemStack, ItemStack> lent = LENT.remove(serverPlayer);
            if (lent != null && contents != null) writeBack(serverPlayer, backpack, contents, lent);
        }
    }

    private static void writeBack(ServerPlayer player, ItemStack backpack, ItemContainerContents contents, Map<ItemStack, ItemStack> lent) {
        boolean changed = false;
        for (Map.Entry<ItemStack, ItemStack> entry : lent.entrySet()) {
            if (!ItemStack.matches(entry.getKey(), entry.getValue())) {
                changed = true;
                break;
            }
        }
        if (!changed) return;
        NonNullList<ItemStack> items = NonNullList.withSize(contents.getSlots(), ItemStack.EMPTY);
        contents.copyInto(items);
        backpack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
        InventoryMenu menu = player.inventoryMenu;
        player.connection.send(new ClientboundContainerSetSlotPacket(menu.containerId, menu.incrementStateId(), InventoryMenu.ARMOR_SLOT_START + 1, backpack.copy()));
    }
}
