package ru.ifmo.ctddev.segal.hw1.model;

import org.apache.commons.math3.complex.Complex;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Latyshev Aleksei
 */
public class ComplexZPowN implements ComplexDifferentiableFunction {

    private final double EPS = 1e-9;
    private final int n;

    public ComplexZPowN(int n) {
        if (n <= 1) {
            throw new IllegalArgumentException("power must be al least two");
        }
        this.n = n;
    }

    @Override
    public Complex getDerivativeValue(Complex x) {
        return x.pow(n - 1).multiply(n);
    }

    @Override
    public Complex getValue(Complex x) {
        return x.pow(n).subtract(Complex.ONE);
    }

    public List<Complex> getRoots() {
        Complex mainRoot = new Complex(StrictMath.cos(2 * StrictMath.PI / n), StrictMath.sin(2 * StrictMath.PI / n));
        List<Complex> ans = new ArrayList<>();
        ans.add(Complex.ONE);
        for (Complex root = mainRoot; root.subtract(Complex.ONE).abs() > EPS; root = root.multiply(mainRoot)) {
            ans.add(root);
        }
        assert (ans.size() == n);
        return ans;
    }
}