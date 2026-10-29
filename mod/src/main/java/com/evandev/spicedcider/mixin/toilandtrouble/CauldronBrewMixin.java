package com.evandev.spicedcider.mixin.toilandtrouble;

import cc.cassian.cauldrons.blocks.entity.BrewingResult;
import cc.cassian.cauldrons.blocks.entity.CauldronBlockEntity;
import cc.cassian.cauldrons.core.CauldronContents;
import cc.cassian.cauldrons.core.CauldronModEvents;
import cc.cassian.cauldrons.core.CauldronModRecipes;
import cc.cassian.cauldrons.core.CauldronModTags;
import cc.cassian.cauldrons.recipe.BrewingRecipeInput;
import com.evandev.spicedcider.compat.toilandtrouble.AutomatedCauldron;
import com.evandev.spicedcider.compat.toilandtrouble.CauldronItemHandler;
import com.evandev.spicedcider.content.brewing.CauldronBrewing;
import com.evandev.spicedcider.registry.ModDataMaps;
import com.evandev.spicedcider.registry.ModPotions;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import oshi.util.tuples.Pair;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@IfModLoaded("toil_and_trouble")
@Mixin(value = CauldronBlockEntity.class, remap = false)
public abstract class CauldronBrewMixin implements AutomatedCauldron {

    @Shadow
    protected CauldronContents contents;

    @Shadow
    protected boolean splashing;

    @Shadow
    protected boolean lingering;

    @Shadow
    @Final
    private ArrayList<ItemStack> items;

    @Unique
    @Nullable
    private Holder<Potion> spicedcider$brew;

    @Unique
    private int spicedcider$seconds;

    @Unique
    private int spicedcider$potency;

    @Unique
    private int spicedcider$count;

    @Unique
    @Nullable
    private List<ItemStack> spicedcider$leftovers;

    @Unique
    private int spicedcider$drained;

    @Shadow
    public abstract Integer getFillLevel();

    @Shadow
    public abstract void setContentsAndUpdate(int value, CauldronContents contents);

    @Shadow
    private BrewingResult updateAfterBrewing(List<ItemStack> stack, CauldronContents contents, ParticleOptions particleType, boolean tryPlaceAsBlock, @Nullable BlockState block) {
        throw new AssertionError();
    }

    @Inject(method = "insert", at = @At("HEAD"), cancellable = true)
    private void spicedcider$guardBrew(ItemStack stack, CallbackInfoReturnable<Pair<ItemInteractionResult, ItemStack>> cir) {
        if (!this.spicedcider$isBrew()) {
            this.spicedcider$reset(null);
            return;
        }
        PotionContents poured = stack.get(DataComponents.POTION_CONTENTS);
        if (poured != null && stack.is(Items.POTION) && poured.equals(this.contents.toPotionContents()) && this.getFillLevel() < 3) {
            this.setContentsAndUpdate(this.getFillLevel() + 1, this.contents);
            cir.setReturnValue(new Pair<>(ItemInteractionResult.SUCCESS, Items.GLASS_BOTTLE.getDefaultInstance()));
            return;
        }
        if (stack.is(CauldronModTags.CLEANS_CAULDRONS)) {
            return;
        }
        if (this.spicedcider$refuses(stack)) {
            cir.setReturnValue(new Pair<>(CauldronModEvents.PASS_TO_EMPTY_HAND, ItemStack.EMPTY));
        }
    }

    @Unique
    private boolean spicedcider$refuses(ItemStack stack) {
        boolean tainted = CauldronBrewing.isTainted(this.contents.potion());
        boolean full = CauldronBrewing.ingredient(stack) != null && this.spicedcider$count + this.items.size() >= CauldronBrewing.MAX_INGREDIENTS;
        boolean foreign = !tainted && !this.spicedcider$fits(stack, null);
        boolean clashes = !tainted && !CauldronBrewing.isClear(this.contents.potion()) && !this.spicedcider$fits(stack, this.contents.potion().get());
        return tainted || full || foreign || clashes;
    }

    @Override
    public boolean spicedcider$accepts(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        if (this.spicedcider$isBrew()) {
            return !stack.is(CauldronModTags.CLEANS_CAULDRONS) && !this.spicedcider$refuses(stack);
        }
        Level level = ((CauldronBlockEntity) (Object) this).getLevel();
        if (level == null || !this.items.isEmpty() || !CauldronBrewing.isWater(this.contents.potion())) {
            return false;
        }
        return CauldronItemHandler.startsBrew(level, this.contents, stack);
    }

