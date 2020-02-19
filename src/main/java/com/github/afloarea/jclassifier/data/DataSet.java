package com.github.afloarea.jclassifier.data;

import java.util.Arrays;
import java.util.Random;

public final class DataSet {
    private double[][] features;
    private int[] labels;

    public DataSet() {
    }

    public DataSet(double[][] features, int[] labels) {
        this.features = features;
        this.labels = labels;
    }

    public static DataSet[] splitInTwo(DataSet original, double firstHalfPercentage) {
        final var firstHalfTotal = (int) (original.size() * firstHalfPercentage);

        final double[][] featuresFirstHalf = Arrays.copyOfRange(original.features, 0, firstHalfTotal);
        final double[][] featuresSecondHalf = Arrays.copyOfRange(original.features, firstHalfTotal, original.size());

        final int[] labelsFirstHalf = Arrays.copyOfRange(original.labels, 0, firstHalfTotal);
        final int[] labelsSecondHalf = Arrays.copyOfRange(original.labels, firstHalfTotal, original.size());

        final DataSet[] result = new DataSet[2];
        result[0] = new DataSet(featuresFirstHalf, labelsFirstHalf);
        result[1] = new DataSet(featuresSecondHalf, labelsSecondHalf);
        return result;
    }

    public static DataSet concatenate(DataSet... dataSets) {
        final int totalSize = Arrays.stream(dataSets).mapToInt(DataSet::size).sum();
        final double[][] allFeatures = new double[totalSize][];
        final int[] allLabels = new int[totalSize];

        int destinationPosition = 0;
        for (DataSet dataSet : dataSets) {
            System.arraycopy(dataSet.features, 0, allFeatures, destinationPosition, dataSet.size());
            System.arraycopy(dataSet.labels, 0, allLabels, destinationPosition, dataSet.size());
            destinationPosition += dataSet.size();
        }

        return new DataSet(allFeatures, allLabels);
    }

    public static DataSet shuffleDataSet(DataSet dataSet, Random random) {
        for (int i = dataSet.size() - 1; i > 0; i--) {
            final int index = random.nextInt(i + 1);
            dataSet.swap(i, index);
        }
        return dataSet;
    }

    private void swap(int firstIndex, int secondIndex) {
        final double[] firstIndexFeatures = features[firstIndex];
        final int firstIndexLabel = labels[firstIndex];

        features[firstIndex] = features[secondIndex];
        labels[firstIndex] = labels[secondIndex];

        features[secondIndex] = firstIndexFeatures;
        labels[secondIndex] = firstIndexLabel;
    }

    public int size() {
        return features.length;
    }

    public double[][] getFeatures() {
        return features;
    }

    public void setFeatures(double[][] features) {
        this.features = features;
    }

    public int[] getLabels() {
        return labels;
    }

    public void setLabels(int[] labels) {
        this.labels = labels;
    }
}
