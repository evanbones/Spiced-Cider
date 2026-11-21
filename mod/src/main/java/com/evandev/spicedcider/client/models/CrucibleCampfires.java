package com.evandev.spicedcider.client.models;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.compat.modernfix.CrucibleCampfireLazyModelWrapping;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, value = Dist.CLIENT)
public final class CrucibleCampfires {
    private static final Set<ResourceLocation> SHALLOW = Set.of(
            ResourceLocation.fromNamespaceAndPath("blockbox", "brazier"),
            ResourceLocation.fromNamespaceAndPath("blockbox", "soul_brazier"));
    private static final float SHALLOW_BOTTOM = 8.0F / 16.0F;

    public static Map<ModelResourceLocation, Float> modelLocations() {
        Map<ModelResourceLocation, Float> locations = new HashMap<>();
        for (Block block : BuiltInRegistries.BLOCK) {
            boolean shallow = SHALLOW.contains(BuiltInRegistries.BLOCK.getKey(block));
            if (block instanceof CampfireBlock || shallow) {
                for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                    locations.put(BlockModelShaper.stateToModelLocation(state), shallow ? SHALLOW_BOTTOM : -1.0F);
                }
            }
        }
        return locations;
    }

    public static CrucibleCampfireModel wrap(BakedModel model, float bottom) {
        return bottom < 0.0F ? new CrucibleCampfireModel(model) : new CrucibleCampfireModel(model, bottom);
    }

    @SubscribeEvent
    public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
        if (ModList.get().isLoaded("modernfix") && CrucibleCampfireLazyModelWrapping.bakesLazily(event.getModelBakery())) return;
        Map<ModelResourceLocation, BakedModel> models = event.getModels();
        for (Map.Entry<ModelResourceLocation, Float> entry : modelLocations().entrySet()) {
            BakedModel model = models.get(entry.getKey());
            if (model != null && !(model instanceof CrucibleCampfireModel)) models.put(entry.getKey(), wrap(model, entry.getValue()));
        }
    }
}
