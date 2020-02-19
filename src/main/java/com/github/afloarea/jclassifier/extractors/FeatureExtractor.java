package com.github.afloarea.jclassifier.extractors;

import com.github.afloarea.jclassifier.utils.Copyable;

import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

public interface FeatureExtractor extends Copyable<FeatureExtractor> {

    int[] extractCount(BufferedImage image);

    default double[] extractNormalized(BufferedImage image) {
        final double totalPixelCount = image.getHeight() * image.getWidth();
        return IntStream.of(extractCount(image)).mapToDouble(value -> value / totalPixelCount).toArray();
    }

    static FeatureExtractor of(ExtractorType extractorType, int... bucketSizes) {
        switch (extractorType) {
            case RGB_CONCATENATED:  return new RgbExtractor(bucketSizes[0], bucketSizes[1], bucketSizes[2]);
            case HSV_CONCATENATED:  return new HsvExtractor(bucketSizes[0], bucketSizes[1], bucketSizes[2]);
            case GRAY_FROM_RGB:     return new RgbToGrayExtractor(bucketSizes[0]);
            case HUE_ONLY:          return new HueOnlyExtractor(bucketSizes[0]);
            default: throw new IllegalArgumentException("Unsupported extractor type");
        }
    }
}
