package com.evandev.spicedcider.mixin.vanity;

import com.evandev.spicedcider.content.smithing.TrimStyling;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.thatgravyboat.vanity.common.block.StylingTableBlockEntity;
import tech.thatgravyboat.vanity.common.menu.provider.StylingProvider;

import java.util.List;

@IfModLoaded("vanity")
@Mixin(value = StylingProvider.class, remap = false)
public class StylingProviderTrimMixin {

    @Unique
    private StylingTableBlockEntity spicedcider$table;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void spicedcider$captureTable(StylingTableBlockEntity table, CallbackInfo ci) {
        this.spicedcider$table = table;
    }

    @Inject(method = "getDesignsForPlayer", at = @At("RETURN"))
    private void spicedcider$addStoredTrims(Player player, CallbackInfoReturnable<List<ResourceLocation>> cir) {
        List<ResourceLocation> designs = cir.getReturnValue();
        for (ItemStack stack : this.spicedcider$table.items()) {
            if (!TrimStyling.isTemplate(stack)) continue;
            TrimStyling.pattern(player.level().registryAccess(), stack).ifPresent(pattern -> {
                ResourceLocation marker = TrimStyling.marker(pattern.key());
                if (!designs.contains(marker)) designs.add(marker);
            });
        }
    }
}
