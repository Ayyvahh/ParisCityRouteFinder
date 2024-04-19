package com.example.parisroutefinderdsaca2;

import com.example.parisroutefinderdsaca2.DataStructure.Graph;
import com.example.parisroutefinderdsaca2.DataStructure.GraphLink;
import com.example.parisroutefinderdsaca2.DataStructure.GraphNode;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import static com.example.parisroutefinderdsaca2.DataStructure.Graph.findCheapestPathDijkstra;

public class RouteFinder implements Initializable {
    public static RouteFinder routeFinder;

    /*-----------JAVAFX------------*/

    public AnchorPane mapPane = new AnchorPane();
    public ListView<GraphNode<String>> currentWaypoints;
    public Slider culturalSlid;
    public ComboBox<GraphNode<String>> startPointBox;
    public ComboBox<GraphNode<String>> avoidBox;
    public ComboBox<GraphNode<String>> endPointBox;
    public Slider prefSlid; /* User preference of how scenic the chosen route is*/

    /*-----------------------------*/
    Graph graph = new Graph();
    @FXML
    public ImageView mapView;
    private boolean isMapPopulated = false;
    public Map<String, GraphNode<String>> graphNodes = new HashMap<>();
    private Tooltip pillTip;

    @FXML
    public void scene2() throws IOException {
        Main.switchToSecondScene();
    }

    public void mapClicked(MouseEvent event) {
        System.out.println((int) event.getX() + ", " + (int) event.getY());
    }

    public void saveXML() throws Exception {
        XStream xstream = new XStream(new DomDriver());
        ObjectOutputStream out = xstream.createObjectOutputStream(new FileWriter("graphNodes.xml"));
        out.writeObject(graphNodes);
        out.close();
    }
    @SuppressWarnings("unchecked") /*warning was annoying me lol*/
    public void loadXML() throws Exception {
        Class<?>[] classes = new Class[] {GraphNode.class, GraphLink.class};

        XStream xstream = new XStream(new DomDriver());
        XStream.setupDefaultSecurity(xstream);
        xstream.allowTypes(classes);

        ObjectInputStream is = xstream.createObjectInputStream(new FileReader("graphNodes.xml"));
        graphNodes = (Map<String, GraphNode<String>>) is.readObject();
        is.close();
    }

    public void populateMap() {
        if (!isMapPopulated) {
            for (GraphNode<String> node : graphNodes.values()) {
                drawNode(node);
                startPointBox.getItems().add(node);
                endPointBox.getItems().add(node);
                avoidBox.getItems().add(node);
            }
            isMapPopulated = true;
        } else {
            System.out.println("Map already populated!");
        }
    }

    private void drawNode(GraphNode<String> node) {
        Circle nodeCircle = new Circle();
        Circle nodeRing = new Circle();

        if (node.isLandmark()) {
            nodeCircle.setCenterX(node.getGraphX());
            nodeCircle.setCenterY(node.getGraphY());
            nodeCircle.setRadius(5);
            nodeCircle.setFill(Color.RED);

            nodeRing.setCenterX(node.getGraphX());
            nodeRing.setCenterY(node.getGraphY());
            nodeRing.setRadius(8);
            nodeRing.setFill(Color.TRANSPARENT);
            nodeRing.setStrokeWidth(2);
            nodeRing.setStroke(Color.RED);
        } else {
            nodeCircle.setCenterX(node.getGraphX());
            nodeCircle.setCenterY(node.getGraphY());
            nodeCircle.setRadius(5);
            nodeCircle.setFill(Color.BLACK);
        }

        mapPane.getChildren().addAll(nodeCircle, nodeRing);
    }

