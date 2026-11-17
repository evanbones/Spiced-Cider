package com.evandev.spicedcider.entities.sporal;

import com.evandev.spicedcider.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Sporal extends Monster {
    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(Sporal.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_DASHING = SynchedEntityData.defineId(Sporal.class, EntityDataSerializers.BOOLEAN);

    public static final byte BESTOW_EVENT = 4;
    private static final double DASH_FALL_GRAVITY = 0.01;
    private static final double FALL_GRAVITY = 0.04;
    private static final float LEAP_BLEND_IN = 0.25F;
    private static final float LEAP_BLEND_OUT = 0.15F;
    private static final int DASH_COOLDOWN = 120;
    private static final double DASH_HORIZONTAL_SPEED = 0.9;
    private static final double DASH_VERTICAL_SPEED = 0.45;

    public final AnimationState bestowAnimationState = new AnimationState();

    @Nullable
    private LivingEntity supported;
    private int dashCooldown;
    private int dashTicks;
    @Nullable
    private Vec3 pendingDashThreat;
    private float leapBlend;
    private float leapBlendO;

    public Sporal(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 16.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.FOLLOW_RANGE, 24.0)
                .add(Attributes.STEP_HEIGHT, 1.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SporalFleeGoal(this, 12.0F, 1.0, 1.3));
        this.goalSelector.addGoal(2, new SporalSupportGoal(this, 16.0, 1.15));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, Variant.PINK.ordinal());
        builder.define(DATA_DASHING, false);
    }

    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor level, @NotNull DifficultyInstance difficulty, @NotNull MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        if (spawnType == MobSpawnType.NATURAL || spawnType == MobSpawnType.CHUNK_GENERATION) {
            this.setVariant(Variant.forBiome(level.getBiome(this.blockPosition()), this.random));
        } else {
            this.setVariant(Variant.random(this.random.nextFloat()));
        }
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            this.leapBlendO = this.leapBlend;
            this.leapBlend = this.isDashing() ? Math.min(1.0F, this.leapBlend + LEAP_BLEND_IN) : Math.max(0.0F, this.leapBlend - LEAP_BLEND_OUT);
            this.spawnBloomParticles();
            return;
        }

        if (this.dashCooldown > 0) {
            this.dashCooldown--;
        }

        if (this.isDashing()) {
            this.dashTicks++;
            if (this.dashTicks > 2 && (this.onGround() || this.isInWater())) {
                this.setDashing(false);
            }
        }

        if (this.pendingDashThreat != null && this.onGround()) {
            if (this.canDash()) {
                this.dashAwayFrom(this.pendingDashThreat);
            }
            this.pendingDashThreat = null;
        }
    }

    public float getLeapBlend(float partialTick) {
        return Mth.lerp(partialTick, this.leapBlendO, this.leapBlend);
    }

    private void spawnBloomParticles() {
        int chance = this.isDashing() ? 2 : 40;
        if (this.random.nextInt(chance) != 0) return;

        double x = this.getX() + (this.random.nextDouble() - 0.5) * this.getBbWidth();
        double y = this.getY() + this.getBbHeight() * (this.isDashing() ? this.random.nextDouble() : 0.75);
        double z = this.getZ() + (this.random.nextDouble() - 0.5) * this.getBbWidth();
        this.level().addParticle(ParticleTypes.SPORE_BLOSSOM_AIR, x, y, z, 0.0, 0.0, 0.0);
    }

    public boolean canDash() {
        return this.dashCooldown <= 0 && this.onGround() && !this.isInWater() && !this.isPassenger();
    }

    public void dashTowards(Vec3 target) {
        Vec3 direction = new Vec3(target.x - this.getX(), 0.0, target.z - this.getZ());
        if (direction.lengthSqr() < 1.0E-4) return;

        direction = direction.normalize();
        this.getNavigation().stop();
        this.setDeltaMovement(direction.x * DASH_HORIZONTAL_SPEED, DASH_VERTICAL_SPEED, direction.z * DASH_HORIZONTAL_SPEED);
        this.hasImpulse = true;

        float yaw = (float) (Mth.atan2(direction.z, direction.x) * Mth.RAD_TO_DEG) - 90.0F;
        this.setYRot(yaw);
        this.yBodyRot = yaw;
        this.yHeadRot = yaw;

        this.dashCooldown = DASH_COOLDOWN + this.random.nextInt(20);
        this.dashTicks = 0;
        this.setDashing(true);
        this.playSound(SoundEvents.BREEZE_JUMP, 0.4F, 1.4F + this.random.nextFloat() * 0.2F);
    }

    public void dashAwayFrom(Vec3 threat) {
        this.dashTowards(this.position().scale(2.0).subtract(threat));
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        boolean hurt = super.hurt(source, amount);
        if (hurt && !this.level().isClientSide() && source.getEntity() instanceof LivingEntity attacker) {
            if (this.canDash()) {
                this.dashAwayFrom(attacker.position());
            } else if (!this.onGround() && !this.isDashing()) {
                this.pendingDashThreat = attacker.position();
            }
        }
        return hurt;
    }

    @Override
    protected double getDefaultGravity() {
        double gravity = super.getDefaultGravity();
        if (this.getDeltaMovement().y > 0.0) return gravity;
        return Math.min(gravity, this.isDashing() ? DASH_FALL_GRAVITY : FALL_GRAVITY);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, @NotNull DamageSource source) {
        return false;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == BESTOW_EVENT) {
            this.bestowAnimationState.start(this.tickCount);
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Nullable
    public LivingEntity getSupported() {
        return this.supported;
    }

    public void setSupported(@Nullable LivingEntity supported) {
        this.supported = supported;
    }

    public boolean isSupporting() {
        return this.supported != null && this.supported.isAlive();
    }

    public boolean isDashing() {
        return this.entityData.get(DATA_DASHING);
    }

    private void setDashing(boolean dashing) {
        this.entityData.set(DATA_DASHING, dashing);
    }

    public Variant getVariant() {
        return Variant.byId(this.entityData.get(DATA_VARIANT));
    }

    public void setVariant(Variant variant) {
        this.entityData.set(DATA_VARIANT, variant.ordinal());
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("Variant", this.getVariant().getSerializedName());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Variant", CompoundTag.TAG_STRING)) {
            this.setVariant(Variant.byName(tag.getString("Variant")));
        }
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource source) {
        return SoundEvents.AZALEA_LEAVES_HIT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.AZALEA_LEAVES_BREAK;
    }

    @Override
    protected void playStepSound(@NotNull BlockPos pos, @NotNull BlockState state) {
        this.playSound(SoundEvents.FLOWERING_AZALEA_STEP, 0.3F, 1.2F);
    }

    public enum Variant implements StringRepresentable {
        PINK("pink"),
        RED("red"),
        WHITE("white");

        private static final float WHITE_CHANCE = 0.05F;
        private static final float SWAMP_WHITE_CHANCE = 0.1F;

        private final String name;

        Variant(String name) {
            this.name = name;
        }

        public static Variant random(float roll) {
            if (roll < WHITE_CHANCE) return WHITE;
            return roll < WHITE_CHANCE + (1.0F - WHITE_CHANCE) / 2.0F ? PINK : RED;
        }

        public static Variant forBiome(Holder<Biome> biome, RandomSource random) {
            if (biome.is(ModTags.Biomes.PINK_SPORAL_SPAWNS) && random.nextBoolean()) return PINK;
            if (biome.is(ModTags.Biomes.WHITE_SPORAL_SPAWNS) && random.nextFloat() < SWAMP_WHITE_CHANCE) return WHITE;
            return RED;
        }

        public static Variant byId(int id) {
            Variant[] variants = values();
            return id >= 0 && id < variants.length ? variants[id] : PINK;
        }

        public static Variant byName(String name) {
            for (Variant variant : values()) {
                if (variant.name.equals(name)) {
                    return variant;
                }
            }
            return PINK;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }
    }
}
