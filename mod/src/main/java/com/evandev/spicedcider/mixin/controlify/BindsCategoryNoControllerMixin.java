package com.evandev.spicedcider.mixin.controlify;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.isxander.controlify.config.settings.profile.ProfileSettings;
import dev.isxander.controlify.controller.ControllerEntity;
import dev.isxander.controlify.gui.screen.ControllerConfigScreenFactory;
import dev.isxander.yacl3.api.ConfigCategory;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Optional;

@IfModLoaded("controlify")
@Mixin(ControllerConfigScreenFactory.class)
public class BindsCategoryNoControllerMixin {

    @WrapMethod(method = "makeBindsCategory")
    private Optional<ConfigCategory> spicedcider$skipWithoutController(ProfileSettings settings, ProfileSettings defaults, Optional<ControllerEntity> controller, Operation<Optional<ConfigCategory>> original) {
        if (controller.isEmpty()) return Optional.empty();
        return original.call(settings, defaults, controller);
    }
}
