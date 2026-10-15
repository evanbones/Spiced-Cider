package com.evandev.spicedcider.content.worm;

import com.evandev.spicedcider.registry.ModAttachments;
import com.evandev.spicedcider.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class WormHuntGoal extends Goal {
    private static final int RANGE = 48;
    private static final int HEIGHT = 16;
    private static final int NEAR_RANGE = 24;

    private final Animal seeker;
    private int runDelay;
    private int lookTimer;
    private Vec3 lookVector = new Vec3(1.0D, 0.0D, 1.0D);

    public WormHuntGoal(Animal seeker) {
        this.seeker = seeker;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    private WormHunt hunt() {
        return this.seeker.getData(ModAttachments.WORM_HUNT);
    }

    @Override
    public boolean canUse() {
        if (!this.seeker.hasData(ModAttachments.WORM_HUNT)) return false;
        if (this.runDelay > 0) {
            --this.runDelay;
            return false;
        }
        this.runDelay = this.adjustedTickDelay(20);
        WormHunt hunt = this.hunt();
        if (hunt.target != null) return true;
        return hunt.time > 0 && this.findWorm(hunt);
    }

    @Override
    public boolean canContinueToUse() {
        WormHunt hunt = this.hunt();
        return hunt.target != null && hunt.time != 0;
    }

    @Override
    public void start() {
        this.lookVector = new Vec3(1.0D, 0.0D, 1.0D);
        this.moveToWorm();
        this.hunt().looking = true;
    }

    @Override
    public void stop() {
        this.hunt().looking = false;
    }

    @Override
    public void tick() {
        WormHunt hunt = this.hunt();
        if (hunt.target == null) return;
        BlockPos target = hunt.target;
        Vec3 pos = this.seeker.position();
        Vec3 direction = new Vec3(target.getX() + 0.5D - pos.x(), 0.0D, target.getZ() + 0.5D - pos.z()).normalize();

        this.seeker.getLookControl().setLookAt(
                pos.x() + direction.x() * this.lookVector.x(),
                this.seeker.getY() - 0.6D + this.lookVector.y(),
                pos.z() + direction.z() * this.lookVector.z(),
                this.seeker.getMaxHeadYRot() + 20, this.seeker.getMaxHeadXRot());

        if (target.closerThan(this.seeker.blockPosition(), 4.0D)) {
            if (hunt.time > 0) hunt.time = -800;
        } else {
            if (this.lookTimer-- <= 0) {
                this.lookTimer = this.adjustedTickDelay(18 + this.seeker.getRandom().nextInt(9));
                this.lookVector = new Vec3(this.seeker.getRandom().nextFloat() * 1.2D, this.seeker.getRandom().nextFloat() * 0.4D, this.seeker.getRandom().nextFloat() * 1.2D);
            }
            this.moveToWorm();
        }
    }

    private void moveToWorm() {
        BlockPos target = this.hunt().target;
        if (target == null) return;
        this.seeker.getNavigation().moveTo(target.getX() + 0.5D, target.getY() + 1, target.getZ() + 0.5D, 1.1D);
    }

    private boolean findWorm(WormHunt hunt) {
        Level level = this.seeker.level();
        if (!level.dimensionType().natural()) return false;

        BlockPos origin = this.seeker.blockPosition();
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        List<BlockPos> candidates = new ArrayList<>();
        boolean nearFound = false;

        for (int i = 0; i < RANGE; ++i) {
            for (int y = -HEIGHT; y < HEIGHT; ++y) {
                for (int x = 0; x <= i; x = x > 0 ? -x : 1 - x) {
                    for (int z = x < i && x > -i ? i : 0; z <= i; z = z > 0 ? -z : 1 - z) {
                        cursor.setWithOffset(origin, x, y - 1, z);
                        if (!level.isLoaded(cursor) || !this.seeker.isWithinRestriction(cursor)) continue;
                        BlockState state = level.getBlockState(cursor);
                        if (state.is(ModBlocks.WORMY_DIRT.get())) {
                            hunt.target = cursor.immutable();
                            return true;
                        }
                        if (state.is(Blocks.DIRT) && isSuitable(level, cursor)) {
                            if (i <= NEAR_RANGE && !nearFound) {
                                nearFound = true;
                                candidates.clear();
                            }
                            candidates.add(cursor.immutable());
                        }
                    }
                }
            }
        }

        if (candidates.isEmpty()) return false;
        BlockPos chosen = candidates.get(this.seeker.getRandom().nextInt(candidates.size()));
        level.setBlock(chosen, ModBlocks.WORMY_DIRT.get().defaultBlockState(), 3);
        hunt.target = chosen;
        return true;
    }

    private static boolean isSuitable(Level level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            BlockPos neighbour = pos.relative(direction);
            if (!level.isLoaded(neighbour)) return false;
            BlockState state = level.getBlockState(neighbour);
            if (direction == Direction.UP) {
                if (!(state.getBlock() instanceof SnowyDirtBlock)) return false;
            } else if (!state.is(BlockTags.DIRT)) {
                return false;
            }
        }
        return true;
    }
}
