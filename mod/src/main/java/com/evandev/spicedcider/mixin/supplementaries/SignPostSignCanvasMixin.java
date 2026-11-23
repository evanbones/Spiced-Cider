package com.evandev.spicedcider.mixin.supplementaries;

import com.evandev.spicedcider.compat.supplementaries.CanvasWaySigns;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.mehvahdjukaar.moonlight.api.set.BlockType;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodType;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodTypeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@IfModLoaded("supplementaries")
@Mixin(targets = "net.mehvahdjukaar.supplementaries.common.block.tiles.SignPostBlockTile$Sign", remap = false)
public abstract class SignPostSignCanvasMixin {
    @Shadow
    private WoodType woodType;

    @WrapOperation(method = "load", at = @At(value = "INVOKE",
            target = "Lnet/mehvahdjukaar/moonlight/api/set/wood/WoodTypeRegistry;get(Lnet/minecraft/resources/ResourceLocation;)Lnet/mehvahdjukaar/moonlight/api/set/BlockType;"))
    private BlockType spicedcider$canvasType(WoodTypeRegistry registry, ResourceLocation id, Operation<BlockType> original) {
        WoodType canvas = CanvasWaySigns.type(id);
        return canvas != null ? canvas : original.call(registry, id);
    }

    @Inject(method = "getItem", at = @At("HEAD"), cancellable = true)
    private void spicedcider$canvasItem(CallbackInfoReturnable<ItemStack> cir) {
        Item item = CanvasWaySigns.item(this.woodType);
        if (item != null) cir.setReturnValue(new ItemStack(item));
    }
}
