package com.evandev.spicedcider.mixin.vanity;

import com.evandev.spicedcider.compat.vanity.TrimAdditionSlot;
import com.evandev.spicedcider.content.smithing.TrimStyling;
import com.evandev.spicedcider.interfaces.ITrimStylingMenu;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.neoforged.fml.ModList;
import tech.thatgravyboat.vanity.api.design.DesignManager;
import tech.thatgravyboat.vanity.api.style.Style;
import tech.thatgravyboat.vanity.common.menu.BaseContainerMenu;
import tech.thatgravyboat.vanity.common.menu.StylingMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@IfModLoaded("vanity")
@Mixin(value = StylingMenu.class, remap = false)
public abstract class StylingMenuTrimMixin extends BaseContainerMenu implements ITrimStylingMenu {

    @Unique
    private static final int spicedcider$NONE = 0;
    @Unique
    private static final int spicedcider$TRIM = 1;
    @Unique
    private static final int spicedcider$MODIFIER = 2;

    @Shadow
    @Final
    private List<ResourceLocation> designs;
    @Shadow
    @Final
    private Map<ResourceLocation, List<String>> styles;
    @Shadow
    @Final
    private Container input;
    @Shadow
    @Final
    private Container result;
    @Shadow
    @Final
    private DesignManager manager;

    @Unique
    private final SimpleContainer spicedcider$addition = new SimpleContainer(1);
    @Unique
    private Player spicedcider$player;
    @Unique
    private int spicedcider$kind = spicedcider$NONE;
    @Unique
    private String spicedcider$pattern;
    @Unique
    private boolean spicedcider$consumes;
    @Unique
    private boolean spicedcider$taking;

    private StylingMenuTrimMixin(int id, @Nullable MenuType<?> type, Inventory inventory, ContainerLevelAccess access) {
        super(id, type, inventory, access);
    }

