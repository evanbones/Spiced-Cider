package com.evandev.spicedcider.mixin.polytone;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.mehvahdjukaar.polytone.content.config.ConfigsManager;
import net.mehvahdjukaar.polytone.content.config.OptionHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Collection;
import java.util.List;

@IfModLoaded("polytone")
@Mixin(value = ConfigsManager.class, remap = false)
public interface ConfigsManagerAccessor {

    @Invoker("shownOptions")
    List<OptionHolder<?>> spicedcider$shownOptions();

    @Invoker("saveConfigsToDisk")
    void spicedcider$saveConfigsToDisk(Collection<OptionHolder<?>> edited);
}
