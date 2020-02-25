package com.github.afloarea.jclassifier.data;

import java.util.Map;

public final class AggregatedData {

    private DataSet trainData;
    private DataSet testData;
    private Map<Integer, String> labelsMap;

    public AggregatedData() {
    }

    public AggregatedData(DataSet trainData, DataSet testData, Map<Integer, String> labelsMap) {
        this.trainData = trainData;
        this.testData = testData;
        this.labelsMap = labelsMap;
    }

    public DataSet getTrainData() {
        return trainData;
    }

    public void setTrainData(DataSet trainData) {
        this.trainData = trainData;
    }

    public DataSet getTestData() {
        return testData;
    }

    public void setTestData(DataSet testData) {
        this.testData = testData;
    }

    public Map<Integer, String> getLabelsMap() {
        return labelsMap;
    }

    public void setLabelsMap(Map<Integer, String> labelsMap) {
        this.labelsMap = labelsMap;
    }
}
