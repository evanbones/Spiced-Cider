package com.evandev.spicedcider.entities.sputter;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class SputterEntity extends AbstractGolem implements RangedAttackMob {
    private static final EntityDataAccessor<ItemStack> DATA_ARROW_STACK = SynchedEntityData.defineId(SputterEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Integer> DATA_WARPAINT_COLOR = SynchedEntityData.defineId(SputterEntity.class, EntityDataSerializers.INT);

    public SputterEntity(EntityType<? extends AbstractGolem> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.2);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.25, 20, 10.0F));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0, 1.0000001E-5F));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Mob.class, 10, true, false, entity -> entity instanceof Enemy));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ARROW_STACK, ItemStack.EMPTY);
        builder.define(DATA_WARPAINT_COLOR, -1);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide && this.random.nextInt(5) == 0) {
            PotionContents potion = getItem().get(DataComponents.POTION_CONTENTS);
            if (potion != null && !potion.equals(PotionContents.EMPTY)) {
                this.level().addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, potion.getColor()),
                        this.getRandomX(0.1), this.getRandomY(), this.getRandomZ(0.1), 0.0, 0.0, 0.0);
            }
        }
    }

    public ItemStack getItem() {
        return this.entityData.get(DATA_ARROW_STACK);
    }

    public void setItem(ItemStack stack) {
        this.entityData.set(DATA_ARROW_STACK, stack);
    }

    @Nullable
    public DyeColor getWarpaintColor() {
        int color = this.entityData.get(DATA_WARPAINT_COLOR);
        return color == -1 ? null : DyeColor.byId(color);
    }

    public void setWarpaintColor(@Nullable DyeColor color) {
        this.entityData.set(DATA_WARPAINT_COLOR, color == null ? -1 : color.getId());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (!getItem().isEmpty()) {
            tag.put("Item", getItem().save(this.registryAccess()));
        }
        DyeColor color = getWarpaintColor();
        tag.putByte("WarpaintColor", (byte) (color == null ? -1 : color.getId()));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Item")) {
            setItem(ItemStack.parseOptional(this.registryAccess(), tag.getCompound("Item")));
        }
        if (tag.contains("WarpaintColor", 99)) {
            int color = tag.getByte("WarpaintColor");
            setWarpaintColor(color < 0 ? null : DyeColor.byId(color));
        }
    }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        if (!getItem().isEmpty()) {
            this.spawnAtLocation(getItem());
            setItem(ItemStack.EMPTY);
        }
    }

    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        ItemStack stack = getItem();
        if (stack.isEmpty()) return;
        ArrowItem arrowItem = stack.getItem() instanceof ArrowItem item ? item : (ArrowItem) Items.ARROW;
        AbstractArrow projectile = arrowItem.createArrow(this.level(), stack, this, null);
        double dx = target.getX() - this.getX();
        double dy = target.getY(0.3333333333333333) - projectile.getY();
        double dz = target.getZ() - this.getZ();
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        projectile.shoot(dx, dy + horizontal * 0.2, dz, 1.6F, 12.0F);
        projectile.setCritArrow(true);
        projectile.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
        if (projectile instanceof Arrow arrow) {
            for (MobEffectInstance effect : this.getActiveEffects()) {
                arrow.addEffect(new MobEffectInstance(effect));
            }
        }
        this.level().addFreshEntity(projectile);
        stack.shrink(1);
        setItem(stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        boolean loading = getItem().isEmpty() && held.is(ItemTags.ARROWS);
        DyeColor dye = held.isEmpty() ? null : DyeColor.getColor(held);
        boolean painting = dye != null && dye != getWarpaintColor();
        boolean unloading = !getItem().isEmpty() && held.isEmpty();
        if (!loading && !painting && !unloading) return super.mobInteract(player, hand);
        if (this.level().isClientSide) return InteractionResult.SUCCESS;

        if (loading) {
            setItem(held.copy());
            if (!player.getAbilities().instabuild) {
                player.setItemInHand(hand, ItemStack.EMPTY);
            }
            this.navigation.stop();
        } else if (painting) {
            setWarpaintColor(dye);
            held.consume(1, player);
        } else {
            player.setItemInHand(hand, getItem());
            setItem(ItemStack.EMPTY);
        }
        return InteractionResult.SUCCESS;
    }
}
