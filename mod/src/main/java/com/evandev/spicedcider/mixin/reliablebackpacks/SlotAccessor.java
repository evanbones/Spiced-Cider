package com.evandev.spicedcider.mixin.reliablebackpacks;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@IfModLoaded("reliable_backpacks")
@Mixin(Slot.class)
public interface SlotAccessor {
    @Mutable
    @Accessor("y")
    void spicedcider$setY(int y);
}
