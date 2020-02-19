package com.github.afloarea.jclassifier.evaluators;

import java.util.Arrays;

public final class BasicEvaluator {

    private BasicEvaluator() {}

    /**
     * Computes the confusion matrix. The first index represents the guessed labels
     * and the second index represents the actual labels.
     * @param trueLabels the actual labels
     * @param guessedLabels the predicted labels
     * @return the confusion matrix as an {@code int[][]}
     */
    public static int[][] getConfusionMatrix(int[] trueLabels, int[] guessedLabels) {
        final int classSize = Arrays.stream(trueLabels).max().getAsInt() + 1;

        final int[][] confusion = new int[classSize][classSize];
        for (int index = 0; index < trueLabels.length; index++) {
            final int trueValue = trueLabels[index];
            final int guessedValue = guessedLabels[index];
            confusion[guessedValue][trueValue]++;
        }

        return confusion;
    }

    /**
     * Generate statistics based on the true labels and guessed labels.
     * @param trueLabels the true labels.
     * @param guessedLabels the guessed labels
     * @return the corresponding statistics
     */
    public static Statistics evaluate(int[] trueLabels, int[] guessedLabels) {
        return new Statistics(getConfusionMatrix(trueLabels, guessedLabels));
    }
}
