package com.evandev.spicedcider.mixin.vanity.client;

import com.evandev.spicedcider.content.smithing.TrimStyling;
import com.evandev.spicedcider.interfaces.ITrimStylingMenu;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.vanity.client.components.list.StyleButton;
import tech.thatgravyboat.vanity.common.menu.StylingMenu;

@IfModLoaded("vanity")
@Mixin(value = StyleButton.class, remap = false)
public class StyleButtonTrimMixin {

    @Shadow
    @Final
    private ResourceLocation design;
    @Shadow
    @Final
    private String style;
    @Shadow
    @Final
    @Mutable
    private ItemStack stack;
    @Shadow
    private boolean selected;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void spicedcider$trimPreview(ResourceLocation design, String style, ItemStack input, CallbackInfo ci) {
        if (!TrimStyling.DESIGN.equals(design)) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) return;
        RegistryAccess registries = minecraft.level.registryAccess();
        ItemStack addition = minecraft.player.containerMenu instanceof ITrimStylingMenu menu ? menu.spicedcider$addition().getItem(0) : ItemStack.EMPTY;
        TrimStyling.pattern(registries, style).ifPresent(pattern -> {
            this.stack = TrimStyling.withTrim(input, pattern, TrimStyling.previewMaterial(registries, input, addition));
            ((AbstractWidget) (Object) this).setTooltip(Tooltip.create(pattern.value().description()));
        });
    }

    @Inject(method = "select", at = @At("TAIL"))
    private void spicedcider$selectTrim(@Nullable ResourceLocation design, @Nullable String style, boolean isEmpty, CallbackInfo ci) {
        if (!TrimStyling.DESIGN.equals(this.design)) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || !(minecraft.player.containerMenu instanceof StylingMenu menu)) return;
        ItemStack result = menu.getResult();
        ArmorTrim trim = result.get(DataComponents.TRIM);
        ResourceLocation pattern = ResourceLocation.tryParse(this.style);
        this.selected = trim != null && pattern != null && trim.pattern().is(pattern);
    }
}
