package com.github.afloarea.jclassifier.data.aggregation;

import com.github.afloarea.jclassifier.data.DataSet;
import com.github.afloarea.jclassifier.extractors.FeatureExtractor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.stream.Stream;

public final class ClassFolderReader {

    private final int classLabel;
    private final Path classFolder;

    public ClassFolderReader(int classLabel, Path classFolder) {
        this.classLabel = classLabel;
        this.classFolder = classFolder;
    }

    public DataSet extractDataSet(FeatureExtractor featureExtractor, boolean normalized) {
        final double[][] features = readFeatures(featureExtractor, normalized);
        final int[] labels = new int[features.length];
        Arrays.fill(labels, classLabel);

        final DataSet dataSet = new DataSet();
        dataSet.setFeatures(features);
        dataSet.setLabels(labels);

        return dataSet;
    }

    private double[][] readFeatures(FeatureExtractor featureExtractor, boolean normalized) {
        try (final Stream<Path> files = Files.list(classFolder)) {
            return files.map(imagePath -> {
                try (InputStream inputStream = Files.newInputStream(imagePath, StandardOpenOption.READ)) {
                    final BufferedImage image = ImageIO.read(inputStream);
                    if (normalized) return featureExtractor.extractNormalized(image);
                    else return convertToDoubleArray(featureExtractor.extractCount(image));
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }).toArray(double[][]::new);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static double[] convertToDoubleArray(int[] array) {
        return Arrays.stream(array).mapToDouble(x -> x).toArray();
    }
}
