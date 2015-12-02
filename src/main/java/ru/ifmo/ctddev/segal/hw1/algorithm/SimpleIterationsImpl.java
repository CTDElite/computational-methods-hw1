package ru.ifmo.ctddev.segal.hw1.algorithm;

import ru.ifmo.ctddev.segal.hw1.model.Function;

import java.util.Collection;
import java.util.NavigableSet;
import java.util.TreeSet;

/**
 * Created by dimatomp on 02.12.15.
 */
public class SimpleIterationsImpl implements SimpleIterations {
    private final int prepare;
    private final int nPoints;
    private final double epsilon;
    private final double lambda;

    /**
     * @param prepare number of iterations to perform before capturing common values
     * @param nPoints number of iterations to perform after {@code prepare}
     * @param epsilon minimal difference between two distinct limits
     * @param lambda coefficient for a single step value (see
     *               <a href="https://en.wikipedia.org/wiki/Fixed-point_iteration">Wikipedia article</a>)
     */
    public SimpleIterationsImpl(int prepare, int nPoints, double epsilon, double lambda) {
        this.prepare = prepare;
        this.nPoints = nPoints;
        this.epsilon = epsilon;
        this.lambda = lambda;
    }

    @Override
    public Collection<Double> getAllLimits(Function<Double> function, double start) {
        for (int i = 0; i < prepare; i++)
            start -= function.getValue(start) * lambda;
        NavigableSet<Double> result = new TreeSet<>();
        for (int i = 0; i < nPoints; i++) {
            start -= function.getValue(start) * lambda;
            Double nearby = result.ceiling(start);
            if (nearby != null && nearby - start < epsilon)
                continue;
            nearby = result.floor(start);
            if (nearby != null && start - nearby < epsilon)
                continue;
            result.add(start);
        }
        return result;
    }
}
