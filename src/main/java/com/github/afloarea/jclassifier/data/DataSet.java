package com.github.afloarea.jclassifier.data;

import java.util.Arrays;
import java.util.Random;

public final class DataSet {
    private final double[][] features;
    private final int[] labels;

    public DataSet(double[][] features, int[] labels) {
        this.features = features;
        this.labels = labels;
    }

    public static AggregatedData splitInTwo(DataSet original, double firstPartPercentage) {
        final var firstPartTotal = (int) (original.size() * firstPartPercentage);

        final double[][] featuresFirstPart = Arrays.copyOfRange(original.features, 0, firstPartTotal);
        final double[][] featuresSecondPart = Arrays.copyOfRange(original.features, firstPartTotal, original.size());

        final int[] labelsFirstPart = Arrays.copyOfRange(original.labels, 0, firstPartTotal);
        final int[] labelsSecondPart = Arrays.copyOfRange(original.labels, firstPartTotal, original.size());

        return new AggregatedData(new DataSet(featuresFirstPart, labelsFirstPart), new DataSet(featuresSecondPart, labelsSecondPart));
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

    public int[] getLabels() {
        return labels;
    }
}
