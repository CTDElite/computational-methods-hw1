package ru.ifmo.ctddev.segal.hw1.ui.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Spinner;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.util.Pair;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;
import ru.ifmo.ctddev.segal.hw1.algorithm.NewtonMethod;
import ru.ifmo.ctddev.segal.hw1.algorithm.NewtonMethodImpl;
import ru.ifmo.ctddev.segal.hw1.model.ComplexZPowN;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Daniyar Itegulov
 */
public class NewtonConvergenceController {

    private static final double EPS = 1e-6;

    private static final double MIN_X = -10.0D;
    private static final double MAX_X = 10.0D;
    private static final double MIN_Y = -10.0D;
    private static final double MAX_Y = 10.0D;

    private static final int MAX_CONVERGENCE_STEPS = 10;
    public static final double DEFAULT_SATURATION = 0.55;

    @FXML
    public Spinner<Integer> power;

    @FXML
    public Button buildButton;

    private NewtonMethod newtonMethod = new NewtonMethodImpl();

    @FXML
    public ImageView mainChart;

    @FXML
    public ProgressBar progressBar;

    volatile int height;
    volatile int width;
    volatile double xStep;
    volatile double yStep;
    volatile WritableImage writableImage;
    volatile PixelWriter pixelWriter;

    volatile ComplexZPowN zPowN;
    volatile List<Complex> roots;
    volatile List<Double> hues;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() {
        EventStream<Number> widthChange = EventStreams.valuesOf(mainChart.fitWidthProperty());
        EventStream<Number> heightChange = EventStreams.valuesOf(mainChart.fitHeightProperty());
        EventStream<Number> sizeChange = EventStreams.merge(widthChange, heightChange);

        sizeChange.subscribe(e -> {
            System.out.println("ImageView was resized");
            height = (int) mainChart.getFitHeight();
            width = (int) mainChart.getFitWidth();
            System.out.format("Image size is %dx%d\n", width, height);
            xStep = (MAX_X - MIN_X) / width;
            yStep = (MAX_Y - MIN_Y) / height;
            System.out.format("Step on `x` is %f, step of `y` is %f\n", xStep, yStep);
            writableImage = new WritableImage(width, height);
            pixelWriter = writableImage.getPixelWriter();
        });

        EventStream<Integer> powerChange = EventStreams.valuesOf(power.valueProperty());

        powerChange.subscribe(e -> {
            System.out.format("New power of z is %d\n", e);
            zPowN = new ComplexZPowN(power.getValue());
            roots = zPowN.getRoots();
            hues = new ArrayList<>();

            for (int i = 0; i < roots.size(); i++) {
                hues.add(i * 1.0D / roots.size() * 360.0D);
            }
        });

        EventStream<MouseEvent> clicks = EventStreams.eventsOf(buildButton, MouseEvent.MOUSE_CLICKED);
        Runnable draw = () -> {
            final int n = NewtonConvergenceController.this.power.getValue();
            final ComplexZPowN zPowN = NewtonConvergenceController.this.zPowN;
            final List<Complex> roots = NewtonConvergenceController.this.roots;
            final List<Double> hues = NewtonConvergenceController.this.hues;
            Platform.runLater(() -> mainChart.setImage(null));
            Pair<Complex, Integer>[][] results = new Pair[height][width];
            double max = 0;
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    double x = MIN_X + xStep * j;
                    double y = MIN_Y + yStep * i;
                    results[i][j] = newtonMethod.getRoot(zPowN, new Complex(x, y));
                    if (results[i][j] != null) {
                        if (results[i][j].getSecond() < MAX_CONVERGENCE_STEPS * n) {
                            max = Math.max(max, results[i][j].getSecond());
                        }
                    }
                }
                final int fi = i;
                Platform.runLater(() -> progressBar.setProgress((1.0D + fi) / height));
            }

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    Pair<Complex, Integer> result = results[i][j];
                    if (result == null) {
                        pixelWriter.setColor(j, i, Color.YELLOW);
                    } else {
                        for (int k = 0; k < roots.size(); k++) {
                            if (result.getFirst().subtract(roots.get(k)).abs() < EPS) {
                                if (result.getSecond() >= MAX_CONVERGENCE_STEPS * n) {
                                    pixelWriter.setColor(j, i, Color.hsb(hues.get(k), DEFAULT_SATURATION, 0.1D));
                                } else {
                                    pixelWriter.setColor(j, i,
                                            Color.hsb(hues.get(k), DEFAULT_SATURATION, 1.0D - result.getSecond() / max));
                                }
                                break;
                            }
                        }
                    }
                }
            }

            Platform.runLater(() -> mainChart.setImage(writableImage));
        };

        clicks.subscribe(e -> executorService.submit(draw));
    }
}
