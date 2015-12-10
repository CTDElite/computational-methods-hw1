package ru.ifmo.ctddev.segal.hw1.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import ru.ifmo.ctddev.segal.hw1.algorithm.SimpleIterations;
import ru.ifmo.ctddev.segal.hw1.algorithm.SimpleIterationsImpl;
import ru.ifmo.ctddev.segal.hw1.model.Function;
import ru.ifmo.ctddev.segal.hw1.model.RealFunction;

import java.util.Collection;

/**
 * Created by Daria on 03.12.2015.
 */
public class SimpleIterationsLimitsController {
    private static final double EPS = 1e-6;

    private static final double MIN_X = 0;
    private static final double MAX_X = 4.2D;
    private static final double MIN_Y = 0;
    private static final double MAX_Y = 1;

    @FXML
    public TextField rTextField;

    @FXML
    public TextField stTextField;

    @FXML
    public Button buildButton;

    @FXML
    public StackPane stackPane;

    private double r;
    private double limitR;
    private double start;

    private SimpleIterations simpleIters = new SimpleIterationsImpl(100, 100, EPS, -1.0);

    private Function<Double> rFun = new RealFunction() {
        @Override
        public Double getValue(Double x) {
            return r * x * (1 - x) - x;
        }

        public double getDerivativeSignAround(double x) {
            return r * (1 - 2 * x) - 1;
        }
    };

    @FXML
    public ImageView mainChart;

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() {
        final NumberAxis xAxis = new NumberAxis(MIN_X, MAX_X, 0.1);
        final NumberAxis yAxis = new NumberAxis(MIN_Y, MAX_Y, 0.1);
        final XYChart<Number, Number> sc = new
                ScatterChart<>(xAxis, yAxis);
        xAxis.setLabel("r");
        yAxis.setLabel("limit");
        sc.setTitle("Simple Iterations Limits");

        XYChart.Series series1 = new XYChart.Series();
        sc.getData().add(series1);
        sc.getStylesheets().add("Chart.css");
        stackPane.getChildren().add(sc);
        buildButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            limitR = Double.parseDouble(rTextField.getText());
            start = Double.parseDouble(stTextField.getText());
            series1.getData().clear();
            for (double rr = 0; rr <= limitR; rr += 0.02) {
                r = rr;
                Collection<Double> current = simpleIters.getAllLimits(rFun, start);
                for (Double curY : current) {
                    Double curX = r;
                    series1.getData().add(new XYChart.Data(curX, curY));
                }
            }
        });
    }
}
