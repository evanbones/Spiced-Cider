package com.evandev.spicedcider.mixin.scholar;

import com.evandev.spicedcider.compat.controlify.TextWidgetKeyboardProcessor;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.isxander.controlify.screenop.ComponentProcessor;
import dev.isxander.controlify.screenop.ComponentProcessorProvider;
import io.github.mortuusars.scholar.client.gui.widget.textbox.TextBox;
import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@IfModLoaded("scholar")
@IfModLoaded("controlify")
@Mixin(value = TextBox.class, remap = false)
public class TextBoxKeyboardMixin implements ComponentProcessorProvider {

    @Unique
    private ComponentProcessor spicedcider$processor;

    @Override
    public ComponentProcessor componentProcessor() {
        if (spicedcider$processor == null) spicedcider$processor = new TextWidgetKeyboardProcessor((AbstractWidget) (Object) this);
        return spicedcider$processor;
    }
}
