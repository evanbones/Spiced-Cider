package com.evandev.spicedcider.entities.balloon;

import com.evandev.spicedcider.registry.ModAttachments;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BalloonTethers {
    public static final IAttachmentSerializer<ListTag, BalloonTethers> SERIALIZER = new IAttachmentSerializer<>() {
        @Override
        public BalloonTethers read(IAttachmentHolder holder, ListTag tag, HolderLookup.Provider provider) {
            BalloonTethers tethers = new BalloonTethers(holder);
            for (int i = 0; i < tag.size(); i++) {
                DyeColor color = DyeColor.byName(tag.getString(i), null);
                if (color != null) tethers.stowed.add(color);
            }
            return tethers;
        }

        @Override
        public @Nullable ListTag write(BalloonTethers tethers, HolderLookup.Provider provider) {
            if (!(tethers.owner instanceof Player)) return null;
            List<DyeColor> colors = tethers.stowed.isEmpty()
                    ? tethers.balloons.stream().map(BalloonEntity::getColor).toList()
                    : tethers.stowed;
            if (colors.isEmpty()) return null;
            ListTag tag = new ListTag();
            colors.forEach(color -> tag.add(StringTag.valueOf(color.getName())));
            return tag;
        }
    };

    private final IAttachmentHolder owner;
    private final List<BalloonEntity> balloons = new ArrayList<>();
    private final List<DyeColor> stowed = new ArrayList<>();

    public BalloonTethers(IAttachmentHolder owner) {
        this.owner = owner;
    }

    public static int count(Entity entity) {
        BalloonTethers tethers = entity.getExistingDataOrNull(ModAttachments.BALLOONS);
        return tethers == null ? 0 : tethers.live().size();
    }

    public static List<BalloonEntity> balloons(Entity entity) {
        BalloonTethers tethers = entity.getExistingDataOrNull(ModAttachments.BALLOONS);
        if (tethers == null || tethers.live().isEmpty()) return List.of();
        List<BalloonEntity> sorted = new ArrayList<>(tethers.balloons);
        sorted.sort(Comparator.comparingInt(Entity::getId));
        return sorted;
    }

    private List<BalloonEntity> live() {
        if (!this.balloons.isEmpty()) this.balloons.removeIf(Entity::isRemoved);
        return this.balloons;
    }

    static void add(Entity entity, BalloonEntity balloon) {
        BalloonTethers tethers = entity.getData(ModAttachments.BALLOONS);
        if (!tethers.balloons.contains(balloon)) tethers.balloons.add(balloon);
    }

    static void remove(Entity entity, BalloonEntity balloon) {
        BalloonTethers tethers = entity.getExistingDataOrNull(ModAttachments.BALLOONS);
        if (tethers != null) tethers.balloons.remove(balloon);
    }

    public static void stow(Player player) {
        BalloonTethers tethers = player.getExistingDataOrNull(ModAttachments.BALLOONS);
        if (tethers == null || tethers.balloons.isEmpty()) return;
        tethers.stowed.clear();
        for (BalloonEntity balloon : List.copyOf(tethers.balloons)) {
            tethers.stowed.add(balloon.getColor());
            balloon.discard();
        }
    }

    public static List<DyeColor> takeStowed(Player player) {
        BalloonTethers tethers = player.getExistingDataOrNull(ModAttachments.BALLOONS);
        if (tethers == null || tethers.stowed.isEmpty()) return List.of();
        List<DyeColor> colors = List.copyOf(tethers.stowed);
        tethers.stowed.clear();
        return colors;
    }

    public static void releaseAll(Entity entity) {
        balloons(entity).forEach(BalloonEntity::release);
    }

    public static double gravity(LivingEntity entity, double gravity) {
        int count = count(entity);
        if (count == 0) return gravity;
        if (count > 3) return (entity.getDeltaMovement().y - 0.05 * (count - 3)) * 0.2;
        if (entity.getDeltaMovement().y > 0.0) return gravity;
        return Math.min(gravity, count == 3 ? 0.005 : 0.01);
    }

    public static void liftBoat(Boat boat) {
        int count = count(boat);
        if (count == 0) return;
        Vec3 motion = boat.getDeltaMovement();
        if (count <= 2) {
            if (motion.y <= 0.0) boat.setDeltaMovement(motion.x, motion.y * 0.85 / count, motion.z);
        } else {
            double boost = count / 2.0 * 0.045;
            boat.setDeltaMovement(motion.x, Mth.clamp(motion.y + boost, -1.0, boost), motion.z);
        }
    }
}
