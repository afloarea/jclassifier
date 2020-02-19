package com.github.afloarea.jclassifier.extractors;

public final class RgbToGrayExtractor extends AbstractFeatureExtractor {
    /* Better Results with uncommented values */
    private static final double RED_WEIGHT = 0.2126;    // 0.2989;
    private static final double GREEN_WEIGHT = 0.7152;  // 0.587;
    private static final double BLUE_WEIGHT = 0.0722;   // 0.114;

    /**
     * Max value for gray. Set to 256 instead of 255 to avoid {@link ArrayIndexOutOfBoundsException}.
     */
    private static final double GRAY_MAX_VALUE = 256;

    private final int grayBucketSize;

    RgbToGrayExtractor(int grayBucketSize) {
        this.grayBucketSize = grayBucketSize;
    }

    @Override
    protected int[] createEmptyFeatureArray() {
        return new int[grayBucketSize];
    }

    @Override
    protected void processPixel(int red, int green, int blue, int[] featuresArray) {
        final int grayValue = (int)Math.round(red * RED_WEIGHT + green * GREEN_WEIGHT + blue * BLUE_WEIGHT);
        featuresArray[(int) Math.floor(grayValue / GRAY_MAX_VALUE * grayBucketSize)]++;
    }

    @Override
    public FeatureExtractor copy() {
        return new RgbToGrayExtractor(grayBucketSize);
    }
}
