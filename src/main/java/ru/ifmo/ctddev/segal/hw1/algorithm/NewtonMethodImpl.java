package ru.ifmo.ctddev.segal.hw1.algorithm;

import org.apache.commons.math3.complex.Complex;
import ru.ifmo.ctddev.segal.hw1.model.FunctionWithDerivative;


/**
 * @author Aleksei Latyshev
 */

public class NewtonMethodImpl implements NewtonMethod {
    public static final double EPS = 1e-16;
    public static final int COUNT = 1_000;

    @Override
    public Complex getRoot(FunctionWithDerivative function, Complex begin) {
        Complex current = begin;
        boolean found = false;
        for (int it = 0; it < COUNT; it++) {
            current = current.subtract(function.getValue(current).divide(function.getDerivativeValue(current)));
            if (function.getValue(current).abs() < EPS) {
                found = true;
                break;
            }
        }
        if (found) {
            return current;
        } else {
            return null;
        }
    }
}