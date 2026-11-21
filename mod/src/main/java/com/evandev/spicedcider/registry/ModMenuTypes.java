package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.content.casting.smeltery.SmelteryMenu;
import com.evandev.spicedcider.content.fishtrap.FishTrapMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, SpicedCider.MOD_ID);


    public static final DeferredHolder<MenuType<?>, MenuType<SmelteryMenu>> SMELTERY = MENU_TYPES.register("smeltery",
            () -> IMenuTypeExtension.create(SmelteryMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<FishTrapMenu>> FISH_TRAP = MENU_TYPES.register("fish_trap",
            () -> new MenuType<>(FishTrapMenu::new, FeatureFlags.VANILLA_SET));
}
