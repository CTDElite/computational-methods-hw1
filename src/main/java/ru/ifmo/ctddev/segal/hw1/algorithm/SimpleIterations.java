package ru.ifmo.ctddev.segal.hw1.algorithm;

import org.apache.commons.math3.util.Pair;
import ru.ifmo.ctddev.segal.hw1.model.RealFunction;

import java.util.List;

/**
 * Created by dimatomp on 18.11.15.
 */
public interface SimpleIterations {
    Pair<List<Double>, Integer> getAllLimits(RealFunction x, double start, int numberOfLimits);
}
