package drip.hw5.render;

import drip.hw5.config.RenderConfig;
import drip.hw5.config.VariationConfig;
import drip.hw5.logging.AppLogger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

public final class FractalFlameRenderer {
    private static final int WARMUP_ITERATIONS = 20;
    private static final int PROGRESS_CHUNKS = 10;
    private static final double BOUNDS_PADDING_RATIO = 0.05;
    private static final int BOUNDS_SAMPLE_LIMIT = 20_000;
    private static final double BOUNDS_LOWER_PERCENTILE = 0.01;
    private static final double BOUNDS_UPPER_PERCENTILE = 0.99;
    private static final double MAX_ABSOLUTE_COORDINATE = 1_000.0;

    private final AppLogger logger;

    public FractalFlameRenderer(AppLogger logger) {
        this.logger = logger;
    }

    public FlameImageBuffer render(RenderConfig config) {
        List<PreparedAffineTransform> transforms = prepareTransforms(config);
        WeightedSelector<VariationConfig> variationSelector = prepareVariationSelector(config);

        logger.info("Estimating render bounds.");
        RenderBounds bounds = estimateBounds(config, transforms, variationSelector).withPadding(BOUNDS_PADDING_RATIO);
        logger.info("Bounds estimated: minX=%.4f maxX=%.4f minY=%.4f maxY=%.4f"
            .formatted(bounds.minX(), bounds.maxX(), bounds.minY(), bounds.maxY()));

        logger.info(config.threads() == 1
            ? "Rendering in single-thread mode."
            : "Rendering in multi-thread mode with %d threads.".formatted(config.threads()));

        return config.threads() == 1
            ? renderSingleThread(config, transforms, variationSelector, bounds)
            : renderMultiThread(config, transforms, variationSelector, bounds);
    }

    private FlameImageBuffer renderSingleThread(
        RenderConfig config,
        List<PreparedAffineTransform> transforms,
        WeightedSelector<VariationConfig> variationSelector,
        RenderBounds bounds
    ) {
        FlameImageBuffer imageBuffer = new FlameImageBuffer(config.size().width(), config.size().height());
        int iterationsDone = 0;
        for (int chunkIndex = 0; chunkIndex < PROGRESS_CHUNKS; chunkIndex++) {
            int chunkIterations = chunkIterations(config.iterationCount(), chunkIndex);
            FlameImageBuffer chunkBuffer = renderIterations(
                config,
                transforms,
                variationSelector,
                bounds,
                chunkIterations,
                config.seed() + chunkIndex
            );
            imageBuffer.merge(chunkBuffer);
            iterationsDone += chunkIterations;
            logProgress(iterationsDone, config.iterationCount());
        }
        return imageBuffer;
    }

    private FlameImageBuffer renderMultiThread(
        RenderConfig config,
        List<PreparedAffineTransform> transforms,
        WeightedSelector<VariationConfig> variationSelector,
        RenderBounds bounds
    ) {
        FlameImageBuffer imageBuffer = new FlameImageBuffer(config.size().width(), config.size().height());
        try (ExecutorService executor = Executors.newFixedThreadPool(config.threads())) {
            int iterationsDone = 0;
            for (int chunkIndex = 0; chunkIndex < PROGRESS_CHUNKS; chunkIndex++) {
                int chunkIterations = chunkIterations(config.iterationCount(), chunkIndex);
                List<Future<FlameImageBuffer>> futures = submitChunkTasks(
                    executor,
                    config,
                    transforms,
                    variationSelector,
                    bounds,
                    chunkIterations,
                    chunkIndex
                );

                for (Future<FlameImageBuffer> future : futures) {
                    imageBuffer.merge(waitForBuffer(future));
                }

                iterationsDone += chunkIterations;
                logProgress(iterationsDone, config.iterationCount());
            }
        }
        return imageBuffer;
    }

