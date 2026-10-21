package com.evandev.spicedcider.entities.balloon;

import com.evandev.spicedcider.content.item.BalloonItem;
import com.evandev.spicedcider.registry.ModEntityTypes;
import com.evandev.spicedcider.registry.ModSounds;
import com.evandev.spicedcider.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BalloonEntity extends Entity {
    private static final EntityDataAccessor<Byte> COLOR = SynchedEntityData.defineId(BalloonEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> HOLDER = SynchedEntityData.defineId(BalloonEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<BlockPos>> FENCE = SynchedEntityData.defineId(BalloonEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);

    public static final int MAX_ON_BOAT = 4;
    public static final int MAX_ON_ENTITY = 6;
    public static final int MAX_ON_FENCE = 4;
    private static final double[][] BOAT_CORNERS = {{0.9, -0.5}, {-0.9, 0.5}, {-0.9, -0.5}, {0.9, 0.5}};

    @Nullable
    private Entity holder;
    @Nullable
    private UUID pendingHolder;
    private int pendingTicks;
    private int slot;
    private int groupSize = 1;
    private double offsetX;
    private double offsetZ;
    private double offsetXO;
    private double offsetZO;
    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;

    public BalloonEntity(EntityType<? extends BalloonEntity> type, Level level) {
        super(type, level);
    }

    public static boolean canHold(Entity target) {
        if (!target.isAlive() || target.isSpectator() || target.getType().is(ModTags.EntityTypes.BALLOON_UNATTACHABLE)) return false;
        if (target instanceof Boat) return BalloonTethers.count(target) < MAX_ON_BOAT;
        return target instanceof LivingEntity && BalloonTethers.count(target) < MAX_ON_ENTITY;
    }

    public static void tieTo(Entity target, DyeColor color) {
        BalloonEntity balloon = ModEntityTypes.BALLOON.get().create(target.level());
        if (balloon == null) return;
        balloon.setColor(color);
        balloon.attachTo(target);
        balloon.moveTo(balloon.knotPosition(1.0F));
        if (target.level().addFreshEntity(balloon)) {
            balloon.playTieSound();
        } else {
            balloon.unlink();
        }
    }

    public static boolean canTieToFence(Level level, BlockPos fence) {
        if (!level.getBlockState(fence).is(BlockTags.FENCES) || !clearAbove(level, fence)) return false;
        return tiedToFence(level, fence).size() < MAX_ON_FENCE;
    }

    public static void tieToFence(Level level, BlockPos fence, DyeColor color) {
        BalloonEntity balloon = ModEntityTypes.BALLOON.get().create(level);
        if (balloon == null) return;
        balloon.setColor(color);
        balloon.entityData.set(FENCE, Optional.of(fence.immutable()));
        balloon.moveTo(balloon.knotPosition(1.0F));
        if (level.addFreshEntity(balloon)) balloon.playTieSound();
    }

    public static void releaseAt(Level level, Vec3 pos, DyeColor color) {
        BalloonEntity balloon = ModEntityTypes.BALLOON.get().create(level);
        if (balloon == null) return;
        balloon.setColor(color);
        balloon.moveTo(pos);
        balloon.setDeltaMovement(0.0, 0.05, 0.0);
        level.addFreshEntity(balloon);
    }

    public static List<BalloonEntity> tiedToFence(Level level, BlockPos fence) {
        AABB area = new AABB(fence).expandTowards(0.0, 4.0, 0.0).inflate(2.0);
        List<BalloonEntity> tied = level.getEntitiesOfClass(BalloonEntity.class, area,
                balloon -> fence.equals(balloon.getFence().orElse(null)));
        tied.sort(Comparator.comparingInt(Entity::getId));
        return tied;
    }

    private static boolean clearAbove(Level level, BlockPos fence) {
        for (int i = 1; i <= 3; i++) {
            BlockState state = level.getBlockState(fence.above(i));
            if (!state.isAir() && !(state.canBeReplaced() && state.getFluidState().isEmpty())) return false;
        }
        return true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        builder.define(COLOR, (byte) DyeColor.WHITE.getId());
        builder.define(HOLDER, -1);
        builder.define(FENCE, Optional.empty());
    }

    public DyeColor getColor() {
        return DyeColor.byId(this.entityData.get(COLOR));
    }

    public void setColor(DyeColor color) {
        this.entityData.set(COLOR, (byte) color.getId());
    }

    public Optional<BlockPos> getFence() {
        return this.entityData.get(FENCE);
    }

    @Nullable
    public Entity getHolder() {
        return this.holder;
    }

    public boolean isTethered() {
        return this.holder != null || this.getFence().isPresent();
    }

    public void attachTo(Entity target) {
        this.unlink();
        this.pendingHolder = null;
        this.holder = target;
        this.entityData.set(HOLDER, target.getId());
        this.entityData.set(FENCE, Optional.empty());
        BalloonTethers.add(target, this);
    }

    public void release() {
        this.unlink();
        this.pendingHolder = null;
        this.entityData.set(HOLDER, -1);
        this.entityData.set(FENCE, Optional.empty());
        this.setDeltaMovement(0.0, 0.05, 0.0);
    }

    private void unlink() {
        if (this.holder != null) {
            BalloonTethers.remove(this.holder, this);
            this.holder = null;
        }
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (HOLDER.equals(key) && this.level().isClientSide) {
            this.syncHolder();
        }
    }

    private void syncHolder() {
        int id = this.entityData.get(HOLDER);
        if (this.holder != null && this.holder.getId() == id && !this.holder.isRemoved()) return;
        this.unlink();
        if (id < 0) return;
        Entity target = this.level().getEntity(id);
        if (target != null && !target.isRemoved()) {
            this.holder = target;
            BalloonTethers.add(target, this);
        }
    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        super.remove(reason);
        this.unlink();
    }

    @Override
    public void onClientRemoval() {
        super.onClientRemoval();
        this.unlink();
    }

    @Override
    public void tick() {
        super.tick();
        this.clearFire();
        this.offsetXO = this.offsetX;
        this.offsetZO = this.offsetZ;
        if (this.level().isClientSide) {
            this.syncHolder();
        } else {
            this.updateTether();
            if (this.isRemoved()) return;
        }
        if (this.isTethered()) {
            this.lerpSteps = 0;
            this.updateOffset();
            this.setPos(this.knotPosition(1.0F));
            this.setDeltaMovement(Vec3.ZERO);
            if (this.holder != null) {
                this.holder.resetFallDistance();
                if (!this.level().isClientSide && this.slot == 0 && this.tickCount % 20 == 0
                        && this.getY() > this.yo && this.getY() > this.level().getMaxBuildHeight() + 16) {
                    this.pop(false);
                }
            }
        } else if (this.level().isClientSide) {
            this.tickLerp();
        } else if (this.pendingHolder == null) {
            this.drift();
        }
    }

    private void updateTether() {
        if (this.pendingHolder != null) {
            Entity target = ((ServerLevel) this.level()).getEntity(this.pendingHolder);
            if (target != null && target.isAlive()) {
                this.attachTo(target);
            } else if (++this.pendingTicks > 1200) {
                this.release();
            }
            return;
        }
        if (this.holder != null) {
            if (this.holder.isRemoved()) {
                if (this.holder.getRemovalReason() == RemovalReason.UNLOADED_TO_CHUNK) {
                    UUID id = this.holder.getUUID();
                    this.unlink();
                    this.pendingHolder = id;
                    this.pendingTicks = 0;
                } else {
                    this.release();
                }
            } else if (!this.holder.isAlive() || this.holder.isSpectator() || this.holder.level() != this.level()) {
                this.release();
            }
            return;
        }
        Optional<BlockPos> fence = this.getFence();
        if (fence.isPresent() && this.tickCount % 10 == 0 && this.level().isLoaded(fence.get())
                && (!this.level().getBlockState(fence.get()).is(BlockTags.FENCES) || !clearAbove(this.level(), fence.get()))) {
            this.release();
        }
    }

    private void updateOffset() {
        if (this.holder != null) {
            List<BalloonEntity> group = BalloonTethers.balloons(this.holder);
            this.groupSize = Math.max(1, group.size());
            this.slot = Math.max(0, group.indexOf(this));
        } else if (this.tickCount % 10 == 1 || this.tickCount < 3) {
            List<BalloonEntity> group = tiedToFence(this.level(), this.getFence().orElseThrow());
            this.groupSize = Math.max(1, group.size());
            this.slot = Math.max(0, group.indexOf(this));
        }
        double targetX = 0.0;
        double targetZ = 0.0;
        if (!(this.holder instanceof Boat) && this.groupSize > 1) {
            double angle = Math.PI * 2.0 * this.slot / this.groupSize;
            double radius = this.holder != null ? 0.5 : 0.55;
            targetX = Math.cos(angle) * radius;
            targetZ = Math.sin(angle) * radius;
        }
        if (this.holder != null) {
            targetX -= Mth.clamp((this.holder.getX() - this.holder.xo) * 3.0, -1.0, 1.0);
            targetZ -= Mth.clamp((this.holder.getZ() - this.holder.zo) * 3.0, -1.0, 1.0);
        }
        this.offsetX += (targetX - this.offsetX) * 0.2;
        this.offsetZ += (targetZ - this.offsetZ) * 0.2;
    }

    private void drift() {
        double t = this.level().getGameTime();
        double phase = this.phase();
        Vec3 motion = this.getDeltaMovement();
        this.setDeltaMovement(
                motion.x * 0.9 + Math.sin(t * 0.031 + phase) * 0.003,
                motion.y + (0.08 - motion.y) * 0.1,
                motion.z * 0.9 + Math.cos(t * 0.027 + phase * 1.3) * 0.003);
        this.move(MoverType.SELF, this.getDeltaMovement());
        if (this.getY() > this.level().getMaxBuildHeight() + 16) {
            this.pop(false);
        }
    }

    private void tickLerp() {
        if (this.lerpSteps <= 0) return;
        double x = this.getX() + (this.lerpX - this.getX()) / this.lerpSteps;
        double y = this.getY() + (this.lerpY - this.getY()) / this.lerpSteps;
        double z = this.getZ() + (this.lerpZ - this.getZ()) / this.lerpSteps;
        this.lerpSteps--;
        this.setPos(x, y, z);
    }

    @Override
    public void lerpTo(double x, double y, double z, float yRot, float xRot, int steps) {
        if (this.isTethered()) return;
        this.lerpX = x;
        this.lerpY = y;
        this.lerpZ = z;
        this.lerpSteps = steps + 2;
    }

    public double phase() {
        return (this.getUUID().getLeastSignificantBits() & 0xFFFF) / 65536.0 * Math.PI * 2.0;
    }

    private double lift() {
        return this.holder instanceof LivingEntity ? 1.4 : 1.6;
    }

    @Nullable
    public Vec3 anchorPosition(float partialTick) {
        if (this.holder != null) {
            Vec3 base = this.holder.getPosition(partialTick);
            if (this.holder instanceof Boat) {
                double[] corner = BOAT_CORNERS[this.slot % BOAT_CORNERS.length];
                float yaw = this.holder.getViewYRot(partialTick);
                Vec3 offset = new Vec3(corner[0], 0.0, corner[1]).yRot((float) (-yaw * Mth.DEG_TO_RAD - Math.PI / 2.0));
                return base.add(offset.x, this.holder.getBbHeight(), offset.z);
            }
            return base.add(0.0, this.holder.getBbHeight(), 0.0);
        }
        return this.getFence().map(fence -> Vec3.atBottomCenterOf(fence).add(0.0, 1.0, 0.0)).orElse(null);
    }

    public Vec3 knotPosition(float partialTick) {
        Vec3 anchor = this.anchorPosition(partialTick);
        if (anchor == null) return this.getPosition(partialTick);
        double t = this.level().getGameTime() + partialTick;
        double phase = this.phase();
        double swayX = Math.sin(t * 0.05 + phase) * 0.12 + Math.sin(t * 0.021 + phase * 3.0) * 0.08;
        double swayZ = Math.cos(t * 0.043 + phase * 2.0) * 0.12 + Math.cos(t * 0.017 + phase * 5.0) * 0.08;
        return anchor.add(
                Mth.lerp(partialTick, this.offsetXO, this.offsetX) + swayX,
                this.lift(),
                Mth.lerp(partialTick, this.offsetZO, this.offsetZ) + swayZ);
    }

    public void pop(boolean drop) {
        if (!(this.level() instanceof ServerLevel level) || this.isRemoved()) return;
        ItemStack stack = new ItemStack(BalloonItem.byColor(this.getColor()));
        this.playSound(ModSounds.BALLOON_POP.get(), 1.0F, 0.9F + this.random.nextFloat() * 0.3F);
        level.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, stack),
                this.getX(), this.getY() + 0.4, this.getZ(), 12, 0.15, 0.15, 0.15, 0.08);
        this.gameEvent(GameEvent.ENTITY_DIE);
        if (drop) this.spawnAtLocation(stack);
        this.discard();
    }

    private void playTieSound() {
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.BALLOON_TIE.get(), SoundSource.PLAYERS, 1.0F, 0.9F + this.random.nextFloat() * 0.2F);
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) return false;
        if (!this.level().isClientSide) {
            this.pop(!source.isCreativePlayer());
        }
        return true;
    }

    @Override
    public boolean skipAttackInteraction(@NotNull Entity attacker) {
        return attacker instanceof Player player && this.hurt(this.damageSources().playerAttack(player), 0.0F);
    }

    @Override
    public @NotNull InteractionResult interact(Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof DyeItem dye && dye.getDyeColor() != this.getColor()) {
            if (!this.level().isClientSide) {
                this.setColor(dye.getDyeColor());
                stack.consume(1, player);
                this.level().playSound(null, this, SoundEvents.DYE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    public @NotNull ItemStack getPickResult() {
        return new ItemStack(BalloonItem.byColor(this.getColor()));
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    public boolean shouldBeSaved() {
        return !(this.holder instanceof Player) && super.shouldBeSaved();
    }

    @Override
    public boolean canUsePortal(boolean allowPassengers) {
        return false;
    }

    @Override
    public boolean displayFireAnimation() {
        return false;
    }

    @Override
    protected @NotNull MovementEmission getMovementEmission() {
        return MovementEmission.NONE;
    }

    @Override
    public @NotNull AABB getBoundingBoxForCulling() {
        return super.getBoundingBoxForCulling().inflate(2.5);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        double range = 96.0 * getViewScale();
        return distance < range * range;
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
        tag.putString("Color", this.getColor().getName());
        if (this.holder != null) {
            tag.putUUID("Holder", this.holder.getUUID());
        } else if (this.pendingHolder != null) {
            tag.putUUID("Holder", this.pendingHolder);
        }
        this.getFence().ifPresent(fence -> tag.put("Fence", NbtUtils.writeBlockPos(fence)));
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
        this.setColor(DyeColor.byName(tag.getString("Color"), DyeColor.WHITE));
        if (tag.hasUUID("Holder")) {
            this.pendingHolder = tag.getUUID("Holder");
        }
        NbtUtils.readBlockPos(tag, "Fence").ifPresent(fence -> this.entityData.set(FENCE, Optional.of(fence)));
    }
}
