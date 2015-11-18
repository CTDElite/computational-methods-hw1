package ru.ifmo.ctddev.segal.hw1.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.util.Pair;
import ru.ifmo.ctddev.segal.hw1.algorithm.NewtonMethod;
import ru.ifmo.ctddev.segal.hw1.algorithm.NewtonMethodImpl;
import ru.ifmo.ctddev.segal.hw1.model.ComplexDifferentiableFunction;

/**
 * @author Daniyar Itegulov
 */
public class NewtonConvergenceController {

    private static final double EPS = 1e-6;

    private static final double MIN_X = -100.0D;
    private static final double MAX_X = 100.0D;
    private static final double MIN_Y = -100.0D;
    private static final double MAX_Y = 100.0D;

    private NewtonMethod newtonMethod = new NewtonMethodImpl();

    private ComplexDifferentiableFunction zCube = new ComplexDifferentiableFunction() {
        @Override
        public Complex getValue(Complex x) {
            return x.pow(3).subtract(Complex.ONE);
        }

        @Override
        public Complex getDerivativeValue(Complex x) {
            return x.pow(2).multiply(3);
        }
    };

    @FXML
    public ImageView mainChart;

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() {
        int height = (int) mainChart.getFitHeight();
        int width = (int) mainChart.getFitWidth();
        System.out.format("Image size is %dx%d\n", width, height);
        double xStep = (MAX_X - MIN_X) / width;
        double yStep = (MAX_Y - MIN_Y) / height;
        System.out.format("Step on `x` is %f, step of `y` is %f\n", xStep, yStep);
        WritableImage writableImage = new WritableImage(width, height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        Pair<Complex, Integer>[][] results = new Pair[height][width];
        double max = 0.0D;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                double x = MIN_X + xStep * j;
                double y = MIN_Y + yStep * i;
                results[i][j] = newtonMethod.getRoot(zCube, new Complex(x, y));
                max = Math.max(max, results[i][j].getSecond());
            }
        }
        max += 2;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Pair<Complex, Integer> result = results[i][j];
                if (result == null) {
                    pixelWriter.setColor(j, i, Color.WHITE);
                } else if (result.getFirst().subtract(Complex.ONE).abs() < EPS) {
                    pixelWriter.setColor(j, i, Color.hsb(50, 0.5, 1 - result.getSecond() / max));
                } else if (result.getFirst().subtract(new Complex(-0.5, Math.sqrt(3) / 2)).abs() < EPS) {
                    pixelWriter.setColor(j, i, Color.hsb(150, 0.75, 1 - result.getSecond() / max));
                } else if (result.getFirst().subtract(new Complex(-0.5, -Math.sqrt(3) / 2)).abs() < EPS) {
                    pixelWriter.setColor(j, i, Color.hsb(200, 1.0, 1 - result.getSecond() / max));
                } else {
                    throw new IllegalStateException("");
                }
            }
        }
        mainChart.setImage(writableImage);
    }
}
