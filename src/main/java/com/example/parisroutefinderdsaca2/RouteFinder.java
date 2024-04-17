package com.example.parisroutefinderdsaca2;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.parisroutefinderdsaca2.Graph.findCheapestPathDijkstra;

public class RouteFinder {
    @FXML
    public ImageView mapView;
    private boolean isMapPopulated = false;
    private Map<String, GraphNode<String>> graphNodes = new HashMap<>();

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

    public void dijkstraTest() {
//        graphNodes.put("ET", new GraphNode<>("Eiffel Tower", true,1,1)); //1,1 PLACEHOLDER, A
//        graphNodes.put("AdT", new GraphNode<>("Arc de Triomphe", true,1,1)); //B
//        graphNodes.put("TL", new GraphNode<>("The Louvre", true,1,1)); //C
//        graphNodes.put("GP", new GraphNode<>("Grand Palais", true,1,1)); //D
//        graphNodes.put("NDC", new GraphNode<>("Notre-Dame Cathedral", true,1,1)); //E
//        graphNodes.put("CE", new GraphNode<>("Champs-Élysées", true,1,1)); //F
//        graphNodes.put("OG", new GraphNode<>("Opera Garnier", true,1,1)); //G
//        graphNodes.put("PdlC", new GraphNode<>("Place de la Concorde", true,1,1)); //H
//        graphNodes.put("RS", new GraphNode<>("River Seine", true,1,1)); //I
//        graphNodes.put("BotSC", new GraphNode<>("Basilica of the Sacré-Coeur", true,1,1)); //J
//        graphNodes.put("TCP", new GraphNode<>("The Centre Pompidou", true,1,1)); //K
//        graphNodes.put("PAIII", new GraphNode<>("Pont Alexandre III", true,1,1)); //L
//        graphNodes.put("MdO", new GraphNode<>("Musée d’Orsay", true,1,1)); //M
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
        } catch (Exception e) {
            System.err.println("Error writing from file: " + e);
        }
    }
}