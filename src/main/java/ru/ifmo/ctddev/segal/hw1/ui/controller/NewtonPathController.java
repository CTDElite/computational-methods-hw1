package ru.ifmo.ctddev.segal.hw1.ui.controller;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.apache.commons.math3.complex.Complex;
import ru.ifmo.ctddev.segal.hw1.algorithm.NewtonMethod;
import ru.ifmo.ctddev.segal.hw1.algorithm.NewtonMethodImpl;
import ru.ifmo.ctddev.segal.hw1.model.ComplexDifferentiableFunction;

import java.util.List;

/**
 * @author Daniyar Itegulov
 */
public class NewtonPathController {

    private static final double EPS = 1e-6;

    private static final double MIN_X = -3.0D;
    private static final double MAX_X = 3.0D;
    private static final double MIN_Y = -3.0D;
    private static final double MAX_Y = 3.0D;

    @FXML
    public TextField xTextField;

    @FXML
    public TextField yTextField;

    @FXML
    public Button buildButton;

    @FXML
    public Canvas canvas;

    @FXML
    public StackPane stackPane;

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
        Axes axes = new Axes((int) canvas.getHeight(), (int) canvas.getWidth(),
                MIN_X, MAX_X, 1, MIN_Y, MAX_Y, 1);
        stackPane.getChildren().add(axes);
        buildButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            double beginX = Double.parseDouble(xTextField.getText());
            double beginY = Double.parseDouble(yTextField.getText());

            List<Complex> complexList = newtonMethod.getPath(zCube, new Complex(beginX, beginY));

            int height = (int) canvas.getHeight();
            int width = (int) canvas.getWidth();

            GraphicsContext gc = canvas.getGraphicsContext2D();

            gc.clearRect(0, 0, width, height);

            for (int i = 1; i < complexList.size(); i++) {
                Complex oldComplex = complexList.get(i - 1);
                Complex newComplex = complexList.get(i);
                gc.strokeLine(
                        mapX(oldComplex.getReal() + 1, axes),
                        mapY(oldComplex.getImaginary() + 0.75, axes),
                        mapX(newComplex.getReal() + 1, axes),
                        mapY(newComplex.getImaginary() + 0.75, axes));
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
