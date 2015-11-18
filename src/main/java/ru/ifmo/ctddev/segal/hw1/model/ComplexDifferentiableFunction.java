package ru.ifmo.ctddev.segal.hw1.model;

import org.apache.commons.math3.complex.Complex;

/**
 * @author Daniyar Itegulov
 */
public interface ComplexDifferentiableFunction extends Function<Complex> {
    Complex getDerivativeValue(Complex x);
}
