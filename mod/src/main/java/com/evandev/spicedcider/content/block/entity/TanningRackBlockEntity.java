package com.evandev.spicedcider.content.block.entity;

import com.evandev.spicedcider.recipe.DryingRecipe;
import com.evandev.spicedcider.registry.ModBlockEntities;
import com.evandev.spicedcider.registry.ModRecipeSerializers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class TanningRackBlockEntity extends BlockEntity {
    public enum Stage {HUNG, DRYING, FINISHED}

    @Nullable
    private ResourceLocation recipeId;
    private ItemStack hung = ItemStack.EMPTY;
    private Stage stage = Stage.HUNG;

    public TanningRackBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TANNING_RACK.get(), pos, state);
    }

    public boolean isEmpty() {
        return recipeId == null;
    }

    public Stage stage() {
        return stage;
    }

    public ItemStack hung() {
        return hung;
    }

    @Nullable
    public DryingRecipe recipe() {
        if (recipeId == null || level == null) return null;
        return level.getRecipeManager().byKey(recipeId)
                .filter(holder -> holder.value().getType() == ModRecipeSerializers.DRYING_TYPE.get())
                .map(holder -> (DryingRecipe) holder.value())
                .orElse(null);
    }

    public boolean isFull() {
        DryingRecipe recipe = recipe();
        return recipe != null && hung.getCount() >= recipe.count();
    }

    public void hang(RecipeHolder<DryingRecipe> holder, ItemStack stack) {
        recipeId = holder.id();
        hung = stack.copyWithCount(1);
        stage = Stage.HUNG;
        changed();
    }

    public void addOne() {
        hung.grow(1);
        changed();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        changed();
    }

    public ItemStack takeContents() {
        ItemStack taken = contents();
        recipeId = null;
        hung = ItemStack.EMPTY;
        stage = Stage.HUNG;
        changed();
        return taken;
    }

    public ItemStack contents() {
        if (recipeId == null) return ItemStack.EMPTY;
        if (stage == Stage.FINISHED) {
            DryingRecipe recipe = recipe();
            return recipe == null ? ItemStack.EMPTY : recipe.result().copy();
        }
        return hung.copy();
    }

    private void changed() {
        setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        recipeId = tag.contains("recipe") ? ResourceLocation.tryParse(tag.getString("recipe")) : null;
        hung = tag.contains("hung") ? ItemStack.parseOptional(registries, tag.getCompound("hung")) : ItemStack.EMPTY;
        int ordinal = tag.getInt("stage");
        stage = ordinal >= 0 && ordinal < Stage.values().length ? Stage.values()[ordinal] : Stage.HUNG;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (recipeId != null) tag.putString("recipe", recipeId.toString());
        if (!hung.isEmpty()) tag.put("hung", hung.save(registries));
        tag.putInt("stage", stage.ordinal());
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
