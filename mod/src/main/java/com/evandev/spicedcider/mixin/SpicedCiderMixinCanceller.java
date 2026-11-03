package com.evandev.spicedcider.mixin;

import com.bawnorton.mixinsquared.api.MixinCanceller;
import net.neoforged.fml.loading.LoadingModList;

import java.util.List;
import java.util.Set;

public class SpicedCiderMixinCanceller implements MixinCanceller {

    private static final Set<String> VANILLA_BACKPORT_MUSIC_MIXINS = Set.of(
            "com.blackgear.vanillabackport.core.mixin.client.music_toast.MusicManagerMixin",
            "com.blackgear.vanillabackport.core.mixin.client.music_toast.PauseScreenMixin",
            "com.blackgear.vanillabackport.core.mixin.client.music_toast.ToastComponentMixin",
            "com.blackgear.vanillabackport.core.mixin.client.music_on_pause.MinecraftMixin",
            "com.blackgear.vanillabackport.core.mixin.client.game_options.SoundOptionsScreenMixin"
    );

    private static final String LIVING_WIND_CHIMES_MIXINS = "com.macatmovie.livingwind.mixin.Chimes";

    @Override
    public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {

        if ("dev.tazer.clutternomore.mixin.compat.emi.ItemEmiStackMixin".equals(mixinClassName)) {
            return true;
        }

        if ("io.wispforest.owo.mixin.ext.ItemStackMixin".equals(mixinClassName)
                || "io.wispforest.owo.mixin.ext.ComponentMapImplMixin".equals(mixinClassName)) {
            return true;
        }

        if ("com.jvn.wherewindsblow.mixin.client.compat.snowrealmagic.SnowRealMagicVariantModelMixin".equals(mixinClassName)) {
            return true;
        }

        if (mixinClassName.startsWith(LIVING_WIND_CHIMES_MIXINS)) {
            return true;
        }

        if ("tech.thatgravyboat.vanity.neoforge.mixin.geckolib.HumanoidArmorLayerMixin".equals(mixinClassName)) {
            return LoadingModList.get().getModFileById("geckolib") == null;
        }

        if (VANILLA_BACKPORT_MUSIC_MIXINS.contains(mixinClassName)) {
            return LoadingModList.get().getModFileById("music_tweaks") != null;
        }

        return false;
    }
}
