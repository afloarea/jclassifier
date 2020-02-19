package com.github.afloarea.jclassifier.extractors;

import java.awt.image.BufferedImage;

public abstract class AbstractFeatureExtractor implements FeatureExtractor {
    private int[] rgbArray = null;

    @Override
    public int[] extractCount(BufferedImage image) {
        rgbArray = getRgbArray(image.getHeight() * image.getWidth());

        final int[] features = createEmptyFeatureArray();
        final int[] rgb = image.getRGB(0, 0, image.getWidth(), image.getHeight(), rgbArray, 0, image.getWidth());

        for (int pixel : rgb) {
            final int red = pixel >> 16 & 0xff;
            final int green = pixel >> 8 & 0xff;
            final int blue = pixel & 0xff;

            processPixel(red, green, blue, features);
        }

        return features;
    }

    protected abstract int[] createEmptyFeatureArray();

    protected abstract void processPixel(int red, int green, int blue, int[] featuresArray);

    private int[] getRgbArray(int newSize) {
        if (rgbArray == null || rgbArray.length != newSize) return new int[newSize];
        else return rgbArray;
    }
}
