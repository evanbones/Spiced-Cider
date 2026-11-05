package com.evandev.spicedcider.client;

import com.evandev.spicedcider.compat.firevariants.AutumnityFireBlocks;
import com.evandev.spicedcider.compat.firevariants.BuzzierBeesFireBlocks;
import com.evandev.spicedcider.compat.firevariants.CavernsFireBlocks;
import com.evandev.spicedcider.compat.firevariants.EnvironmentalFireBlocks;
import com.evandev.spicedcider.compat.firevariants.FireVariantCompat;
import com.evandev.spicedcider.compat.firevariants.SupplementariesFireBlocks;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredBlock;

public class FireVariantClient {
    public static void setup() {
        if (FireVariantCompat.buzzierBees) {
            cutout(BuzzierBeesFireBlocks.sulfurCandle, BuzzierBeesFireBlocks.sulfurCandleCake,
                    BuzzierBeesFireBlocks.glowlightCandle, BuzzierBeesFireBlocks.glowlightCandleCake);
            if (ModList.get().isLoaded("amendments")) AmendmentsSkullCandleTextures.register();
        }
        if (FireVariantCompat.supplementaries) {
            cutout(SupplementariesFireBlocks.sulfurSconce, SupplementariesFireBlocks.sulfurSconceWall);
            SupplementariesFireBlocks.CANDLE_HOLDERS.forEach(FireVariantClient::cutout);
        }
        if (FireVariantCompat.caverns) cutout(CavernsFireBlocks.sulfurBrazier);
        if (FireVariantCompat.environmental) cutout(EnvironmentalFireBlocks.dwarfSpruceSulfurTorch, EnvironmentalFireBlocks.dwarfSprucePlantSulfurTorch);
        if (FireVariantCompat.autumnity) cutout(AutumnityFireBlocks.largeSulfurSlice);
    }

    @SafeVarargs
    private static void cutout(DeferredBlock<Block>... blocks) {
        for (DeferredBlock<Block> block : blocks) {
            if (block != null) ItemBlockRenderTypes.setRenderLayer(block.get(), RenderType.cutout());
        }
    }

    private static void cutout(DeferredBlock<Block> block) {
        if (block != null) ItemBlockRenderTypes.setRenderLayer(block.get(), RenderType.cutout());
    }
}
