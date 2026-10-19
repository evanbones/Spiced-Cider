package com.evandev.spicedcider.mixin.vanillabackport;

import com.blackgear.vanillabackport.common.level.entities.mob.animal.golem.copper_golem.CopperGolem;
import com.blackgear.vanillabackport.common.registries.entities.ModMemoryModuleTypes;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.teamabnormals.caverns_and_chasms.core.interfaces.ControllableGolem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("vanillabackport")
@IfModLoaded("caverns_and_chasms")
@Mixin(CopperGolem.class)
public abstract class CopperGolemTuningForkMixin extends AbstractGolem implements ControllableGolem {

    private CopperGolemTuningForkMixin(EntityType<? extends AbstractGolem> type, Level level) {
        super(type, level);
    }

    @Inject(method = "customServerAiStep", at = @At("HEAD"))
    @SuppressWarnings("unchecked")
    private void spicedcider$walkToTuningFork(CallbackInfo ci) {
        if (!this.isBeingTuningForkControlled()) {
            return;
        }
        Brain<AbstractGolem> brain = (Brain<AbstractGolem>) this.getBrain();
        brain.eraseMemory(ModMemoryModuleTypes.TRANSPORT_ITEMS_COOLDOWN_TICKS.get());
        BlockPos pos = this.getTuningForkPos();
        if (pos != null) {
            brain.stopAll((ServerLevel) this.level(), this);
            brain.eraseMemory(MemoryModuleType.WALK_TARGET);
            brain.eraseMemory(MemoryModuleType.PATH);
            this.getNavigation().moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 1.0);
            this.setTuningForkPos(null);
        }
    }

    @WrapOperation(method = "customServerAiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/Brain;tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;)V"))
    private void spicedcider$pauseBrainWhileWalking(Brain<?> brain, ServerLevel level, LivingEntity entity, Operation<Void> original) {
        if (!this.isBeingTuningForkControlled() || this.getNavigation().isDone()) {
            original.call(brain, level, entity);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onTuningForkControlStart(Player player) {
        if (this.level() instanceof ServerLevel level) {
            Brain<AbstractGolem> brain = (Brain<AbstractGolem>) this.getBrain();
            brain.stopAll(level, this);
            brain.eraseMemory(MemoryModuleType.WALK_TARGET);
            this.getNavigation().stop();
        }
    }
}
