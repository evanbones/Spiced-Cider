package com.evandev.spicedcider.content.mixing;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.compat.toilandtrouble.ToilAndTroubleCauldrons;
import com.evandev.spicedcider.networking.CauldronStirPayload;
import com.evandev.spicedcider.registry.ModRecipeSerializers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.fluids.CauldronFluidContent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class CauldronMixing {
    public static final TagKey<Item> SPOONS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("manual_labour", "ladles"));
    public static final TagKey<Block> HEAT_SOURCES = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("farmersdelight", "heat_sources"));
    public static final int STIR_TICKS = 60;
    private static final int GRACE_TICKS = 10;

    private static final Map<Key, Hold> HOLDS = new HashMap<>();

    private record Unit(ItemStack stack, Consumer<Integer> take) {
    }

    private record Key(ServerLevel level, BlockPos pos) {
    }

    private static final class Hold {
        final ServerPlayer player;
        final InteractionHand hand;
        int ticks;
        int grace = GRACE_TICKS;

        Hold(ServerPlayer player, InteractionHand hand) {
            this.player = player;
            this.hand = hand;
        }
    }

    private record Match(RecipeHolder<CauldronMixingRecipe> recipe, List<Unit> units, int[] assignment) {
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void use(UseItemOnBlockEvent event) {
        if (event.getUsePhase() != UseItemOnBlockEvent.UsePhase.ITEM_BEFORE_BLOCK) {
            return;
        }
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        if (!state.is(BlockTags.CAULDRONS)) {
            return;
        }
        ItemStack held = event.getItemStack();
        if (held.is(SPOONS)) {
            stir(event, level, pos, held);
        } else if (dropIn(event, level, pos, state, held)) {
            event.cancelWithResult(ItemInteractionResult.sidedSuccess(level.isClientSide()));
        }
    }

    private static void stir(UseItemOnBlockEvent event, Level level, BlockPos pos, ItemStack spoon) {
        event.cancelWithResult(ItemInteractionResult.sidedSuccess(level.isClientSide()));
        if (!(level instanceof ServerLevel server) || !(event.getPlayer() instanceof ServerPlayer player)) {
            return;
        }
        Key key = new Key(server, pos);
        Hold hold = HOLDS.get(key);
        if (hold != null) {
            hold.grace = GRACE_TICKS;
            return;
        }
        if (find(server, pos) == null) {
            return;
        }
        HOLDS.put(key, new Hold(player, event.getHand()));
        broadcast(server, pos, spoon);
    }

    private static boolean dropIn(UseItemOnBlockEvent event, Level level, BlockPos pos, BlockState state, ItemStack held) {
        if (held.isEmpty() || level.getBlockEntity(pos) != null && isToil(level.getBlockEntity(pos))) {
            return false;
        }
        if (state.is(Blocks.WATER_CAULDRON) && ModList.get().isLoaded("toil_and_trouble")) {
            return false;
        }
        Fluid fluid = fluidIn(state, null, false);
        if (fluid == null || fluid == Fluids.EMPTY || !isIngredientFor(level, fluid, held)) {
            return false;
        }
        if (!level.isClientSide()) {
            ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, held.copyWithCount(1), 0, 0, 0);
            entity.setPickUpDelay(40);
            level.addFreshEntity(entity);
            level.playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 0.3F, 1.2F);
            Player player = event.getPlayer();
            if (player == null || !player.getAbilities().instabuild) {
                held.shrink(1);
            }
        }
        return true;
    }

    private static boolean isIngredientFor(Level level, Fluid fluid, ItemStack stack) {
        for (RecipeHolder<CauldronMixingRecipe> holder : level.getRecipeManager().getAllRecipesFor(ModRecipeSerializers.CAULDRON_MIXING_TYPE.get())) {
            if (holder.value().fluid() == fluid && holder.value().ingredients().stream().anyMatch(ingredient -> ingredient.test(stack))) {
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void tick(LevelTickEvent.Post event) {
        if (HOLDS.isEmpty() || !(event.getLevel() instanceof ServerLevel server)) {
            return;
        }
        Iterator<Map.Entry<Key, Hold>> iterator = HOLDS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Key, Hold> entry = iterator.next();
            Key key = entry.getKey();
            if (key.level() != server) {
                continue;
            }
            Hold hold = entry.getValue();
            BlockPos pos = key.pos();
            if (--hold.grace <= 0 || hold.player.isRemoved() || !hold.player.getItemInHand(hold.hand).is(SPOONS)) {
                iterator.remove();
                broadcast(server, pos, ItemStack.EMPTY);
                continue;
            }
            if (hold.ticks % 10 == 0) {
                server.sendParticles(ParticleTypes.SPLASH, pos.getX() + 0.5, pos.getY() + 0.95, pos.getZ() + 0.5, 4, 0.2, 0.02, 0.2, 0.0);
                server.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 0.25F, 1.3F + server.random.nextFloat() * 0.2F);
            }
            if (++hold.ticks < STIR_TICKS) {
                continue;
            }
            iterator.remove();
            broadcast(server, pos, ItemStack.EMPTY);
            Match match = find(server, pos);
            if (match != null) {
                complete(server, pos, match, hold.player, hold.hand);
            }
        }
    }

    @Nullable
    private static Match find(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        boolean toil = isToil(blockEntity);
        Fluid fluid = fluidIn(state, blockEntity, toil);
        if (fluid == null) {
            return null;
        }
        boolean heated = level.getBlockState(pos.below()).is(HEAT_SOURCES);
        List<Unit> units = units(level, pos, blockEntity, toil);
        List<ItemStack> stacks = units.stream().map(Unit::stack).toList();
        Match best = null;
        for (RecipeHolder<CauldronMixingRecipe> holder : level.getRecipeManager().getAllRecipesFor(ModRecipeSerializers.CAULDRON_MIXING_TYPE.get())) {
            CauldronMixingRecipe recipe = holder.value();
            if (recipe.fluid() != fluid || recipe.heated() && !heated) {
                continue;
            }
            if (best != null && recipe.ingredients().size() <= best.recipe().value().ingredients().size()) {
                continue;
            }
            int[] assignment = recipe.match(stacks);
            if (assignment != null) {
                best = new Match(holder, units, assignment);
            }
        }
        return best;
    }

    private static void complete(ServerLevel level, BlockPos pos, Match match, ServerPlayer player, InteractionHand hand) {
        CauldronMixingRecipe recipe = match.recipe().value();
        BlockEntity blockEntity = level.getBlockEntity(pos);
        boolean toil = isToil(blockEntity);
        for (int unit : match.assignment()) {
            match.units().get(unit).take().accept(1);
        }
        if (toil) {
            ToilAndTroubleCauldrons.changed(blockEntity);
        }
        if (!recipe.result().isEmpty()) {
            ItemStack result = recipe.result().copy();
            if (!player.addItem(result)) {
                player.drop(result, false);
            }
        }
        if (!recipe.keepFluid()) {
            if (toil) {
                for (ItemStack leftover : ToilAndTroubleCauldrons.items(blockEntity)) {
                    Block.popResourceFromFace(level, pos, Direction.UP, leftover.copy());
                }
                ToilAndTroubleCauldrons.items(blockEntity).clear();
            }
            level.setBlockAndUpdate(pos, recipe.resultFluid().map(CauldronMixing::fullCauldron).orElse(Blocks.CAULDRON.defaultBlockState()));
        }
        player.getItemInHand(hand).hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
        level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 0.8F, 1.0F);
        level.sendParticles(ParticleTypes.SPLASH, pos.getX() + 0.5, pos.getY() + 0.95, pos.getZ() + 0.5, 16, 0.25, 0.05, 0.25, 0.0);
    }

    private static void broadcast(ServerLevel level, BlockPos pos, ItemStack spoon) {
        PacketDistributor.sendToPlayersTrackingChunk(level, new ChunkPos(pos), new CauldronStirPayload(pos, spoon.copy()));
    }

    private static boolean isToil(@Nullable BlockEntity blockEntity) {
        return blockEntity != null && ModList.get().isLoaded("toil_and_trouble") && ToilAndTroubleCauldrons.is(blockEntity);
    }

    @Nullable
    private static Fluid fluidIn(BlockState state, @Nullable BlockEntity blockEntity, boolean toil) {
        if (state.is(Blocks.CAULDRON)) {
            return Fluids.EMPTY;
        }
        if (toil) {
            return ToilAndTroubleCauldrons.fullFluid(blockEntity, state);
        }
        CauldronFluidContent content = CauldronFluidContent.getForBlock(state.getBlock());
        if (content == null || content.currentLevel(state) < content.maxLevel) {
            return null;
        }
        return content.fluid;
    }

    public static BlockState fullCauldron(Fluid fluid) {
        CauldronFluidContent content = CauldronFluidContent.getForFluid(fluid);
        if (content == null) {
            return Blocks.CAULDRON.defaultBlockState();
        }
        BlockState state = content.block.defaultBlockState();
        return content.levelProperty == null ? state : state.setValue(content.levelProperty, content.maxLevel);
    }

    private static List<Unit> units(Level level, BlockPos pos, @Nullable BlockEntity blockEntity, boolean toil) {
        List<Unit> units = new ArrayList<>();
        if (toil) {
            for (ItemStack stack : ToilAndTroubleCauldrons.items(blockEntity)) {
                for (int i = 0; i < stack.getCount(); i++) {
                    units.add(new Unit(stack.copyWithCount(1), stack::shrink));
                }
            }
        }
        AABB inside = new AABB(Vec3.atLowerCornerOf(pos).add(0.05, 0.1, 0.05), Vec3.atLowerCornerOf(pos).add(0.95, 1.6, 0.95));
        for (ItemEntity entity : level.getEntitiesOfClass(ItemEntity.class, inside)) {
            ItemStack stack = entity.getItem();
            for (int i = 0; i < stack.getCount(); i++) {
                units.add(new Unit(stack.copyWithCount(1), amount -> {
                    entity.getItem().shrink(amount);
                    if (entity.getItem().isEmpty()) {
                        entity.discard();
                    }
                }));
            }
        }
        return units;
    }
}
