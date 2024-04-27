package com.example.parisroutefinderdsaca2;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.Objects;

public class BWConverter {

    private static int[] pixelArray;  //I DON'T KNOW IF WE NEED THESE PUBLIC OR INLINE YET FOR BFS

    public static int[] getPixelArray() {
        return pixelArray;
    }

    public static final Color roadColor = Color.rgb(255, 254, 255);

    public static Image BWImage;



    public static Image convert() {
        int width = 750;
        int height = 500;
        WritableImage convertedMap = new WritableImage(width, height);
        PixelWriter pw = convertedMap.getPixelWriter();

        // Load the image from resources
        Image mapViewImage = new Image(Objects.requireNonNull(BWConverter.class.getResourceAsStream("greyMap.png")));
        PixelReader pr = mapViewImage.getPixelReader();

        pixelArray = new int[width * height];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Color pixelColor = pr.getColor(col, row);

                if (isColorInRange(pixelColor)) {
                    pw.setColor(col, row, Color.WHITE); // WHITE IF COLOR MATCHES ROAD
                    pixelArray[row * width + col] = 0;
                } else {
                    pw.setColor(col, row, Color.BLACK); // Set black otherwise
                    pixelArray[row * width + col] = -1;
                }
            }
        }
        BWImage = convertedMap;
        return BWImage;
    }

    private static boolean isColorInRange(Color color) {
        int redDiff = Math.abs((int) (color.getRed() * 255) - (int) (BWConverter.roadColor.getRed() * 255));
        int greenDiff = Math.abs((int) (color.getGreen() * 255) - (int) (BWConverter.roadColor.getGreen() * 255));
        int blueDiff = Math.abs((int) (color.getBlue() * 255) - (int) (BWConverter.roadColor.getBlue() * 255));

        return (redDiff <= 28 && greenDiff <= 28 && blueDiff <= 28);
    }

































//    public static void convert() {
//        int width = 750;
//        int height = 500;
//        WritableImage convertedMap = new WritableImage(width, height);
//        PixelWriter pw = convertedMap.getPixelWriter();
//
//        // Load the image from resources
//        Image mapViewImage = new Image(Objects.requireNonNull(BWConverter.class.getResourceAsStream("greyMap.png")));
//        PixelReader pr = mapViewImage.getPixelReader();
//
//        pixelArray = new int[width * height];
//
//         final Color ROAD_COLOR = Color.rgb(252, 252, 252);
//
//        for (int row = 0; row < height; row++) {
//            for (int col = 0; col < width; col++) {
//                Color pixelColor = pr.getColor(col, row);
//
//                if (isColorInRange(pixelColor, ROAD_COLOR)) {
//                    pw.setColor(col, row, Color.WHITE); // WHITE IF COLOR MATCHES ROAD
//                    pixelArray[row * width + col] = 0;
//                } else {
//                    pw.setColor(col, row, Color.BLACK); // Set black otherwise
//                    pixelArray[row * width + col] = -1;
//                }
//            }
//        }
//        BWImage = convertedMap;
//        routeFinder.mapView.setImage(BWImage);
//    }
//    private static boolean isColorInRange(Color color, Color targetColor) {
//        int redDiff = Math.abs((int) (color.getRed() * 255) - (int) (targetColor.getRed() * 255));
//        int greenDiff = Math.abs((int) (color.getGreen() * 255) - (int) (targetColor.getGreen() * 255));
//        int blueDiff = Math.abs((int) (color.getBlue() * 255) - (int) (targetColor.getBlue() * 255));
//
//        return (redDiff <= 28 && greenDiff <= 28 && blueDiff <= 28);
//    }
}




