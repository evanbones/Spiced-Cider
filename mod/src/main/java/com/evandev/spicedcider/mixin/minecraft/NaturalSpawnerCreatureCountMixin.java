package com.evandev.spicedcider.mixin.minecraft;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.LocalMobCapCalculator;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(NaturalSpawner.class)
public class NaturalSpawnerCreatureCountMixin {

    @WrapOperation(method = "lambda$createState$2", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/Object2IntOpenHashMap;addTo(Ljava/lang/Object;I)I"))
    private static int spicedcider$countCreaturesNearPlayersOnly(Object2IntOpenHashMap<MobCategory> counts, Object category, int amount, Operation<Integer> original,
                                                                 @Local(argsOnly = true) LocalMobCapCalculator calculator, @Local(argsOnly = true) LevelChunk chunk) {
        if (category == MobCategory.CREATURE && ((LocalMobCapCalculatorAccessor) calculator).spicedcider$getPlayersNear(chunk.getPos()).isEmpty()) {
            return counts.getInt(category);
        }
        return original.call(counts, category, amount);
    }
}
