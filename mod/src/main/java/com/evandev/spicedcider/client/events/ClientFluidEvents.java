package com.evandev.spicedcider.client.events;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.registry.ModFluidTypes;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, value = Dist.CLIENT)
public class ClientFluidEvents {

    @SubscribeEvent
    public static void registerFluidExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(textured("resin"), ModFluidTypes.RESIN.get());
        event.registerFluidType(textured("maple_syrup"), ModFluidTypes.MAPLE_SYRUP.get());
    }

    private static IClientFluidTypeExtensions textured(String name) {
        ResourceLocation still = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "block/fluid/" + name + "_still");
        ResourceLocation flowing = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "block/fluid/" + name + "_flow");
        return new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return still;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return flowing;
            }
        };
    }
}
