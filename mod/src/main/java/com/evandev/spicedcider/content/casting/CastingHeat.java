package com.evandev.spicedcider.content.casting;

import com.evandev.spicedcider.SpicedCider;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

public enum CastingHeat implements StringRepresentable {
    NONE("none"),
    LOW("low"),
    HIGH("high");

    public static final Codec<CastingHeat> CODEC = StringRepresentable.fromEnum(CastingHeat::values);
    public static final TagKey<Block> LOW_HEAT = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "crucible_low_heat"));
    public static final TagKey<Block> HIGH_HEAT = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "crucible_high_heat"));
    private static final ResourceLocation BLAZE_BURNER = ResourceLocation.fromNamespaceAndPath("create", "blaze_burner");

    private final String name;

    CastingHeat(String name) {
        this.name = name;
    }

    public boolean reaches(CastingHeat required) {
        return ordinal() >= required.ordinal();
    }

    public Component displayName() {
        return Component.translatable("spicedcider.casting_heat." + this.name);
    }

    public Component stateName() {
        return Component.translatable("spicedcider.casting_heat.state." + this.name);
    }

    public static CastingHeat below(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos.below());
        if (state.hasProperty(BlockStateProperties.LIT) && !state.getValue(BlockStateProperties.LIT)) {
            return NONE;
        }
        if (BuiltInRegistries.BLOCK.getKey(state.getBlock()).equals(BLAZE_BURNER)) {
            return blazeBurner(state);
        }
        if (state.is(HIGH_HEAT) || state.getFluidState().is(FluidTags.LAVA)) {
            return HIGH;
        }
        return state.is(LOW_HEAT) ? LOW : NONE;
    }

    private static CastingHeat blazeBurner(BlockState state) {
        for (Property<?> property : state.getProperties()) {
            if (property.getName().equals("blaze")) {
                String value = state.getValue(property).toString().toLowerCase();
                return switch (value) {
                    case "seething", "kindled" -> HIGH;
                    case "fading" -> LOW;
                    default -> NONE;
                };
            }
        }
        return NONE;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
