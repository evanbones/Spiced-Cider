package com.evandev.spicedcider.compat.bigbrain;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Animal;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Map;
import java.util.WeakHashMap;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class HerdFearDecay {
    public static final int FEAR_TICKS = 200;
    private static final ResourceLocation SAW_HUNT = ResourceLocation.fromNamespaceAndPath("bigbrain", "saw_hunt");
    private static AttachmentType<Integer> type;
    private static boolean resolved;
    private static final Map<Animal, Long> LAST_TICK = new WeakHashMap<>();

    @SuppressWarnings("unchecked")
    private static AttachmentType<Integer> type() {
        if (!resolved) {
            resolved = true;
            if (ModList.get().isLoaded("bigbrain")) {
                type = (AttachmentType<Integer>) NeoForgeRegistries.ATTACHMENT_TYPES.get(SAW_HUNT);
            }
        }
        return type;
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Animal animal) || animal.level().isClientSide()) return;
        AttachmentType<Integer> fear = type();
        if (fear == null || !animal.hasData(fear) || !SpicedCiderConfig.COMMON.calmerHerdPanic.get()) return;
        int left = animal.getData(fear);
        long now = animal.level().getGameTime();
        Long last = left > 0 ? LAST_TICK.put(animal, now) : LAST_TICK.remove(animal);
        if (left <= 0) return;
        long elapsed = last == null ? 1 : Math.max(1, now - last);
        animal.setData(fear, (int) Math.max(0, Math.min(left, FEAR_TICKS) - elapsed));
    }
}
