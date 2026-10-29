package com.evandev.spicedcider.mixin.create;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.simibubi.create.content.fluids.FlowSource;
import com.simibubi.create.content.fluids.PipeConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

@IfModLoaded("create")
@Mixin(value = PipeConnection.class, remap = false)
public interface PipeConnectionSourceAccessor {

    @Accessor("source")
    Optional<FlowSource> spicedcider$source();
}
