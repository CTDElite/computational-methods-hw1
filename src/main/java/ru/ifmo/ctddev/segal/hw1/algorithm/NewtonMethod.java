package ru.ifmo.ctddev.segal.hw1.algorithm;

import org.apache.commons.math3.complex.Complex;
import ru.ifmo.ctddev.segal.hw1.model.FunctionWithDerivative;

/**
 * @author Daniyar Itegulov
 */
public interface NewtonMethod {

    Complex getRoot(FunctionWithDerivative function, Complex begin);
}
