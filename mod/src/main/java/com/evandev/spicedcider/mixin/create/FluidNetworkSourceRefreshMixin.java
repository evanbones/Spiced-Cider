package com.evandev.spicedcider.mixin.create;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.simibubi.create.content.fluids.FluidNetwork;
import com.simibubi.create.content.fluids.FlowSource;
import com.simibubi.create.content.fluids.PipeConnection;
import com.simibubi.create.foundation.ICapabilityProvider;
import net.createmod.catnip.math.BlockFace;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;
import java.util.function.Supplier;

@IfModLoaded("create")
@Mixin(value = FluidNetwork.class, remap = false)
public class FluidNetworkSourceRefreshMixin {

    @Shadow
    BlockFace start;

    @Shadow
    ICapabilityProvider<IFluidHandler> source;

    @Shadow
    @Nullable
    private PipeConnection get(BlockFace location) {
        throw new AssertionError();
    }

    @Shadow
    Supplier<ICapabilityProvider<IFluidHandler>> sourceSupplier;

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/ICapabilityProvider;getCapability()Ljava/lang/Object;"))
    private Object spicedcider$refreshInvalidatedSource(ICapabilityProvider<?> provider, Operation<Object> original) {
        Object capability = original.call(provider);
        if (capability != null || provider != this.source) {
            return capability;
        }
        ICapabilityProvider<IFluidHandler> fresh = this.sourceSupplier.get();
        if (fresh != null && fresh != provider) {
            this.source = fresh;
            return original.call(fresh);
        }
        PipeConnection connection = this.get(this.start);
        if (connection == null) {
            return null;
        }
        Optional<FlowSource> current = ((PipeConnectionSourceAccessor) connection).spicedcider$source();
        if (current.isEmpty()) {
            return null;
        }
        FlowSource flowSource = current.get();
        this.sourceSupplier = flowSource::provideHandler;
        ICapabilityProvider<IFluidHandler> rebound = flowSource.provideHandler();
        if (rebound == null) {
            return null;
        }
        this.source = rebound;
        return original.call(rebound);
    }
}
