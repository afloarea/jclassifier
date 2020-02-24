package com.github.afloarea.jclassifier.extractors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HsvExtractorTest {
    private static final int HUE_BUCKET_SIZE = 10, SATURATION_BUCKET_SIZE = 5, BRIGHTNESS_BUCKET_SIZE = 3;

    // subject
    private static FeatureExtractor featureExtractor =
            FeatureExtractor.of(ExtractorType.HSV_CONCATENATED, HUE_BUCKET_SIZE, SATURATION_BUCKET_SIZE, BRIGHTNESS_BUCKET_SIZE);

    private static final BufferedImage testImage = new BufferedImage(3, 3, BufferedImage.TYPE_INT_RGB);

    @BeforeAll
    static void setUpImage() {
        testImage.setRGB(0, 0, Color.HSBtoRGB(0.05f, 0.1f, 0.2f));
        testImage.setRGB(1, 0, Color.HSBtoRGB(0.15f, 0.3f, 0.2f));
        testImage.setRGB(2, 0, Color.HSBtoRGB(0.25f, 0.5f, 0.2f));

        testImage.setRGB(0, 1, Color.HSBtoRGB(0.35f, 0.7f, 0.3f));
        testImage.setRGB(1, 1, Color.HSBtoRGB(0.45f, 0.9f, 0.4f));
        testImage.setRGB(2, 1, Color.HSBtoRGB(0.55f, 0.9f, 0.4f));

        testImage.setRGB(0, 2, Color.HSBtoRGB(0.65f, 0.1f, 0.9f));
        testImage.setRGB(1, 2, Color.HSBtoRGB(0.75f, 0.1f, 0.9f));
        testImage.setRGB(2, 2, Color.HSBtoRGB(0.85f, 0.1f, 0.9f));
    }

    @Test
    void testSimpleExtractFeatureCount() {
        final int[] features = featureExtractor.extractCount(testImage);
        assertArrayEquals(new int[] {
                1, 1, 1, 1, 1, 1, 1, 1, 1, 0,   // hue
                4, 1, 1, 1, 2,                  // saturation
                4, 2, 3                         // brightness
        }, features);
    }

    @Test
    void testSumIsEqualToOneForEachColorChannel() {
        final var delta = 0.0000001;
        final double[] normalizedFeatures = featureExtractor.extractNormalized(testImage);
        assertEquals(3.0, Arrays.stream(normalizedFeatures).sum(), delta);

        assertEquals(1.0,
                Arrays.stream(normalizedFeatures).limit(HUE_BUCKET_SIZE).sum(),
                delta);
        assertEquals(1.0,
                Arrays.stream(normalizedFeatures).skip(HUE_BUCKET_SIZE).limit(SATURATION_BUCKET_SIZE).sum(),
                delta);
        assertEquals(1.0,
                Arrays.stream(normalizedFeatures).skip(HUE_BUCKET_SIZE + SATURATION_BUCKET_SIZE).sum(),
                delta);
    }
}
