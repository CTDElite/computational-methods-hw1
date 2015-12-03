package ru.ifmo.ctddev.segal.hw1.ui.controller;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.NumberAxis;
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
        private static final double MIN_Y = -20.0D;
        private static final double MAX_Y = 20.0D;

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
            Axes axes = new Axes((int) canvas.getHeight(), (int) canvas.getWidth(),
                    MIN_X, MAX_X, 1, MIN_Y, MAX_Y, 1);
            stackPane.getChildren().add(axes);
            buildButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                r = Double.parseDouble(rTextField.getText());
                start = Double.parseDouble(stTextField.getText());

                Iterator<Double> current = simpleIters.applyMethod(rFun, start);

                int height = (int) canvas.getHeight();
                int width = (int) canvas.getWidth();

                GraphicsContext gc = canvas.getGraphicsContext2D();

                gc.clearRect(0, 0, width, height);
                int x = 0;
                Double prevX = (double)0;
                Double prevY = current.next();
                for (int i = 0; i < 20; i++) {
                    Double curY = current.next();
                    x++;
                    Double curX = (double)x;
                    System.out.format("[%d] (%f, %f)", i, prevY, curY);
                    Double newValue = (double)x;
                    gc.strokeLine(
                            mapX(prevX + 6.75, axes),
                            mapY(prevY + 4.5, axes),
                            mapX(curX + 6.75, axes),
                            mapY(curY + 4.5, axes));
                    prevX = curX;
                    prevY = curY;
                }
                System.out.println("Printed path on chart");
            });
        }

        private double mapX(double x, Axes axes) {
            double tx = axes.getPrefWidth() / 2;
            double sx = axes.getPrefWidth() /
                    (axes.getXAxis().getUpperBound() -
                            axes.getXAxis().getLowerBound());

            return x * sx + tx;
        }

        private double mapY(double y, Axes axes) {
            double ty = axes.getPrefHeight() / 2;
            double sy = axes.getPrefHeight() /
                    (axes.getYAxis().getUpperBound() -
                            axes.getYAxis().getLowerBound());

            return -y * sy + ty;
        }

        private static class Axes extends Pane {
            private NumberAxis xAxis;
            private NumberAxis yAxis;

            public Axes(
                    int width, int height,
                    double xLow, double xHi, double xTickUnit,
                    double yLow, double yHi, double yTickUnit) {
                setMinSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
                setPrefSize(width, height);
                setMaxSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);

                xAxis = new NumberAxis(xLow, xHi, xTickUnit);
                xAxis.setSide(Side.BOTTOM);
                xAxis.setMinorTickVisible(false);
                xAxis.setPrefWidth(width);
                xAxis.setLayoutY(height / 2);

                yAxis = new NumberAxis(yLow, yHi, yTickUnit);
                yAxis.setSide(Side.LEFT);
                yAxis.setMinorTickVisible(false);
                yAxis.setPrefHeight(height);
                yAxis.layoutXProperty().bind(
                        Bindings.subtract(
                                (width / 2) + 1,
                                yAxis.widthProperty()
                        )
                );

                getChildren().setAll(xAxis, yAxis);
            }

            public NumberAxis getXAxis() {
                return xAxis;
            }

            public NumberAxis getYAxis() {
                return yAxis;
            }
        }
}

