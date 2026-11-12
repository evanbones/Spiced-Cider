package com.evandev.spicedcider.mixin.nomansland;

import com.evandev.spicedcider.compat.nomansland.ClayStatueWeather;
import com.farcr.nomansland.common.blockentity.MoonlightBasinBlockEntity;
import com.farcr.nomansland.common.friend.FriendMoon;
import com.farcr.nomansland.common.friend.dialogue.DialogueLocation;
import com.farcr.nomansland.common.friend.offering.OfferingContext;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@IfModLoaded("nomansland")
@Mixin(value = MoonlightBasinBlockEntity.class, remap = false)
public class MoonlightBasinClayStatueMixin {

    @Shadow
    private OfferingContext inspectionContext;

    @Unique
    private int spicedcider$statueTicks = -1;

    @Shadow
    private void setInspectionContext(OfferingContext context, FriendMoon friendMoon) {
        throw new AssertionError();
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lcom/farcr/nomansland/common/friend/dialogue/DialogueLocation;dispatch(Lnet/minecraft/world/level/Level;Ljava/util/List;)I"))
    private static int spicedcider$timeStatue(DialogueLocation location, Level level, List<ServerPlayer> players, Operation<Integer> original,
                                              @Local(argsOnly = true) MoonlightBasinBlockEntity blockEntity) {
        int length = original.call(location, level, players);
        MoonlightBasinClayStatueMixin basin = (MoonlightBasinClayStatueMixin) (Object) blockEntity;
        if (basin.inspectionContext != null && ClayStatueWeather.isStatue(basin.inspectionContext.getEntity())) {
            basin.spicedcider$statueTicks = Math.max(length, 40);
        }
        return length;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private static void spicedcider$crumbleStatue(Level level, BlockPos pos, BlockState state, MoonlightBasinBlockEntity blockEntity, CallbackInfo ci) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        MoonlightBasinClayStatueMixin basin = (MoonlightBasinClayStatueMixin) (Object) blockEntity;
        if (basin.spicedcider$statueTicks < 0 || --basin.spicedcider$statueTicks > 0) return;
        basin.spicedcider$statueTicks = -1;
        OfferingContext context = basin.inspectionContext;
        if (context == null || !(context.getEntity() instanceof ItemEntity item) || !item.isAlive() || !ClayStatueWeather.isStatue(item)) return;
        basin.setInspectionContext(null, FriendMoon.getOrDefault(serverLevel.getServer().overworld()));
        ClayStatueWeather.crumble(serverLevel, item);
    }
}
