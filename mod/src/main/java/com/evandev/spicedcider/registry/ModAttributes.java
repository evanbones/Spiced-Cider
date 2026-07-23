package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, SpicedCider.MOD_ID);

    public static final DeferredHolder<Attribute, Attribute> EXPLOSIVE_DAMAGE_REDUCTION = ATTRIBUTES.register("explosive_damage_reduction",
            () -> new RangedAttribute("attribute.name.spicedcider.explosive_damage_reduction", 0.0D, 0.0D, 1.0D));
}
