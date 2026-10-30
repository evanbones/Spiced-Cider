package com.evandev.spicedcider.mixin.vanity.client;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import tech.thatgravyboat.vanity.client.components.list.StylesListWidget;

@IfModLoaded("vanity")
@Mixin(value = StylesListWidget.class, remap = false)
public interface StylesListWidgetAccessor {

    @Accessor("oldHash")
    void spicedcider$setOldHash(int hash);
}
