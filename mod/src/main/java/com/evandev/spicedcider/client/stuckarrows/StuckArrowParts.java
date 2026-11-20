package com.evandev.spicedcider.client.stuckarrows;

import com.evandev.spicedcider.content.stuckarrows.StuckArrows;
import com.evandev.spicedcider.mixin.minecraft.client.AgeableListModelAccessor;
import com.evandev.spicedcider.mixin.minecraft.client.ModelPartAccessor;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class StuckArrowParts {
    private static final String[] LIMBS = {"leg", "arm", "wing", "tail", "fin", "foot", "paw", "flipper"};

    public static int classify(EntityModel<?> model, ModelPart part) {
        if (model instanceof HumanoidModel<?> humanoid) {
            if (part == humanoid.head || part == humanoid.hat) return StuckArrows.HEAD;
            if (part == humanoid.body || (humanoid instanceof PlayerModel<?> player && part == player.jacket)) return StuckArrows.BODY;
            return StuckArrows.LIMB;
        }
        if (model instanceof AgeableListModel<?> ageable) {
            for (ModelPart head : ((AgeableListModelAccessor) ageable).spicedcider$headParts()) {
                if (head == part || path(head, part, new ArrayList<>()) != null) return StuckArrows.HEAD;
            }
        }
        ModelPart root = model instanceof HierarchicalModel<?> hierarchical ? hierarchical.root() : null;
        if (root == null) return StuckArrows.BODY;
        List<String> names = path(root, part, new ArrayList<>());
        if (names == null) return StuckArrows.BODY;
        for (String name : names) {
            String lower = name.toLowerCase(Locale.ROOT);
            if (lower.contains("head") || lower.contains("skull")) return StuckArrows.HEAD;
        }
        for (String name : names) {
            String lower = name.toLowerCase(Locale.ROOT);
            for (String limb : LIMBS) {
                if (lower.contains(limb)) return StuckArrows.LIMB;
            }
        }
        return StuckArrows.BODY;
    }

    @Nullable
    private static List<String> path(ModelPart from, ModelPart target, List<String> names) {
        for (Map.Entry<String, ModelPart> child : ((ModelPartAccessor) (Object) from).spicedcider$children().entrySet()) {
            names.add(child.getKey());
            if (child.getValue() == target || path(child.getValue(), target, names) != null) return names;
            names.removeLast();
        }
        return null;
    }
}
