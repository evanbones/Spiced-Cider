package com.evandev.spicedcider.mixin.reliableremover;

import com.evandev.reliable_remover.config.ModConfig;
import com.evandev.reliable_remover.config.RuleConfigIO;
import com.evandev.reliable_remover.config.RuleManager;
import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.compat.reliableremover.ModRemovalFiles;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;

import java.io.IOException;

@IfModLoaded("reliable_remover")
@Mixin(value = RuleConfigIO.class, remap = false)
public class RuleConfigIOMixin {

    @WrapMethod(method = "addRemovalRule")
    private static boolean spicedcider$addToModFile(String itemId, Operation<Boolean> original) {
        if (ModConfig.get().blacklistedItems.contains(itemId)) return false;
        try {
            if (!ModRemovalFiles.add(itemId)) return false;
        } catch (IOException | RuntimeException e) {
            SpicedCider.LOGGER.error("Failed to write {} to its Reliable Remover mod file, using the blacklist instead", itemId, e);
            return original.call(itemId);
        }
        RuleManager.load();
        return true;
    }

    @WrapMethod(method = "removeRemovalRule")
    private static boolean spicedcider$removeFromModFile(String itemId, Operation<Boolean> original) {
        boolean removed = false;
        try {
            removed = ModRemovalFiles.remove(itemId);
        } catch (IOException | RuntimeException e) {
            SpicedCider.LOGGER.error("Failed to remove {} from its Reliable Remover mod file", itemId, e);
        }
        if (ModConfig.get().blacklistedItems.contains(itemId)) return original.call(itemId) || removed;
        if (removed) RuleManager.load();
        return removed;
    }
}
