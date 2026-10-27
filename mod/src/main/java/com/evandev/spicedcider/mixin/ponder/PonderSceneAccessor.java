package com.evandev.spicedcider.mixin.ponder;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.instruction.PonderInstruction;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@IfModLoaded("ponder")
@Mixin(value = PonderScene.class, remap = false)
public interface PonderSceneAccessor {

    @Accessor("sceneId")
    ResourceLocation spicedcider$getSceneId();

    @Accessor("schedule")
    List<PonderInstruction> spicedcider$getSchedule();
}
