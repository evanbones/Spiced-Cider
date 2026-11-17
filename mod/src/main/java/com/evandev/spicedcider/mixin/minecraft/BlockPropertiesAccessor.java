package com.evandev.spicedcider.mixin.minecraft;

import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockBehaviour.Properties.class)
public interface BlockPropertiesAccessor {

    @Accessor("isSuffocating")
    BlockBehaviour.StatePredicate spicedcider$getIsSuffocating();

    @Accessor("isViewBlocking")
    BlockBehaviour.StatePredicate spicedcider$getIsViewBlocking();
}
