package com.github.afloarea.jclassifier.data.aggregation;

import com.github.afloarea.jclassifier.data.AggregatedData;
import com.github.afloarea.jclassifier.data.DataSet;
import com.github.afloarea.jclassifier.extractors.FeatureExtractor;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataAggregator {

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

    public AggregatedData aggregateData(boolean inParallel) throws IOException {
        final Map<String, Integer> labelsMap = mapLabels();

        final DataSet[][] dataSets =
                inParallel ? retrieveDataInParallel(labelsMap) : retrieveDataSequentially(labelsMap);

        final DataSet[] trainDataSets = new DataSet[dataSets.length];
        final DataSet[] testDataSets = new DataSet[dataSets.length];

        // separate train and test data sets
        for (int i = 0; i < dataSets.length; i++) {
            trainDataSets[i] = dataSets[i][0];
            testDataSets[i] = dataSets[i][1];
        }

        final DataSet trainDataSet = DataSet.concatenate(trainDataSets);
        final DataSet testDataSet = DataSet.concatenate(testDataSets);

        return new AggregatedData(trainDataSet, testDataSet, new TreeMap<>(labelsMap));
    }

    private DataSet[][] retrieveDataSequentially(Map<String, Integer> labelsMap) throws IOException {
        try (Stream<Path> folderPaths = Files.list(Paths.get(rootDataFolderPath))) {
            return folderPaths
                    .map(path -> new ClassFolderReader(labelsMap.get(path.getFileName().toString()), path))
                    .map(classFolderReader -> {
                        try {
                            return classFolderReader.extractDataSet(featureExtractor, normalized);
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    })
                    .map(dataSet -> DataSet.splitInTwo(DataSet.shuffleDataSet(dataSet, random), trainPercentage))
                    .toArray(DataSet[][]::new);
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }

    private DataSet[][] retrieveDataInParallel(Map<String, Integer> labelsMap) throws IOException {
        final ExecutorService executorService =
                Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        try {
            return retrieveData(labelsMap, executorService);
        } catch (UncheckedInterruptedException e) {
            throw new IOException(e.getCause());
        } finally {
            executorService.shutdown();
        }
    }

    /**
     * Walk folders and read data from each folder in parallel.
     *
     * @param labelsMap       contains the mapping between folder names and an Integer label
     * @param executorService the {@link ExecutorService} used for parallel processing
     * @return 2D {@link DataSet} array, first dimension is the class (folder name), second dimension is 0 for train, 1 for test
     * @throws IOException                   when the folders / images cannot be read
     * @throws UncheckedInterruptedException wrapper of an {@link InterruptedException} or {@link ExecutionException}, which
     *                                       is recommended to be caught
     */
    private DataSet[][] retrieveData(Map<String, Integer> labelsMap, ExecutorService executorService)
            throws IOException {
        try (Stream<Path> folderPaths = Files.list(Paths.get(rootDataFolderPath))) {
            return folderPaths
                    .map(folderPath ->
                            new ClassFolderReader(labelsMap.get(folderPath.getFileName().toString()), folderPath))
                    .map(classFolderReader -> executorService
                            .submit(() -> classFolderReader.extractDataSet(featureExtractor.copy(), normalized)))
                    .collect(Collectors.toList())
                    .stream()
                    .map(dataSetFuture -> {
                        try {
                            return dataSetFuture.get();
                        } catch (InterruptedException | ExecutionException e) {
                            Thread.currentThread().interrupt();
                            throw new UncheckedInterruptedException(e);
                        }
                    })
                    .map(dataSet -> DataSet.splitInTwo(DataSet.shuffleDataSet(dataSet, random), trainPercentage))
                    .toArray(DataSet[][]::new);
        }
    }

    private Map<String, Integer> mapLabels() throws IOException {
        final Map<String, Integer> labelsMap = new HashMap<>();
        try (Stream<Path> dataFolders = Files.list(Paths.get(rootDataFolderPath))) {
            dataFolders
                    .map(Path::getFileName)
                    .map(String::valueOf)
                    .forEach(label -> labelsMap.put(label, labelsMap.size()));
            return labelsMap;
        }
    }
}
