package com.evandev.spicedcider.compat.firevariants;

import com.evandev.spicedcider.registry.ModBlocks;
import com.evandev.spicedcider.registry.ModFireBlocks;
import com.evandev.spicedcider.registry.ModItems;
import com.evandev.spicedcider.registry.ModParticleTypes;
import net.mehvahdjukaar.supplementaries.common.block.blocks.CandleHolderBlock;
import net.mehvahdjukaar.supplementaries.common.block.blocks.SconceBlock;
import net.mehvahdjukaar.supplementaries.common.block.blocks.SconceWallBlock;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class SupplementariesFireBlocks {
    public static final List<DeferredBlock<Block>> CANDLE_HOLDERS = new ArrayList<>();
    public static final List<DeferredItem<BlockItem>> CANDLE_HOLDER_ITEMS = new ArrayList<>();

    public static DeferredBlock<Block> sulfurSconce;
    public static DeferredBlock<Block> sulfurSconceWall;
    public static DeferredItem<Item> sulfurSconceItem;

    public static void register(boolean candles, boolean glowlight) {
        sulfurSconce = ModBlocks.BLOCKS.register("sconce_sulfur", () -> new SconceBlock(BlockBehaviour.Properties.of()
                .noCollission().noOcclusion().pushReaction(PushReaction.DESTROY).instabreak().sound(SoundType.LANTERN),
                ModFireBlocks.SULFUR_LIGHT, ModParticleTypes.SULFUR_FIRE_FLAME));
        sulfurSconceWall = ModBlocks.BLOCKS.register("sconce_wall_sulfur", () -> new SconceWallBlock(
                BlockBehaviour.Properties.ofFullCopy(sulfurSconce.get()).dropsLike(sulfurSconce.get()), ModParticleTypes.SULFUR_FIRE_FLAME));
        sulfurSconceItem = ModItems.ITEMS.register("sconce_sulfur",
                () -> new StandingAndWallBlockItem(sulfurSconce.get(), sulfurSconceWall.get(), new Item.Properties(), Direction.DOWN));
        if (candles) {
            holder("candle_holder_sulfur", BuzzierBeesFireBlocks.SULFUR_FLAME, CandleHolderBlock::getDefaultParticleOffsets);
            if (glowlight) {
                holder("candle_holder_glowlight", BuzzierBeesFireBlocks.GLOWLIGHT_FLAME, CandleHolderBlock::getDefaultParticleOffsets);
            }
        }
    }

    public static void holder(String name, ResourceLocation flame, Function<BlockState, List<Vec3>> offsets) {
        DeferredBlock<Block> block = ModBlocks.BLOCKS.register(name, () -> new CandleHolderBlock(null,
                BlockBehaviour.Properties.ofFullCopy(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("supplementaries", "sconce"))),
                particle(flame), offsets));
        CANDLE_HOLDERS.add(block);
        CANDLE_HOLDER_ITEMS.add(ModItems.ITEMS.registerSimpleBlockItem(block));
    }

    @SuppressWarnings("unchecked")
    private static Supplier<ParticleType<? extends ParticleOptions>> particle(ResourceLocation id) {
        return () -> (ParticleType<? extends ParticleOptions>) BuiltInRegistries.PARTICLE_TYPE.get(id);
    }
}
