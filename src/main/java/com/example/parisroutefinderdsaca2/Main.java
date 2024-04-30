package com.example.parisroutefinderdsaca2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {

    private double xOffset = 0;
    private double yOffset = 0;

    public static Stage mainStage;
    public static Scene mainPage, secondPage;

    @Override
    public void start(Stage primaryStage) throws IOException {
        mainStage = primaryStage; // Assign the primaryStage to the static mainStage variable

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("dashboard.fxml"));
        mainPage = new Scene(fxmlLoader.load(), 1550, 950);


        mainPage.getStylesheets().add(Objects.requireNonNull(getClass().getResource("stylesheet.css")).toExternalForm()); //TODO change when done
        mainStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("icon1.png"))));
        mainStage.setTitle("PARIS CITY ROUTE FINDER");
        mainStage.setScene(mainPage);
        mainStage.initStyle(StageStyle.TRANSPARENT);
        mainStage.centerOnScreen();
        mainStage.setResizable(false);
        mainStage.show();

        // Make the stage draggable
        AnchorPane root = (AnchorPane) mainPage.getRoot();
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            mainStage.setX(event.getScreenX() - xOffset);
            mainStage.setY(event.getScreenY() - yOffset);
        });
    }

    public static void switchToSecondScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("firstPage.fxml")); //TODO change when done
        secondPage = new Scene(fxmlLoader.load(), 1630, 900);
        secondPage.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("stylesheet.css")).toExternalForm());

        mainStage.setScene(secondPage);
        mainStage.centerOnScreen();
        RouteFinder.routeFinder.populateDatabase();
    }

    public static void main(String[] args) {
        launch();
    }
}
