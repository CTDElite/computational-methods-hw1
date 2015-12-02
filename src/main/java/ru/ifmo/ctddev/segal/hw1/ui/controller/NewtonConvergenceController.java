package ru.ifmo.ctddev.segal.hw1.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.util.Pair;
import ru.ifmo.ctddev.segal.hw1.algorithm.NewtonMethod;
import ru.ifmo.ctddev.segal.hw1.algorithm.NewtonMethodImpl;
import ru.ifmo.ctddev.segal.hw1.model.ComplexZPowN;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniyar Itegulov
 */
public class NewtonConvergenceController {

    private static final double EPS = 1e-6;

    private static final double MIN_X = -100.0D;
    private static final double MAX_X = 100.0D;
    private static final double MIN_Y = -100.0D;
    private static final double MAX_Y = 100.0D;

    @FXML
    public Spinner<Integer> power;

    @FXML
    public Button buildButton;

    private NewtonMethod newtonMethod = new NewtonMethodImpl();

    @FXML
    public ImageView mainChart;

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() {
        buildButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            ComplexZPowN zPowN = new ComplexZPowN(power.getValue());

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
                    results[i][j] = newtonMethod.getRoot(zPowN, new Complex(x, y));
                    if (results[i][j] != null) {
                        max = Math.max(max, results[i][j].getSecond());
                    }
                }
            }

            List<Complex> roots = zPowN.getRoots();
            List<Double> hues = new ArrayList<>();

            for (int i = 0; i < roots.size(); i++) {
                hues.add(i * 1.0D / roots.size() * 360.0D);
            }

            max += 2;
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    Pair<Complex, Integer> result = results[i][j];
                    if (result == null) {
                        pixelWriter.setColor(j, i, Color.WHITE);
                    } else {
                        pixelWriter.setColor(j, i, Color.WHITE);
                        for (int k = 0; k < roots.size(); k++) {
                            if (result.getFirst().subtract(roots.get(k)).abs() < EPS) {
                                pixelWriter.setColor(j, i, Color.hsb(hues.get(k), 0.69, 1 - result.getSecond() / max));
                                break;
                            }
                        }
                    }
                }
            }
            mainChart.setImage(writableImage);
        });
    }
}
