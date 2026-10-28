package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.content.block.LatexCauldronBlock;
import com.evandev.spicedcider.registry.ModBlocks;
import com.evandev.spicedcider.registry.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MilkBucketItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.fluids.CauldronFluidContent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.RegisterCauldronFluidContentEvent;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class CauldronFluidEvents {
    private static Map<Fluid, Item> buckets;

    @SubscribeEvent
    public static void registerCauldronContent(RegisterCauldronFluidContentEvent event) {
        event.register(ModBlocks.LATEX_CAULDRON.get(), ModFluids.LATEX.get(), FluidType.BUCKET_VOLUME, LatexCauldronBlock.LEVEL);
    }

    @SubscribeEvent
    public static void useBucketOnCauldron(UseItemOnBlockEvent event) {
        Player player = event.getPlayer();
        if (event.getUsePhase() != UseItemOnBlockEvent.UsePhase.BLOCK || player == null) return;
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        InteractionHand hand = event.getHand();
        ItemStack stack = event.getItemStack();

        if (stack.is(Items.BUCKET)) {
            CauldronFluidContent content = CauldronFluidContent.getForBlock(state.getBlock());
            if (content == null || isVanilla(content.block) || content.currentLevel(state) < content.maxLevel) return;
            Item bucket = bucketFor(content.fluid);
            if (bucket == null) return;
            event.cancelWithResult(CauldronInteraction.fillBucket(state, level, pos, player, hand, stack, new ItemStack(bucket), s -> true, SoundEvents.BUCKET_FILL));
        } else if (stack.getItem() instanceof BucketItem || stack.getItem() instanceof MilkBucketItem) {
            Fluid fluid = FluidUtil.getFluidContained(stack).map(FluidStack::getFluid).orElse(Fluids.EMPTY);
            CauldronFluidContent content = CauldronFluidContent.getForFluid(fluid);
            if (content == null || isVanilla(content.block)) return;
            boolean partial = state.is(content.block) && content.currentLevel(state) < content.maxLevel;
            if (!state.is(Blocks.CAULDRON) && !partial) return;
            BlockState full = content.block.defaultBlockState();
            if (content.levelProperty != null) full = full.setValue(content.levelProperty, content.maxLevel);
            ItemInteractionResult result = CauldronInteraction.emptyBucket(level, pos, player, hand, stack, full, SoundEvents.BUCKET_EMPTY);
            event.cancelWithResult(result);
        }
    }

    private static boolean isVanilla(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).getNamespace().equals("minecraft");
    }

    private static Item bucketFor(Fluid fluid) {
        if (fluid.getBucket() != Items.AIR) return fluid.getBucket();
        if (buckets == null) {
            Map<Fluid, Item> found = new HashMap<>();
            for (Item item : BuiltInRegistries.ITEM) {
                if (item.getClass() == BucketItem.class) found.putIfAbsent(((BucketItem) item).content, item);
            }
            buckets = found;
        }
        return buckets.get(fluid);
    }
}
