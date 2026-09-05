package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.util.PetArmorDurability;
import com.evandev.spicedcider.util.PetArmorUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.List;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class PetArmorEvents {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void givePetArmorDurability(ModifyDefaultComponentsEvent event) {
        if (!SpicedCiderConfig.STARTUP.unifiedPetArmor.get()) return;

        List<Item> needsDurability = event.getAllItems()
                .filter(item -> item instanceof AnimalArmorItem armor
                        && armor.getBodyType() == AnimalArmorItem.BodyType.EQUESTRIAN
                        && !item.components().has(DataComponents.MAX_DAMAGE))
                .toList();

        for (Item item : needsDurability) {
            int durability = PetArmorDurability.bodyDurabilityFor(((AnimalArmorItem) item).getMaterial());
            event.modify(item, builder -> builder
                    .set(DataComponents.MAX_STACK_SIZE, 1)
                    .set(DataComponents.MAX_DAMAGE, durability)
                    .set(DataComponents.DAMAGE, 0));
        }
    }

    @SubscribeEvent
    public static void petArmorAbsorbsDamage(LivingDamageEvent.Pre event) {
        if (!SpicedCiderConfig.STARTUP.unifiedPetArmor.get()) return;

        LivingEntity pet = event.getEntity();
        if (!(pet instanceof AbstractHorse)) return;
        if (event.getNewDamage() <= 0.0F) return;
        if (!PetArmorUtil.canAbsorb(pet, event.getSource())) return;

        PetArmorUtil.absorb(pet, event.getOriginalDamage());
        event.setNewDamage(0.0F);
    }
}
