package com.example.parisroutefinderdsaca2;

import com.example.parisroutefinderdsaca2.DataStructure.Graph;
import com.example.parisroutefinderdsaca2.DataStructure.GraphLink;
import com.example.parisroutefinderdsaca2.DataStructure.GraphNode;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import static com.example.parisroutefinderdsaca2.DataStructure.Graph.findCheapestPathDijkstra;

public class RouteFinder implements Initializable {
    public static RouteFinder routeFinder;
    public AnchorPane mapPane;
    Graph graph = new Graph();
    @FXML
    public ImageView mapView;
    private boolean isMapPopulated = false;
    public Map<String, GraphNode<String>> graphNodes = new HashMap<>();

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
            }
            isMapPopulated = true;
        } else {
            System.out.println("Map already populated!");
        }
    }

    private void drawNode(GraphNode<String> node) {
        Circle nodeCircle = new Circle();
        Circle nodeRing = new Circle();

        nodeCircle.setCenterX(node.getGraphX());
        nodeCircle.setCenterY(node.getGraphY());
        nodeCircle.setRadius(5);

        nodeRing.setCenterX(node.getGraphX());
        nodeRing.setCenterY(node.getGraphY());
        nodeRing.setRadius(8);
        nodeRing.setFill(Color.TRANSPARENT);
        nodeRing.setStrokeWidth(2);

        if (node.isLandmark()) {
            nodeCircle.setFill(Color.RED);
            nodeRing.setStroke(Color.RED);
        } else {
            nodeCircle.setFill(Color.LAWNGREEN);
            nodeRing.setStroke(Color.LAWNGREEN);
        }

        mapPane.getChildren().addAll(nodeCircle, nodeRing);
    }

    public void dijkstraTest() {
//        graphNodes.put("ET", new GraphNode<>("Eiffel Tower", true,30,20)); //1,1 PLACEHOLDER, A
//        graphNodes.put("AdT", new GraphNode<>("Arc de Triomphe", true,20,50)); //B
//        graphNodes.put("TL", new GraphNode<>("The Louvre", true,213,250)); //C
//        graphNodes.put("GP", new GraphNode<>("Grand Palais", false,234,134)); //D
//        graphNodes.put("NDC", new GraphNode<>("Notre-Dame Cathedral", true,121,345)); //E
//        graphNodes.put("CE", new GraphNode<>("Champs-Élysées", true,112,444)); //F
//        graphNodes.put("OG", new GraphNode<>("Opera Garnier", false,444,324)); //G
//        graphNodes.put("PdlC", new GraphNode<>("Place de la Concorde", true,312,433)); //H
//        graphNodes.put("RS", new GraphNode<>("River Seine", true,493,123)); //I
//        graphNodes.put("BotSC", new GraphNode<>("Basilica of the Sacré-Coeur", true,479,268)); //J
//        graphNodes.put("TCP", new GraphNode<>("The Centre Pompidou", false,408,189)); //K
//        graphNodes.put("PAIII", new GraphNode<>("Pont Alexandre III", true,333,378)); //L
//        graphNodes.put("MdO", new GraphNode<>("Musée d’Orsay", true,468,498)); //M
//
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
//        try {
//            saveXML();
//        } catch (Exception e) {
//            System.err.println("Error writing from file: " + e);
//        }

        try {
            loadXML();

            System.out.println("The cheapest path from Eiffel Tower to Musée d’Orsay");
            System.out.println("using Dijkstra's algorithm:");
            System.out.println("-------------------------------------");

            Graph.CostedPath cpa = findCheapestPathDijkstra(graphNodes.get("ET"), "Musée d’Orsay");

            assert cpa != null;
            for (GraphNode<?> n : cpa.pathList)
                System.out.println(n.name);
            System.out.println("\nThe total path cost is: " + cpa.pathCost);

            populateMap();
        } catch (Exception e) {
            System.err.println("Error writing from file: " + e);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        routeFinder = this;
    }
}