package ru.ifmo.ctddev.segal.hw1.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

/**
 * @author Daniyar Itegulov
 */
public class MainController {

    @FXML
    private Button button;

    @FXML
    public void initialize(){
        button.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> button.setText("Pressed"));
    }
}
