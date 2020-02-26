package com.github.afloarea.jclassifier;

import com.github.afloarea.jclassifier.classifiers.knn.Knn;
import com.github.afloarea.jclassifier.data.DataSet;
import com.github.afloarea.jclassifier.data.LabeledAggregatedData;
import com.github.afloarea.jclassifier.data.aggregation.DataAggregator;
import com.github.afloarea.jclassifier.evaluators.BasicEvaluator;
import com.github.afloarea.jclassifier.evaluators.Statistics;
import com.github.afloarea.jclassifier.extractors.ExtractorType;
import com.github.afloarea.jclassifier.extractors.FeatureExtractor;

import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws IOException {
        final DataAggregator aggregator = new DataAggregator.Builder()
                .withRandom(new Random(12))
                .withRootDataFolderPath("datasets/UCMerced_LandUse/Images")
                .withTrainPercentage(0.7)
                .withNormalized(false)
                .withFeatureExtractor(FeatureExtractor.of(ExtractorType.HSV_CONCATENATED, 360, 256, 256))
                .build();

        LOGGER.info("Start collecting data...");
        long ref = System.currentTimeMillis();
        final LabeledAggregatedData aggregatedData = aggregator.aggregateData(true);
        LOGGER.log(Level.INFO, "Finished reading data and extracting features: {0} seconds", (System.currentTimeMillis() - ref) / 1000);

        final DataSet trainSet = aggregatedData.getTrainData();
        final DataSet testSet = aggregatedData.getTestData();

        final Knn knn = Knn.of(trainSet.getFeatures(), trainSet.getLabels(), 7);
        LOGGER.info("Start classification...");
        ref = System.currentTimeMillis();
        final int[] guessedLabels = knn.classify(testSet.getFeatures());
        LOGGER.log(Level.INFO, "Finished classifying data: {0} seconds", (System.currentTimeMillis() - ref) / 1000);

        final Statistics stats = BasicEvaluator.evaluate(testSet.getLabels(), guessedLabels);

        System.out.println(stats);
        System.out.println(aggregatedData.getLabels());
    }

}
