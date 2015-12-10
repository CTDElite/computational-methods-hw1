package ru.ifmo.ctddev.segal.hw1.ui.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Spinner;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.util.Pair;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;
import org.reactfx.Subscription;
import ru.ifmo.ctddev.segal.hw1.algorithm.NewtonMethod;
import ru.ifmo.ctddev.segal.hw1.algorithm.NewtonMethodImpl;
import ru.ifmo.ctddev.segal.hw1.model.ComplexDifferentiableFunction;
import ru.ifmo.ctddev.segal.hw1.model.ComplexZPowN;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Daniyar Itegulov, Ignat Loskutov
 */
public class NewtonConvergenceController {

    private static final double EPS = 1e-6;

    private static final double MIN_X = -10.0D;
    private static final double MAX_X = 10.0D;
    private static final double MIN_Y = -10.0D;
    private static final double MAX_Y = 10.0D;

    private static final int MAX_CONVERGENCE_STEPS = 10;
    public static final double DEFAULT_SATURATION = 2.0D/3;

    @FXML
    public Spinner<Integer> power;

    @FXML
    public Button buildButton;

    private NewtonMethod newtonMethod = new NewtonMethodImpl();

    @FXML
    public ImageView mainChart;

    @FXML
    public ProgressBar progressBar;

    @FXML
    public Canvas canvas;

    @FXML
    public StackPane stackPane;

    volatile int height;
    volatile int width;
    volatile double xStep;
    volatile double yStep;
    volatile WritableImage writableImage;
    volatile PixelWriter pixelWriter;

    volatile ComplexZPowN zPowN;
    volatile List<Complex> roots;
    volatile List<Double> hues;

    private Executor executor = Executors.newSingleThreadExecutor();
    private Subscription subscription;

    private Consumer<MouseEvent> pathDrawer(ComplexDifferentiableFunction f) {
        return e -> {
            double beginX = (-stackPane.getWidth() / 2 + e.getX()) * xStep;
            double beginY = (stackPane.getHeight() / 2 - e.getY()) * yStep;

            List<Point2D> coordList = newtonMethod.getPath(f, new Complex(beginX, beginY)).stream()
                    .map(this::mapZ).collect(Collectors.toList());

            GraphicsContext gc = canvas.getGraphicsContext2D();

            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.setLineWidth(2);

            for (int i = 1; i < coordList.size(); i++) {
                Point2D oldPoint = coordList.get(i - 1);
                Point2D newPoint = coordList.get(i);
                gc.strokeLine(oldPoint.getX(), oldPoint.getY(), newPoint.getX(), newPoint.getY());
            }
        };
    }

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
                hues.add(i * 360.0D / roots.size());
            }
        });

        EventStream<MouseEvent> buildClicks = EventStreams.eventsOf(buildButton, MouseEvent.MOUSE_CLICKED);
        EventStream<MouseEvent> canvasClicks = EventStreams.eventsOf(stackPane, MouseEvent.MOUSE_CLICKED);
        Axis<Number> xAxis = new NumberAxis(MIN_X, MAX_X, 1.0D);
        Axis<Number> yAxis = new NumberAxis(MIN_Y, MAX_Y, 1.0D);

        XYChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setVisible(false);
        Runnable draw = () -> {
            canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            chart.setVisible(false);
            final int n = power.getValue();
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
                        pixelWriter.setColor(j, i, Color.BLACK);
                    } else {
                        for (int k = 0; k < roots.size(); k++) {
                            if (result.getFirst().subtract(roots.get(k)).abs() < EPS) {
                                pixelWriter.setColor(j, i, Color.hsb(hues.get(k),  DEFAULT_SATURATION,
                                        (result.getSecond() < MAX_CONVERGENCE_STEPS * n)
                                                ? 1.0D - result.getSecond() / max
                                                : 0.1D)
                                );
                                break;
                            }
                        }
                    }
                }
            }

            Platform.runLater(() -> { mainChart.setImage(writableImage); chart.setVisible(true); });

            if (subscription != null) {
                subscription.unsubscribe();
            }
            subscription = canvasClicks.subscribe(pathDrawer(zPowN));
        };

        buildClicks.subscribe(e -> executor.execute(draw));



        chart.setPadding(new Insets(49, 60, 26, 30));

        chart.setHorizontalGridLinesVisible(false);
        chart.setVerticalGridLinesVisible(false);
        chart.getStylesheets().add("Newton.css");

        stackPane.getChildren().add(chart);
    }

    private double mapX(double x) {
        return canvas.getWidth() / 2 + x / xStep;
    }

    private double mapY(double y) {
        return canvas.getHeight() / 2 - y / yStep;
    }

    private Point2D mapZ(Complex z) {
        return new Point2D(mapX(z.getReal()), mapY(z.getImaginary()));
    }

}
