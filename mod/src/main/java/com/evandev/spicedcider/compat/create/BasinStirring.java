package com.evandev.spicedcider.compat.create;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.content.brewing.Brewing;
import com.evandev.spicedcider.networking.BasinStirPayload;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import com.simibubi.create.content.fluids.transfer.GenericItemFilling;
import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BasinStirring {
    public static final TagKey<Item> SPOONS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("manual_labour", "ladles"));
    public static final TagKey<Item> MIXER_ONLY = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "mixer_only"));
    public static final int STIR_TICKS = 60;
    private static final int GRACE_TICKS = 10;

    private static final Map<Key, Hold> HOLDS = new HashMap<>();

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

    public static void register() {
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, BasinStirring::use);
        NeoForge.EVENT_BUS.addListener(BasinStirring::tick);
    }

    private static void use(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        if (!(level.getBlockEntity(event.getPos()) instanceof BasinBlockEntity basin)) return;
        Player player = event.getEntity();
        ItemStack held = event.getItemStack();
        boolean handled;
        if (held.is(SPOONS)) {
            handled = true;
            if (level instanceof ServerLevel server && player instanceof ServerPlayer serverPlayer) {
                stir(server, basin, serverPlayer, event.getHand(), held);
            }
        } else if (event.getFace() != Direction.UP || player.isSecondaryUseActive()) {
            handled = false;
        } else if (BasinWashing.tryWash(level, basin, player, event.getHand(), held)) {
            handled = true;
        } else if (insertable(level, held)) {
            handled = true;
            if (!level.isClientSide()) insert(level, basin, player, held);
        } else {
            handled = false;
        }
        if (handled) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide()));
        }
    }

    private static void stir(ServerLevel level, BasinBlockEntity basin, ServerPlayer player, InteractionHand hand, ItemStack spoon) {
        BlockPos pos = basin.getBlockPos();
        Key key = new Key(level, pos);
        Hold hold = HOLDS.get(key);
        if (hold != null) {
            if (hold.grace < GRACE_TICKS - 5) broadcast(level, pos, spoon);
            hold.grace = GRACE_TICKS;
            return;
        }
        if (find(level, basin) == null) return;
        HOLDS.put(key, new Hold(player, hand));
        broadcast(level, pos, spoon);
    }

    private static boolean insertable(Level level, ItemStack stack) {
        if (stack.isEmpty() || GenericItemEmptying.canItemBeEmptied(level, stack) || GenericItemFilling.canItemBeFilled(level, stack)) return false;
        if (stack.is(Items.ARROW) || stack.is(Brewing.MAKES_SPLASH) || stack.is(Brewing.MAKES_LINGERING) || Brewing.ingredient(stack) != null) return true;
        for (RecipeHolder<MixingRecipe> holder : mixingRecipes(level)) {
            if (handMixable(holder.value())) {
                for (Ingredient ingredient : holder.value().getIngredients()) {
                    if (ingredient.test(stack)) return true;
                }
            }
        }
        return false;
    }

    private static void insert(Level level, BasinBlockEntity basin, @Nullable Player player, ItemStack held) {
        ItemStack one = held.copyWithCount(1);
        if (!ItemHandlerHelper.insertItem(basin.getInputInventory(), one, true).isEmpty()) return;
        ItemHandlerHelper.insertItem(basin.getInputInventory(), one, false);
        if (player == null || !player.getAbilities().instabuild) held.shrink(1);
        level.playSound(null, basin.getBlockPos(), SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.5F, 1.2F);
    }

    private static void tick(LevelTickEvent.Post event) {
        if (HOLDS.isEmpty() || !(event.getLevel() instanceof ServerLevel server)) return;
        Iterator<Map.Entry<Key, Hold>> iterator = HOLDS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Key, Hold> entry = iterator.next();
            Key key = entry.getKey();
            if (key.level() != server) continue;
            Hold hold = entry.getValue();
            BlockPos pos = key.pos();
            if (--hold.grace <= 0 || hold.player.isRemoved() || !hold.player.getItemInHand(hold.hand).is(SPOONS)
                    || !(server.getBlockEntity(pos) instanceof BasinBlockEntity basin)) {
                iterator.remove();
                broadcast(server, pos, ItemStack.EMPTY);
                continue;
            }
            if (hold.ticks % 10 == 0) {
                server.sendParticles(ParticleTypes.SPLASH, pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, 4, 0.2, 0.02, 0.2, 0.0);
                server.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 0.25F, 1.3F + server.random.nextFloat() * 0.2F);
            }
            if (++hold.ticks < STIR_TICKS) continue;
            hold.ticks = 0;
            Recipe<?> recipe = find(server, basin);
            if (recipe == null || !complete(server, basin, recipe, hold) || find(server, basin) == null) {
                iterator.remove();
                broadcast(server, pos, ItemStack.EMPTY);
            }
        }
    }

    @Nullable
    private static Recipe<?> find(Level level, BasinBlockEntity basin) {
        if (BasinBrewing.canBrew(basin)) return BasinBrewing.RECIPE;
        MixingRecipe best = null;
        int bestSize = -1;
        for (RecipeHolder<MixingRecipe> holder : mixingRecipes(level)) {
            MixingRecipe recipe = holder.value();
            int size = recipe.getIngredients().size() + recipe.getFluidIngredients().size();
            if (size <= bestSize || !handMixable(recipe) || !BasinRecipe.match(basin, recipe)) continue;
            best = recipe;
            bestSize = size;
        }
        return best;
    }

    public static boolean handMixable(MixingRecipe recipe) {
        if (recipe.getRequiredHeat() == HeatCondition.SUPERHEATED) return false;
        for (ProcessingOutput output : recipe.getRollableResults()) {
            if (output.getStack().is(MIXER_ONLY)) return false;
        }
        return true;
    }

    public static Iterable<RecipeHolder<MixingRecipe>> mixingRecipes(Level level) {
        return level.getRecipeManager().getAllRecipesFor(AllRecipeTypes.MIXING.<RecipeInput, MixingRecipe>getType());
    }

    private static boolean complete(ServerLevel level, BasinBlockEntity basin, Recipe<?> recipe, Hold hold) {
        boolean done = recipe == BasinBrewing.RECIPE ? BasinBrewing.brew(basin) : BasinRecipe.apply(basin, recipe);
        if (!done) return false;
        BlockPos pos = basin.getBlockPos();
        hold.player.getItemInHand(hold.hand).hurtAndBreak(1, hold.player, LivingEntity.getSlotForHand(hold.hand));
        level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 0.8F, 1.0F);
        level.sendParticles(ParticleTypes.SPLASH, pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, 16, 0.25, 0.05, 0.25, 0.0);
        return true;
    }

    private static void broadcast(ServerLevel level, BlockPos pos, ItemStack spoon) {
        PacketDistributor.sendToPlayersTrackingChunk(level, new ChunkPos(pos), new BasinStirPayload(pos, spoon.copy()));
    }
}
