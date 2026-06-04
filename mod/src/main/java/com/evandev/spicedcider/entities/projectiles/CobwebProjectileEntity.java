package com.evandev.spicedcider.entities.projectiles;

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
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class CobwebProjectileEntity extends Projectile implements GeoAnimatable {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public boolean delayedSpawnParticles;

    public CobwebProjectileEntity(EntityType<? extends CobwebProjectileEntity> entityType, Level level) {
        super(entityType, level);
    }

    public CobwebProjectileEntity(Level level, LivingEntity owner) {
        this(ModEntityTypes.COBWEB_PROJECTILE.get(), level);
        super.setOwner(owner);

        double x = owner.getX() - (double) (owner.getBbWidth() + 1.0F) * 0.5D * (double) Mth.sin(owner.yBodyRot * ((float) Math.PI / 180F));
        double y = owner.getEyeY() - 0.1D;
        double z = owner.getZ() + (double) (owner.getBbWidth() + 1.0F) * 0.5D * (double) Mth.cos(owner.yBodyRot * ((float) Math.PI / 180F));

        this.setPos(x, y, z);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
    }

    @Override
    public void tick() {
        super.tick();

        if (this.delayedSpawnParticles) {
            this.delayedSpawnParticles = false;
            this.createSpawnParticles();
        }

        Vec3 movement = this.getDeltaMovement();
        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);

        if (hitResult.getType() != HitResult.Type.MISS && !net.neoforged.neoforge.event.EventHooks.onProjectileImpact(this, hitResult)) {
            this.onHit(hitResult);
        }

        double newX = this.getX() + movement.x;
        double newY = this.getY() + movement.y;
        double newZ = this.getZ() + movement.z;
        this.updateRotation();

        float drag = 0.99F;
        float gravity = 0.06F;

        if (this.level().getBlockStates(this.getBoundingBox()).noneMatch(BlockBehaviour.BlockStateBase::isAir)) {
            this.remove(RemovalReason.DISCARDED);
        } else if (this.isInWaterOrBubble()) {
            this.remove(RemovalReason.DISCARDED);
        } else {
            this.setDeltaMovement(movement.scale(drag));
            if (!this.isNoGravity()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -gravity, 0.0D));
            }

            this.setPos(newX, newY, newZ);
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
    public void handleEntityEvent(byte eventId) {
        if (eventId == 1) {
            for (int i = 0; i < 5; i++) {
                this.level().addParticle(ParticleTypes.POOF, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
            }
        } else {
            super.handleEntityEvent(eventId);
        }
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
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
                    trapsTarget.cider$setTargetTrapped(true, true);
                    trapsTarget.cider$setTargetTrappedCounter(100);
                }
            }
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