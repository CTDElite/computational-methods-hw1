package ru.ifmo.ctddev.segal.hw1.ui.controller;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.apache.commons.math3.complex.Complex;
import ru.ifmo.ctddev.segal.hw1.algorithm.SimpleIterations;
import ru.ifmo.ctddev.segal.hw1.algorithm.SimpleIterationsImpl;
import ru.ifmo.ctddev.segal.hw1.model.ComplexDifferentiableFunction;
import ru.ifmo.ctddev.segal.hw1.model.Function;
import ru.ifmo.ctddev.segal.hw1.model.RealFunction;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;

/**
 * Created by Daria on 03.12.2015.
 */
public class SimpleIterationsController {
    private static final double EPS = 1e-6;

    private static final double MIN_X = -20.0D;
    private static final double MAX_X = 20.0D;
    private static final double MIN_Y = -2.0D;
    private static final double MAX_Y = 2.0D;

    @FXML
    public TextField rTextField;

    @FXML
    public TextField stTextField;

    @FXML
    public Label rLabel;

    @FXML
    public Label stLabel;

    @FXML
    public Button buildButton;

    @FXML
    public Canvas canvas;

    @FXML
    public StackPane stackPane;

    private double r;
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

        final NumberAxis xAxis = new NumberAxis(MIN_X, MAX_X, 1);
        final NumberAxis yAxis = new NumberAxis(MIN_Y, MAX_Y, 0.1);
        final XYChart<Number, Number> sc = new
                LineChart<Number, Number>(xAxis, yAxis);
        xAxis.setLabel("x_n");
        yAxis.setLabel("n");
        sc.setTitle("Simple Iterations");

        XYChart.Series series1 = new XYChart.Series();
        sc.getData().add(series1);
        sc.getStylesheets().add("Chart.css");
        stackPane.getChildren().add(sc);

        buildButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            r = Double.parseDouble(rTextField.getText());
            start = Double.parseDouble(stTextField.getText());

            series1.getData().clear();

            Iterator<Double> current = simpleIters.applyMethod(rFun, start);

            int x = 0;
            for (int i = 0; i < 20; i++) {
                Double curY = current.next();
                Double curX = (double) x;
//                    System.out.format("[%d] (%f, %f)", i, prevY, curY);
                series1.getData().add(new XYChart.Data(curX, curY));
                x++;
            }

            System.out.println("Printed path on chart");
        });
    }
}