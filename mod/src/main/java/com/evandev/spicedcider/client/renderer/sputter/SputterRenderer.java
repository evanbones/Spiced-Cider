package com.evandev.spicedcider.client.renderer.sputter;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.client.models.sputter.SputterModel;
import com.evandev.spicedcider.entities.sputter.SputterEntity;
import com.evandev.spicedcider.registry.ModModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class SputterRenderer extends MobRenderer<SputterEntity, SputterModel<SputterEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "textures/entity/sputter/sputter.png");

    public SputterRenderer(EntityRendererProvider.Context context) {
        super(context, new SputterModel<>(context.bakeLayer(ModModelLayers.SPUTTER)), 0.5F);
        this.addLayer(new SputterArrowfulLayer(this, context.getModelSet()));
        this.addLayer(new SputterWarpaintLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(SputterEntity entity) {
        return TEXTURE;
    }
}