    public void toolTipHover(@NotNull MouseEvent e) {
        double mouseX = e.getX();
        double mouseY = e.getY();

        boolean onLandmark = false;
        GraphNode<String> landmarkOn = null;

        for (GraphNode<String> g : graphNodes.values()) {
            if (g.isLandmark()) {
                if (Math.abs(mouseX - g.getGraphX()) <= 3 && Math.abs(mouseY - g.getGraphY()) <= 3) {
                    onLandmark = true;
                    landmarkOn = g;
                    break;
                }
            }
        }

        if (onLandmark) {
            Main.secondPage.setCursor(Cursor.HAND);
            pillTip.setText("Name: " + landmarkOn.getName() + ",\nLandmark X: " + landmarkOn.getGraphX() +
                    ",\nLandmark Y: " + landmarkOn.getGraphY());
            pillTip.show(mapView, e.getScreenX() + 10, e.getScreenY() + 10);
        } else {
            pillTip.hide();
            Main.secondPage.setCursor(Cursor.DEFAULT);
        }
    }

    public void dijkstraTest() {
        graphNodes.put("ET", new GraphNode<>("Eiffel Tower", true,5,126,377)); //A
        graphNodes.put("AdT", new GraphNode<>("Arc de Triomphe", true,5,80,220)); //B
        graphNodes.put("TL", new GraphNode<>("The Louvre", true,4,133,78)); //C
        graphNodes.put("GP", new GraphNode<>("Grand Palais", true,4,275,285)); //D
        graphNodes.put("NDC", new GraphNode<>("Notre-Dame Cathedral", true,5,309,270)); //E
        graphNodes.put("CE", new GraphNode<>("Champs-Élysées", true,3,358,192)); //F
        graphNodes.put("OG", new GraphNode<>("Opera Garnier", true,2,342,349)); //G
        graphNodes.put("PdlC", new GraphNode<>("Place de la Concorde", true,5,411,436)); //H
        graphNodes.put("RS", new GraphNode<>("River Seine", true,4,349,77)); //I
        graphNodes.put("BotSC", new GraphNode<>("Basilica of the Sacré-Coeur", true,3,447,31)); //J
        graphNodes.put("TCP", new GraphNode<>("The Centre Pompidou", true,1,505,355)); //K
        graphNodes.put("PAIII", new GraphNode<>("Pont Alexandre III", true,2,659,326)); //L
        graphNodes.put("MdO", new GraphNode<>("Musée d’Orsay", true,1,532,248)); //M

        graphNodes.put("N1", new GraphNode<>("N1", false, 0,108,370));
        graphNodes.put("N2", new GraphNode<>("N2", false, 0,93,363));
        graphNodes.put("N3", new GraphNode<>("N3", false, 0,85,278));
        graphNodes.put("N4", new GraphNode<>("N4", false, 0,73,270));
        graphNodes.put("N5", new GraphNode<>("N5", false, 0,96,236));

        graphNodes.get("N1").connectToNodeUndirected(graphNodes.get("ET"), (int) Math.sqrt(Math.pow(graphNodes.get("ET").getGraphX() - graphNodes.get("N1").getGraphX(), 2) + Math.pow(graphNodes.get("ET").getGraphY() - graphNodes.get("N1").getGraphY(), 2)));
        graphNodes.get("N2").connectToNodeUndirected(graphNodes.get("N1"), (int) Math.sqrt(Math.pow(graphNodes.get("N1").getGraphX() - graphNodes.get("N2").getGraphX(), 2) + Math.pow(graphNodes.get("N1").getGraphY() - graphNodes.get("N2").getGraphY(), 2)));
        graphNodes.get("N3").connectToNodeUndirected(graphNodes.get("N2"), (int) Math.sqrt(Math.pow(graphNodes.get("N2").getGraphX() - graphNodes.get("N3").getGraphX(), 2) + Math.pow(graphNodes.get("N2").getGraphY() - graphNodes.get("N3").getGraphY(), 2)));
        graphNodes.get("N4").connectToNodeUndirected(graphNodes.get("N3"), (int) Math.sqrt(Math.pow(graphNodes.get("N3").getGraphX() - graphNodes.get("N4").getGraphX(), 2) + Math.pow(graphNodes.get("N3").getGraphY() - graphNodes.get("N4").getGraphY(), 2)));
        graphNodes.get("N5").connectToNodeUndirected(graphNodes.get("N4"), (int) Math.sqrt(Math.pow(graphNodes.get("N4").getGraphX() - graphNodes.get("N5").getGraphX(), 2) + Math.pow(graphNodes.get("N4").getGraphY() - graphNodes.get("N5").getGraphY(), 2)));
        graphNodes.get("AdT").connectToNodeUndirected(graphNodes.get("N5"), (int) Math.sqrt(Math.pow(graphNodes.get("AdT").getGraphX() - graphNodes.get("N5").getGraphX(), 2) + Math.pow(graphNodes.get("AdT").getGraphY() - graphNodes.get("N5").getGraphY(), 2)));


//        graphNodes.get("ET").connectToNodeUndirected(graphNodes.get("AdT"), 5); //adding streets between landmarks/junctions
//        graphNodes.get("ET").connectToNodeUndirected(graphNodes.get("TL"), 9);
//        graphNodes.get("AdT").connectToNodeUndirected(graphNodes.get("TL"), 2);
//        graphNodes.get("AdT").connectToNodeUndirected(graphNodes.get("GP"), 6);
//        graphNodes.get("TL").connectToNodeUndirected(graphNodes.get("NDC"), 5);
//        graphNodes.get("GP").connectToNodeUndirected(graphNodes.get("PdlC"), 8);
//        graphNodes.get("GP").connectToNodeUndirected(graphNodes.get("OG"), 9);
//        graphNodes.get("NDC").connectToNodeUndirected(graphNodes.get("OG"), 3);
//        graphNodes.get("NDC").connectToNodeUndirected(graphNodes.get("CE"), 7);
//        graphNodes.get("CE").connectToNodeUndirected(graphNodes.get("OG"), 6);
//        graphNodes.get("OG").connectToNodeUndirected(graphNodes.get("PdlC"), 2);
//        graphNodes.get("PdlC").connectToNodeUndirected(graphNodes.get("RS"), 5);
//        graphNodes.get("RS").connectToNodeUndirected(graphNodes.get("BotSC"), 3);
//        graphNodes.get("BotSC").connectToNodeUndirected(graphNodes.get("TCP"), 1);
//        graphNodes.get("TCP").connectToNodeUndirected(graphNodes.get("PAIII"), 3);
//        graphNodes.get("PAIII").connectToNodeUndirected(graphNodes.get("MdO"), 2);
//
        try {
            saveXML();
            System.out.println("Database saved!");
        } catch (Exception e) {
            System.err.println("Error writing from file: " + e);
        }

        try {
            loadXML();
            System.out.println("Database loaded!");

            System.out.println("The cheapest path from Eiffel Tower to Arc de Triomphe");
            System.out.println("using Dijkstra's algorithm:");
            System.out.println("-------------------------------------");

            Graph.CostedPath cpa = findCheapestPathDijkstra(graphNodes.get("ET"), "Arc de Triomphe");

            assert cpa != null;
            for (GraphNode<?> n : cpa.pathList)
                System.out.println(n.name);
            System.out.println("\nThe total path cost is: " + cpa.pathCost);

            for (int i = 0; i < cpa.pathList.size()-1; i++) {
                    GraphNode<?> firstNode = cpa.pathList.get(i);
                    GraphNode<?> secondNode = cpa.pathList.get(i+1);

                    Line line = new Line(firstNode.getGraphX(), firstNode.getGraphY(), secondNode.getGraphX(), secondNode.getGraphY());
                    line.setStroke(Color.BLUEVIOLET);
                    line.setStrokeWidth(3);

                    mapPane.getChildren().add(line);
            }

            populateMap();
        } catch (Exception e) {
            System.err.println("Error writing from file: " + e);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        routeFinder = this;
        pillTip = new Tooltip("TEST");
        mapPane.setOnMouseMoved(this::toolTipHover);
    }
}