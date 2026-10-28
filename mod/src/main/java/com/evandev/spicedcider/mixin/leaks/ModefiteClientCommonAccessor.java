package com.evandev.spicedcider.mixin.leaks;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Collection;

@IfModLoaded("modefite")
@Mixin(targets = "timmychips.modefiteitemdefinitions.ModefiteClientCommon", remap = false)
public interface ModefiteClientCommonAccessor {
    @Accessor("modelIds")
    static Collection<ResourceLocation> spicedcider$modelIds() {
        throw new AssertionError();
    }
}