    @Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;Ltech/thatgravyboat/vanity/api/design/DesignManager;Ljava/util/List;)V", at = @At("TAIL"))
    private void spicedcider$addAdditionSlot(int id, Inventory inventory, ContainerLevelAccess access, DesignManager manager, List<ResourceLocation> designs, CallbackInfo ci) {
        this.spicedcider$player = inventory.player;
        this.spicedcider$addition.addListener(container -> this.spicedcider$refresh());
        this.addSlot(new TrimAdditionSlot(this.spicedcider$addition, inventory.player, 80, 72));
    }

    @Inject(method = "updateDesign", at = @At(value = "INVOKE", target = "Ljava/util/Map;hashCode()I", ordinal = 1))
    private void spicedcider$listTrims(CallbackInfo ci) {
        ItemStack in = this.input.getItem(0);
        if (!ModList.get().isLoaded("geckolib")) {
            this.styles.forEach((design, names) -> this.manager.getDesign(design).ifPresent(found -> names.removeIf(name -> {
                Style style = found.getStyleForItem(name, in);
                return style != null && style.assets().containsKey("geckolib_armor");
            })));
            this.styles.values().removeIf(List::isEmpty);
        }
        if (!TrimStyling.isTrimmable(in)) return;
        List<String> patterns = new ArrayList<>();
        for (ResourceLocation design : this.designs) {
            TrimStyling.patternFromMarker(design).filter(pattern -> !patterns.contains(pattern)).ifPresent(patterns::add);
        }
        if (!patterns.isEmpty()) {
            this.styles.put(TrimStyling.DESIGN, patterns);
        }
    }

    @Inject(method = "updateDesign", at = @At("TAIL"))
    private void spicedcider$afterUpdate(CallbackInfo ci) {
        if (this.spicedcider$taking) return;
        if (this.spicedcider$kind == spicedcider$TRIM && this.result.getItem(0).isEmpty()) {
            this.spicedcider$kind = spicedcider$NONE;
        }
        this.spicedcider$refresh();
    }

    @Inject(method = "select", at = @At("HEAD"), cancellable = true)
    private void spicedcider$selectTrim(ResourceLocation design, @Nullable String style, CallbackInfo ci) {
        if (!TrimStyling.DESIGN.equals(design)) {
            this.spicedcider$kind = spicedcider$NONE;
            this.spicedcider$consumes = false;
            return;
        }
        ci.cancel();
        List<String> available = this.styles.get(TrimStyling.DESIGN);
        if (style == null || available == null || !available.contains(style)) return;
        this.spicedcider$pattern = style;
        this.spicedcider$kind = spicedcider$TRIM;
        this.spicedcider$refresh();
    }

    @Inject(method = "removed", at = @At("TAIL"))
    private void spicedcider$returnAddition(Player player, CallbackInfo ci) {
        this.access.execute((level, pos) -> this.clearContainer(player, this.spicedcider$addition));
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        if (index < 36) {
            Slot slot = this.slots.get(index);
            ItemStack stack = slot.getItem();
            if (!stack.isEmpty() && !TrimStyling.isTrimmable(stack) && TrimStyling.isAddition(player.level().registryAccess(), stack)) {
                ItemStack moved = stack.copy();
                if (this.moveItemStackTo(stack, ADDITION_SLOT, ADDITION_SLOT + 1, false)) {
                    if (stack.isEmpty()) {
                        slot.setByPlayer(ItemStack.EMPTY);
                    } else {
                        slot.setChanged();
                    }
                    return moved;
                }
            }
        }
        return super.quickMoveStack(player, index);
    }

    @Override
    public Container spicedcider$addition() {
        return this.spicedcider$addition;
    }

    @Override
    public void spicedcider$beforeResultTaken(Player player) {
        this.spicedcider$taking = true;
        boolean consumes = this.spicedcider$kind != spicedcider$NONE && this.spicedcider$consumes;
        this.spicedcider$kind = spicedcider$NONE;
        this.spicedcider$consumes = false;
        if (consumes) {
            this.spicedcider$addition.removeItem(0, 1);
        }
    }

    @Override
    public void spicedcider$afterResultTaken() {
        this.spicedcider$taking = false;
        this.spicedcider$refresh();
    }

    @Unique
    private void spicedcider$refresh() {
        if (this.spicedcider$taking || this.spicedcider$player == null) return;
        RegistryAccess registries = this.spicedcider$player.level().registryAccess();
        ItemStack in = this.input.getItem(0);
        ItemStack addition = this.spicedcider$addition.getItem(0);

        if (this.spicedcider$kind == spicedcider$TRIM) {
            ItemStack trimmed = this.spicedcider$pattern == null ? ItemStack.EMPTY : TrimStyling.trimResult(registries, in, addition, this.spicedcider$pattern);
            if (!trimmed.isEmpty()) {
                this.spicedcider$consumes = TrimStyling.trimConsumesAddition(registries, addition);
                this.spicedcider$setResult(trimmed);
                return;
            }
            this.spicedcider$kind = spicedcider$NONE;
            this.spicedcider$setResult(ItemStack.EMPTY);
        }

        ItemStack modified = TrimStyling.modifierResult(in, addition);
        if (!modified.isEmpty() && (this.spicedcider$kind == spicedcider$MODIFIER || this.result.getItem(0).isEmpty())) {
            this.spicedcider$kind = spicedcider$MODIFIER;
            this.spicedcider$consumes = true;
            this.spicedcider$setResult(modified);
        } else if (modified.isEmpty() && this.spicedcider$kind == spicedcider$MODIFIER) {
            this.spicedcider$kind = spicedcider$NONE;
            this.spicedcider$setResult(ItemStack.EMPTY);
        }
    }

    @Unique
    private void spicedcider$setResult(ItemStack stack) {
        if (!ItemStack.matches(this.result.getItem(0), stack)) {
            this.result.setItem(0, stack);
        }
    }
}
