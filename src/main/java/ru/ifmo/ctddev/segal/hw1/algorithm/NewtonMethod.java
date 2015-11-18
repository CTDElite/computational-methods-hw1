package ru.ifmo.ctddev.segal.hw1.algorithm;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.util.Pair;
import ru.ifmo.ctddev.segal.hw1.model.FunctionWithDerivative;

/**
 * @author Daniyar Itegulov
 */
public interface NewtonMethod {

    Pair<Complex, Integer> getRoot(FunctionWithDerivative function, Complex begin);
}
