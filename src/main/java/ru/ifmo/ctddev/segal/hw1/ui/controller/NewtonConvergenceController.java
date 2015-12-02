package ru.ifmo.ctddev.segal.hw1.ui.controller;

import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ObservableValue;
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
import org.reactfx.Change;
import org.reactfx.EventSource;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;
import org.reactfx.util.Tuple3;
import org.reactfx.util.Tuples;
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

    volatile int height;
    volatile int width;
    volatile double xStep;
    volatile double yStep;
    volatile WritableImage writableImage;
    volatile PixelWriter pixelWriter;

    volatile ComplexZPowN zPowN;
    volatile List<Complex> roots;
    volatile List<Double> hues;

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
        EventSource<Tuple3<Integer, Integer, Pair<Complex, Integer>>> newtonResults = new EventSource<>();
        clicks.subscribe(e -> {
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    double x = MIN_X + xStep * j;
                    double y = MIN_Y + yStep * i;
                    Pair<Complex, Integer> result = newtonMethod.getRoot(zPowN, new Complex(x, y));
                    newtonResults.push(Tuples.t(j, i, result));
                }
            }
            mainChart.setImage(writableImage);
        });

        newtonResults.subscribe(e -> {
            int x = e._1;
            int y = e._2;
            Pair<Complex, Integer> result = e._3;

            if (result == null) {
                pixelWriter.setColor(x, y, Color.WHITE);
            } else {
                pixelWriter.setColor(x, y, Color.WHITE);
                for (int k = 0; k < roots.size(); k++) {
                    if (result.getFirst().subtract(roots.get(k)).abs() < EPS) {
                        pixelWriter.setColor(x, y, Color.hsb(hues.get(k), 0.65, Math.max(0.1D, 1 - result.getSecond() / 50.0D)));
                        break;
                    }
                }
            }
        });
    }
}