    @Override
    public int spicedcider$drained() {
        return this.spicedcider$drained;
    }

    @Override
    public void spicedcider$setDrained(int drained) {
        this.spicedcider$drained = drained;
    }

    @Inject(method = "canPlaceItemThroughFace", at = @At("HEAD"), cancellable = true)
    private void spicedcider$automatedInsert(int slot, ItemStack stack, @Nullable Direction side, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(this.spicedcider$accepts(stack));
    }

    @Inject(method = "canTakeItemThroughFace", at = @At("HEAD"), cancellable = true)
    private void spicedcider$noAutomatedTake(int slot, ItemStack stack, Direction side, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    @Unique
    private boolean spicedcider$fits(ItemStack stack, @Nullable Holder<Potion> brewed) {
        if (stack.is(CauldronModTags.CREATES_SPLASH_POTIONS) || stack.is(CauldronModTags.CREATES_LINGERING_POTIONS)) {
            return true;
        }
        ModDataMaps.BrewingIngredient ingredient = CauldronBrewing.ingredient(stack);
        return ingredient != null && (brewed == null || ingredient.potion().map(potion -> CauldronBrewing.same(potion, brewed)).orElse(true));
    }

    @Inject(method = "brew", at = @At("HEAD"), cancellable = true)
    private void spicedcider$brew(BlockState newState, boolean heated, CallbackInfoReturnable<BrewingResult> cir) {
        if (!this.spicedcider$isBrew()) {
            if (CauldronBrewing.isWater(this.contents.potion()) && this.items.size() > 1) {
                this.spicedcider$reagentFirst(heated);
            }
            this.spicedcider$leftovers = CauldronBrewing.isWater(this.contents.potion()) && this.items.size() > 1
                    ? this.items.subList(1, this.items.size()).stream().map(ItemStack::copy).toList()
                    : null;
            return;
        }
        this.spicedcider$syncState();
        boolean tainted = false;
        boolean changed = false;
        for (ItemStack stack : this.items) {
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.is(CauldronModTags.CREATES_SPLASH_POTIONS)) {
                this.splashing = true;
                this.lingering = false;
                changed = true;
                continue;
            }
            if (stack.is(CauldronModTags.CREATES_LINGERING_POTIONS)) {
                this.splashing = false;
                this.lingering = true;
                changed = true;
                continue;
            }
            ModDataMaps.BrewingIngredient ingredient = CauldronBrewing.ingredient(stack);
            if (ingredient == null || this.spicedcider$count >= CauldronBrewing.MAX_INGREDIENTS) {
                tainted = true;
                break;
            }
            if (ingredient.potion().isPresent()) {
                Holder<Potion> potion = ingredient.potion().get();
                if (this.spicedcider$brew != null && !CauldronBrewing.same(this.spicedcider$brew, potion)) {
                    tainted = true;
                    break;
                }
                this.spicedcider$brew = potion;
            }
            this.spicedcider$seconds += ingredient.seconds();
            this.spicedcider$potency += ingredient.potency();
            this.spicedcider$count++;
            changed = true;
        }
        if (tainted) {
            this.spicedcider$brew = ModPotions.TAINTED;
            CauldronContents result = new CauldronContents(ResourceLocation.withDefaultNamespace("potion"), Optional.of(ModPotions.TAINTED),
                    Optional.of(CauldronBrewing.TAINTED_COLOR), List.of(), 0, Optional.empty());
            this.contents = result;
            BrewingResult brewed = this.updateAfterBrewing(List.of(), result, ParticleTypes.SMOKE, false, null);
            Level level = ((CauldronBlockEntity) (Object) this).getLevel();
            if (level != null) {
                level.playSound(null, ((CauldronBlockEntity) (Object) this).getBlockPos(), SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 0.8F);
                level.playSound(null, ((CauldronBlockEntity) (Object) this).getBlockPos(), SoundEvents.WITCH_CELEBRATE, SoundSource.BLOCKS, 0.8F, 1.0F);
            }
            cir.setReturnValue(brewed);
            return;
        }
        CauldronContents result = this.contents;
        if (this.spicedcider$brew != null) {
            result = new CauldronContents(ResourceLocation.withDefaultNamespace("potion"), Optional.of(this.spicedcider$brew), Optional.empty(),
                    CauldronBrewing.effects(this.spicedcider$brew, this.spicedcider$seconds, this.spicedcider$potency), 0, Optional.empty());
        }
        if (!changed) {
            this.items.clear();
            cir.setReturnValue(new BrewingResult(newState, false));
            return;
        }
        this.contents = result;
        cir.setReturnValue(this.updateAfterBrewing(List.of(), result, ParticleTypes.BUBBLE, false, null));
    }

