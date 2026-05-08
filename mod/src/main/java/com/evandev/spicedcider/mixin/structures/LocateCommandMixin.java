package com.evandev.spicedcider.mixin.structures;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceOrTagKeyArgument;
import net.minecraft.server.commands.LocateCommand;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({LocateCommand.class})
public class LocateCommandMixin {
    @Unique
    private static int prevLimit = 0;
    @Unique
    private static int prevTimeout = 0;

    @ModifyConstant(method = {"locateStructure"}, constant = {@Constant(intValue = 100)}, require = 0)
    private static int setRadius(int org) {
        return SpicedCiderConfig.COMMON.locateSearchRadius.get();
    }

    @Inject(method = {"locateStructure"}, at = {@At("HEAD")})
    private static void adjustLimit(CommandSourceStack p_214472_, ResourceOrTagKeyArgument.Result<Structure> p_249893_, CallbackInfoReturnable<Integer> cir) {
        prevLimit = SpicedCiderConfig.COMMON.globalSearchRadius.get();
        prevTimeout = SpicedCiderConfig.COMMON.structureSearchTimeout.get();
        SpicedCiderConfig.COMMON.globalSearchRadius.set(SpicedCiderConfig.COMMON.locateSearchRadius.get());
        SpicedCiderConfig.COMMON.structureSearchTimeout.set(prevTimeout += 30);
    }


    @Inject(method = {"locateStructure"}, at = {@At("RETURN")})
    private static void restoreLimit(CommandSourceStack p_214472_, ResourceOrTagKeyArgument.Result<Structure> p_249893_, CallbackInfoReturnable<Integer> cir) {
        SpicedCiderConfig.COMMON.globalSearchRadius.set(prevLimit);
        SpicedCiderConfig.COMMON.structureSearchTimeout.set(prevTimeout);
    }
}