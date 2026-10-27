package com.evandev.spicedcider.mixin.ponder;

import com.evandev.spicedcider.compat.ponder.PonderFilter;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.createmod.ponder.foundation.PonderTag;
import net.createmod.ponder.foundation.registration.PonderTagRegistry;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Set;

@IfModLoaded("ponder")
@Mixin(value = PonderTagRegistry.class, remap = false)
public abstract class PonderTagRegistryMixin {

    @ModifyReturnValue(method = "getItems(Lnet/minecraft/resources/ResourceLocation;)Ljava/util/Set;", at = @At("RETURN"))
    private Set<ResourceLocation> spicedcider$hideRemovedItems(Set<ResourceLocation> items) {
        return PonderFilter.shownComponents(items);
    }

    @ModifyReturnValue(method = "getTags", at = @At("RETURN"))
    private Set<PonderTag> spicedcider$hideRemovedComponent(Set<PonderTag> tags, ResourceLocation item) {
        return PonderFilter.isRemoved(item) ? Set.of() : tags;
    }

    @ModifyReturnValue(method = "getListedTags", at = @At("RETURN"))
    private List<PonderTag> spicedcider$hideEmptyTags(List<PonderTag> tags) {
        PonderTagRegistry self = (PonderTagRegistry) (Object) this;
        return tags.stream().filter(tag -> !self.getItems(tag).isEmpty()).toList();
    }
}
