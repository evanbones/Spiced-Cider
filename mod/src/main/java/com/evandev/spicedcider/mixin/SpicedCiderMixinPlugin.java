package com.evandev.spicedcider.mixin;

import net.neoforged.fml.loading.FMLLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class SpicedCiderMixinPlugin implements IMixinConfigPlugin {

    private static boolean isModLoaded(String modId) {
        return FMLLoader.getLoadingModList().getModFileById(modId) != null;
    }

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.contains("namingunconvention.ModernWorldCreationGameTabMixin")) {
            return isModLoaded("modernworldcreation");
        }
        if (mixinClassName.contains("cookscollection")) {
            return isModLoaded("cookscollection");
        }
        if (mixinClassName.contains("environmental")) {
            return isModLoaded("environmental");
        }
        if (mixinClassName.contains("tide")) {
            return isModLoaded("tide");
        }
        if (mixinClassName.contains("jade")) {
            return isModLoaded("jade");
        }
        if (mixinClassName.contains("quark")) {
            return isModLoaded("quark");
        }
        if (mixinClassName.contains("snowyspirit")) {
            return isModLoaded("snowyspirit");
        }
        if (mixinClassName.contains("caverns_and_chasms")) {
            return isModLoaded("caverns_and_chasms");
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}