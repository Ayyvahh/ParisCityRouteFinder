package com.example.parisroutefinderdsaca2;

import javafx.scene.control.Alert;

public class Utils {
    /*Creates GUI alerts for exceptions*/
    public static void showWarningAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();

    }
}


