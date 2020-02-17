package com.github.afloarea.jclassifier.classifiers.knn;

import java.util.Objects;
import java.util.stream.IntStream;

public final class Neighbour implements Comparable<Neighbour> {
    private final double distance;
    private final int label;

    public Neighbour(double distance, int label) {
        this.distance = distance;
        this.label = label;
    }

    @Override
    public int compareTo(Neighbour that) {
        return (int)Math.signum(this.distance - that.distance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Neighbour)) return false;
        Neighbour neighbour = (Neighbour) o;
        return Double.compare(neighbour.distance, distance) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance);
    }

    public static Neighbour[] createGroupFrom(double[] distances, int[] labels) {
        return IntStream
                .range(0, distances.length)
                .mapToObj(index -> new Neighbour(distances[index], labels[index]))
                .toArray(Neighbour[]::new);
    }

    public double getDistance() {
        return distance;
    }

    public int getLabel() {
        return label;
    }
}
