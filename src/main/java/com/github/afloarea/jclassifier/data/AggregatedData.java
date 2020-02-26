package com.github.afloarea.jclassifier.data;

import java.util.Arrays;

public class AggregatedData {

    private final DataSet trainData;
    private final DataSet testData;

    public static AggregatedData concatenate(AggregatedData... data) {
        final DataSet[] trainData = Arrays.stream(data)
                .map(AggregatedData::getTrainData)
                .toArray(DataSet[]::new);

        final DataSet[] testData = Arrays.stream(data)
                .map(AggregatedData::getTestData)
                .toArray(DataSet[]::new);

        return new AggregatedData(DataSet.concatenate(trainData), DataSet.concatenate(testData));
    }

    public AggregatedData(DataSet trainData, DataSet testData) {
        this.trainData = trainData;
        this.testData = testData;
    }

    public DataSet getTrainData() {
        return trainData;
    }

    public DataSet getTestData() {
        return testData;
    }
}
