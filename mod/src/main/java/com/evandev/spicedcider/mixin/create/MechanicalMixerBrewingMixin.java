package com.evandev.spicedcider.mixin.create;

import com.evandev.spicedcider.compat.create.BasinBrewing;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@IfModLoaded("create")
@Mixin(value = MechanicalMixerBlockEntity.class, remap = false)
public abstract class MechanicalMixerBrewingMixin extends BasinOperatingBlockEntity {

    public MechanicalMixerBrewingMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @ModifyReturnValue(method = "getMatchingRecipes", at = @At("RETURN"))
    private List<Recipe<?>> spicedcider$brewing(List<Recipe<?>> recipes) {
        if (!recipes.isEmpty()) return recipes;
        return this.getBasin().filter(BasinBrewing::canBrew).<List<Recipe<?>>>map(basin -> List.of(BasinBrewing.RECIPE)).orElse(recipes);
    }
}
