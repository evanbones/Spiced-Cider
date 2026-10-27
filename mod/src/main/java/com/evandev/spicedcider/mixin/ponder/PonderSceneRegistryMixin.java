package com.evandev.spicedcider.mixin.ponder;

import com.evandev.spicedcider.compat.ponder.PonderCuts;
import com.evandev.spicedcider.compat.ponder.PonderFilter;
import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.createmod.ponder.api.registration.StoryBoardEntry;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.registration.PonderSceneRegistry;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collection;
import java.util.Map;

@IfModLoaded("ponder")
@Mixin(value = PonderSceneRegistry.class, remap = false)
public class PonderSceneRegistryMixin {

    @Shadow
    @Final
    private Multimap<ResourceLocation, StoryBoardEntry> scenes;

    @ModifyReturnValue(method = "getRegisteredEntries", at = @At("RETURN"))
    private Collection<Map.Entry<ResourceLocation, StoryBoardEntry>> spicedcider$hideEntries(Collection<Map.Entry<ResourceLocation, StoryBoardEntry>> entries) {
        return PonderFilter.shownEntries(entries);
    }

    @ModifyReturnValue(method = "doScenesExistForId", at = @At("RETURN"))
    private boolean spicedcider$hideComponent(boolean exists, ResourceLocation id) {
        return exists && !PonderFilter.shown(scenes.get(id)).isEmpty();
    }

    @ModifyExpressionValue(method = "compile(Lnet/minecraft/resources/ResourceLocation;)Ljava/util/List;", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Multimap;get(Ljava/lang/Object;)Ljava/util/Collection;"))
    private Collection<StoryBoardEntry> spicedcider$hideScenes(Collection<StoryBoardEntry> entries) {
        return PonderFilter.shown(entries);
    }

    @ModifyReturnValue(method = "compileScene", at = @At("RETURN"))
    private static PonderScene spicedcider$cutScene(PonderScene scene) {
        return PonderCuts.apply(scene);
    }
}