    @Unique
    private void spicedcider$reagentFirst(boolean heated) {
        Level level = ((CauldronBlockEntity) (Object) this).getLevel();
        if (level == null) {
            return;
        }
        for (int i = 0; i < this.items.size(); i++) {
            BrewingRecipeInput input = new BrewingRecipeInput(List.of(this.items.get(i)), this.contents, heated);
            if (level.getRecipeManager().getRecipeFor(CauldronModRecipes.BREWING, input, level).isPresent()) {
                if (i > 0) {
                    this.items.addFirst(this.items.remove(i));
                }
                return;
            }
        }
    }

    @Inject(method = "brew", at = @At("RETURN"))
    private void spicedcider$keepLeftovers(BlockState newState, boolean heated, CallbackInfoReturnable<BrewingResult> cir) {
        List<ItemStack> leftovers = this.spicedcider$leftovers;
        this.spicedcider$leftovers = null;
        if (leftovers == null || CauldronBrewing.isWater(this.contents.potion()) || !this.items.isEmpty()) {
            return;
        }
        Level level = ((CauldronBlockEntity) (Object) this).getLevel();
        for (ItemStack stack : leftovers) {
            if (this.contents.isPotion() && (CauldronBrewing.ingredient(stack) != null
                    || stack.is(CauldronModTags.CREATES_SPLASH_POTIONS) || stack.is(CauldronModTags.CREATES_LINGERING_POTIONS))) {
                this.items.add(stack);
            } else if (level != null) {
                Block.popResourceFromFace(level, ((CauldronBlockEntity) (Object) this).getBlockPos(), Direction.UP, stack);
            }
        }
        ((CauldronBlockEntity) (Object) this).setChanged();
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void spicedcider$saveBrew(CompoundTag tag, HolderLookup.Provider provider, CallbackInfo ci) {
        if (this.spicedcider$brew != null) {
            this.spicedcider$brew.unwrapKey().ifPresent(key -> tag.putString("spicedcider.brew", key.location().toString()));
        }
        tag.putInt("spicedcider.brew_seconds", this.spicedcider$seconds);
        tag.putInt("spicedcider.brew_potency", this.spicedcider$potency);
        tag.putInt("spicedcider.brew_count", this.spicedcider$count);
        tag.putInt("spicedcider.drained", this.spicedcider$drained);
    }

    @Inject(method = "loadAdditional", at = @At("TAIL"))
    private void spicedcider$loadBrew(CompoundTag tag, HolderLookup.Provider provider, CallbackInfo ci) {
        this.spicedcider$brew = tag.contains("spicedcider.brew")
                ? BuiltInRegistries.POTION.getHolder(ResourceLocation.parse(tag.getString("spicedcider.brew"))).orElse(null)
                : null;
        this.spicedcider$seconds = tag.getInt("spicedcider.brew_seconds");
        this.spicedcider$potency = tag.getInt("spicedcider.brew_potency");
        this.spicedcider$count = tag.getInt("spicedcider.brew_count");
        this.spicedcider$drained = tag.getInt("spicedcider.drained");
    }

    @Unique
    private boolean spicedcider$isBrew() {
        Optional<Holder<Potion>> potion = this.contents.potion();
        return this.contents.isPotion() && potion.isPresent() && !CauldronBrewing.isWater(potion);
    }

    @Unique
    private void spicedcider$syncState() {
        Optional<Holder<Potion>> potion = this.contents.potion();
        if (CauldronBrewing.isClear(potion)) {
            if (this.spicedcider$brew != null) {
                this.spicedcider$reset(null);
            }
        } else if (potion.isPresent() && (this.spicedcider$brew == null || !CauldronBrewing.same(this.spicedcider$brew, potion.get()))) {
            this.spicedcider$reset(potion.get());
        }
    }

    @Unique
    private void spicedcider$reset(@Nullable Holder<Potion> brew) {
        this.spicedcider$brew = brew;
        this.spicedcider$seconds = 0;
        this.spicedcider$potency = 0;
        this.spicedcider$count = 0;
        this.spicedcider$drained = 0;
    }
}
