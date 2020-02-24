package com.github.afloarea.jclassifier.data;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class DataSetTest {

    @Test
    void testSplit() {
        final double[][] sampleData = new double[][] {
                { 1,  2,  3},
                { 4,  5,  6},
                { 7,  8,  9},
                {10, 11, 12}
        };

        final int[] labels = new int[] {1, 1, 2, 2};
        final DataSet originalData = new DataSet(sampleData, labels);
        final DataSet[] splitData = DataSet.splitInTwo(originalData, 0.75);

        assertEquals(3, splitData[0].size());
        assertEquals(1, splitData[1].size());
    }

    @Test
    void testConcatenate() {
        final var firstFeatures = new double[][] {
                {1, 2, 3},
                {4, 5, 6}
        };
        final var secondFeatures = new double[][] {
                {7, 8, 9},
                {0, 0, 0}
        };
        final var thirdFeatures = new double[][] {
                {1, 1, 1},
                {2, 2, 2}
        };
        final var firstLabels = new int[] {0, 0};
        final var secondLabels = new int[] {1, 1};
        final var thirdLabels = new int[] {2, 2};

        final var firstDataSet = new DataSet(firstFeatures, firstLabels);
        final var secondDataSet = new DataSet(secondFeatures, secondLabels);
        final var thirdDataSet = new DataSet(thirdFeatures, thirdLabels);

        final var result = DataSet.concatenate(firstDataSet, secondDataSet, thirdDataSet);

        assertEquals(6, result.size());
    }

    @Test
    void testShuffle() {
        final double[][] sampleData = new double[][] {
                { 1,  2,  3},
                { 4,  5,  6},
                { 7,  8,  9},
                {10, 11, 12}
        };

        final int[] labels = new int[] {0, 0, 2, 2};
        final DataSet originalData = new DataSet(sampleData, labels);

        final Random r = new Random(14);
        final DataSet result = DataSet.shuffleDataSet(originalData, r);

        assertEquals(4, result.size());
        final double[][] resultFeatures = result.getFeatures();
        assertFalse(resultFeatures[0][0] == 1.0 && resultFeatures[1][0] == 4.0
                && resultFeatures[2][0] == 7.0 && resultFeatures[3][0] == 10.0);
    }

}
