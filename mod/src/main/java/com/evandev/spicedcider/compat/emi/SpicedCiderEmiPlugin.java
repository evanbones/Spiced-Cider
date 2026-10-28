package com.evandev.spicedcider.compat.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import net.neoforged.fml.ModList;

@EmiEntrypoint
public class SpicedCiderEmiPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        if (ModList.get().isLoaded("nomansland")) {
            TappingEmiRecipe.register(registry);
        }
    }
}
