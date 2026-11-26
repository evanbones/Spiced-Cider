package com.evandev.spicedcider.entities.leadweight;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.content.block.lead.LeadWeightBlock;
import com.evandev.spicedcider.registry.ModDataMaps;
import com.evandev.spicedcider.registry.ModEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class LeadWeightEntity extends FallingBlockEntity {
    public static final ResourceKey<DamageType> DAMAGE_TYPE = ResourceKey.create(Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "lead_weight"));
    public static final float BASE_DAMAGE = 6.0F;
    public static final float DAMAGE_PER_BLOCK = 2.0F;
    private static final float SMASH_THRESHOLD = 1.5F;
    private static final double KNOCKBACK_POWER = 0.2;
    private static final double KNOCKBACK_DOWN = 0.3;
    private static final float MOMENTUM_LOSS_PER_HIT = 0.8F;
    private static final float MIN_MOMENTUM = 0.1F;
    private static final double SPEED_KEPT_PER_HIT = 0.85;

    @Nullable
    private UUID ownerUUID;
    private final Set<Integer> hitEntityIds = new HashSet<>();
    private float momentum = 1.0F;
    private boolean wasInWater;
    private boolean settled;

    public LeadWeightEntity(EntityType<? extends FallingBlockEntity> type, Level level) {
        super(type, level);
    }

    public static LeadWeightEntity fromBlock(Level level, BlockPos pos, BlockState state, @Nullable Player owner) {
        BlockState fallingState = state.hasProperty(BlockStateProperties.WATERLOGGED)
                ? state.setValue(BlockStateProperties.WATERLOGGED, false) : state;
        LeadWeightEntity weight = create(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, fallingState, owner);
        level.setBlock(pos, state.getFluidState().createLegacyBlock(), 3);
        level.addFreshEntity(weight);
        return weight;
    }

    public static LeadWeightEntity inAir(Level level, double x, double y, double z, BlockState state, @Nullable Player owner) {
        LeadWeightEntity weight = create(level, x, y, z, state, owner);
        level.addFreshEntity(weight);
        return weight;
    }

    private static LeadWeightEntity create(Level level, double x, double y, double z, BlockState state, @Nullable Player owner) {
        LeadWeightEntity weight = new LeadWeightEntity(ModEntityTypes.LEAD_WEIGHT.get(), level);
        weight.blockState = state;
        weight.blocksBuilding = true;
        weight.setPos(x, y, z);
        weight.setDeltaMovement(Vec3.ZERO);
        weight.xo = x;
        weight.yo = y;
        weight.zo = z;
        weight.setStartPos(weight.blockPosition());
        if (owner != null) {
            weight.ownerUUID = owner.getUUID();
        }
        level.playSound(null, x, y, z, SoundEvents.ANVIL_FALL, SoundSource.BLOCKS, 0.9F, 0.6F);
        return weight;
    }

    @Override
    public void tick() {
        if (!level().isClientSide() && isAlive()) {
            crushEntitiesInPath();
            handleWater();
        }
        super.tick();
        if (!level().isClientSide() && isRemoved()) {
            convertHopperLanding();
        }
    }

    private void convertHopperLanding() {
        BlockPos pos = blockPosition();
        if (!(level().getBlockState(pos).getBlock() instanceof LeadWeightBlock)) return;
        if (!(level().getBlockState(pos.below()).getBlock() instanceof HopperBlock)) return;
        level().removeBlock(pos, false);
        spawnAtLocation(getBlockState().getBlock());
    }

    public boolean settleOnImpactedBlock(Level level, BlockPos pos) {
        BlockState inside = level.getBlockState(pos);
        ModDataMaps.ImpactTransform transform = inside.getBlockHolder().getData(ModDataMaps.LEAD_WEIGHT_IMPACTS);
        BlockPos above = pos.above();
        if (transform == null || getStartPos().getY() - pos.getY() < LeadWeightBlock.TRANSFORM_FALL_DISTANCE
                || !level.getBlockState(above).canBeReplaced()) {
            return false;
        }
        level.setBlockAndUpdate(pos, transform.block().withPropertiesOf(inside));
        BlockState placed = getBlockState();
        if (placed.hasProperty(BlockStateProperties.WATERLOGGED)) {
            placed = placed.setValue(BlockStateProperties.WATERLOGGED, level.getFluidState(above).is(FluidTags.WATER) && level.getFluidState(above).isSource());
        }
        level.setBlock(above, placed, 3);
        settled = true;
        if (placed.getBlock() instanceof LeadWeightBlock block) {
            block.onLand(level, above, placed, inside, this);
        }
        return true;
    }

    @Override
    public ItemEntity spawnAtLocation(ItemLike item) {
        return settled ? null : super.spawnAtLocation(item);
    }

    private void handleWater() {
        boolean inWater = waterInFallPath();
        if (inWater && !wasInWater && level() instanceof ServerLevel server) {
            double x = getX(), y = getY(), z = getZ();
            server.playSound(null, x, y, z, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 1.0F, 0.8F);
            server.sendParticles(ParticleTypes.SPLASH, x, y + 0.1, z, 40, 0.3, 0.1, 0.3, 0.2);
            server.sendParticles(ParticleTypes.BUBBLE, x, y, z, 20, 0.25, 0.1, 0.25, 0.1);
        }
        wasInWater = inWater;
    }

    private boolean waterInFallPath() {
        double startY = getY();
        double endY = startY + Math.min(0.0, getDeltaMovement().y) - 0.04;
        int x = Mth.floor(getX());
        int z = Mth.floor(getZ());
        for (int y = Mth.floor(startY); y >= Mth.floor(endY); y--) {
            if (level().getFluidState(new BlockPos(x, y, z)).is(FluidTags.WATER)) return true;
        }
        return false;
    }

    private void crushEntitiesInPath() {
        double fall = getStartPos().getY() - getY();
        if (fall < SMASH_THRESHOLD || momentum < MIN_MOMENTUM) return;
        float damage = BASE_DAMAGE + DAMAGE_PER_BLOCK * (float) fall;
        Player owner = ownerUUID == null ? null : level().getPlayerByUUID(ownerUUID);
        DamageSource source = damageSource(owner);
        for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(0.1))) {
            if (target == owner || !hitEntityIds.add(target.getId())) continue;
            if (momentum < MIN_MOMENTUM) break;
            if (target.hurt(source, damage * momentum)) {
                knockBack(target);
                momentum *= MOMENTUM_LOSS_PER_HIT;
                setDeltaMovement(getDeltaMovement().multiply(1.0, SPEED_KEPT_PER_HIT, 1.0));
            }
        }
    }

    private void knockBack(LivingEntity target) {
        double dx = getX() - target.getX();
        double dz = getZ() - target.getZ();
        if (dx * dx + dz * dz < 1.0E-8) {
            double angle = random.nextDouble() * Math.PI * 2.0;
            dx = Math.cos(angle);
            dz = Math.sin(angle);
        }
        target.knockback(KNOCKBACK_POWER, dx, dz);
        target.setDeltaMovement(target.getDeltaMovement().add(0.0, -KNOCKBACK_DOWN, 0.0));
        target.hurtMarked = true;
    }

    private DamageSource damageSource(@Nullable Player owner) {
        var type = level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DAMAGE_TYPE);
        return owner != null ? new DamageSource(type, this, owner) : new DamageSource(type, this);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (ownerUUID != null) tag.putUUID("Owner", ownerUUID);
        tag.putFloat("Momentum", momentum);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID("Owner")) ownerUUID = tag.getUUID("Owner");
        if (tag.contains("Momentum")) momentum = tag.getFloat("Momentum");
    }
}
