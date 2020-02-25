package com.github.afloarea.jclassifier.data.aggregation;

import com.github.afloarea.jclassifier.data.AggregatedData;
import com.github.afloarea.jclassifier.data.DataSet;
import com.github.afloarea.jclassifier.extractors.FeatureExtractor;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class DataAggregator {

    private final String rootDataFolderPath;
    private final FeatureExtractor featureExtractor;
    private final Random random;
    private final double trainPercentage;
    private final boolean normalized;

    private DataAggregator(String rootDataFolderPath,
                           FeatureExtractor featureExtractor,
                           Random random,
                           double trainPercentage,
                           boolean normalized) {
        this.rootDataFolderPath = rootDataFolderPath;
        this.featureExtractor = featureExtractor;
        this.random = random;
        this.trainPercentage = trainPercentage;
        this.normalized = normalized;
    }

    public AggregatedData aggregateData(boolean inParallel) throws IOException {
        final Map<Integer, Path> pathsByLabels = mapLabels();

        final DataSet[][] dataSets;
        try {
            dataSets = inParallel ? retrieveDataInParallel(pathsByLabels) : retrieveDataSequentially(pathsByLabels);
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }

        final var trainDataSets = new DataSet[dataSets.length];
        final var testDataSets = new DataSet[dataSets.length];

        // separate train and test data sets
        for (int index = 0; index < dataSets.length; index++) {
            trainDataSets[index] = dataSets[index][0];
            testDataSets[index] = dataSets[index][1];
        }

        final DataSet trainDataSet = DataSet.concatenate(trainDataSets);
        final DataSet testDataSet = DataSet.concatenate(testDataSets);

        final var labelsMap = pathsByLabels.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getFileName().toString()));
        return new AggregatedData(trainDataSet, testDataSet, new TreeMap<>(labelsMap));
    }

    private DataSet[][] retrieveDataSequentially(Map<Integer, Path> labelsMap) {
        return labelsMap.entrySet().stream()
                .map(entry -> new ClassFolderReader(entry.getKey(), entry.getValue()))
                .map(classFolderReader -> classFolderReader.extractDataSet(featureExtractor, normalized))
                .map(dataSet -> DataSet.splitInTwo(DataSet.shuffleDataSet(dataSet, random), trainPercentage))
                .toArray(DataSet[][]::new);
    }

    private DataSet[][] retrieveDataInParallel(Map<Integer, Path> labelsMap) {
        return labelsMap.entrySet().parallelStream()
                .map(entry -> new ClassFolderReader(entry.getKey(), entry.getValue()))
                .map(classFolderReader -> classFolderReader.extractDataSet(featureExtractor.copy(), normalized))
                .map(dataSet -> DataSet.splitInTwo(DataSet.shuffleDataSet(dataSet, random), trainPercentage))
                .toArray(DataSet[][]::new);
    }

    private Map<Integer, Path> mapLabels() throws IOException {
        final List<Path> folders;
        try (Stream<Path> dataFolders = Files.list(Paths.get(rootDataFolderPath))) {
            folders = dataFolders.collect(Collectors.toList());
        }

        return IntStream.range(0, folders.size())
                .boxed()
                .collect(Collectors.toMap(Function.identity(), folders::get));
    }

    public static class Builder {
        private String rootDataFolderPath;
        private FeatureExtractor featureExtractor;
        private Random random;
        private double trainPercentage;
        private boolean normalized;

        public Builder withRootDataFolderPath(String rootDataFolderPath) {
            this.rootDataFolderPath = rootDataFolderPath;
            return this;
        }

        public Builder withFeatureExtractor(FeatureExtractor featureExtractor) {
            this.featureExtractor = featureExtractor;
            return this;
        }

        public Builder withRandom(Random random) {
            this.random = random;
            return this;
        }

        public Builder withTrainPercentage(double trainPercentage) {
            this.trainPercentage = trainPercentage;
            return this;
        }

        public Builder withNormalized(boolean normalized) {
            this.normalized = normalized;
            return this;
        }

        public DataAggregator build() {
            return new DataAggregator(rootDataFolderPath, featureExtractor, random, trainPercentage, normalized);
        }
    }
}
