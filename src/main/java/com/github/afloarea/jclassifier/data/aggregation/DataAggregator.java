package com.github.afloarea.jclassifier.data.aggregation;

import com.github.afloarea.jclassifier.data.AggregatedData;
import com.github.afloarea.jclassifier.data.DataSet;
import com.github.afloarea.jclassifier.data.LabeledAggregatedData;
import com.github.afloarea.jclassifier.extractors.FeatureExtractor;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
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

    public LabeledAggregatedData aggregateData(boolean inParallel) throws IOException {
        final Map<Integer, Path> pathsByLabels = mapLabels();

        final AggregatedData aggregatedData;
        try {
            aggregatedData = retrieveData(inParallel, pathsByLabels);
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }

        final var labelsMap = pathsByLabels.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getFileName().toString()));
        return new LabeledAggregatedData(aggregatedData, new TreeMap<>(labelsMap));
    }

    private AggregatedData retrieveData(boolean isParallel, Map<Integer, Path> labelsMap) {
        final AggregatedData[] aggregatedData = streamFrom(labelsMap.entrySet(), isParallel)
                .map(entry -> new ClassFolderReader(entry.getKey(), entry.getValue()))
                .map(classFolderReader -> classFolderReader.extractDataSet(getExtractor(isParallel), normalized))
                .map(dataSet -> DataSet.shuffleDataSet(dataSet, random))
                .map(dataSet -> DataSet.splitInTwo(dataSet, trainPercentage))
                .toArray(AggregatedData[]::new);

        return AggregatedData.concatenate(aggregatedData);
    }

    private <T> Stream<T> streamFrom(Collection<T> collection, boolean parallel) {
        return parallel ? collection.parallelStream() : collection.stream();
    }

    private FeatureExtractor getExtractor(boolean isParallel) {
        return isParallel ? featureExtractor.copy() : featureExtractor;
    }

    private Map<Integer, Path> mapLabels() throws IOException {
        final List<Path> folders;
        try (Stream<Path> dataFolders = Files.list(Path.of(rootDataFolderPath))) {
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
