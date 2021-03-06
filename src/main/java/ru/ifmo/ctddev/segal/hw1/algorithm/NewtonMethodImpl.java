package ru.ifmo.ctddev.segal.hw1.algorithm;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.util.Pair;
import ru.ifmo.ctddev.segal.hw1.model.ComplexDifferentiableFunction;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Aleksei Latyshev
 */

public class NewtonMethodImpl implements NewtonMethod {
    public static final double EPS = 1e-12;
    public static final int COUNT = 1_000;

    @Override
    public Pair<Complex, Integer> getRoot(ComplexDifferentiableFunction function, Complex begin) {
        Complex current = begin;
        int found = -1;
        for (int it = 0; it < COUNT; it++) {
            if (function.getValue(current).abs() < EPS) {
                found = it;
                break;
            }
            current = current.subtract(function.getValue(current).divide(function.getDerivativeValue(current)));
        }
        if (found != -1) {
            return new Pair<>(current, found);
        } else {
            return null;
        }
    }

    @Override
    public List<Complex> getPath(ComplexDifferentiableFunction function, Complex begin) {
        List<Complex> ans = new ArrayList<>();
        Complex current = begin;
        ans.add(current);
        boolean found = false;
        for (int it = 0; it < COUNT; it++) {
            if (function.getValue(current).abs() < EPS) {
                found = true;
                break;
            }
            ans.add(current = current.subtract(function.getValue(current).divide(function.getDerivativeValue(current))));
        }
        if (found) {
            return ans;
        } else {
            return null;
        }
    }
}