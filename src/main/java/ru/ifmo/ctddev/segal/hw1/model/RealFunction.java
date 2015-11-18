package ru.ifmo.ctddev.segal.hw1.model;

/**
 * Created by dimatomp on 18.11.15.
 */
public interface RealFunction extends Function<Double> {
    double getDerivativeSignAround(double x);
}
