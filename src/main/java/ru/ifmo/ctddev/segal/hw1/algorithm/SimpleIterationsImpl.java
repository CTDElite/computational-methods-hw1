package ru.ifmo.ctddev.segal.hw1.algorithm;

import ru.ifmo.ctddev.segal.hw1.model.Function;

import java.util.Collection;
import java.util.Iterator;
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
    public Iterator<Double> applyMethod(Function<Double> x, double start) {
        return new Iterator<Double>() {
            double current = start;

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public Double next() {
                double retv = current;
                current -= x.getValue(current) * lambda;
                return retv;
            }
        };
    }

    @Override
    public Double getSlicedLimit(Function<Double> x, double start, int fIndex, int step) {
        Iterator<Double> iterator = applyMethod(x, start);
        for (int i = 0; i < fIndex + step * prepare; i++)
            iterator.next();
        double xCur = iterator.next();
        double xNext = xCur;
        for (int i = 0; i < step; i++)
            xNext = iterator.next();
        return Math.abs(xNext - xCur) < epsilon ? xNext : null;
    }

    @Override
    public Collection<Double> getAllLimits(Function<Double> function, double start) {
        Iterator<Double> processed = applyMethod(function, start);
        for (int i = 0; i < prepare; i++)
            processed.next();
        NavigableSet<Double> result = new TreeSet<>();
        for (int i = 0; i < nPoints; i++) {
            start = processed.next();
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
