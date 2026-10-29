package com.evandev.spicedcider.entities.strawgolem;

import com.evandev.spicedcider.content.handler.StrawGolemEvents;
import com.evandev.spicedcider.entities.strawgolem.goals.GolemAvoidEntityGoal;
import com.evandev.spicedcider.entities.strawgolem.goals.GolemDepositGoal;
import com.evandev.spicedcider.entities.strawgolem.goals.GolemGrabGoal;
import com.evandev.spicedcider.entities.strawgolem.goals.GolemHarvestGoal;
import com.evandev.spicedcider.entities.strawgolem.goals.GolemWanderGoal;
import com.evandev.spicedcider.registry.ModItems;
import com.evandev.spicedcider.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class StrawGolem extends AbstractGolem {
    public static final float MAX_HEALTH = 6.0F;
    public static final int BARREL_HEALTH = 100;
    public static final double MOVEMENT_SPEED = 0.23;
    public static final double WALK_SPEED = 0.5;
    public static final double RUN_SPEED = 0.8;
    public static final int WANDER_RANGE = 24;
    public static final int TETHER_RANGE = 12;
    public static final float FLEE_RANGE = 15.0F;
    public static final int SEARCH_RANGE = 24;
    public static final int SEARCH_RANGE_VERTICAL = 3;
    public static final double REACH = 1.5;

    private static final EntityDataAccessor<Boolean> HAT = SynchedEntityData.defineId(StrawGolem.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> PANIC = SynchedEntityData.defineId(StrawGolem.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> PICKUP_STATUS = SynchedEntityData.defineId(StrawGolem.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> BARREL = SynchedEntityData.defineId(StrawGolem.class, EntityDataSerializers.INT);

    public final AnimationState harvestAnimationState = new AnimationState();
    public final StrawGolemDeliverer deliverer = new StrawGolemDeliverer(this);
    @Nullable
    private BlockPos priorityPos;

    public StrawGolem(EntityType<? extends StrawGolem> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(Attributes.MAX_HEALTH, MAX_HEALTH);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(0, new PanicGoal(this, RUN_SPEED * 1.2));
        goalSelector.addGoal(1, new GolemAvoidEntityGoal<>(this, Sheep.class, mob -> mob.getTarget() instanceof StrawGolem, FLEE_RANGE, WALK_SPEED, RUN_SPEED, EntitySelector.NO_CREATIVE_OR_SPECTATOR::test));
        goalSelector.addGoal(1, new GolemAvoidEntityGoal<>(this, Cow.class, mob -> mob.getTarget() instanceof StrawGolem, FLEE_RANGE, WALK_SPEED, RUN_SPEED, EntitySelector.NO_CREATIVE_OR_SPECTATOR::test));
        goalSelector.addGoal(1, new GolemAvoidEntityGoal<>(this, Pig.class, mob -> mob.getTarget() instanceof StrawGolem, FLEE_RANGE, WALK_SPEED, RUN_SPEED, EntitySelector.NO_CREATIVE_OR_SPECTATOR::test));
        goalSelector.addGoal(1, new GolemDepositGoal(this));
        goalSelector.addGoal(1, new GolemHarvestGoal(this));
        goalSelector.addGoal(1, new GolemGrabGoal(this));
        goalSelector.addGoal(2, new GolemWanderGoal(this));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HAT, false);
        builder.define(PANIC, false);
        builder.define(PICKUP_STATUS, 0);
        builder.define(BARREL, 0);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (PICKUP_STATUS.equals(key)) {
            if (pickupStatus() != 0) {
                harvestAnimationState.start(tickCount);
            } else {
                harvestAnimationState.stop();
            }
        }
    }

    @Override
    public void tick() {
        if (level() instanceof ServerLevel && isAlive()) {
            setPanic(isRunningScaredGoal());
            if (isPanicking()) {
                dropHeldItem();
            }
        }
        super.tick();
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND) {
            return super.mobInteract(player, hand);
        }
        ItemStack stack = player.getItemInHand(hand);
        boolean client = level().isClientSide;
        if (stack.isEmpty()) {
            if (!player.isShiftKeyDown()) {
                return super.mobInteract(player, hand);
            }
            if (!client) {
                playSound(ModSounds.STRAW_GOLEM_HAPPY.get());
                boolean ordering = StrawGolemEvents.toggleOrdering(player, this);
                player.displayClientMessage(Component.translatable(ordering ? "entity.spicedcider.straw_golem.ordering.start" : "entity.spicedcider.straw_golem.ordering.stop"), true);
            }
            return InteractionResult.sidedSuccess(client);
        }

        SoundEvent sound;
        if (stack.is(Items.BARREL) && barrelHealth() != BARREL_HEALTH) {
            sound = ModSounds.STRAW_GOLEM_INTERESTED.get();
            if (!client) {
                entityData.set(BARREL, BARREL_HEALTH);
            }
        } else if (stack.is(Items.WHEAT) && getHealth() < getMaxHealth()) {
            sound = ModSounds.STRAW_GOLEM_INTERESTED.get();
            if (!client) {
                heal(3.0F);
            }
        } else if (stack.is(ModItems.STRAW_HAT.get()) && !hasHat()) {
            sound = ModSounds.STRAW_GOLEM_INTERESTED.get();
            if (!client) {
                entityData.set(HAT, true);
            }
        } else if (stack.is(Items.BRUSH)) {
            sound = ModSounds.STRAW_GOLEM_HAPPY.get();
        } else {
            return InteractionResult.PASS;
        }

        if (!client) {
            playSound(ModSounds.STRAW_GOLEM_HAPPY.get());
            playSound(sound);
            if (!stack.is(Items.BRUSH)) {
                stack.consume(1, player);
            }
        }
        return InteractionResult.sidedSuccess(client);
    }

    @Override
    protected void actuallyHurt(DamageSource source, float amount) {
        if (hasBarrel()) {
            if (barrelHealth() - amount > 0) {
                entityData.set(BARREL, (int) (barrelHealth() - amount));
                return;
            }
            amount -= barrelHealth();
            entityData.set(BARREL, 0);
            playSound(SoundEvents.SHIELD_BREAK);
        }
        super.actuallyHurt(source, amount);
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, source, recentlyHit);
        if (hasHat()) {
            spawnAtLocation(ModItems.STRAW_HAT.get());
        }
    }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        dropHeldItem();
    }

    private void dropHeldItem() {
        ItemStack held = getMainHandItem();
        if (!held.isEmpty()) {
            spawnAtLocation(held);
            setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }
    }

    @Override
    protected float getEquipmentDropChance(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? 0.0F : super.getEquipmentDropChance(slot);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.STRAW_GOLEM_AMBIENT.get();
    }

    @Override
    public int getAmbientSoundInterval() {
        return 50;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return hasBarrel() ? SoundEvents.SHIELD_BLOCK : ModSounds.STRAW_GOLEM_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.STRAW_GOLEM_DEATH.get();
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(HAT, tag.getBoolean("hat"));
        entityData.set(BARREL, tag.getInt("barrel_health"));
        priorityPos = NbtUtils.readBlockPos(tag, "priority_pos").orElse(null);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("hat", hasHat());
        tag.putInt("barrel_health", barrelHealth());
        if (priorityPos != null) {
            tag.put("priority_pos", NbtUtils.writeBlockPos(priorityPos));
        }
    }

    public int healthStatus() {
        float health = getHealth();
        return health / MAX_HEALTH > 0.8F ? 0 : health > MAX_HEALTH / 3.0F ? 1 : 2;
    }

    public int carryStatus() {
        ItemStack held = getMainHandItem();
        if (held.isEmpty()) {
            return 0;
        }
        return isBlock(held) ? 2 : 1;
    }

    public static boolean isBlock(ItemStack stack) {
        return stack.getItem() instanceof BlockItem && !(stack.getItem() instanceof ItemNameBlockItem);
    }

    public boolean holdsItemAbove() {
        int carry = carryStatus();
        return carry == 2 || carry == 1 && hasBarrel();
    }

    public int pickupStatus() {
        return entityData.get(PICKUP_STATUS);
    }

    public void setPickupStatus(int status) {
        entityData.set(PICKUP_STATUS, status);
    }

    public void setPickupStatus(@Nullable ItemStack stack) {
        setPickupStatus(stack == null || stack.isEmpty() ? 0 : isBlock(stack) ? 2 : 1);
    }

    public boolean isHarvestingBlock() {
        return pickupStatus() == 2 || hasBarrel();
    }

    public boolean hasHat() {
        return entityData.get(HAT);
    }

    public void setHat(boolean hat) {
        entityData.set(HAT, hat);
    }

    public int barrelHealth() {
        return entityData.get(BARREL);
    }

    public boolean hasBarrel() {
        return barrelHealth() > 0;
    }

    public void setBarrelHealth(int health) {
        entityData.set(BARREL, health);
    }

    @Nullable
    public BlockPos getPriorityPos() {
        return priorityPos;
    }

    public void setPriorityPos(@Nullable BlockPos pos) {
        priorityPos = pos;
    }

    public boolean isPanicking() {
        return entityData.get(PANIC);
    }

    public void setPanic(boolean panic) {
        entityData.set(PANIC, panic);
    }

    private boolean isInRain() {
        BlockPos pos = blockPosition();
        return level().isRainingAt(pos) || level().isRainingAt(BlockPos.containing(pos.getX(), getBoundingBox().maxY, pos.getZ()));
    }

    private boolean isCold() {
        return !level().getBiome(blockPosition()).value().warmEnoughToRain(blockPosition());
    }

    public boolean shouldShiver() {
        return isInWater() || isInPowderSnow || !hasHat() && isInRain() || isCold();
    }

    private boolean isRunningScaredGoal() {
        for (WrappedGoal goal : goalSelector.getAvailableGoals()) {
            if (goal.isRunning() && (goal.getGoal() instanceof PanicGoal || goal.getGoal() instanceof GolemAvoidEntityGoal<?>)) {
                return true;
            }
        }
        return false;
    }
}
