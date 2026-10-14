package com.evandev.spicedcider.mixin.moonlight;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.mehvahdjukaar.moonlight.api.client.TextureCache;
import net.mehvahdjukaar.moonlight.api.misc.TriResult;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;

import java.util.function.Predicate;

@IfModLoaded("moonlight")
@Mixin(value = TextureCache.class, remap = false)
public class TextureCacheThreadSafetyMixin {

    @WrapMethod(method = "getCachedTexture")
    private static TriResult<String> spicedcider$lockGet(ItemLike block, Predicate<String> texturePredicate, Operation<TriResult<String>> original) {
        synchronized (TextureCache.class) {
            return original.call(block, texturePredicate);
        }
    }

    @WrapMethod(method = "add")
    private static void spicedcider$lockAdd(ItemLike block, String texture, Operation<Void> original) {
        synchronized (TextureCache.class) {
            original.call(block, texture);
        }
    }

    @WrapMethod(method = "clear")
    private static void spicedcider$lockClear(Operation<Void> original) {
        synchronized (TextureCache.class) {
            original.call();
        }
    }
}
