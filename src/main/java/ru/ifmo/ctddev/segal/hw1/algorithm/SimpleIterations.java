package ru.ifmo.ctddev.segal.hw1.algorithm;

import ru.ifmo.ctddev.segal.hw1.model.Function;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by dimatomp on 18.11.15.
 */
public interface SimpleIterations {
    Collection<Double> getAllLimits(Function<Double> x, double start);
    Iterator<Double> applyMethod(Function<Double> x, double start);

    /** @return a limit of x_(fIndex + step * i) or null if it has not been found */
    Double getSlicedLimit(Function<Double> x, double start, int fIndex, int step);
}
