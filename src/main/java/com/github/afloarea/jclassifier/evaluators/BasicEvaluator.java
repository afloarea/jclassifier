package com.github.afloarea.jclassifier.evaluators;

import java.util.Arrays;

public class BasicEvaluator {

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
        for (int i = 0; i < trueLabels.length; i++) {
            final int trueValue = trueLabels[i];
            final int guessedValue = guessedLabels[i];
            confusion[guessedValue][trueValue]++;
        }

        return confusion;
    }

    /**
     * <p>Generate statistics based on the confusion matrix which is assumed
     * to have the first dimension represented by the guessed labels
     * and the second dimension represented by the actual labels.</p>
     * <br>
     * The calculated statistics are:
     * <ul>
     *     <li>accuracy</li>
     *     <li>precision</li>
     *     <li>recall</li>
     *     <li>F1 score</li>
     * </ul>
     *
     * @param confusionMatrix used to calculate accuracy, precision, recall and F1 score.
     * @return the calculated statistics
     */
    public static Statistics stats(int[][] confusionMatrix) {
        final Statistics stats = new Statistics();
        stats.setConfusionMatrix(confusionMatrix);

        // Accuracy
        int diagSum = 0;
        int totalSum = 0;
        for (int i = 0; i < confusionMatrix.length; i++) {
            diagSum += confusionMatrix[i][i];
            for (int j = 0; j < confusionMatrix[i].length; j++) {
                totalSum += confusionMatrix[i][j];
            }
        }
        stats.setAccuracy((double) diagSum / totalSum);

        double precisionSum = 0, recallSum = 0, f1ScoreSum = 0;
        for (int i = 0; i < confusionMatrix.length; i++) {
            final int truePositives = confusionMatrix[i][i];

            // column for false positives and row for false negatives
            int falsePositives = 0;
            int falseNegatives = 0;
            for (int j = 0; j < confusionMatrix.length; j++) {
                if (j != i) {
                    falseNegatives += confusionMatrix[j][i];
                    falsePositives += confusionMatrix[i][j];
                }
            }
            final double classPrecision = (double) truePositives / (truePositives + falsePositives);
            final double classRecall = (double) truePositives / (truePositives + falseNegatives);
            final double classF1Score = 2 / (1 / classPrecision + 1 / classRecall);

            precisionSum += classPrecision;
            recallSum += classRecall;
            f1ScoreSum += classF1Score;
        }

        stats.setPrecision(precisionSum / confusionMatrix.length);
        stats.setRecall(recallSum / confusionMatrix.length);
        stats.setF1Score(f1ScoreSum / confusionMatrix.length);

        return stats;
    }
}
