package ru.ifmo.ctddev.segal.hw1.algorithm;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.util.Pair;
import ru.ifmo.ctddev.segal.hw1.model.ComplexDifferentiableFunction;

import java.util.List;

/**
 * @author Daniyar Itegulov
 */
public interface NewtonMethod {
    Pair<Complex, Integer> getRoot(ComplexDifferentiableFunction function, Complex begin);
    List<Complex> getPath(ComplexDifferentiableFunction complexDifferentiableFunction, Complex begin);
}
