package com.evandev.spicedcider.content.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class WorkstoneProcessTrigger extends SimpleCriterionTrigger<WorkstoneProcessTrigger.TriggerInstance> {
    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, ItemStack input, ItemStack tool) {
        trigger(player, instance -> instance.matches(input, tool));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ItemPredicate> item,
                                  Optional<ItemPredicate> tool) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                ItemPredicate.CODEC.optionalFieldOf("item").forGetter(TriggerInstance::item),
                ItemPredicate.CODEC.optionalFieldOf("tool").forGetter(TriggerInstance::tool)
        ).apply(instance, TriggerInstance::new));

        public boolean matches(ItemStack input, ItemStack tool) {
            return item.map(predicate -> predicate.test(input)).orElse(true)
                    && this.tool.map(predicate -> predicate.test(tool)).orElse(true);
        }
    }
}
