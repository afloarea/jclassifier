package com.github.afloarea.jclassifier.evaluators;

import java.util.stream.IntStream;

public class Statistics {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private int[][] confusionMatrix;
    private double accuracy;
    private double precision;
    private double recall;
    private double f1Score;

    Statistics() {}

    @Override
    public String toString() {
        return LINE_SEPARATOR + "-".repeat(26) + LINE_SEPARATOR +
                String.format("Accuracy:           %.4f", accuracy) + LINE_SEPARATOR +
                String.format("Precision (avg):    %.4f", precision) + LINE_SEPARATOR +
                String.format("Recall (avg):       %.4f", recall) + LINE_SEPARATOR +
                String.format("F1 Score (avg):     %.4f", f1Score) + LINE_SEPARATOR +
                "-".repeat(26) + LINE_SEPARATOR +
                "Confusion Matrix ( G - actual, A - actual): " + LINE_SEPARATOR + getConfusionMatrixString() + LINE_SEPARATOR;
    }

    private String getConfusionMatrixString() {
        final StringBuilder builder = new StringBuilder(String.format("%5s", "G\\A"));
        IntStream.range(0, confusionMatrix.length).forEach(value -> builder.append(String.format("%5d", value)));
        builder.append(LINE_SEPARATOR);
        for (int i = 0; i < confusionMatrix.length; i++) {
            builder.append(String.format("%5d", i));
            IntStream.of(confusionMatrix[i]).forEach(value -> builder.append(String.format("%5d", value)));
            builder.append(LINE_SEPARATOR);
        }

        return builder.toString();
    }

    public int[][] getConfusionMatrix() {
        return confusionMatrix;
    }

    public void setConfusionMatrix(int[][] confusionMatrix) {
        this.confusionMatrix = confusionMatrix;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public double getPrecision() {
        return precision;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }

    public double getRecall() {
        return recall;
    }

    public void setRecall(double recall) {
        this.recall = recall;
    }

    public double getF1Score() {
        return f1Score;
    }

    public void setF1Score(double f1Score) {
        this.f1Score = f1Score;
    }
}
