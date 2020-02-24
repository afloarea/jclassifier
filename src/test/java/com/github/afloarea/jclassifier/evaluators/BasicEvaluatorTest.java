package com.github.afloarea.jclassifier.evaluators;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BasicEvaluatorTest {

    private static final int[] trueLabels =     {0, 1, 1, 0};
    private static final int[] guessedLabels =  {0, 1, 1, 1};

    @Test
    void testSimpleConfusionMatrix() {
        final int[][] confusionMatrix = BasicEvaluator.getConfusionMatrix(trueLabels, guessedLabels);
        final int[][] expectedMatrix = {
                {1, 0},
                {1, 2}
        };

        Assertions.assertArrayEquals(expectedMatrix[0], confusionMatrix[0]);
        Assertions.assertArrayEquals(expectedMatrix[1], confusionMatrix[1]);
    }

    @Test
    void testStatistics() {
        final int[][] confusionMatrix = {
                {5, 2, 0},
                {3, 3, 2},
                {0, 1, 11}
        };

        final Statistics stats = new Statistics(confusionMatrix);
        System.out.println(stats);
        Assertions.assertEquals(0.7037, stats.getAccuracy(), 0.00009);
        Assertions.assertEquals(0.6687, stats.getPrecision(), 0.00009);
        Assertions.assertEquals(0.6571, stats.getRecall(), 0.00009);
        Assertions.assertEquals(0.6584, stats.getF1Score(), 0.00009);
    }

}
