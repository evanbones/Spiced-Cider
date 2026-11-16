package com.evandev.spicedcider.mixin.reliableadvancements;

import com.evandev.reliable_advancements.gui.EnhancedAdvancementTab;
import com.evandev.reliable_advancements.gui.EnhancedAdvancementWidget;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@IfModLoaded("reliable_advancements")
@Mixin(EnhancedAdvancementTab.class)
public abstract class AdvancementTopDownScrollMixin {

    @Unique
    private static final int spicedcider$TOP_MARGIN = 16;

    @Unique
    private static final Set<ResourceLocation> spicedcider$TOP_DOWN_ROOTS = Set.of(
            ResourceLocation.fromNamespaceAndPath("spicedcider", "adventure/root"),
            ResourceLocation.fromNamespaceAndPath("spicedcider", "husbandry/root"),
            ResourceLocation.fromNamespaceAndPath("spicedcider", "industry/root")
    );

    @Shadow
    public int scrollY;

    @Shadow
    public abstract AdvancementNode getPrimaryRoot();

    @Shadow
    public abstract EnhancedAdvancementWidget getWidget(ResourceLocation id);

    @Inject(method = "centreOnContent", at = @At("TAIL"))
    private void spicedcider$pinTopDownRootToTop(int scaledWidth, int scaledHeight, CallbackInfo ci) {
        AdvancementNode root = getPrimaryRoot();
        if (root == null || !spicedcider$TOP_DOWN_ROOTS.contains(root.holder().id())) return;
        EnhancedAdvancementWidget widget = getWidget(root.holder().id());
        if (widget != null) {
            this.scrollY = spicedcider$TOP_MARGIN - widget.getY();
        }
    }
}
