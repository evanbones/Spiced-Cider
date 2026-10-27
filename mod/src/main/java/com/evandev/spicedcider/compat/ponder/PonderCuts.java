package com.evandev.spicedcider.compat.ponder;

import com.evandev.spicedcider.mixin.ponder.PonderSceneAccessor;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.instruction.DisplayWorldSectionInstruction;
import net.createmod.ponder.foundation.instruction.PonderInstruction;
import net.createmod.ponder.foundation.instruction.TextInstruction;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public final class PonderCuts {

    private static final Map<ResourceLocation, Integer> CUT_AT_TEXT = Map.of(
            ResourceLocation.fromNamespaceAndPath("create", "stock_ticker"), 10
    );

    public static PonderScene apply(PonderScene scene) {
        PonderSceneAccessor accessor = (PonderSceneAccessor) scene;
        Integer cutAt = CUT_AT_TEXT.get(accessor.spicedcider$getSceneId());
        if (cutAt == null) return scene;
        List<PonderInstruction> schedule = accessor.spicedcider$getSchedule();
        int texts = 0;
        for (int i = 0; i < schedule.size(); i++) {
            if (!(schedule.get(i) instanceof TextInstruction) || ++texts < cutAt) continue;
            int start = i;
            while (start > 0 && schedule.get(start - 1) instanceof DisplayWorldSectionInstruction) {
                start--;
            }
            schedule.subList(start, schedule.size()).clear();
            break;
        }
        return scene;
    }
}