    private List<Future<FlameImageBuffer>> submitChunkTasks(
        ExecutorService executor,
        RenderConfig config,
        List<PreparedAffineTransform> transforms,
        WeightedSelector<VariationConfig> variationSelector,
        RenderBounds bounds,
        int totalChunkIterations,
        int chunkIndex
    ) {
        List<Callable<FlameImageBuffer>> tasks = new ArrayList<>();
        int workerCount = Math.min(config.threads(), Math.max(1, totalChunkIterations));
        int baseIterations = totalChunkIterations / workerCount;
        int remainder = totalChunkIterations % workerCount;

        for (int workerIndex = 0; workerIndex < workerCount; workerIndex++) {
            int iterations = baseIterations + (workerIndex < remainder ? 1 : 0);
            if (iterations == 0) {
                continue;
            }

            long workerSeed = config.seed() + (long) chunkIndex * 100_003L + workerIndex * 7_919L;
            tasks.add(() -> renderIterations(config, transforms, variationSelector, bounds, iterations, workerSeed));
        }

        List<Future<FlameImageBuffer>> futures = new ArrayList<>();
        for (Callable<FlameImageBuffer> task : tasks) {
            futures.add(executor.submit(task));
        }
        return futures;
    }

    private FlameImageBuffer waitForBuffer(Future<FlameImageBuffer> future) {
        try {
            return future.get();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Rendering was interrupted.", exception);
        } catch (ExecutionException exception) {
            throw new IllegalStateException("Rendering task failed.", exception.getCause());
        }
    }

    private FlameImageBuffer renderIterations(
        RenderConfig config,
        List<PreparedAffineTransform> transforms,
        WeightedSelector<VariationConfig> variationSelector,
        RenderBounds bounds,
        int iterationCount,
        long seed
    ) {
        FlameImageBuffer imageBuffer = new FlameImageBuffer(config.size().width(), config.size().height());
        RandomGenerator random = RandomGeneratorFactory.of("L64X128MixRandom").create(seed);
        Point2D point = randomPoint(random);
        RgbColor currentColor = RgbColor.black();

        for (int iteration = 0; iteration < iterationCount + WARMUP_ITERATIONS; iteration++) {
            PreparedAffineTransform transform = transforms.get(random.nextInt(transforms.size()));
            point = transform.apply(point);
            VariationConfig variation = variationSelector.select(random);
            point = VariationApplier.apply(variation.name(), point);

            if (!isUsablePoint(point)) {
                point = randomPoint(random);
                currentColor = RgbColor.black();
                continue;
            }

            currentColor = currentColor.blend(transform.color());

            if (iteration < WARMUP_ITERATIONS) {
                continue;
            }
            plotSymmetry(imageBuffer, point, currentColor, bounds, config.symmetryLevel());
        }

        return imageBuffer;
    }

    private RenderBounds estimateBounds(
        RenderConfig config,
        List<PreparedAffineTransform> transforms,
        WeightedSelector<VariationConfig> variationSelector
    ) {
        int iterations = Math.max(4_000, Math.min(config.iterationCount() * 2, 100_000));
        RandomGenerator random = RandomGeneratorFactory.of("L64X128MixRandom").create(config.seed());
        Point2D point = randomPoint(random);
        List<Double> xValues = new ArrayList<>();
        List<Double> yValues = new ArrayList<>();

        for (int iteration = 0; iteration < iterations + WARMUP_ITERATIONS && xValues.size() < BOUNDS_SAMPLE_LIMIT; iteration++) {
            PreparedAffineTransform transform = transforms.get(random.nextInt(transforms.size()));
            point = transform.apply(point);
            VariationConfig variation = variationSelector.select(random);
            point = VariationApplier.apply(variation.name(), point);

            if (!isUsablePoint(point)) {
                point = randomPoint(random);
                continue;
            }

            if (iteration < WARMUP_ITERATIONS) {
                continue;
            }

            for (int symmetryIndex = 0; symmetryIndex < config.symmetryLevel(); symmetryIndex++) {
                Point2D rotated = rotate(point, symmetryIndex, config.symmetryLevel());
                if (!isUsablePoint(rotated)) {
                    continue;
                }
                xValues.add(rotated.x());
                yValues.add(rotated.y());
            }
        }

        if (xValues.isEmpty() || yValues.isEmpty()) {
            return new RenderBounds(-1.0, 1.0, -1.0, 1.0);
        }

        Collections.sort(xValues);
        Collections.sort(yValues);
        double minX = percentile(xValues, BOUNDS_LOWER_PERCENTILE);
        double maxX = percentile(xValues, BOUNDS_UPPER_PERCENTILE);
        double minY = percentile(yValues, BOUNDS_LOWER_PERCENTILE);
        double maxY = percentile(yValues, BOUNDS_UPPER_PERCENTILE);

        if (minX == maxX) {
            minX -= 1.0;
            maxX += 1.0;
        }
        if (minY == maxY) {
            minY -= 1.0;
            maxY += 1.0;
        }

        return new RenderBounds(minX, maxX, minY, maxY);
    }

