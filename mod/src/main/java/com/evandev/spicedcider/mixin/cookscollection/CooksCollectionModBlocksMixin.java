package com.evandev.spicedcider.mixin.cookscollection;

import com.baisylia.cookscollection.block.ModBlocks;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Supplier;

@Mixin(ModBlocks.class)
public class CooksCollectionModBlocksMixin {

    @WrapOperation(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/baisylia/cookscollection/block/ModBlocks;registerBlock(Ljava/lang/String;Ljava/util/function/Supplier;)Lnet/neoforged/neoforge/registries/DeferredBlock;"
            )
    )
    private static DeferredBlock<?> spicedcider$fixSaltedDripstoneOcclusion(
            String name,
            Supplier<Block> supplier,
            Operation<DeferredBlock<?>> original) {

        if ("salted_dripstone_block".equals(name) && SpicedCiderConfig.STARTUP.cooksCollectionDripstoneFix.get()) {
            supplier = () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.DRIPSTONE_BLOCK));
        }

        return original.call(name, supplier);
    }
}