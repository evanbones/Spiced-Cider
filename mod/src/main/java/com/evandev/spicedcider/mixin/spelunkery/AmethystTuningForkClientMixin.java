package com.evandev.spicedcider.mixin.spelunkery;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.ordana.spelunkery.items.AmethystTuningForkItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@IfModLoaded("spelunkery")
@Mixin(AmethystTuningForkItem.class)
public class AmethystTuningForkClientMixin {

    @ModifyExpressionValue(method = "animateItemFirstPerson", at = @At(value = "FIELD", target = "Lcom/ordana/spelunkery/items/AmethystTuningForkItem;tolling:Z"))
    private boolean spicedcider$tollWhileUsing(boolean original, @Local(argsOnly = true) Player entity, @Local(argsOnly = true) ItemStack stack) {
        return entity.isUsingItem() && AmethystTuningForkItem.isAmethystNearby(stack);
    }

    @WrapOperation(method = "animateItemFirstPerson", at = @At(value = "INVOKE", target = "Lcom/ordana/spelunkery/items/AmethystTuningForkItem;getModifierAngle(Lnet/minecraft/world/entity/Entity;JLnet/minecraft/core/BlockPos;)F"))
    private float spicedcider$smoothPointing(AmethystTuningForkItem item, Entity entity, long time, BlockPos pos, Operation<Float> original, @Local(argsOnly = true, ordinal = 0) float partialTicks) {
        Vec3 from = entity.getPosition(partialTicks);
        Vec3 to = Vec3.atCenterOf(pos);
        double toward = Math.atan2(to.z - from.z, to.x - from.x) / (Math.PI * 2);
        double facing = Mth.positiveModulo(entity.getViewYRot(partialTicks) / 360.0, 1.0);
        return Mth.positiveModulo((float) (toward + 0.5 - (facing - 0.25)), 1.0F);
    }

    @ModifyExpressionValue(method = "animateItemFirstPerson", at = @At(value = "INVOKE", target = "Ljava/lang/StrictMath;cbrt(D)D"))
    private double spicedcider$avoidZeroDistance(double distance) {
        return Math.max(distance, 1.0);
    }

    @Inject(method = "poseRightArm", at = @At("HEAD"), cancellable = true)
    private void spicedcider$poseRightArm(ItemStack stack, HumanoidModel<?> model, LivingEntity entity, HumanoidArm arm, CallbackInfoReturnable<Boolean> cir) {
        if (spicedcider$poseArm(model.rightArm, stack, entity)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "poseLeftArm", at = @At("HEAD"), cancellable = true)
    private void spicedcider$poseLeftArm(ItemStack stack, HumanoidModel<?> model, LivingEntity entity, HumanoidArm arm, CallbackInfoReturnable<Boolean> cir) {
        if (spicedcider$poseArm(model.leftArm, stack, entity)) {
            cir.setReturnValue(true);
        }
    }

    @Unique
    private static boolean spicedcider$poseArm(ModelPart part, ItemStack stack, LivingEntity entity) {
        if (!entity.isUsingItem() || entity.getItemInHand(entity.getUsedItemHand()) != stack) {
            return false;
        }
        part.xRot = part.xRot * 0.5F - (float) (Math.PI / 5);
        part.yRot = 0.0F;
        GlobalPos geode = AmethystTuningForkItem.getAmethystPos(stack);
        if (geode == null || geode.dimension() != entity.level().dimension()) {
            return true;
        }
        float partialTicks = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);
        Vec3 from = entity.getPosition(partialTicks);
        Vec3 to = Vec3.atCenterOf(geode.pos());
        float towardYaw = (float) (Mth.atan2(to.z - from.z, to.x - from.x) * Mth.RAD_TO_DEG) - 90.0F;
        float bodyYaw = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        float relative = Mth.clamp(Mth.wrapDegrees(towardYaw - bodyYaw), -70.0F, 70.0F);
        part.yRot = relative * Mth.DEG_TO_RAD;
        return true;
    }
}
