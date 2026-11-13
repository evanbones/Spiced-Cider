package com.evandev.spicedcider.client.events;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.registry.ModFluidTypes;
import com.evandev.spicedcider.registry.ModMoltenMetals;
import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.client.event.ViewportEvent;
import org.jetbrains.annotations.Nullable;
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
        event.registerFluidType(textured("latex"), ModFluidTypes.LATEX.get());
        event.registerFluidType(textured(ResourceLocation.fromNamespaceAndPath("nomansland", "block/fluid/resin_oil"),
                ResourceLocation.fromNamespaceAndPath("nomansland", "block/fluid/flowing_resin_oil")), ModFluidTypes.RESIN_OIL.get());
        ModMoltenMetals.ENTRIES.values().forEach(entry -> event.registerFluidType(molten(entry), entry.type().get()));
    }

    private static IClientFluidTypeExtensions molten(ModMoltenMetals.Entry entry) {
        String name = "molten_" + entry.metal().name();
        return textured(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "block/fluid/" + name + "_still"),
                ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "block/fluid/" + name + "_flow"));
    }

    @Nullable
    private static ModMoltenMetals.Entry moltenAtCamera(Camera camera) {
        if (camera.getEntity() == null) return null;
        Level level = camera.getEntity().level();
        BlockPos pos = camera.getBlockPosition();
        FluidState state = level.getFluidState(pos);
        if (state.isEmpty() || camera.getPosition().y >= pos.getY() + state.getHeight(level, pos)) return null;
        for (ModMoltenMetals.Entry entry : ModMoltenMetals.ENTRIES.values()) {
            if (state.getFluidType() == entry.type().get()) return entry;
        }
        return null;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void moltenFogColor(ViewportEvent.ComputeFogColor event) {
        ModMoltenMetals.Entry entry = moltenAtCamera(event.getCamera());
        if (entry == null) return;
        int color = entry.metal().fogColor();
        event.setRed(((color >> 16) & 0xFF) / 255.0F);
        event.setGreen(((color >> 8) & 0xFF) / 255.0F);
        event.setBlue((color & 0xFF) / 255.0F);
    }

    private static IClientFluidTypeExtensions textured(String name) {
        return textured(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "block/fluid/" + name + "_still"),
                ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "block/fluid/" + name + "_flow"));
    }

    private static IClientFluidTypeExtensions textured(ResourceLocation still, ResourceLocation flowing) {
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
