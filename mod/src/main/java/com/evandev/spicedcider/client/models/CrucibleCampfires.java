package com.evandev.spicedcider.client.models;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.compat.modernfix.CrucibleCampfireLazyModelWrapping;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, value = Dist.CLIENT)
public final class CrucibleCampfires {
    public static Set<ModelResourceLocation> modelLocations() {
        Set<ModelResourceLocation> locations = new HashSet<>();
        for (Block block : BuiltInRegistries.BLOCK) {
            if (block instanceof CampfireBlock) {
                for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                    locations.add(BlockModelShaper.stateToModelLocation(state));
                }
            }
        }
        return locations;
    }

    @SubscribeEvent
    public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
        if (ModList.get().isLoaded("modernfix") && CrucibleCampfireLazyModelWrapping.bakesLazily(event.getModelBakery())) return;
        Map<ModelResourceLocation, BakedModel> models = event.getModels();
        for (ModelResourceLocation location : modelLocations()) {
            BakedModel model = models.get(location);
            if (model != null && !(model instanceof CrucibleCampfireModel)) models.put(location, new CrucibleCampfireModel(model));
        }
    }
}
