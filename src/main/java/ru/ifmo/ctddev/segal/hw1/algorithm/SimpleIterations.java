package ru.ifmo.ctddev.segal.hw1.algorithm;

import ru.ifmo.ctddev.segal.hw1.model.Function;

import java.util.Collection;

/**
 * Created by dimatomp on 18.11.15.
 */
public interface SimpleIterations {
    Collection<Double> getAllLimits(Function<Double> x, double start);
}