    private void plotSymmetry(
        FlameImageBuffer imageBuffer,
        Point2D point,
        RgbColor color,
        RenderBounds bounds,
        int symmetryLevel
    ) {
        for (int symmetryIndex = 0; symmetryIndex < symmetryLevel; symmetryIndex++) {
            Point2D rotated = rotate(point, symmetryIndex, symmetryLevel);
            int x = toPixelX(rotated.x(), bounds, imageBuffer.width());
            int y = toPixelY(rotated.y(), bounds, imageBuffer.height());
            imageBuffer.addPoint(x, y, color);
        }
    }

    private Point2D rotate(Point2D point, int step, int symmetryLevel) {
        if (symmetryLevel == 1) {
            return point;
        }
        double angle = (2.0 * Math.PI * step) / symmetryLevel;
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new Point2D(
            point.x() * cos - point.y() * sin,
            point.x() * sin + point.y() * cos
        );
    }

    private int toPixelX(double x, RenderBounds bounds, int width) {
        double normalized = (x - bounds.minX()) / bounds.width();
        return (int) Math.floor(normalized * (width - 1));
    }

    private int toPixelY(double y, RenderBounds bounds, int height) {
        double normalized = (y - bounds.minY()) / bounds.height();
        return height - 1 - (int) Math.floor(normalized * (height - 1));
    }

    private List<PreparedAffineTransform> prepareTransforms(RenderConfig config) {
        List<PreparedAffineTransform> transforms = new ArrayList<>();
        for (int index = 0; index < config.affineParams().size(); index++) {
            transforms.add(new PreparedAffineTransform(
                config.affineParams().get(index),
                RgbColor.fromHue(index, config.affineParams().size())
            ));
        }
        return List.copyOf(transforms);
    }

    private WeightedSelector<VariationConfig> prepareVariationSelector(RenderConfig config) {
        List<Double> weights = config.functions().stream()
            .map(VariationConfig::weight)
            .toList();
        return new WeightedSelector<>(config.functions(), weights);
    }

    private Point2D randomPoint(RandomGenerator random) {
        return new Point2D(
            random.nextDouble(-1.0, 1.0),
            random.nextDouble(-1.0, 1.0)
        );
    }

    private int chunkIterations(int totalIterations, int chunkIndex) {
        int baseIterations = totalIterations / PROGRESS_CHUNKS;
        int remainder = totalIterations % PROGRESS_CHUNKS;
        return baseIterations + (chunkIndex < remainder ? 1 : 0);
    }

    private void logProgress(int done, int total) {
        int percent = (int) Math.round(done * 100.0 / total);
        logger.info("Render progress: %d%%".formatted(percent));
    }

    private boolean isUsablePoint(Point2D point) {
        return Double.isFinite(point.x())
            && Double.isFinite(point.y())
            && Math.abs(point.x()) <= MAX_ABSOLUTE_COORDINATE
            && Math.abs(point.y()) <= MAX_ABSOLUTE_COORDINATE;
    }

    private double percentile(List<Double> values, double percentile) {
        int index = (int) Math.floor((values.size() - 1) * percentile);
        return values.get(Math.max(0, Math.min(values.size() - 1, index)));
    }
}
