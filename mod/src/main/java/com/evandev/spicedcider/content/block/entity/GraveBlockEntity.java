package com.evandev.spicedcider.content.block.entity;

import com.evandev.spicedcider.content.gravestone.Graves;
import com.evandev.spicedcider.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GraveBlockEntity extends BlockEntity {
    public record Entry(int slot, ItemStack stack) {
    }

    private final List<Entry> items = new ArrayList<>();
    private int experience;
    @Nullable
    private ResolvableProfile owner;
    private long deathTime;
    private long createdTick;
    private int deathDamage;
    private int ageDamage;

    public GraveBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GRAVE.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, GraveBlockEntity grave) {
        if (level.getGameTime() % 20 == 0) {
            Graves.timeDecay(level, pos, state, grave);
        }
    }

    public void fill(List<Entry> items, int experience, ResolvableProfile owner, long deathTime, long createdTick) {
        this.items.clear();
        this.items.addAll(items);
        this.experience = experience;
        this.owner = owner;
        this.deathTime = deathTime;
        this.createdTick = createdTick;
        setChanged();
    }

    public List<Entry> takeItems() {
        List<Entry> taken = new ArrayList<>(this.items);
        this.items.clear();
        setChanged();
        return taken;
    }

    public int takeExperience() {
        int taken = this.experience;
        this.experience = 0;
        setChanged();
        return taken;
    }

    @Nullable
    public ResolvableProfile getOwner() {
        return this.owner;
    }

    @Nullable
    public UUID getOwnerId() {
        return this.owner == null ? null : this.owner.id().orElse(null);
    }

    public String getOwnerName() {
        return this.owner == null ? "???" : this.owner.name().orElse("???");
    }

    public long getDeathTime() {
        return this.deathTime;
    }

    public long getCreatedTick() {
        return this.createdTick;
    }

    public int getTotalDamage() {
        return this.ageDamage + this.deathDamage;
    }

    public int getAgeDamage() {
        return this.ageDamage;
    }

    public void setAgeDamage(int ageDamage) {
        if (this.ageDamage != ageDamage) {
            this.ageDamage = ageDamage;
            setChanged();
        }
    }

    public void addDeathDamage() {
        this.deathDamage++;
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ListTag list = new ListTag();
        for (Entry entry : this.items) {
            if (entry.stack().isEmpty()) continue;
            CompoundTag entryTag = new CompoundTag();
            entryTag.putInt("Slot", entry.slot());
            entryTag.put("Item", entry.stack().save(registries));
            list.add(entryTag);
        }
        tag.put("Items", list);
        tag.putInt("Experience", this.experience);
        tag.putInt("DeathDamage", this.deathDamage);
        tag.putInt("AgeDamage", this.ageDamage);
        tag.putLong("CreatedTick", this.createdTick);
        saveDisplay(tag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items.clear();
        ListTag list = tag.getList("Items", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entryTag = list.getCompound(i);
            ItemStack stack = ItemStack.parseOptional(registries, entryTag.getCompound("Item"));
            if (!stack.isEmpty()) {
                this.items.add(new Entry(entryTag.getInt("Slot"), stack));
            }
        }
        this.experience = tag.getInt("Experience");
        this.deathDamage = tag.getInt("DeathDamage");
        this.ageDamage = tag.getInt("AgeDamage");
        this.createdTick = tag.getLong("CreatedTick");
        this.deathTime = tag.getLong("DeathTime");
        this.owner = tag.contains("Owner")
                ? ResolvableProfile.CODEC.parse(NbtOps.INSTANCE, tag.get("Owner")).result().orElse(null)
                : null;
    }

    private void saveDisplay(CompoundTag tag) {
        tag.putLong("DeathTime", this.deathTime);
        if (this.owner != null) {
            ResolvableProfile.CODEC.encodeStart(NbtOps.INSTANCE, this.owner).result().ifPresent(owner -> tag.put("Owner", owner));
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveDisplay(tag);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
