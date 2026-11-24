package com.evandev.spicedcider.mixin.beams;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.syl.beams.compat.everycomp.BeamsEveryCompatModule;
import net.mehvahdjukaar.every_compat.misc.HardcodedBlockType;
import net.mehvahdjukaar.moonlight.api.events.AfterLanguageLoadEvent;
import net.mehvahdjukaar.moonlight.api.set.BlockType;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("beams")
@Mixin(value = BeamsEveryCompatModule.class, remap = false)
public class BeamsEveryCompatModuleMixin {

    @WrapOperation(method = "isEntryAlreadyRegistered", at = @At(value = "INVOKE", target = "Lnet/mehvahdjukaar/moonlight/api/set/BlockType;isVanilla()Z"))
    private boolean spicedcider$onlyRealVanillaWoods(BlockType type, Operation<Boolean> original) {
        return HardcodedBlockType.isKnownVanillaType(type);
    }

    @WrapOperation(method = "addTranslations", at = @At(value = "INVOKE", target = "Lnet/mehvahdjukaar/moonlight/api/set/wood/WoodType;getReadableName()Ljava/lang/String;"))
    private String spicedcider$translatedWoodName(WoodType type, Operation<String> original, @Local(argsOnly = true) AfterLanguageLoadEvent lang) {
        String name = lang.getEntry(type.getTranslationKey());
        return name != null ? name : original.call(type);
    }
}
