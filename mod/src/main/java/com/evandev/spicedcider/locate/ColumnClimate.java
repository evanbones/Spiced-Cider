package com.evandev.spicedcider.locate;

import com.evandev.spicedcider.SpicedCider;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.DensityFunction;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public final class ColumnClimate {
    private static final int PROBE_COLUMNS = 48;
    private static final int PROBE_RANGE = 30000;
    private static final int PROBE_Y_STEP = 16;
    private static final long PROBE_SEED = 0x5ca1ab1eL;
    private static final List<Entry> SHAPES = new ArrayList<>();

    private final Climate.Sampler sampler;
    private final Shape shape;

    private ColumnClimate(Climate.Sampler sampler, Shape shape) {
        this.sampler = sampler;
        this.shape = shape;
    }

    public static ColumnClimate of(Climate.Sampler sampler, LevelHeightAccessor level) {
        return new ColumnClimate(sampler, shapeOf(sampler, level));
    }

    public Climate.Sampler newSampler() {
        return new Climate.Sampler(
                this.shape.flat[0] ? new ColumnCached(this.sampler.temperature()) : this.sampler.temperature(),
                this.shape.flat[1] ? new ColumnCached(this.sampler.humidity()) : this.sampler.humidity(),
                this.shape.flat[2] ? new ColumnCached(this.sampler.continentalness()) : this.sampler.continentalness(),
                this.shape.flat[3] ? new ColumnCached(this.sampler.erosion()) : this.sampler.erosion(),
                this.shape.separableDepth ? new SeparableDepth(this.sampler.depth()) : this.sampler.depth(),
                this.shape.flat[4] ? new ColumnCached(this.sampler.weirdness()) : this.sampler.weirdness(),
                this.sampler.spawnTarget());
    }

    private static synchronized Shape shapeOf(Climate.Sampler sampler, LevelHeightAccessor level) {
        SHAPES.removeIf(entry -> entry.sampler.get() == null);
        for (Entry entry : SHAPES) {
            if (entry.sampler.get() == sampler && entry.minY == level.getMinBuildHeight() && entry.maxY == level.getMaxBuildHeight()) return entry.shape;
        }
        Shape shape = probe(sampler, level.getMinBuildHeight(), level.getMaxBuildHeight());
        SHAPES.add(new Entry(new WeakReference<>(sampler), level.getMinBuildHeight(), level.getMaxBuildHeight(), shape));
        SpicedCider.LOGGER.info("Locate climate shape: height-independent [temperature={}, humidity={}, continentalness={}, erosion={}, weirdness={}], separable depth={}",
                shape.flat[0], shape.flat[1], shape.flat[2], shape.flat[3], shape.flat[4], shape.separableDepth);
        return shape;
    }

    private static Shape probe(Climate.Sampler sampler, int minY, int maxY) {
        DensityFunction[] functions = {sampler.temperature(), sampler.humidity(), sampler.continentalness(), sampler.erosion(), sampler.weirdness()};
        boolean[] flat = {true, true, true, true, true};
        boolean separable = true;
        int heights = (maxY - minY) / PROBE_Y_STEP + 1;
        RandomSource random = RandomSource.create(PROBE_SEED);
        int[] ys = new int[heights];
        for (int i = 0; i < heights; i++) ys[i] = Math.min(maxY, minY + i * PROBE_Y_STEP + random.nextInt(PROBE_Y_STEP));
        int refX = random.nextInt(2 * PROBE_RANGE) - PROBE_RANGE;
        int refZ = random.nextInt(2 * PROBE_RANGE) - PROBE_RANGE;
        double[] reference = new double[heights];
        for (int i = 0; i < heights; i++) reference[i] = sampler.depth().compute(new DensityFunction.SinglePointContext(refX, ys[i], refZ));
        for (int column = 0; column < PROBE_COLUMNS; column++) {
            int x = random.nextInt(2 * PROBE_RANGE) - PROBE_RANGE;
            int z = random.nextInt(2 * PROBE_RANGE) - PROBE_RANGE;
            for (int f = 0; f < functions.length; f++) {
                if (!flat[f]) continue;
                double first = functions[f].compute(new DensityFunction.SinglePointContext(x, ys[0], z));
                for (int i = 1; i < heights && flat[f]; i++) {
                    flat[f] = functions[f].compute(new DensityFunction.SinglePointContext(x, ys[i], z)) == first;
                }
            }
            if (!separable) continue;
            int start = column % heights;
            double offset = sampler.depth().compute(new DensityFunction.SinglePointContext(x, ys[start], z)) - reference[start];
            for (int i = 0; i < heights && separable; i++) {
                double actual = sampler.depth().compute(new DensityFunction.SinglePointContext(x, ys[i], z));
                separable = (float) (offset + reference[i]) == (float) actual;
            }
        }
        return new Shape(flat, separable);
    }

    private record Entry(WeakReference<Climate.Sampler> sampler, int minY, int maxY, Shape shape) {
    }

    private record Shape(boolean[] flat, boolean separableDepth) {
    }

    private abstract static class Wrapped implements DensityFunction {
        protected final DensityFunction base;

        Wrapped(DensityFunction base) {
            this.base = base;
        }

        @Override
        public void fillArray(double[] array, ContextProvider contextProvider) {
            contextProvider.fillAllDirectly(array, this);
        }

        @Override
        public DensityFunction mapAll(Visitor visitor) {
            return visitor.apply(this);
        }

        @Override
        public double minValue() {
            return this.base.minValue();
        }

        @Override
        public double maxValue() {
            return this.base.maxValue();
        }

        @Override
        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return this.base.codec();
        }
    }

    private static final class ColumnCached extends Wrapped {
        private boolean filled;
        private int x;
        private int z;
        private double value;

        ColumnCached(DensityFunction base) {
            super(base);
        }

        @Override
        public double compute(FunctionContext context) {
            int x = context.blockX();
            int z = context.blockZ();
            if (this.filled && this.x == x && this.z == z) return this.value;
            this.value = this.base.compute(context);
            this.x = x;
            this.z = z;
            this.filled = true;
            return this.value;
        }
    }

    private static final class SeparableDepth extends Wrapped {
        private final Int2DoubleOpenHashMap reference = new Int2DoubleOpenHashMap();
        private boolean filled;
        private int refX;
        private int refZ;
        private int x;
        private int z;
        private double offset;

        SeparableDepth(DensityFunction base) {
            super(base);
        }

        @Override
        public double compute(FunctionContext context) {
            int x = context.blockX();
            int y = context.blockY();
            int z = context.blockZ();
            if (this.filled && this.x == x && this.z == z) return this.offset + this.reference(y);
            double value = this.base.compute(context);
            if (!this.filled) {
                this.refX = x;
                this.refZ = z;
                this.reference.put(y, value);
            }
            this.offset = value - this.reference(y);
            this.x = x;
            this.z = z;
            this.filled = true;
            return value;
        }

        private double reference(int y) {
            if (this.reference.containsKey(y)) return this.reference.get(y);
            double value = this.base.compute(new SinglePointContext(this.refX, y, this.refZ));
            this.reference.put(y, value);
            return value;
        }
    }
}
