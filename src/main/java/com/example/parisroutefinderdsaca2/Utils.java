package com.example.parisroutefinderdsaca2;

import javafx.scene.control.Alert;

public class Utils {


    public static boolean intValidRange(int numberToCheck, int min, int max) {
        return ((numberToCheck >= min) && (numberToCheck <= max));
    }

    /*Creates GUI alerts for exceptions*/
    public static void showWarningAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();

    }

    public static void showInformationAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        alert.showAndWait();
    }
}


