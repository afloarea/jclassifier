package com.github.afloarea.jclassifier.evaluators;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Statistics comprised of:
 * <ul>
 *     <li>
 *         confusion matrix:
 *         <ul>
 *             <li>first index represents the guessed labels</li>
 *             <li>second index represents the actual labels</li>
 *         </ul>
 *     </li>
 *     <li>accuracy</li>
 *     <li>precision</li>
 *     <li>recall</li>
 *     <li>F1 score</li>
 * </ul>
 */
public final class Statistics {

    private final int[][] confusionMatrix;
    private final double accuracy;
    private final double precision;
    private final double recall;
    private final double f1Score;

    Statistics(int[][] confusionMatrix) {
        this.confusionMatrix = confusionMatrix;

        final CoreStats stats = CoreStats.fromConfusionMatrix(confusionMatrix);
        this.accuracy = stats.accuracy;
        this.precision = stats.precision;
        this.recall = stats.recall;
        this.f1Score = stats.f1Score;
    }

    @Override
    public String toString() {
        return String.format("%n--------------------------%n" +
                "%-15s: %8.4f %n" +
                "%-15s: %8.4f %n" +
                "%-15s: %8.4f %n" +
                "%-15s: %8.4f %n" +
                "--------------------------%n" +
                "Confusion Matrix ( G - guessed, A - actual ):%n" +
                "%s%n",
                "Accuracy", accuracy,
                "Precision (avg)", precision,
                "Recall (avg)", recall,
                "F1 Score (avg)", f1Score,
                getConfusionMatrixString());
    }

    private String getConfusionMatrixString() {
        String template = ("%5d".repeat(confusionMatrix.length + 1) + "%n").repeat(confusionMatrix.length);
        template = "%5s" + template.substring(3);

        Stream<Object> stream = Stream.concat(Stream.of("G\\A"), IntStream.range(0, confusionMatrix.length).boxed());
        for (int index = 0; index < confusionMatrix.length; index++) {
            stream = Stream.concat(stream, Stream.concat(Stream.of(index), Arrays.stream(confusionMatrix[index]).boxed()));
        }

        return String.format(template, stream.toArray());
    }

    /**
     * The first index of the matrix corresponds to the guessed labels while the second index corresponds to the actual labels.
     * @return the confusion matrix
     */
    public int[][] getConfusionMatrix() {
        return confusionMatrix;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public double getPrecision() {
        return precision;
    }

    public double getRecall() {
        return recall;
    }

    public double getF1Score() {
        return f1Score;
    }

    private static final class CoreStats {
        private double accuracy;
        private double precision;
        private double recall;
        private double f1Score;

        private static CoreStats fromConfusionMatrix(int[][] confusionMatrix) {
            int     diagSum         = 0;
            int     totalSum        = 0;
            double  precisionSum    = 0;
            double  recallSum       = 0;
            double  f1ScoreSum      = 0;

            for (int firstIndex = 0; firstIndex < confusionMatrix.length; firstIndex++) {

                final int truePositives = confusionMatrix[firstIndex][firstIndex];
                diagSum += truePositives;

                int falsePositives = 0;
                int falseNegatives = 0;

                for (int secondIndex = 0; secondIndex < confusionMatrix.length; secondIndex++) {

                    totalSum += confusionMatrix[firstIndex][secondIndex];
                    if (secondIndex != firstIndex) {
                        falseNegatives += confusionMatrix[secondIndex][firstIndex];
                        falsePositives += confusionMatrix[firstIndex][secondIndex];
                    }

                }

                final double classPrecision = (double) truePositives / (truePositives + falsePositives);
                final double classRecall = (double) truePositives / (truePositives + falseNegatives);

                precisionSum += classPrecision;
                recallSum += classRecall;
                f1ScoreSum += 2 / (1 / classPrecision + 1 / classRecall);
            }

            final CoreStats stats = new CoreStats();
            stats.accuracy = (double) diagSum / totalSum;
            stats.precision = precisionSum / confusionMatrix.length;
            stats.recall = recallSum / confusionMatrix.length;
            stats.f1Score = f1ScoreSum / confusionMatrix.length;
            return stats;
        }
    }
}
