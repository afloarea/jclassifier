package com.github.afloarea.jclassifier.extractors;

import java.awt.*;

public final class HueOnlyExtractor extends AbstractFeatureExtractor{
    private final float[] buffer = new float[3];

    private final int hueBucketSize;

    HueOnlyExtractor(int hueBucketSize) {
        this.hueBucketSize = hueBucketSize;
    }

    @Override
    protected int[] createEmptyFeatureArray() {
        return new int[hueBucketSize];
    }

    @Override
    protected void processPixel(int red, int green, int blue, int[] featuresArray) {
        final float hue = Color.RGBtoHSB(red, green, blue, buffer)[0];
        featuresArray[(int) Math.min(Math.floor(hue * hueBucketSize), hueBucketSize - 1)]++;
    }

    @Override
    public FeatureExtractor copy() {
        return new HueOnlyExtractor(hueBucketSize);
    }
}
