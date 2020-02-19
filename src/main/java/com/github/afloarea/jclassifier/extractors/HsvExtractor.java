package com.github.afloarea.jclassifier.extractors;

import java.awt.*;

public final class HsvExtractor extends AbstractFeatureExtractor {
    private final float[] hsvBuffer = new float[3];

    private final int hueBucketSize;
    private final int saturationBucketSize;
    private final int brightnessBucketSize;

    HsvExtractor(int hueBucketSize, int saturationBucketSize, int brightnessBucketSize) {
        this.hueBucketSize = hueBucketSize;
        this.saturationBucketSize = saturationBucketSize;
        this.brightnessBucketSize = brightnessBucketSize;
    }

    @Override
    protected int[] createEmptyFeatureArray() {
        return new int[hueBucketSize + saturationBucketSize + brightnessBucketSize];
    }

    @Override
    protected void processPixel(int red, int green, int blue, int[] featuresArray) {
        final float[] hsb = Color.RGBtoHSB(red, green, blue, hsvBuffer);

        featuresArray[(int) Math.min(Math.floor(hsb[0] * hueBucketSize), hueBucketSize - 1)]++;
        featuresArray[hueBucketSize +
                (int) Math.min(Math.floor(hsb[1] * saturationBucketSize), saturationBucketSize - 1)]++;
        featuresArray[hueBucketSize + saturationBucketSize +
                (int) Math.min(Math.floor(hsb[2] * brightnessBucketSize), brightnessBucketSize - 1)]++;
    }

    @Override
    public FeatureExtractor copy() {
        return new HsvExtractor(hueBucketSize, saturationBucketSize, brightnessBucketSize);
    }

}
