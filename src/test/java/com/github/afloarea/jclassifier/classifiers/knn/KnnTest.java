package com.github.afloarea.jclassifier.classifiers.knn;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class KnnTest {
    private static final double[][] trainFeatures = {
            { 1,  2},
            {-1, -2}
    };
    private static final int[] trainLabels = {1, 2};

    @Test
    public void testSimpleClassification() {
        final var label = Knn.of(trainFeatures, trainLabels, 1).classify(new double[] {2, 4});
        Assertions.assertEquals(1, label);
    }
}
