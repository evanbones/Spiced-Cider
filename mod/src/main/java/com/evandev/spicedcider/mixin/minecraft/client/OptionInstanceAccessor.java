package com.evandev.spicedcider.mixin.minecraft.client;

import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(OptionInstance.class)
public interface OptionInstanceAccessor<T> {

    @Accessor("caption")
    Component spicedcider$caption();

    @Accessor("tooltip")
    OptionInstance.TooltipSupplier<T> spicedcider$tooltip();

    @Accessor("initialValue")
    T spicedcider$initialValue();
}
