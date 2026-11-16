package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.content.advancement.WorkstoneProcessTrigger;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCriteria {
    public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS =
            DeferredRegister.create(Registries.TRIGGER_TYPE, SpicedCider.MOD_ID);

    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> FIELD_GUIDE_SCAN =
            TRIGGERS.register("field_guide_scan", PlayerTrigger::new);

    public static final DeferredHolder<CriterionTrigger<?>, WorkstoneProcessTrigger> WORKSTONE_PROCESS =
            TRIGGERS.register("workstone_process", WorkstoneProcessTrigger::new);

    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> CAMPFIRE_LIT =
            TRIGGERS.register("campfire_lit", PlayerTrigger::new);
}
