package com.evandev.spicedcider.mixin.spelunkery;

import com.evandev.spicedcider.compat.cavernsandchasms.TuningForkGolems;
import com.evandev.spicedcider.registry.ModDataComponents;
import com.evandev.spicedcider.util.TuningForkNotes;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.ordana.spelunkery.items.AmethystTuningForkItem;
import com.ordana.spelunkery.reg.ModComponents;
import com.ordana.spelunkery.reg.ModGameEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@IfModLoaded("spelunkery")
@Mixin(AmethystTuningForkItem.class)
public abstract class AmethystTuningForkNoteMixin extends Item {

    private AmethystTuningForkNoteMixin(Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Integer note = TuningForkNotes.get(stack);
        if (note == null || player == null || player.isSecondaryUseActive() || !(state.getBlock() instanceof NoteBlock)) {
            return super.onItemUseFirst(stack, context);
        }
        if (!level.isClientSide) {
            BlockState tuned = state.setValue(NoteBlock.NOTE, note);
            level.setBlock(pos, tuned, 3);
            if (tuned.getValue(NoteBlock.INSTRUMENT).worksAboveNoteBlock() || level.getBlockState(pos.above()).isAir()) {
                level.blockEvent(pos, tuned.getBlock(), 0, 0);
                level.gameEvent(player, GameEvent.NOTE_BLOCK_PLAY, pos);
            }
            player.awardStat(Stats.TUNE_NOTEBLOCK);
            player.displayClientMessage(Component.translatable("item.spicedcider.tuning_fork.apply_note", TuningForkNotes.name(note)), true);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        ItemStack stack = context.getItemInHand();
        if (player == null) {
            return super.useOn(context);
        }
        if (state.getBlock() instanceof NoteBlock) {
            if (!player.isSecondaryUseActive()) {
                return super.useOn(context);
            }
            int note = state.getValue(NoteBlock.NOTE);
            Integer current = TuningForkNotes.get(stack);
            if (current == null || current != note) {
                stack.set(ModDataComponents.FORK_NOTE.get(), note);
                if (!level.isClientSide) {
                    player.displayClientMessage(Component.translatable("item.spicedcider.tuning_fork.capture_note", TuningForkNotes.name(note)), true);
                    TuningForkNotes.play(level, player, player.getX(), player.getY(), player.getZ(), note);
                    if (player instanceof ServerPlayer serverPlayer && TuningForkGolems.isLoaded()) {
                        TuningForkGolems.triggerUse(serverPlayer);
                    }
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (player.isSecondaryUseActive()) {
            return super.useOn(context);
        }
        if (stack.has(ModDataComponents.FORK_CONDUCTING.get())) {
            if (!level.isClientSide) {
                BlockPos target = state.getCollisionShape(level, pos).isEmpty() ? pos : pos.relative(context.getClickedFace());
                if (TuningForkGolems.isLoaded() && TuningForkGolems.hasControlledGolem(player)) {
                    TuningForkGolems.attract(target, player);
                    TuningForkNotes.play(level, player, target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5, spicedcider$noteOrMiddle(stack));
                    TuningForkNotes.particle(level, target.getX() + 0.5, target.getY() + 0.25, target.getZ() + 0.5, spicedcider$noteOrMiddle(stack));
                } else {
                    stack.remove(ModDataComponents.FORK_CONDUCTING.get());
                    spicedcider$ping((ServerLevel) level, player, stack);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (level.dimension() != Level.OVERWORLD) {
            return super.useOn(context);
        }
        if (level instanceof ServerLevel serverLevel) {
            spicedcider$ping(serverLevel, player, stack);
        }
        player.startUsingItem(context.getHand());
        return InteractionResult.CONSUME;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (!TuningForkGolems.isLoaded() || !TuningForkGolems.isControllable(target, player)) {
            return super.interactLivingEntity(stack, player, target, hand);
        }
        Level level = player.level();
        if (!level.isClientSide) {
            ItemStack held = player.getItemInHand(hand);
            if (TuningForkGolems.toggleControl(player, target)) {
                held.set(ModDataComponents.FORK_CONDUCTING.get(), Unit.INSTANCE);
            } else {
                held.remove(ModDataComponents.FORK_CONDUCTING.get());
            }
            int note = spicedcider$noteOrMiddle(held);
            TuningForkNotes.play(level, player, target.getX(), target.getEyeY(), target.getZ(), note);
            TuningForkNotes.particle(level, target.getX(), target.getEyeY(), target.getZ(), note);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void spicedcider$pingForGeode(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isSecondaryUseActive() || level.dimension() != Level.OVERWORLD) {
            return;
        }
        if (level instanceof ServerLevel serverLevel) {
            spicedcider$ping(serverLevel, player, stack);
        }
        player.startUsingItem(hand);
        cir.setReturnValue(InteractionResultHolder.consume(stack));
    }

    @Unique
    private static int spicedcider$noteOrMiddle(ItemStack stack) {
        Integer note = TuningForkNotes.get(stack);
        return note == null ? 12 : note;
    }

    @Unique
    private void spicedcider$ping(ServerLevel level, Player player, ItemStack stack) {
        stack.remove(ModComponents.AMETHYST_POS.get());
        level.gameEvent(player, ModGameEvents.FORK_TONE_EVENT, player.blockPosition());
        GlobalPos geode = AmethystTuningForkItem.getAmethystPos(stack);
        if (geode != null) {
            TuningForkNotes.chimeForGeode(level, player, geode.pos(), TuningForkNotes.get(stack));
        }
    }

    @Inject(method = "appendHoverText", at = @At("HEAD"))
    private void spicedcider$noteTooltip(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag, CallbackInfo ci) {
        Integer note = TuningForkNotes.get(stack);
        if (note != null) {
            tooltip.add(TuningForkNotes.name(note).withStyle(ChatFormatting.GRAY));
        }
    }
}
