package com.evandev.spicedcider.client.models.projectile;


import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.entities.projectiles.CobwebProjectileEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class CobwebProjectileModel extends GeoModel<CobwebProjectileEntity> {

    @Override
    public ResourceLocation getAnimationResource(CobwebProjectileEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "animations/web_projectile.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(CobwebProjectileEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "geo/web_projectile.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(CobwebProjectileEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "textures/entity/projectile/web_projectile.png");
    }

    @Override
    public RenderType getRenderType(CobwebProjectileEntity animatable, ResourceLocation textureLocation) {
        return RenderType.entityTranslucent(textureLocation);
    }

    @Override
    public void setCustomAnimations(CobwebProjectileEntity entity, long uniqueID, AnimationState<CobwebProjectileEntity> customPredicate) {
        super.setCustomAnimations(entity, uniqueID, customPredicate);
        var everything = this.getAnimationProcessor().getBone("everything");

        everything.setRotY(-1.5708F);
    }
}