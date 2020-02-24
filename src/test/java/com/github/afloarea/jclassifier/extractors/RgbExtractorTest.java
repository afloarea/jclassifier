package com.github.afloarea.jclassifier.extractors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RgbExtractorTest {

    private static final int RED_BUCKET_SIZE = 256, GREEN_BUCKET_SIZE = 256, BLUE_BUCKET_SIZE = 256;

    // subject
    private static FeatureExtractor featureExtractor =
            FeatureExtractor.of(ExtractorType.RGB_CONCATENATED, RED_BUCKET_SIZE, GREEN_BUCKET_SIZE, BLUE_BUCKET_SIZE);

    private static final BufferedImage testImage = new BufferedImage(3, 3, BufferedImage.TYPE_INT_RGB);

    @BeforeAll
    static void setUpImage() {
        testImage.setRGB(0, 0, 255 << 16);
        testImage.setRGB(1, 0, 255 << 8);
        testImage.setRGB(2, 0, (255 << 8) + 255);

        testImage.setRGB(0, 1, (128 << 16) + (128 << 8) + 128);
        testImage.setRGB(1, 1, (128 << 16) + 128);
        testImage.setRGB(2, 1, (128 << 8) + 128);

        testImage.setRGB(0, 2, 0);
        testImage.setRGB(1, 2, 0);
        testImage.setRGB(2, 2, 0);
    }

    @Test
    void testCountWithSimpleImage() {
        final var colorCount = featureExtractor.extractCount(testImage);
        assertEquals(1, colorCount[255]);
        assertEquals(2, colorCount[128]);
        assertEquals(6, colorCount[0]);

        assertEquals(2, colorCount[RED_BUCKET_SIZE + 255]);
        assertEquals(2, colorCount[RED_BUCKET_SIZE + 128]);
        assertEquals(5, colorCount[RED_BUCKET_SIZE]);

        assertEquals(1, colorCount[RED_BUCKET_SIZE + GREEN_BUCKET_SIZE + 255]);
        assertEquals(3, colorCount[RED_BUCKET_SIZE + GREEN_BUCKET_SIZE + 128]);
        assertEquals(5, colorCount[RED_BUCKET_SIZE + GREEN_BUCKET_SIZE]);
    }

    @Test
    void testNormalizedWithSimpleImage() {
        final double totalPixelCount = testImage.getHeight() * testImage.getWidth();

        final var delta = 0.000_000_01;
        final var histogram = featureExtractor.extractNormalized(testImage);

        assertEquals(1 / totalPixelCount , histogram[255], delta);
        assertEquals(2 / totalPixelCount, histogram[128], delta);
        assertEquals(6 / totalPixelCount, histogram[0], delta);

        assertEquals(2 / totalPixelCount, histogram[RED_BUCKET_SIZE + 255], delta);
        assertEquals(2 / totalPixelCount, histogram[RED_BUCKET_SIZE + 128], delta);
        assertEquals(5 / totalPixelCount, histogram[RED_BUCKET_SIZE], delta);

        assertEquals(1 / totalPixelCount, histogram[RED_BUCKET_SIZE + GREEN_BUCKET_SIZE + 255], delta);
        assertEquals(3 / totalPixelCount, histogram[RED_BUCKET_SIZE + GREEN_BUCKET_SIZE + 128], delta);
        assertEquals(5 / totalPixelCount, histogram[RED_BUCKET_SIZE + GREEN_BUCKET_SIZE], delta);
    }

}
