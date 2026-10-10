package com.evandev.spicedcider.mixin.modestmining;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@IfModLoaded("modestmining")
@Mixin(targets = "com.baisylia.modestmining.block.entity.custom.ShellBlock", remap = false)
public class ShellBlockSoundMixin {

    @Unique
    private static SoundType spicedcider$seashellSound;

    public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        if (spicedcider$seashellSound == null) {
            SoundEvent breakSound = spicedcider$sound("break");
            SoundEvent step = spicedcider$sound("step");
            SoundEvent place = spicedcider$sound("place");
            SoundEvent hit = spicedcider$sound("hit");
            SoundEvent fall = spicedcider$sound("fall");
            if (breakSound == null || step == null || place == null || hit == null || fall == null) return SoundType.CORAL_BLOCK;
            spicedcider$seashellSound = new SoundType(1.0F, 1.0F, breakSound, step, place, hit, fall);
        }
        return spicedcider$seashellSound;
    }

    @Unique
    private static SoundEvent spicedcider$sound(String type) {
        return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath("nomansland", "block.seashells." + type));
    }
}
