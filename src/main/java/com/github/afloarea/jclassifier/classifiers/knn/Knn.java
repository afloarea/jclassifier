package com.github.afloarea.jclassifier.classifiers.knn;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class Knn {

    private final double[][] trainFeatures;
    private final int[] trainLabels;
    private final int kNumberOfNeighbours;

    private Knn(double[][] trainFeatures, int[] trainLabels, int kNumberOfNeighbours) {
        this.trainFeatures = trainFeatures;
        this.trainLabels = trainLabels;
        this.kNumberOfNeighbours = kNumberOfNeighbours;
    }

    public static Knn of(double[][] trainFeatures, int[] trainLabels, int kNumberOfNeighbours) {
        return new Knn(trainFeatures, trainLabels, kNumberOfNeighbours);
    }

    public static Knn of(double[][] trainFeaturesWithLastColumnLabels, int kNumberOfNeighbours) {
        final int[] trainLabels = new int[trainFeaturesWithLastColumnLabels.length];
        final double[][] trainFeatures = new double[trainFeaturesWithLastColumnLabels.length][];

        final int labelIndex = trainFeaturesWithLastColumnLabels[0].length - 1;

        for (int i = 0; i < trainFeaturesWithLastColumnLabels.length; i++) {
            trainFeatures[i] = Arrays.copyOf(trainFeaturesWithLastColumnLabels[i], labelIndex);
            trainLabels[i] = (int) trainFeaturesWithLastColumnLabels[i][labelIndex];
        }

        return new Knn(trainFeatures, trainLabels, kNumberOfNeighbours);
    }

    private static double calculateEuclidianDistance(double[] point1, double[] point2) {
        if (point1.length != point2.length) throw new IllegalArgumentException("Points have different length");
        final double sum = IntStream
                .range(0, point1.length)
                .mapToDouble(index -> Math.pow(point1[index] - point2[index], 2))
                .sum();
        return Math.sqrt(sum);
    }

    public int classify(double[] features) {
        final double[] distances = Arrays
                .stream(trainFeatures)
                .mapToDouble(trainFeature -> calculateEuclidianDistance(features, trainFeature))
                .toArray();
        final Neighbour[] neighbours = Neighbour.createGroupFrom(distances, trainLabels);
        Arrays.sort(neighbours);

        final Map<Integer, Long> groupedNeighbours = Stream.of(neighbours)
                .limit(kNumberOfNeighbours)
                .collect(Collectors.groupingBy(Neighbour::getLabel, Collectors.counting()));

        return groupedNeighbours.entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey();
    }

    public int[] classify(double[][] featuresSet) {
        return Stream.of(featuresSet).mapToInt(this::classify).toArray();
    }

}
