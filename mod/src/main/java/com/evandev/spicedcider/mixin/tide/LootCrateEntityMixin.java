package com.evandev.spicedcider.mixin.tide;

import com.li64.tide.data.loot.LootTableRef;
import com.li64.tide.registries.entities.misc.LootCrateEntity;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@IfModLoaded("tide")
@Mixin(value = LootCrateEntity.class, remap = false)
public abstract class LootCrateEntityMixin extends FallingBlockEntity {

    @Unique
    private static final String SPICEDCIDER$LOOT_TAG = "spicedcider:crate_loot";
    @Unique
    private static final int SPICEDCIDER$CRATE_SLOTS = 27;

    @Shadow
    private LootTableRef tableKey;
    @Shadow
    private LootParams lootParams;

    protected LootCrateEntityMixin(EntityType<? extends FallingBlockEntity> type, Level level) {
        super(type, level);
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lcom/li64/tide/registries/entities/misc/LootCrateEntity;dropCrateLoot()V"))
    private void spicedcider$keepLootForCrateItem(LootCrateEntity self, Operation<Void> original) {
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lcom/li64/tide/registries/entities/misc/LootCrateEntity;spawnAtLocation(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/entity/item/ItemEntity;"))
    private ItemEntity spicedcider$dropFilledCrate(LootCrateEntity self, ItemLike item, Operation<ItemEntity> original) {
        if (!(level() instanceof ServerLevel level) || tableKey == null) return original.call(self, item);
        List<ItemStack> loot = tableKey.getTable(level.getServer()).getRandomItems(spicedcider$lootParams(level));
        tableKey = null;
        ItemStack crate = new ItemStack(item);
        int packed = Math.min(loot.size(), SPICEDCIDER$CRATE_SLOTS);
        if (packed > 0) crate.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(loot.subList(0, packed)));
        for (ItemStack extra : loot.subList(packed, loot.size())) spawnAtLocation(extra);
        return spawnAtLocation(crate);
    }

    @Inject(method = "addCrateLoot", at = @At("HEAD"))
    private void spicedcider$restoreLootParams(CallbackInfo ci) {
        if (level() instanceof ServerLevel level && tableKey != null) spicedcider$lootParams(level);
    }

    @Unique
    private LootParams spicedcider$lootParams(ServerLevel level) {
        if (lootParams == null) {
            lootParams = new LootParams.Builder(level)
                    .withParameter(LootContextParams.ORIGIN, position())
                    .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                    .withParameter(LootContextParams.BLOCK_STATE, getBlockState())
                    .create(LootContextParamSets.FISHING);
        }
        return lootParams;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (tableKey != null) tag.putString(SPICEDCIDER$LOOT_TAG, tableKey.getId().toString());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains(SPICEDCIDER$LOOT_TAG)) {
            ResourceLocation id = ResourceLocation.tryParse(tag.getString(SPICEDCIDER$LOOT_TAG));
            if (id != null) tableKey = LootTableRef.createNew(id);
        }
    }
}
