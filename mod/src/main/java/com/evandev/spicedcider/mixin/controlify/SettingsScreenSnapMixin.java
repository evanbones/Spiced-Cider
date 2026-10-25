package com.evandev.spicedcider.mixin.controlify;

import com.evandev.spicedcider.client.options.SettingsScreen;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.isxander.controlify.api.vmousesnapping.ISnapBehaviour;
import dev.isxander.controlify.api.vmousesnapping.SnapPoint;
import org.spongepowered.asm.mixin.Mixin;

import java.util.function.Consumer;

@IfModLoaded("controlify")
@Mixin(SettingsScreen.class)
public class SettingsScreenSnapMixin implements ISnapBehaviour {

    @Override
    public void controlify$collectSnapPoints(Consumer<SnapPoint> consumer) {
        ((SettingsScreen) (Object) this).collectSnapPoints((x, y, range) -> consumer.accept(new SnapPoint(x, y, range)));
    }
}
