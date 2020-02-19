package com.github.afloarea.jclassifier.extractors;

public final class RgbExtractor extends AbstractFeatureExtractor {
    /**
     * Max RGB value, set to 256.0 to avoid {@link ArrayIndexOutOfBoundsException} when blue color value is 255.
     */
    private static final double RGB_MAX_COLOR_VALUE = 256.0;

    private final int redBucketSize;
    private final int greenBucketSize;
    private final int blueBucketSize;

    RgbExtractor(int redBucketSize, int greenBucketSize, int blueBucketSize) {
        this.redBucketSize = redBucketSize;
        this.greenBucketSize = greenBucketSize;
        this.blueBucketSize = blueBucketSize;
    }

    @Override
    protected int[] createEmptyFeatureArray() {
        return new int[redBucketSize + greenBucketSize + blueBucketSize];
    }

    @Override
    protected void processPixel(int red, int green, int blue, int[] featuresArray) {
        featuresArray[(int) Math.floor(red / RGB_MAX_COLOR_VALUE * redBucketSize)]++;
        featuresArray[redBucketSize + (int) Math.floor(green / RGB_MAX_COLOR_VALUE * greenBucketSize)]++;
        featuresArray[redBucketSize + greenBucketSize +
                (int) Math.floor(blue / RGB_MAX_COLOR_VALUE * blueBucketSize)]++;
    }

    @Override
    public FeatureExtractor copy() {
        return new RgbExtractor(redBucketSize, greenBucketSize, blueBucketSize);
    }
}
