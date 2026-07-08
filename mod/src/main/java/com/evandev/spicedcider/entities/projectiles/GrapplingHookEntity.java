package com.evandev.spicedcider.entities.projectiles;

import com.evandev.spicedcider.interfaces.IPlayerWithGrapplingHook;
import com.evandev.spicedcider.registry.ModEntityTypes;
import com.evandev.spicedcider.registry.ModSounds;
import com.li64.tide.data.rods.CustomRodManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class GrapplingHookEntity extends Projectile {
    private static final EntityDataAccessor<Integer> DATA_HOOKED_ENTITY = SynchedEntityData.defineId(GrapplingHookEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_STICKY = SynchedEntityData.defineId(GrapplingHookEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<ItemStack> DATA_HOOK = SynchedEntityData.defineId(GrapplingHookEntity.class, EntityDataSerializers.ITEM_STACK);
    private final int RANGE = 64;
    private final int RANGE_SQR = RANGE * RANGE;
    public int shakeTime;
    protected boolean isAttached;
    @Nullable
    private BlockState lastState;
    @Nullable
    private BlockPos attachedBlockPos;
    @Nullable
    private Entity hookedIn;

    public GrapplingHookEntity(EntityType<? extends GrapplingHookEntity> entityType, Level level) {
        this(entityType, level, ItemStack.EMPTY, false);
    }

    private GrapplingHookEntity(EntityType<? extends GrapplingHookEntity> entityType, Level level, ItemStack hookItem, boolean isSticky) {
        super(entityType, level);
        this.noCulling = true;

        this.getEntityData().set(DATA_HOOK, hookItem);
        this.getEntityData().set(DATA_STICKY, isSticky);
    }

    public GrapplingHookEntity(Player player, Level level, ItemStack hookItem, boolean isSticky) {
        this(ModEntityTypes.GRAPPLING_HOOK.get(), level, hookItem, isSticky);
        this.setOwner(player);
        this.moveTo(player.getX(), player.getEyeY(), player.getZ(), 0.0F, 0.0F);

        float playerXRot = player.getXRot();
        float playerYRot = player.getYRot();
        float x = -Mth.sin(playerYRot * ((float) Math.PI / 180F)) * Mth.cos(playerXRot * ((float) Math.PI / 180F));
        float y = -Mth.sin((playerXRot) * ((float) Math.PI / 180F));
        float z = Mth.cos(playerYRot * ((float) Math.PI / 180F)) * Mth.cos(playerXRot * ((float) Math.PI / 180F));
        Vec3 direction = new Vec3(x, y, z).normalize();

        double velocity = 1.3;
        Vec3 playerMovement = player.getDeltaMovement();
        this.setDeltaMovement(direction.scale(velocity).add(playerMovement.x, player.onGround() ? 0 : playerMovement.y, playerMovement.z));
    }

    static public double getPullingRatio(Entity pulledEntity, Entity player, boolean pullingPlayer) {
        double playerBias = 1;
        double playerVolume = player.getBbWidth() * player.getBbWidth() * player.getBbHeight() * playerBias;
        double entityVolume = pulledEntity.getBbWidth() * pulledEntity.getBbWidth() * pulledEntity.getBbHeight();
        double total = playerVolume + entityVolume;
        if (pullingPlayer) {
            return entityVolume / total;
        } else {
            return playerVolume / total;
        }
    }

    static public double getSizeRatio(Entity entity, Entity player) {
        double playerVolume = player.getBbWidth() * player.getBbWidth() * player.getBbHeight();
        double entityVolume = entity.getBbWidth() * entity.getBbWidth() * entity.getBbHeight();

        return Mth.clamp(entityVolume / playerVolume, 0.2, 3.0);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < RANGE_SQR;
    }

    public boolean isAttached() {
        return isAttached;
    }

    public ItemStack getHookItem() {
        return entityData.get(DATA_HOOK);
    }

    public boolean hasHookItem() {
        return !this.getHookItem().isEmpty();
    }

    public void tick() {
        super.tick();
        Player player = this.getPlayerOwner();
        if (player == null) {
            this.discard();
        } else if (this.level().isClientSide() || !this.shouldStopFishing(player)) {

            Vec3 deltaMovement = this.getDeltaMovement();

            if (this.shakeTime > 0) {
                --this.shakeTime;
            }

            // Set initial rotation
            if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
                this.setYRot((float) (Mth.atan2(deltaMovement.x, deltaMovement.z) * (double) (180F / (float) Math.PI)));
                this.setXRot((float) (Mth.atan2(deltaMovement.y, deltaMovement.horizontalDistance()) * (double) (180F / (float) Math.PI)));
                this.yRotO = this.getYRot();
                this.xRotO = this.getXRot();
            }

            BlockPos blockpos = this.blockPosition();
            BlockState blockstate = this.level().getBlockState(blockpos);

            if (this.hookedIn != null) {
                if (!this.hookedIn.isRemoved() && this.hookedIn.level().dimension() == this.level().dimension()) {
                    this.setPos(this.hookedIn.getX(), this.hookedIn.getY(0.8), this.hookedIn.getZ());
                    if (!this.level().isClientSide()) {
                        this.pullEntity(this.hookedIn);
                        this.level().broadcastEntityEvent(this, (byte) 31);
                    }
                } else {
                    this.setHookedEntity(null);
                }
            } else if (isAttached) {
                if (this.shouldFall()) {
                    this.startFalling();
                }
            } else {
                Vec3 currentPosition = this.position();
                Vec3 movedPosition = currentPosition.add(deltaMovement);

                // Check for rope collision with blocks
                ClipContext context = new ClipContext(
                        currentPosition,
                        player.getEyePosition(),
                        ClipContext.Block.COLLIDER,
                        ClipContext.Fluid.NONE,
                        this
                );
                BlockHitResult blockEdgeHitResult = level().clip(context);

                if (blockEdgeHitResult.getType() == HitResult.Type.BLOCK) {
                    if (blockEdgeHitResult.getDirection() == Direction.DOWN) {
                        this.breakRope(player);
                    } else if (blockEdgeHitResult.getDirection() != Direction.UP) {
                        // Line of sight from hook to player collides with side of block
                        // Pretend we hit the top to avoid bounce off
                        blockEdgeHitResult = new BlockHitResult(blockEdgeHitResult.getLocation(), Direction.UP, blockEdgeHitResult.getBlockPos(), blockEdgeHitResult.isInside());
                    }

                    this.onHit(blockEdgeHitResult);
                    this.hasImpulse = true;
                } else {
                    // Check for hook collision with block
                    HitResult blockHitResult = this.level().clip(new ClipContext(currentPosition, movedPosition, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
                    if (blockHitResult.getType() == HitResult.Type.BLOCK) {
                        this.onHit(blockHitResult);
                        this.hasImpulse = true;
                    }
                }

                // Check for entity hit
                HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
                if (hitresult.getType() != HitResult.Type.MISS) {
                    this.onHit(hitresult);
                }

                // Stop updating movement if attached or hooked
                if (this.isAttached || this.hookedIn != null) {
                    return;
                }

                deltaMovement = this.getDeltaMovement();
                double deltaX = deltaMovement.x;
                double deltaY = deltaMovement.y;
                double deltaZ = deltaMovement.z;

                // If closer than one block
                if (this.distanceToSqr(player.getEyePosition()) <= 1.0) {
                    // Rotate to align with movement direction
                    this.setYRot((float) (Mth.atan2(deltaX, deltaZ) * (double) (180F / (float) Math.PI)));
                    this.setXRot((float) (Mth.atan2(deltaY, deltaMovement.horizontalDistance()) * (double) (180F / (float) Math.PI)));
                } else {
                    // Rotate to align with line of sight
                    Vec3 ropeVec = player.getEyePosition().subtract(this.position()).reverse();
                    this.setYRot((float) (Mth.atan2(ropeVec.x, ropeVec.z) * (double) (180F / (float) Math.PI)));
                    this.setXRot((float) (Mth.atan2(ropeVec.y, ropeVec.horizontalDistance()) * (double) (180F / (float) Math.PI)));
                }

                this.setXRot(lerpRotation(this.xRotO, this.getXRot()));
                this.setYRot(lerpRotation(this.yRotO, this.getYRot()));

                double newX = this.getX() + deltaX;
                double newY = this.getY() + deltaY;
                double newZ = this.getZ() + deltaZ;

                float inertia = 0.99F;
                float gravity = 0.05F;
                if (this.isInWater()) {
                    for (int j = 0; j < 4; ++j) {
                        double f2 = 0.25;
                        this.level().addParticle(ParticleTypes.BUBBLE, newX - deltaX * f2, newY - deltaY * f2, newZ - deltaZ * f2, deltaX, deltaY, deltaZ);
                    }
                    inertia = 0.6F;
                }
                this.setDeltaMovement(deltaMovement.scale(inertia));

                deltaMovement = this.getDeltaMovement();
                this.setDeltaMovement(deltaMovement.x, deltaMovement.y - gravity, deltaMovement.z);

                this.setPos(newX, newY, newZ);

                if (!blockstate.isAir()) {
                    VoxelShape voxelshape = blockstate.getCollisionShape(this.level(), blockpos);
                    if (!voxelshape.isEmpty()) {
                        Vec3 vec31 = this.position();

                        for (AABB aabb : voxelshape.toAabbs()) {
                            if (aabb.move(blockpos).contains(vec31)) {
                                this.isAttached = true;
                                break;
                            }
                        }
                    }
                }

                this.checkInsideBlocks();
            }
        }
    }

    @Override
    public void lerpTo(double x, double y, double z, float yRot, float xRot, int steps) {

    }

    public int retrieve(ItemStack fishingRod) {
        Player player = this.getPlayerOwner();
        if (!this.level().isClientSide() && player != null && !this.shouldStopFishing(player)) {
            int rodDamage = 1;

            if (this.hookedIn != null) {
                rodDamage = this.hookedIn instanceof ItemEntity ? 3 : 5;
            }

            this.discard();
            return rodDamage;
        } else {
            return 0;
        }
    }

    private void breakRope(Player player) {
        // Play line break sound
        this.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.LEASH_KNOT_BREAK, SoundSource.PLAYERS, 0.25F, 1.0F);
        this.discard();
    }

    private void setHookedEntity(@Nullable Entity hookedEntity) {
        this.hookedIn = hookedEntity;
        this.getEntityData().set(DATA_HOOKED_ENTITY, hookedEntity == null ? 0 : hookedEntity.getId() + 1);
    }

    private boolean shouldStopFishing(Player player) {
        ItemStack mainHandRod = player.getMainHandItem();
        ItemStack offhandRod = player.getOffhandItem();
        boolean flag = mainHandRod.getItem() instanceof FishingRodItem;
        boolean flag1 = offhandRod.getItem() instanceof FishingRodItem;

        if (!player.isRemoved() && player.isAlive() && (flag || flag1) && !(this.distanceToSqr(player) > RANGE_SQR)) {
            return false;
        } else {
            if (this.distanceToSqr(player) > RANGE_SQR) {
                this.breakRope(player);
            } else {
                this.discard();
            }
            return true;
        }
    }

    private boolean shouldFall() {
        if (this.attachedBlockPos != null) {
            BlockState state = this.level().getBlockState(this.attachedBlockPos);
            return this.lastState != state && state.isAir();
        }
        return false;
    }

    private void startFalling() {
        this.isAttached = false;
        this.lastState = null;
        this.attachedBlockPos = null;
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.multiply(this.random.nextFloat() * 0.2F, this.random.nextFloat() * 0.2F, this.random.nextFloat() * 0.2F));
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return super.canHitEntity(target) || target.isAlive() && target instanceof ItemEntity;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        this.shakeTime = 7;
        if (!this.level().isClientSide()) {
            this.setHookedEntity(result.getEntity());
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        this.attachedBlockPos = result.getBlockPos();
        this.lastState = this.level().getBlockState(this.attachedBlockPos);
        super.onHitBlock(result);
        Vec3 deltaMovement = this.getDeltaMovement();

        BlockState blockState = level().getBlockState(result.getBlockPos());
        level().playSound(null, this.getX(), this.getY(), this.getZ(), blockState.getSoundType().getHitSound(), SoundSource.PLAYERS, 1F, 1);

        Direction hitDirection = result.getDirection();
        Vec3 hitPos = result.getLocation();

        // Bounce if hitting the side or bottom of a block
        if (!hitDirection.equals(Direction.UP) && !this.isSticky()) {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.1));
            if (hitDirection.getAxis() == Direction.Axis.X) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(-1, 1, 1));
            } else if (hitDirection.getAxis() == Direction.Axis.Z) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(1, 1, -1));
            }
            return;
        }

        RandomSource random = level().getRandom();

        if (this.isSticky()) {
            this.playSound(SoundEvents.SLIME_SQUISH, 0.5F, 1.0F);
            for (int particles = 4; particles > 0; particles--) {
                Vec3 reversedMovement = deltaMovement.reverse().add(random.nextDouble() - 0.5, random.nextDouble() - 0.5, random.nextDouble() - 0.5).normalize().scale(0.05);
                level().addParticle(ParticleTypes.ITEM_SLIME, hitPos.x, hitPos.y, hitPos.z, reversedMovement.x, reversedMovement.y, reversedMovement.z);
            }
        } else {
            this.playSound(SoundEvents.CHAIN_HIT, 0.5F, 0.75F);
            for (int particles = 3; particles > 0; particles--) {
                Vec3 reversedMovement = deltaMovement.reverse().add(random.nextDouble() - 0.5, random.nextDouble() - 0.5, random.nextDouble() - 0.5).normalize().scale(0.05);
                level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockState), hitPos.x, hitPos.y, hitPos.z, reversedMovement.x, reversedMovement.y, reversedMovement.z);
            }
        }

        this.setDeltaMovement(Vec3.ZERO);
        this.setPos(hitPos);
        this.shakeTime = 7;
        this.isAttached = true;

        Player player = this.getPlayerOwner();
        if (player != null) {
            Vec3 playerPos = player.getEyePosition();
            Vec3 ropeVec = this.position().subtract(playerPos);

            // Pull back a little to hook nicely onto corners
            this.setPos(this.position().subtract(ropeVec.normalize().scale(3F / 16)));

            // Move hook down a little to avoid clipping
            if (hitDirection.equals(Direction.DOWN)) {
                this.setPos(this.position().subtract(0, 0.15, 0));
            }

            Vec3 soundPosition = playerPos.add(ropeVec.normalize().scale(2));
            level().playSound(null, soundPosition.x, soundPosition.y, soundPosition.z, ModSounds.GRAPPLING_HOOK_TIGHTEN.get(), SoundSource.PLAYERS, 0.25F, 1);
        }
    }

    @Override
    protected MovementEmission getMovementEmission() {
        return MovementEmission.NONE;
    }

    @Override
    public void remove(RemovalReason reason) {
        this.updateOwnerInfo(null);
        super.remove(reason);
    }

    @Override
    public void onClientRemoval() {
        this.updateOwnerInfo(null);
    }

    @Override
    public void setOwner(@Nullable Entity owner) {
        super.setOwner(owner);
        this.updateOwnerInfo(this);
    }

    public void handleEntityEvent(byte id) {
        if (id == 31 && this.level().isClientSide() && this.hookedIn instanceof Player && ((Player) this.hookedIn).isLocalPlayer()) {
            this.pullEntity(this.hookedIn);
        }

        super.handleEntityEvent(id);
    }

    protected void pullEntity(Entity entity) {
        Entity player = this.getOwner();
        if (entity != null && player != null) {
            Vec3 playerPos = player.getEyePosition();
            Vec3 entityPos = this.position();
            Vec3 rope = playerPos.subtract(entityPos);
            Vec3 ropeDirection = rope.normalize().reverse();
            double distanceSqr = rope.lengthSqr();

            Vec3 velocity = entity.getDeltaMovement();
            double targetLength = 1.5F;
            targetLength = targetLength * getSizeRatio(entity, player);
            double targetLengthSqr = targetLength * targetLength;
            double stiffness = 0.01D;
            double maxPull = 0.15;
            maxPull = maxPull * GrapplingHookEntity.getPullingRatio(entity, player, false);

            if (distanceSqr > targetLengthSqr) {
                double elasticity = 1;
                double radialVelocity = Math.max(velocity.dot(ropeDirection), 0);
                Vec3 radialMovement = ropeDirection.scale(radialVelocity * elasticity);
                Vec3 tangentialMovement = velocity.subtract(radialMovement);

                entity.setDeltaMovement(tangentialMovement);

                double pullDistance = Math.max(distanceSqr - targetLengthSqr, 0);
                double pull = Math.min(pullDistance * stiffness, maxPull);
                Vec3 pullVector = ropeDirection.reverse().scale(pull);

                entity.setDeltaMovement(entity.getDeltaMovement().add(pullVector));
            }
        }
    }

    private void updateOwnerInfo(@Nullable GrapplingHookEntity grapplingHook) {
        Player player = this.getPlayerOwner();
        if (player != null) {
            ((IPlayerWithGrapplingHook) player).spicedcider$setHook(grapplingHook);
        }
    }

    @Nullable
    public Player getPlayerOwner() {
        Entity entity = this.getOwner();
        return entity instanceof Player ? (Player) entity : null;
    }

    public ItemStack getFishingLine() {
        Player player = getPlayerOwner();
        if (player != null) {
            ItemStack rod = player.getUseItem();
            if (rod.isEmpty()) {
                rod = player.getMainHandItem();
            }
            if (rod.isEmpty()) {
                rod = player.getOffhandItem();
            }
            if (!rod.isEmpty() && rod.getItem() instanceof FishingRodItem) {
                return CustomRodManager.getLine(rod);
            }
        }
        return ItemStack.EMPTY;
    }

    public ItemStack getBobber() {
        Player player = getPlayerOwner();
        if (player != null) {
            ItemStack rod = player.getUseItem();
            if (rod.isEmpty()) {
                rod = player.getMainHandItem();
            }
            if (rod.isEmpty()) {
                rod = player.getOffhandItem();
            }
            if (!rod.isEmpty() && rod.getItem() instanceof FishingRodItem) {
                return CustomRodManager.getBobber(rod);
            }
        }
        return ItemStack.EMPTY;
    }

    public boolean isSticky() {
        return getEntityData().get(DATA_STICKY);
    }

    @Nullable
    public Entity getHookedIn() {
        return this.hookedIn;
    }

    @Override
    public boolean canChangeDimensions(Level oldLevel, Level newLevel) {
        return false;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entity) {
        return super.getAddEntityPacket(entity);
    }

    public EntityType<?> getType() {
        return ModEntityTypes.GRAPPLING_HOOK.get();
    }

    @Override
    public Component getName() {
        return Component.translatable("item.spicedcider.grappling_hook");
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_HOOKED_ENTITY, 0);
        builder.define(DATA_STICKY, false);
        builder.define(DATA_HOOK, ItemStack.EMPTY);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (DATA_HOOKED_ENTITY.equals(key)) {
            int i = this.getEntityData().get(DATA_HOOKED_ENTITY);
            this.hookedIn = i > 0 ? this.level().getEntity(i - 1) : null;
        }

        super.onSyncedDataUpdated(key);
    }
}
