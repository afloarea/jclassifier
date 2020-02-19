package com.github.afloarea.jclassifier.data;

import java.util.Map;

public final class AggregatedData {

    private DataSet trainData;
    private DataSet testData;
    private Map<String, Integer> labelsMap;

    public AggregatedData() {
    }

    public AggregatedData(DataSet trainData, DataSet testData, Map<String, Integer> labelsMap) {
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

    public Map<String, Integer> getLabelsMap() {
        return labelsMap;
    }

    public void setLabelsMap(Map<String, Integer> labelsMap) {
        this.labelsMap = labelsMap;
    }
}
