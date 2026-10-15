package com.evandev.spicedcider.mixin.tide;

import com.evandev.spicedcider.compat.tide.TideHookEffects;
import com.li64.tide.data.rods.BaitContents;
import com.li64.tide.registries.entities.misc.fishing.TideFishingHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@IfModLoaded("tide")
@Mixin(value = TideFishingHook.class, remap = false)
public class TideFishingHookMixin {

    @Shadow
    protected TideFishingHook.CatchType catchType;

    @Inject(method = "retrieve(Z)V", at = @At("HEAD"))
    private void spicedcider$perfectCatchReward(boolean perfectCatch, CallbackInfo ci) {
        if (perfectCatch && this.catchType == TideFishingHook.CatchType.FISH) {
            TideHookEffects.rewardPerfectCatch((TideFishingHook) (Object) this);
        }
    }

    @ModifyVariable(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;IIFLnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"), argsOnly = true, ordinal = 1)
    private static int spicedcider$lureEffect(int lureSpeed, @Local(argsOnly = true) Player player) {
        return TideHookEffects.lureSpeed(player, lureSpeed);
    }

    @ModifyVariable(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;IIFLnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"), argsOnly = true)
    private static float spicedcider$lightHookCast(float charge, @Local(argsOnly = true) ItemStack rod) {
        return TideHookEffects.castCharge(rod, charge);
    }

    @ModifyArg(method = "getContext", at = @At(value = "INVOKE", target = "Lcom/li64/tide/data/fishing/FishingContext;<init>(Lnet/minecraft/server/level/ServerLevel;Lcom/li64/tide/registries/entities/misc/fishing/TideFishingHook;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/core/BlockPos;ILjava/lang/String;Lnet/minecraft/core/Holder;Lnet/minecraft/core/Holder;Lnet/minecraft/resources/ResourceKey;FILcom/li64/tide/compat/seasons/Season;)V"), index = 6)
    private int spicedcider$hookLuck(int luck) {
        return TideHookEffects.luck((TideFishingHook) (Object) this, luck);
    }

    @WrapOperation(method = "retrieve(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/player/Player;)I", at = @At(value = "INVOKE", target = "Lcom/li64/tide/data/rods/BaitContents$Mutable;shrinkAll()V"))
    private void spicedcider$necromiumKeepsBait(BaitContents.Mutable contents, Operation<Void> original) {
        if (!TideHookEffects.keepsBait((TideFishingHook) (Object) this)) original.call(contents);
    }
}
