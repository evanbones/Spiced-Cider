package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, SpicedCider.MOD_ID);

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> RESIN = FLUIDS.register("resin",
            () -> new BaseFlowingFluid.Source(ModFluids.resinProperties()));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_RESIN = FLUIDS.register("flowing_resin",
            () -> new BaseFlowingFluid.Flowing(ModFluids.resinProperties()));

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> MAPLE_SYRUP = FLUIDS.register("maple_syrup",
            () -> new BaseFlowingFluid.Source(ModFluids.mapleSyrupProperties()));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_MAPLE_SYRUP = FLUIDS.register("flowing_maple_syrup",
            () -> new BaseFlowingFluid.Flowing(ModFluids.mapleSyrupProperties()));

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> LATEX = FLUIDS.register("latex",
            () -> new BaseFlowingFluid.Source(ModFluids.latexProperties()));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_LATEX = FLUIDS.register("flowing_latex",
            () -> new BaseFlowingFluid.Flowing(ModFluids.latexProperties()));

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> RESIN_OIL = FLUIDS.register("resin_oil",
            () -> new BaseFlowingFluid.Source(ModFluids.resinOilProperties()));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_RESIN_OIL = FLUIDS.register("flowing_resin_oil",
            () -> new BaseFlowingFluid.Flowing(ModFluids.resinOilProperties()));

    private static BaseFlowingFluid.Properties resinProperties() {
        return new BaseFlowingFluid.Properties(ModFluidTypes.RESIN, RESIN, FLOWING_RESIN).slopeFindDistance(2).levelDecreasePerBlock(2).tickRate(30)
                .bucket(ModItems.RESIN_BUCKET).block(ModBlocks.RESIN);
    }

    private static BaseFlowingFluid.Properties mapleSyrupProperties() {
        return new BaseFlowingFluid.Properties(ModFluidTypes.MAPLE_SYRUP, MAPLE_SYRUP, FLOWING_MAPLE_SYRUP).slopeFindDistance(2).levelDecreasePerBlock(2).tickRate(20)
                .bucket(ModItems.MAPLE_SYRUP_BUCKET).block(ModBlocks.MAPLE_SYRUP);
    }

    private static BaseFlowingFluid.Properties resinOilProperties() {
        return new BaseFlowingFluid.Properties(ModFluidTypes.RESIN_OIL, RESIN_OIL, FLOWING_RESIN_OIL).slopeFindDistance(3).levelDecreasePerBlock(1).tickRate(10)
                .bucket(ModItems.RESIN_OIL_BUCKET).block(ModBlocks.RESIN_OIL);
    }

    private static BaseFlowingFluid.Properties latexProperties() {
        return new BaseFlowingFluid.Properties(ModFluidTypes.LATEX, LATEX, FLOWING_LATEX).slopeFindDistance(2).levelDecreasePerBlock(2).tickRate(25)
                .bucket(ModItems.LATEX_BUCKET).block(ModBlocks.LATEX);
    }
}
