package com.evandev.spicedcider.content.item;

import com.evandev.spicedcider.registry.ModAttachments;
import com.evandev.spicedcider.registry.ModSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CrudeBowItem extends BowItem {
    public static final float VELOCITY_MULTIPLIER = 0.7F;
    public static final double GRAVITY_MULTIPLIER = 2.0;

    public CrudeBowItem(Properties properties) {
        super(properties);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player)) return;
        ItemStack ammo = player.getProjectile(stack);
        if (ammo.isEmpty()) return;
        int charge = EventHooks.onArrowLoose(stack, level, player, this.getUseDuration(stack, entity) - timeLeft, true);
        if (charge < 0) return;
        float power = getPowerForTime(charge);
        if (power < 0.1F) return;
        List<ItemStack> projectiles = draw(stack, ammo, player);
        if (level instanceof ServerLevel serverLevel && !projectiles.isEmpty()) {
            this.shoot(serverLevel, player, player.getUsedItemHand(), stack, projectiles, power * 3.0F, 1.0F, false, null);
        }
        level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.CRUDE_BOW_SHOOT.get(), SoundSource.PLAYERS,
                1.0F, 0.9F + level.getRandom().nextFloat() * 0.2F + power * 0.15F);
        player.awardStat(Stats.ITEM_USED.get(this));
    }

    @Override
    protected Projectile createProjectile(Level level, LivingEntity shooter, ItemStack weapon, ItemStack ammo, boolean isCrit) {
        Projectile projectile = super.createProjectile(level, shooter, weapon, ammo, false);
        projectile.setData(ModAttachments.CRUDE_ARROW, true);
        return projectile;
    }

    @Override
    protected void shootProjectile(LivingEntity shooter, Projectile projectile, int index, float velocity, float inaccuracy, float angle, @Nullable LivingEntity target) {
        super.shootProjectile(shooter, projectile, index, velocity * VELOCITY_MULTIPLIER, inaccuracy, angle, target);
    }
}
