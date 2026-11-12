package com.evandev.spicedcider.mixin.nomansland;

import com.evandev.spicedcider.util.BrokenItemUtil;
import com.farcr.nomansland.common.event.PathMakingEvents;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.spongepowered.asm.mixin.Mixin;

@IfModLoaded("nomansland")
@Mixin(value = PathMakingEvents.class, remap = false)
public class PathMakingBrokenToolMixin {

    @WrapMethod(method = "canContinue")
    private static boolean spicedcider$brokenToolNoPaths(PlayerInteractEvent.RightClickBlock event, Operation<Boolean> original) {
        return !BrokenItemUtil.isBroken(event.getItemStack()) && original.call(event);
    }
}
