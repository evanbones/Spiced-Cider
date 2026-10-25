package com.evandev.spicedcider.compat.embersandicicles;

import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.fml.ModList;

public final class IceCubes {

    private static final ResourceLocation ICE_CUBES = ResourceLocation.fromNamespaceAndPath("neapolitan", "ice_cubes");
    private static final ResourceLocation ICE_BALL = ResourceLocation.fromNamespaceAndPath("embers_and_icicles", "ice_ball");
    private static final ResourceLocation THROW_SOUND = ResourceLocation.fromNamespaceAndPath("embers_and_icicles", "entity.ice_ball.throw");

    private static Boolean active;

    public static boolean isActive() {
        if (active == null) {
            active = ModList.get().isLoaded("embers_and_icicles") && ModList.get().isLoaded("neapolitan");
        }
        return active;
    }

    public static Item item() {
        return BuiltInRegistries.ITEM.get(ICE_CUBES);
    }

    public static Item itemOr(Item fallback) {
        Item item = item();
        return item == Items.AIR ? fallback : item;
    }

    public static boolean is(ItemStack stack) {
        return isActive() && stack.is(item());
    }

    private static ThrowableItemProjectile create(Level level) {
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(ICE_BALL);
        return type.create(level) instanceof ThrowableItemProjectile projectile ? projectile : null;
    }

    public static void throwFrom(Level level, LivingEntity thrower, ItemStack stack) {
        SoundEvent sound = BuiltInRegistries.SOUND_EVENT.get(THROW_SOUND);
        if (sound != null) {
            level.playSound(null, thrower.getX(), thrower.getY(), thrower.getZ(), sound, SoundSource.NEUTRAL,
                    0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
        }
        if (level.isClientSide()) return;
        ThrowableItemProjectile projectile = create(level);
        if (projectile == null) return;
        projectile.setOwner(thrower);
        projectile.setPos(thrower.getX(), thrower.getEyeY() - 0.1F, thrower.getZ());
        projectile.setItem(stack.copyWithCount(1));
        projectile.shootFromRotation(thrower, thrower.getXRot(), thrower.getYRot(), 0.0F, 1.5F, 1.0F);
        level.addFreshEntity(projectile);
    }

    public static void registerDispenserBehavior() {
        if (!isActive()) return;
        DispenserBlock.registerBehavior(item(), new DefaultDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource source, ItemStack stack) {
                ServerLevel level = source.level();
                ThrowableItemProjectile projectile = create(level);
                if (projectile == null) return super.execute(source, stack);
                Direction direction = source.state().getValue(DispenserBlock.FACING);
                var pos = DispenserBlock.getDispensePosition(source);
                projectile.setPos(pos.x(), pos.y(), pos.z());
                projectile.setItem(stack.copyWithCount(1));
                projectile.shoot(direction.getStepX(), direction.getStepY() + 0.1F, direction.getStepZ(), 1.1F, 6.0F);
                level.addFreshEntity(projectile);
                stack.shrink(1);
                return stack;
            }
        });
    }
}
