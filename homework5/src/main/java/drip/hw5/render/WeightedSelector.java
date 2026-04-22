package drip.hw5.render;

import java.util.List;
import java.util.random.RandomGenerator;

final class WeightedSelector<T> {
    private final List<T> values;
    private final double[] cumulativeWeights;
    private final double totalWeight;

    WeightedSelector(List<T> values, List<Double> weights) {
        if (values.isEmpty() || values.size() != weights.size()) {
            throw new IllegalArgumentException("Values and weights must be non-empty and have equal size.");
        }

        this.values = List.copyOf(values);
        this.cumulativeWeights = new double[weights.size()];

        double sum = 0.0;
        for (int index = 0; index < weights.size(); index++) {
            sum += weights.get(index);
            cumulativeWeights[index] = sum;
        }
        this.totalWeight = sum;
    }

    T select(RandomGenerator random) {
        double value = random.nextDouble() * totalWeight;
        for (int index = 0; index < cumulativeWeights.length; index++) {
            if (value < cumulativeWeights[index]) {
                return values.get(index);
            }
        }
        return values.get(values.size() - 1);
    }
}
