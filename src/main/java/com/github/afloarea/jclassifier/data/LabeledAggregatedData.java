package com.github.afloarea.jclassifier.data;

import java.util.Map;

public final class LabeledAggregatedData extends AggregatedData {
    private final Map<Integer, String> labels;

    public LabeledAggregatedData(AggregatedData aggregatedData, Map<Integer, String> labels) {
        super(aggregatedData.getTrainData(), aggregatedData.getTestData());
        this.labels = labels;
    }

    public Map<Integer, String> getLabels() {
        return labels;
    }
}
