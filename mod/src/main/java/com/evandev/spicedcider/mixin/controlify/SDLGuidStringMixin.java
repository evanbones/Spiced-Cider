package com.evandev.spicedcider.mixin.controlify;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.isxander.controlify.driver.sdl.SDLCommonDriver;
import dev.isxander.sdl3java.api.guid.SDL_GUID;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HexFormat;

@IfModLoaded("controlify")
@Mixin(SDLCommonDriver.class)
public class SDLGuidStringMixin {

    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Ldev/isxander/sdl3java/api/guid/SdlGuid;SDL_GUIDToString(Ldev/isxander/sdl3java/api/guid/SDL_GUID;)Ljava/lang/String;"))
    private String spicedcider$guidToStringInJava(SDL_GUID guid, Operation<String> original) {
        return HexFormat.of().formatHex(guid.data);
    }
}
