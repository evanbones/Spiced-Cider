package com.evandev.spicedcider.compat.controlify;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.compat.clutternomore.ShapeWheel;
import dev.isxander.controlify.api.ControlifyApi;
import dev.isxander.controlify.api.entrypoint.ControlifyEntrypoint;
import dev.isxander.controlify.api.entrypoint.InitContext;
import dev.isxander.controlify.api.entrypoint.PreInitContext;
import dev.isxander.controlify.api.guide.Fact;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;

public class ControlifyGuides implements ControlifyEntrypoint {

    @Override
    public void onControlifyPreInit(PreInitContext context) {
        boolean shapes = ModList.get().isLoaded("clutternomore");
        context.guideRegistries().inGame().registerFact(Fact.of(
                ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "can_change_shape"),
                ctx -> shapes && ShapeWheel.canChangeShape()));
    }

    @Override
    public void onControlifyInit(InitContext context) {
    }

    @Override
    public void onControllersDiscovered(ControlifyApi controlify) {
    }
}
