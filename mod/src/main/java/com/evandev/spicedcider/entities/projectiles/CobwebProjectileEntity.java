package com.evandev.spicedcider.entities.projectiles;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.interfaces.ITrapsTarget;
import com.evandev.spicedcider.registry.ModBlocks;
import com.evandev.spicedcider.registry.ModEntityTypes;
import com.evandev.spicedcider.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.lang.reflect.Field;

public class CobwebProjectileEntity extends Projectile implements GeoAnimatable {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public boolean delayedSpawnParticles;

    public CobwebProjectileEntity(EntityType<? extends CobwebProjectileEntity> p_i50162_1_, Level p_i50162_2_) {
        super(p_i50162_1_, p_i50162_2_);
    }

    public CobwebProjectileEntity(Level p_i47273_1_, LivingEntity p_i47273_2_) {
        this(ModEntityTypes.COBWEB_PROJECTILE.get(), p_i47273_1_);
        super.setOwner(p_i47273_2_);
        this.setPos(p_i47273_2_.getX() - (double) (p_i47273_2_.getBbWidth() + 1.0F) * 0.5D * (double) Mth.sin(p_i47273_2_.yBodyRot * ((float) Math.PI / 180F)), p_i47273_2_.getEyeY() - (double) 0.1F, p_i47273_2_.getZ() + (double) (p_i47273_2_.getBbWidth() + 1.0F) * 0.5D * (double) Mth.cos(p_i47273_2_.yBodyRot * ((float) Math.PI / 180F)));
    }

    @OnlyIn(Dist.CLIENT)
    public CobwebProjectileEntity(Level p_i47274_1_, double p_i47274_2_, double p_i47274_4_, double p_i47274_6_, double p_i47274_8_, double p_i47274_10_, double p_i47274_12_) {
        this(ModEntityTypes.COBWEB_PROJECTILE.get(), p_i47274_1_);
        this.setPos(p_i47274_2_, p_i47274_4_, p_i47274_6_);

        for (int i = 0; i < 7; ++i) {
            double d0 = 0.4D + 0.1D * (double) i;
            p_i47274_1_.addParticle(ParticleTypes.SPIT, p_i47274_2_, p_i47274_4_, p_i47274_6_, p_i47274_8_ * d0, p_i47274_10_, p_i47274_12_ * d0);
        }

        this.setDeltaMovement(p_i47274_8_, p_i47274_10_, p_i47274_12_);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {

    }

    public void tick() {
        super.tick();

        if (this.delayedSpawnParticles) {
            this.delayedSpawnParticles = false;
            this.createSpawnParticles();
        }

        Vec3 vector3d = this.getDeltaMovement();
        HitResult raytraceresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (raytraceresult.getType() != HitResult.Type.MISS && !EventHooks.onProjectileImpact(this, raytraceresult)) {
            this.onHit(raytraceresult);
        }

        double d0 = this.getX() + vector3d.x;
        double d1 = this.getY() + vector3d.y;
        double d2 = this.getZ() + vector3d.z;
        this.updateRotation();
        float f = 0.99F;
        float f1 = 0.06F;
        if (this.level().getBlockStates(this.getBoundingBox()).noneMatch(BlockBehaviour.BlockStateBase::isAir)) {
            this.remove(RemovalReason.DISCARDED);
        } else if (this.isInWaterOrBubble()) {
            this.remove(RemovalReason.DISCARDED);
        } else {
            this.setDeltaMovement(vector3d.scale(f));
            if (!this.isNoGravity()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -f1, 0.0D));
            }

            this.setPos(d0, d1, d2);
        }
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult hitResult) {
        super.onHitEntity(hitResult);

        Entity target = hitResult.getEntity();
        Entity owner = this.getOwner();

        if (!(target instanceof LivingEntity livingTarget)) {
            return;
        }

        if (livingTarget.getType().is(EntityTypeTags.ARTHROPOD)) {
            return;
        }

        if (owner instanceof LivingEntity livingOwner) {
            livingTarget.hurt(
                    damageSources().indirectMagic(this, livingOwner),
                    1.0F
            );
        }

        if (!this.level().isClientSide) {
            this.spawnTrap(
                    livingTarget.getX(),
                    livingTarget.getY(),
                    livingTarget.getZ()
            );

            this.remove(RemovalReason.DISCARDED);
        }
    }

    public void createSpawnParticles() {
        if (!this.level().isClientSide()) {
            this.level().broadcastEntityEvent(this, (byte) 1);
        } else {
            for (int i = 0; i < 5; i++) {
                this.level().addParticle(ParticleTypes.POOF, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    public void handleEntityEvent(byte p_70103_1_) {
        if (p_70103_1_ == 1) {
            for (int i = 0; i < 5; i++) {
                this.level().addParticle(ParticleTypes.POOF, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
            }
        } else {
            super.handleEntityEvent(p_70103_1_);
        }
    }

    protected void onHitBlock(@NotNull BlockHitResult p_230299_1_) {
        super.onHitBlock(p_230299_1_);
        if (!this.level().isClientSide) {
            this.spawnTrap(this.getX(), this.getY(), this.getZ());

            this.remove(RemovalReason.DISCARDED);
        }
    }

    public void spawnTrap(double x, double y, double z) {
        BlockPos pos = BlockPos.containing(x, y, z);
        BlockState currentState = this.level().getBlockState(pos);

        if (currentState.isAir() || currentState.canBeReplaced()) {
            this.level().setBlock(pos, ModBlocks.TEMPORARY_COBWEB.get().defaultBlockState(), 3);
        }

        this.playSound(ModSounds.SPIDER_WEB_IMPACT.get(), 1.0F, 1.0F);

        Entity owner = this.getOwner();

        if (owner instanceof Mob mobOwner && owner instanceof ITrapsTarget trapsTarget) {
            LivingEntity target = mobOwner.getTarget();

            if (target != null) {
                double distSqr = target.distanceToSqr(x, y, z);
                if (distSqr < 4.0) {
                    trapsTarget.setTargetTrapped(true, true);
                    this.setTrappedCounter(owner, 100);
                }
            }
        }
    }

    private void setTrappedCounter(Entity entity, int value) {
        try {
            Field field = entity.getClass().getField("targetTrappedCounter");
            field.setInt(entity, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            SpicedCider.LOGGER.error("Failed to set targetTrappedCounter", e);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 2, this::predicate));
    }

    private <P extends GeoAnimatable> PlayState predicate(AnimationState<P> event) {
        event.getController().setAnimation(RawAnimation.begin().then("web_projectile_idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object object) {
        return tickCount;
    }
}