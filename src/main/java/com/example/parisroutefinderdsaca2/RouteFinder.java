package com.example.parisroutefinderdsaca2;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;

import java.io.IOException;

public class RouteFinder {

    public ImageView mapView;

    @FXML
    public void scene2() throws IOException {
        Main.switchToSecondScene();
    }
}