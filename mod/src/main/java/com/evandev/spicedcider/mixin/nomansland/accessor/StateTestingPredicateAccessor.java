package com.evandev.spicedcider.mixin.nomansland.accessor;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.StateTestingPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@IfModLoaded("nomansland")
@Mixin(StateTestingPredicate.class)
public interface StateTestingPredicateAccessor {

    @Accessor("offset")
    Vec3i spicedcider$getOffset();

    @Invoker("test")
    boolean spicedcider$testState(BlockState state);
}
