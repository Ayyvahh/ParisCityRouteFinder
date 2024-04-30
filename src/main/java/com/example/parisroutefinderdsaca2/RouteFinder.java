package com.example.parisroutefinderdsaca2;

import com.example.parisroutefinderdsaca2.DataStructure.Graph;
import com.example.parisroutefinderdsaca2.DataStructure.GraphLink;
import com.example.parisroutefinderdsaca2.DataStructure.GraphNode;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import org.jetbrains.annotations.NotNull;

import javafx.geometry.Point2D;
import java.io.*;
import java.net.URL;
import java.util.*;

import static com.example.parisroutefinderdsaca2.DataStructure.Graph.*;
import static com.example.parisroutefinderdsaca2.Main.mainStage;

public class RouteFinder implements Initializable {
    public static RouteFinder routeFinder;

    /*-----------JAVAFX------------*/
    public AnchorPane mapPane = new AnchorPane();
    public ListView<GraphNode<String>> currentWaypoints = new ListView<>();
    public Slider culturalSlid = new Slider(0, 5, 2);
    public ComboBox<GraphNode<String>> startPointBox = new ComboBox<>();
    public ComboBox<GraphNode<String>> avoidBox = new ComboBox<>();
    public ComboBox<GraphNode<String>> endPointBox = new ComboBox<>();
    public RadioButton landmarkBox;
    public RadioButton junctionBox;
    public TextField nameField;
    public double x;
    public double y;
    public Label systemMessage = new Label();
    public RadioButton dfsButton;
    public RadioButton bfsButton;
    public RadioButton dijkstraButton;
    public ToggleGroup algoSelection;
    public Label avoidingLabel = new Label();
    public ListView<CostedPath> dfsListView;


    public Slider historicalVal;
    public Label visitLabel = new Label();
    public ComboBox<GraphNode<String>> visitBox;
    Color visit = Color.rgb(0, 191, 99);
    Color route = Color.rgb(13, 137, 232);
    Color landMark = Color.rgb(0, 74, 173);
    Circle circle; /*Circle for user to see where they've clicked*/
    Text text;
    @FXML
    public ImageView mapView;
    private Tooltip nodeTip;
    /*-----------------------------*/
    private boolean isMapPopulated = false;
    public Map<String, GraphNode<String>> graphNodes = new HashMap<>();
    public Set<GraphNode<String>> avoidNodes = new HashSet<>();
    public Set<GraphNode<String>> visitNodes = new HashSet<>();

    @FXML
    public void scene2() throws IOException {
        Main.switchToSecondScene();
    }

    public void mapClicked(MouseEvent event) {
        System.out.println((int) event.getX() + ", " + (int) event.getY());
        x = event.getX();
        y = event.getY();

        /*Method for clearing the circle every new click*/
        clearMarkers();

        /* Circle marker that shows the user where they have clicked*/
        circle = new Circle(x, y, 5);
        circle.setFill(Color.BLUE);
        mapPane.getChildren().add(circle);

        // Create and add new text
        text = new Text(x - 37, y - 15, "YOU ARE HERE");
        text.setFill(Color.BLUE);
        mapPane.getChildren().add(text);
    }

    public void saveXML() throws Exception {
        XStream xstream = new XStream(new DomDriver());
        ObjectOutputStream out = xstream.createObjectOutputStream(new FileWriter("graphNodes.xml"));
        out.writeObject(graphNodes);
        out.close();
    }

    public void loadXML() throws Exception {
        Class<?>[] classes = new Class[]{GraphNode.class, GraphLink.class};

        XStream xstream = new XStream(new DomDriver());
        XStream.setupDefaultSecurity(xstream);
        xstream.allowTypes(classes);

        ObjectInputStream is = xstream.createObjectInputStream(new FileReader("graphNodes.xml"));
        graphNodes = Collections.unmodifiableMap((Map<String, GraphNode<String>>) is.readObject());
        is.close();
    }

    public void populateMap() {
        if (!isMapPopulated) {
            avoidBox.getItems().add(new GraphNode<>("AVOID NONE", false, 0, 0, 0));
            visitBox.getItems().add(new GraphNode<>("MUST VISIT NONE", false, 0, 0, 0));

            for (GraphNode<String> node : graphNodes.values()) {
                drawNode(node,landMark);

                if (node.isLandmark()) {
                    startPointBox.getItems().add(node);
                    endPointBox.getItems().add(node);
                    avoidBox.getItems().add(node);
                    visitBox.getItems().add(node);

                }
            }
            isMapPopulated = true;
        } else {
            System.out.println("Map already populated!");
        }
    }


    public void addLandmarkOrJunction() {
        /*Validation checks on name and if its Duplicated*/
        if (!nameField.getText().isEmpty() && !isDuplicateWayPoint()) {
            int cult = (int) culturalSlid.getValue();
            String name = nameField.getText();
            boolean isJunction = junctionBox.isSelected();
            boolean isLandmark = landmarkBox.isSelected();

            /* Different behaviour for landmark and junction, so they display right on map */
            if (isJunction || isLandmark) {
                GraphNode<String> waypoint = new GraphNode<>(name, true, cult, (int) x, (int) y); // Using class-level x and y
                if (isJunction) {
                    /* clearing the location marker, adding to hashmap and setting the node on map */
                    waypoint.setLandmark(false);
                    graphNodes.put(name, waypoint);
                    clearMarkers();
                    clearFields();
                    drawNode(waypoint,landMark);
                    currentWaypoints.getItems().add(waypoint);
                }
                if (isLandmark) {
                    clearMarkers();
                    waypoint.setLandmark(true);
                    graphNodes.put(name, waypoint);
                    drawNode(waypoint,landMark);
                    clearFields();
                    currentWaypoints.getItems().add(waypoint);
                }
            } else {
                Utils.showWarningAlert("ERROR", "PLEASE SPECIFY WHETHER YOU WANT TO ADD A JUNCTION OR A LANDMARK");
            }
        } else {
            Utils.showWarningAlert("ERROR", "PLEASE ENTER A NAME FOR THE WAYPOINT");
        }
    }

    public void clearMarkers() {
        /*Method for clearing the circle every new click or addition of waypoint*/
        if (circle != null && text != null) {
            mapPane.getChildren().remove(circle);
            mapPane.getChildren().remove(text);
        }
    }


    public void showSelectedNodes() {

        if (startPointBox == null || endPointBox == null) {
            return;
        }

        clearAllCircles();
        String startPointName = startPointBox.getSelectionModel().getSelectedItem().getName();
        String endPointName = endPointBox.getSelectionModel().getSelectedItem().getName();

        for (GraphNode<String> node : graphNodes.values()) {
            if (avoidNodes != null && avoidNodes.contains(node)) {
                drawNode(node, Color.RED);
            } else if (node.getName().equals(startPointName)) {
                drawNode(node, route);
            } else if (node.getName().equals(endPointName)) {
                drawNode(node, route);
            } else {
                drawNode(node, landMark);
            }if (visitNodes != null && visitNodes.contains(node)) {
                drawNode(node, visit);
            }
        }
    }




    public void removeLandmarkOrJunction() {
        /*Validation Checks*/
        if (currentWaypoints != null && currentWaypoints.getSelectionModel().getSelectedItem() != null) {
            GraphNode<String> selectedNode = currentWaypoints.getSelectionModel().getSelectedItem();

            if (graphNodes.containsKey(selectedNode.getName())) {
                currentWaypoints.getItems().remove(selectedNode);

                graphNodes.remove(selectedNode.getName());


                clearAllCircles();
                /*Removing all circles from the pane first, repopulating after with contents of graph nodes
                 * so the removed waypoint also removes from the map*/

                for (GraphNode<String> node : graphNodes.values()) {
                    drawNode(node,landMark);
                }
            }
        } else {
            Utils.showWarningAlert("ERROR", "Please select a waypoint");
        }
    }

    private void clearAllCircles() {
        mapPane.getChildren().removeIf(node -> node instanceof Circle);
    }

    public boolean isDuplicateWayPoint() {
        return graphNodes.containsKey(nameField.getText());
    }

    private void drawNode(GraphNode<String> node, Color color) {
        Circle nodeCircle = new Circle();
        Circle nodeRing = new Circle();


        if (node.isLandmark()) {
            nodeCircle.setCenterX(node.getGraphX());
            nodeCircle.setCenterY(node.getGraphY());
            nodeCircle.setRadius(5);
            nodeCircle.setFill(color);

            nodeRing.setCenterX(node.getGraphX());
            nodeRing.setCenterY(node.getGraphY());
            nodeRing.setRadius(8);
            nodeRing.setFill(Color.TRANSPARENT);
            nodeRing.setStrokeWidth(2);
            nodeRing.setStroke(color);
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
            Main.mainPage.setCursor(Cursor.HAND);
            nodeTip.setText(landmarkOn.getName() + ",\n X :  " + landmarkOn.getGraphX() +
                    "  |   Y :  " + landmarkOn.getGraphY());
            nodeTip.show(mapView, e.getScreenX() + 10, e.getScreenY() + 10);
        } else {
            nodeTip.hide();
            Main.mainPage.setCursor(Cursor.DEFAULT);
        }
    }

    public int calculateEuclideanDistance(GraphNode<String> n1, GraphNode<String> n2) {
        int x1 = n1.getGraphX();
        int x2 = n2.getGraphX();
        int y1 = n1.getGraphY();
        int y2 = n2.getGraphY();

        return (int) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    private void printAdjacentNodes(GraphNode<String> node) {
        // Get the adjacent list of the given station
        List<GraphLink> adjacentList = node.getAdjList();

        // Check if the adjacent list is not empty
        if (!adjacentList.isEmpty()) {
            // Print out the header
            System.out.println("Adjacent nodes to " + node.getName() + ":");

            // Iterate over the adjacent stations and print out their names
            for (GraphLink adj : adjacentList) {
                System.out.println(adj.destNode.getName().toString() + "\n ---------------------------------- \n");
            }
        }
    }




    public void addToAvoid() {
        GraphNode<String> selectedItem = avoidBox.getSelectionModel().getSelectedItem();

        if (selectedItem != null && selectedItem.getName().equals("AVOID NONE") && !visitNodes.contains(selectedItem)) {
            avoidNodes.clear();
            avoidingLabel.setText(null);
            printAvoidNodes();  // Print the updated avoidNodes
            showSelectedNodes();
            return;
        }

        if (selectedItem != null&& !visitNodes.contains(selectedItem) && !startPointBox.getSelectionModel().getSelectedItem().getName().equals(selectedItem.getName()) && !endPointBox.getSelectionModel().getSelectedItem().getName().equals(selectedItem.getName())) {
            avoidNodes.add(selectedItem);
            printAvoidNodes();  // Print the updated avoidNodes
            showSelectedNodes();  // Show the newly avoided node
        }
    }

    public void addToVisit() {
        GraphNode<String> selectedItem = visitBox.getSelectionModel().getSelectedItem();

        if (selectedItem != null && visitBox.getItems().getFirst().getName().equals(selectedItem.getName())) {
            visitNodes.clear();
            visitLabel.setText(null);
            printVisitNodes();  // Print the updated avoidNodes
            showSelectedNodes();
            return;
        }

        if (selectedItem != null&& !avoidNodes.contains(selectedItem) && !startPointBox.getSelectionModel().getSelectedItem().getName().equals(selectedItem.getName()) && !endPointBox.getSelectionModel().getSelectedItem().getName().equals(selectedItem.getName())) {

            visitNodes.add(selectedItem);
            printVisitNodes();  // Print the updated avoidNodes
            showSelectedNodes();  // Show the newly avoided node
        }
    }

    private void printAvoidNodes() {
        String s = "AVOIDING :  ";
        // Check if the adjacent list is not empty
        if (!avoidNodes.isEmpty()) {

            for (GraphNode<String> a : avoidNodes) {
                s += a.getName() + ",  ";

            }
            avoidingLabel.setText(s);
        }

    }
    private void printVisitNodes() {
        String s = "VISITING :  ";

        if (!visitNodes.isEmpty()) {

            for (GraphNode<String> a : visitNodes) {
                s += a.getName() + ",  ";

            }
            visitLabel.setText(s);
        }

    }


    public void populateDatabase() {
//        graphNodes.put("Eiffel Tower", new GraphNode<>("Eiffel Tower", true,5,126,377)); //A
//        graphNodes.put("Arc de Triomphe", new GraphNode<>("Arc de Triomphe", true,5,80,220)); //B
//        graphNodes.put("The Louvre", new GraphNode<>("The Louvre", true,4,133,78)); //C
//        graphNodes.put("Grand Palais", new GraphNode<>("Grand Palais", true,3,275,285)); //D
//        graphNodes.put("Notre-Dame Cathedral", new GraphNode<>("Notre-Dame Cathedral", true,3,309,270)); //E
//        graphNodes.put("Champs-Élysées", new GraphNode<>("Champs-Élysées", true,3,358,192)); //F
//        graphNodes.put("Opera Garnier", new GraphNode<>("Opera Garnier", true,3,342,349)); //G
//        graphNodes.put("Place de la Concorde", new GraphNode<>("Place de la Concorde", true,3,411,436)); //H
//        graphNodes.put("River Seine", new GraphNode<>("River Seine", true,2,349,77)); //I
//        graphNodes.put("Basilica of the Sacré-Coeur", new GraphNode<>("Basilica of the Sacré-Coeur", true,2,447,31)); //J
//        graphNodes.put("The Centre Pompidou", new GraphNode<>("The Centre Pompidou", true,2,505,355)); //K
//        graphNodes.put("Pont Alexandre III", new GraphNode<>("Pont Alexandre III", true,5,659,326)); //L
//        graphNodes.put("Musée d’Orsay", new GraphNode<>("Musée d’Orsay", true,5,532,248)); //M
//        graphNodes.put("The Panthéon", new GraphNode<>("The Panthéon", true,5,572,98)); //M
//
//        graphNodes.put("N1", new GraphNode<>("N1", false, 0,108,370));
//        graphNodes.put("N2", new GraphNode<>("N2", false, 0,93,363));
//        graphNodes.put("N3", new GraphNode<>("N3", false, 0,85,278));
//        graphNodes.put("N4", new GraphNode<>("N4", false, 0,73,270));
//        graphNodes.put("N5", new GraphNode<>("N5", false, 0,96,236));
//        graphNodes.put("N6", new GraphNode<>("N6", false, 0,133,274));
//        graphNodes.put("N7", new GraphNode<>("N7", false, 0,224,262));
//        graphNodes.put("N8", new GraphNode<>("N8", false, 0,172,240));
//        graphNodes.put("N9", new GraphNode<>("N9", false, 0,367,235));
//        graphNodes.put("N10", new GraphNode<>("N10", false, 0,151,395));
//        graphNodes.put("N11", new GraphNode<>("N11", false, 0,197,356));
//        graphNodes.put("N12", new GraphNode<>("N12", false, 0,240,331));
//        graphNodes.put("N13", new GraphNode<>("N13", false, 0,288,309));
//        graphNodes.put("N14", new GraphNode<>("N14", false, 0,320,360));
//        graphNodes.put("N15", new GraphNode<>("N15", false, 0,366,436));
//        graphNodes.put("N16", new GraphNode<>("N16", false, 0,331,288));
//        graphNodes.put("N17", new GraphNode<>("N17", false, 0,383,318));
//        graphNodes.put("N18", new GraphNode<>("N18", false, 0,398,383));
//        graphNodes.put("N19", new GraphNode<>("N19", false, 0,366,436));
//        graphNodes.put("N20", new GraphNode<>("N20", false, 0,89,130));
//        graphNodes.put("N21", new GraphNode<>("N21", false, 0,366,436));
//        graphNodes.put("N22", new GraphNode<>("N22", false, 0,145,139));
//        graphNodes.put("N23", new GraphNode<>("N23", false, 0,366,436));
//        graphNodes.put("N24", new GraphNode<>("N24", false, 0,188,45));
//        graphNodes.put("N25", new GraphNode<>("N25", false, 0,334,114));
//        graphNodes.put("N26", new GraphNode<>("N26", false, 0,334,114));
//        graphNodes.put("N27", new GraphNode<>("N27", false, 0,323,141));
//        graphNodes.put("N28", new GraphNode<>("N28", false, 0,387,155));
//        graphNodes.put("N29", new GraphNode<>("N29", false, 0,401,52));
//        graphNodes.put("N30", new GraphNode<>("N30", false, 0,225,124));
//        graphNodes.put("N31", new GraphNode<>("N31", false, 0,221,62));
//        graphNodes.put("N32", new GraphNode<>("N32", false, 0,476,134));
//        graphNodes.put("N33", new GraphNode<>("N33", false, 0,396,131));
//        graphNodes.put("N34", new GraphNode<>("N34", false, 0,481,186));
//        graphNodes.put("N35", new GraphNode<>("N35", false, 0,467,216));
//        graphNodes.put("N36", new GraphNode<>("N36", false, 0,422,337));
//        graphNodes.put("N37", new GraphNode<>("N37", false, 0,268,389));
//        graphNodes.put("N38", new GraphNode<>("N38", false, 0,360,475));
//        graphNodes.put("N39", new GraphNode<>("N39", false, 0,531,408));
//        graphNodes.put("N40", new GraphNode<>("N40", false, 0,508,300));
//        graphNodes.put("N41", new GraphNode<>("N41", false, 0,493,249));
//        graphNodes.put("N42", new GraphNode<>("N42", false, 0,566,237));
//        graphNodes.put("N43", new GraphNode<>("N43", false, 0,645,345));
//        graphNodes.put("N44", new GraphNode<>("N44", false, 0,530,409));
//        graphNodes.put("N45", new GraphNode<>("N45", false, 0,491,421));
//        graphNodes.put("N46", new GraphNode<>("N46", false, 0,479,454));
//        graphNodes.put("N47", new GraphNode<>("N47", false, 0,236,171));
//        graphNodes.put("N48", new GraphNode<>("N48", false, 0,454,134));
//        graphNodes.put("N49", new GraphNode<>("N49", false, 0,438,170));
//        graphNodes.put("N50", new GraphNode<>("N50", false, 0,571,209));
//        graphNodes.put("N51", new GraphNode<>("N51", false, 0,581,125));
//        graphNodes.put("N52", new GraphNode<>("N52", false, 0,624,286));
//        graphNodes.put("N53", new GraphNode<>("N53", false, 0,597,288));
//        graphNodes.put("N54", new GraphNode<>("N54", false, 0,605,222));
//        graphNodes.put("N55", new GraphNode<>("N55", false, 0,523,6));
//        graphNodes.put("N56", new GraphNode<>("N56", false, 0,563,332));
//        graphNodes.put("N57", new GraphNode<>("N57", false, 0,580,298));
//        graphNodes.put("N58", new GraphNode<>("N58", false, 0,249,212));
//        graphNodes.put("N59", new GraphNode<>("N59", false, 0,286,216));
//        graphNodes.put("N60", new GraphNode<>("N60", false, 0,414,253));
//        graphNodes.put("N61", new GraphNode<>("N61", false, 0,455,248));
//        graphNodes.put("N62", new GraphNode<>("N62", false, 0,233,432));
//        graphNodes.put("N63", new GraphNode<>("N63", false, 0,445,153));
//        graphNodes.put("N64", new GraphNode<>("N64", false, 0,367,45));
//        graphNodes.put("N65", new GraphNode<>("N65", false, 0,318,163));
//        graphNodes.put("N66", new GraphNode<>("N66", false, 0,306,30));
//        graphNodes.put("N67", new GraphNode<>("N67", false, 0,330,126));
//        graphNodes.put("N68", new GraphNode<>("N68", false, 0,339,104));
//        graphNodes.put("N69", new GraphNode<>("N69", false, 0,460,77));
//        graphNodes.put("N70", new GraphNode<>("N70", false, 0,460,53));
//        graphNodes.put("N71", new GraphNode<>("N71", false, 0,529,88));
//        graphNodes.put("N72", new GraphNode<>("N72", false, 0,111,97));
//
//        graphNodes.get("N1").connectToNodeUndirected(graphNodes.get("Eiffel Tower"), calculateDistance(graphNodes.get("N1"), graphNodes.get("Eiffel Tower")));
//        graphNodes.get("N2").connectToNodeUndirected(graphNodes.get("N1"), calculateDistance(graphNodes.get("N2"), graphNodes.get("N1")));
//        graphNodes.get("N3").connectToNodeUndirected(graphNodes.get("N2"), calculateDistance(graphNodes.get("N3"), graphNodes.get("N2")));
//        graphNodes.get("N4").connectToNodeUndirected(graphNodes.get("N3"), calculateDistance(graphNodes.get("N4"), graphNodes.get("N3")));
//        graphNodes.get("N5").connectToNodeUndirected(graphNodes.get("N4"), calculateDistance(graphNodes.get("N5"), graphNodes.get("N4")));
//        graphNodes.get("Arc de Triomphe").connectToNodeUndirected(graphNodes.get("N5"), calculateDistance(graphNodes.get("Arc de Triomphe"), graphNodes.get("N5")));
//        graphNodes.get("N3").connectToNodeUndirected(graphNodes.get("N6"), calculateDistance(graphNodes.get("N3"), graphNodes.get("N6")));
//        graphNodes.get("N6").connectToNodeUndirected(graphNodes.get("N5"), calculateDistance(graphNodes.get("N6"), graphNodes.get("N5")));
//        graphNodes.get("N2").connectToNodeUndirected(graphNodes.get("N6"), calculateDistance(graphNodes.get("N2"), graphNodes.get("N6")));
//        graphNodes.get("N6").connectToNodeUndirected(graphNodes.get("N7"), calculateDistance(graphNodes.get("N6"), graphNodes.get("N7")));
//        graphNodes.get("N7").connectToNodeUndirected(graphNodes.get("Grand Palais"), calculateDistance(graphNodes.get("N7"), graphNodes.get("Grand Palais")));
//        graphNodes.get("N1").connectToNodeUndirected(graphNodes.get("Grand Palais"), calculateDistance(graphNodes.get("N1"), graphNodes.get("Grand Palais")));
//        graphNodes.get("N7").connectToNodeUndirected(graphNodes.get("N8"), calculateDistance(graphNodes.get("N7"), graphNodes.get("N8")));
//        graphNodes.get("Grand Palais").connectToNodeUndirected(graphNodes.get("Notre-Dame Cathedral"), calculateDistance(graphNodes.get("Grand Palais"), graphNodes.get("Notre-Dame Cathedral")));
//        graphNodes.get("Notre-Dame Cathedral").connectToNodeUndirected(graphNodes.get("N9"), calculateDistance(graphNodes.get("Notre-Dame Cathedral"), graphNodes.get("N9")));
//        graphNodes.get("N9").connectToNodeUndirected(graphNodes.get("Champs-Élysées"), calculateDistance(graphNodes.get("N9"), graphNodes.get("Champs-Élysées")));
//        graphNodes.get("Grand Palais").connectToNodeUndirected(graphNodes.get("N59"), calculateDistance(graphNodes.get("Grand Palais"), graphNodes.get("N59")));
//        graphNodes.get("Grand Palais").connectToNodeUndirected(graphNodes.get("N59"), calculateDistance(graphNodes.get("Grand Palais"), graphNodes.get("N59")));
//        graphNodes.get("N59").connectToNodeUndirected(graphNodes.get("N58"), calculateDistance(graphNodes.get("N59"), graphNodes.get("N58")));
//        graphNodes.get("N58").connectToNodeUndirected(graphNodes.get("N47"), calculateDistance(graphNodes.get("N58"), graphNodes.get("N47")));
//        graphNodes.get("N47").connectToNodeUndirected(graphNodes.get("N65"), calculateDistance(graphNodes.get("N47"), graphNodes.get("N65")));
//        graphNodes.get("N65").connectToNodeUndirected(graphNodes.get("Champs-Élysées"), calculateDistance(graphNodes.get("N65"), graphNodes.get("Champs-Élysées")));
//        graphNodes.get("Champs-Élysées").connectToNodeUndirected(graphNodes.get("N27"), calculateDistance(graphNodes.get("Champs-Élysées"), graphNodes.get("N27")));
//        graphNodes.get("Champs-Élysées").connectToNodeUndirected(graphNodes.get("N28"), calculateDistance(graphNodes.get("Champs-Élysées"), graphNodes.get("N28")));
//        graphNodes.get("Notre-Dame Cathedral").connectToNodeUndirected(graphNodes.get("N16"), calculateDistance(graphNodes.get("Notre-Dame Cathedral"), graphNodes.get("N16")));
//        graphNodes.get("N16").connectToNodeUndirected(graphNodes.get("Opera Garnier"), calculateDistance(graphNodes.get("N16"), graphNodes.get("Opera Garnier")));
//        graphNodes.get("Opera Garnier").connectToNodeUndirected(graphNodes.get("N21"), calculateDistance(graphNodes.get("Opera Garnier"), graphNodes.get("N21")));
//        graphNodes.get("Opera Garnier").connectToNodeUndirected(graphNodes.get("N18"), calculateDistance(graphNodes.get("Opera Garnier"), graphNodes.get("N18")));
//        graphNodes.get("Champs-Élysées").connectToNodeUndirected(graphNodes.get("N41"), calculateDistance(graphNodes.get("Champs-Élysées"), graphNodes.get("N41")));
//        graphNodes.get("N41").connectToNodeUndirected(graphNodes.get("Musée d’Orsay"), calculateDistance(graphNodes.get("N41"), graphNodes.get("Musée d’Orsay")));
//        graphNodes.get("The Centre Pompidou").connectToNodeUndirected(graphNodes.get("N40"), calculateDistance(graphNodes.get("The Centre Pompidou"), graphNodes.get("N40")));
//        graphNodes.get("N60").connectToNodeUndirected(graphNodes.get("N61"), calculateDistance(graphNodes.get("N60"), graphNodes.get("N61")));
//        graphNodes.get("N61").connectToNodeUndirected(graphNodes.get("N35"), calculateDistance(graphNodes.get("N61"), graphNodes.get("N35")));
//        graphNodes.get("N35").connectToNodeUndirected(graphNodes.get("N34"), calculateDistance(graphNodes.get("N35"), graphNodes.get("N34")));
//        graphNodes.get("N18").connectToNodeUndirected(graphNodes.get("The Centre Pompidou"), calculateDistance(graphNodes.get("N18"), graphNodes.get("The Centre Pompidou")));
//        graphNodes.get("N10").connectToNodeUndirected(graphNodes.get("N11"), calculateDistance(graphNodes.get("N10"), graphNodes.get("N11")));
//        graphNodes.get("N11").connectToNodeUndirected(graphNodes.get("N12"), calculateDistance(graphNodes.get("N11"), graphNodes.get("N12")));
//        graphNodes.get("N12").connectToNodeUndirected(graphNodes.get("N13"), calculateDistance(graphNodes.get("N12"), graphNodes.get("N13")));
//        graphNodes.get("Grand Palais").connectToNodeUndirected(graphNodes.get("N13"), calculateDistance(graphNodes.get("Grand Palais"), graphNodes.get("N13")));
//        graphNodes.get("N13").connectToNodeUndirected(graphNodes.get("N14"), calculateDistance(graphNodes.get("N13"), graphNodes.get("N14")));
//        graphNodes.get("N14").connectToNodeUndirected(graphNodes.get("Opera Garnier"), calculateDistance(graphNodes.get("N14"), graphNodes.get("Opera Garnier")));
//        graphNodes.get("N7").connectToNodeUndirected(graphNodes.get("N12"), calculateDistance(graphNodes.get("N7"), graphNodes.get("N12")));
//        graphNodes.get("N12").connectToNodeUndirected(graphNodes.get("N37"), calculateDistance(graphNodes.get("N12"), graphNodes.get("N37")));
//        graphNodes.get("N3").connectToNodeUndirected(graphNodes.get("N11"), calculateDistance(graphNodes.get("N3"), graphNodes.get("N11")));
//        graphNodes.get("N11").connectToNodeUndirected(graphNodes.get("N37"), calculateDistance(graphNodes.get("N11"), graphNodes.get("N37")));
//        graphNodes.get("N37").connectToNodeUndirected(graphNodes.get("N21"), calculateDistance(graphNodes.get("N37"), graphNodes.get("N21")));
//        graphNodes.get("N62").connectToNodeUndirected(graphNodes.get("N37"), calculateDistance(graphNodes.get("N62"), graphNodes.get("N37")));
//        graphNodes.get("N37").connectToNodeUndirected(graphNodes.get("N13"), calculateDistance(graphNodes.get("N37"), graphNodes.get("N13")));
//        graphNodes.get("N13").connectToNodeUndirected(graphNodes.get("N16"), calculateDistance(graphNodes.get("N13"), graphNodes.get("N16")));
//        graphNodes.get("N16").connectToNodeUndirected(graphNodes.get("N60"), calculateDistance(graphNodes.get("N16"), graphNodes.get("N60")));
//        graphNodes.get("N9").connectToNodeUndirected(graphNodes.get("N17"), calculateDistance(graphNodes.get("N9"), graphNodes.get("N17")));
//        graphNodes.get("N59").connectToNodeUndirected(graphNodes.get("Notre-Dame Cathedral"), calculateDistance(graphNodes.get("N59"), graphNodes.get("Notre-Dame Cathedral")));
//        graphNodes.get("N47").connectToNodeUndirected(graphNodes.get("N30"), calculateDistance(graphNodes.get("N47"), graphNodes.get("N30")));
//        graphNodes.get("N8").connectToNodeUndirected(graphNodes.get("N47"), calculateDistance(graphNodes.get("N8"), graphNodes.get("N47")));
//        graphNodes.get("N7").connectToNodeUndirected(graphNodes.get("N59"), calculateDistance(graphNodes.get("N7"), graphNodes.get("N59")));
//        graphNodes.get("N57").connectToNodeUndirected(graphNodes.get("N53"), calculateDistance(graphNodes.get("N57"), graphNodes.get("N53")));
//        graphNodes.get("N21").connectToNodeUndirected(graphNodes.get("Place de la Concorde"), calculateDistance(graphNodes.get("N21"), graphNodes.get("Place de la Concorde")));
//        graphNodes.get("Place de la Concorde").connectToNodeUndirected(graphNodes.get("N46"), calculateDistance(graphNodes.get("Place de la Concorde"), graphNodes.get("N46")));
//        graphNodes.get("Place de la Concorde").connectToNodeUndirected(graphNodes.get("N18"), calculateDistance(graphNodes.get("Place de la Concorde"), graphNodes.get("N18")));
//        graphNodes.get("N18").connectToNodeUndirected(graphNodes.get("N17"), calculateDistance(graphNodes.get("N18"), graphNodes.get("N17")));
//        graphNodes.get("N16").connectToNodeUndirected(graphNodes.get("N17"), calculateDistance(graphNodes.get("N16"), graphNodes.get("N17")));
//        graphNodes.get("N17").connectToNodeUndirected(graphNodes.get("N9"), calculateDistance(graphNodes.get("N17"), graphNodes.get("N9")));
//        graphNodes.get("N17").connectToNodeUndirected(graphNodes.get("N36"), calculateDistance(graphNodes.get("N17"), graphNodes.get("N36")));
//        graphNodes.get("N36").connectToNodeUndirected(graphNodes.get("The Centre Pompidou"), calculateDistance(graphNodes.get("N36"), graphNodes.get("The Centre Pompidou")));
//        graphNodes.get("N18").connectToNodeUndirected(graphNodes.get("N36"), calculateDistance(graphNodes.get("N18"), graphNodes.get("N36")));
//        graphNodes.get("Eiffel Tower").connectToNodeUndirected(graphNodes.get("N10"), calculateDistance(graphNodes.get("Eiffel Tower"), graphNodes.get("N10")));
//        graphNodes.get("N10").connectToNodeUndirected(graphNodes.get("N62"), calculateDistance(graphNodes.get("N10"), graphNodes.get("N62")));
//        graphNodes.get("N62").connectToNodeUndirected(graphNodes.get("N38"), calculateDistance(graphNodes.get("N62"), graphNodes.get("N38")));
//        graphNodes.get("N38").connectToNodeUndirected(graphNodes.get("N21"), calculateDistance(graphNodes.get("N38"), graphNodes.get("N21")));
//        graphNodes.get("N21").connectToNodeUndirected(graphNodes.get("N38"), calculateDistance(graphNodes.get("N21"), graphNodes.get("N38")));
//        graphNodes.get("N46").connectToNodeUndirected(graphNodes.get("N45"), calculateDistance(graphNodes.get("N46"), graphNodes.get("N45")));
//        graphNodes.get("N45").connectToNodeUndirected(graphNodes.get("N39"), calculateDistance(graphNodes.get("N45"), graphNodes.get("N39")));
//        graphNodes.get("N39").connectToNodeUndirected(graphNodes.get("The Centre Pompidou"), calculateDistance(graphNodes.get("N39"), graphNodes.get("The Centre Pompidou")));
//        graphNodes.get("N18").connectToNodeUndirected(graphNodes.get("N45"), calculateDistance(graphNodes.get("N18"), graphNodes.get("N45")));
//        graphNodes.get("N39").connectToNodeUndirected(graphNodes.get("N43"), calculateDistance(graphNodes.get("N39"), graphNodes.get("N43")));
//        graphNodes.get("N43").connectToNodeUndirected(graphNodes.get("Pont Alexandre III"), calculateDistance(graphNodes.get("N43"), graphNodes.get("Pont Alexandre III")));
//        graphNodes.get("N39").connectToNodeUndirected(graphNodes.get("N56"), calculateDistance(graphNodes.get("N39"), graphNodes.get("N56")));
//        graphNodes.get("N56").connectToNodeUndirected(graphNodes.get("N57"), calculateDistance(graphNodes.get("N56"), graphNodes.get("N57")));
//        graphNodes.get("N57").connectToNodeUndirected(graphNodes.get("Musée d’Orsay"), calculateDistance(graphNodes.get("N57"), graphNodes.get("Musée d’Orsay")));
//        graphNodes.get("Musée d’Orsay").connectToNodeUndirected(graphNodes.get("N42"), calculateDistance(graphNodes.get("Musée d’Orsay"), graphNodes.get("N42")));
//        graphNodes.get("N42").connectToNodeUndirected(graphNodes.get("N50"), calculateDistance(graphNodes.get("N42"), graphNodes.get("N50")));
//        graphNodes.get("N50").connectToNodeUndirected(graphNodes.get("N51"), calculateDistance(graphNodes.get("N50"), graphNodes.get("N51")));
//        graphNodes.get("N51").connectToNodeUndirected(graphNodes.get("The Panthéon"), calculateDistance(graphNodes.get("N51"), graphNodes.get("The Panthéon")));
//        graphNodes.get("Arc de Triomphe").connectToNodeUndirected(graphNodes.get("N20"), calculateDistance(graphNodes.get("Arc de Triomphe"), graphNodes.get("N20")));
//        graphNodes.get("N20").connectToNodeUndirected(graphNodes.get("N22"), calculateDistance(graphNodes.get("N20"), graphNodes.get("N22")));
//        graphNodes.get("N22").connectToNodeUndirected(graphNodes.get("The Louvre"), calculateDistance(graphNodes.get("N22"), graphNodes.get("The Louvre")));
//        graphNodes.get("N22").connectToNodeUndirected(graphNodes.get("N30"), calculateDistance(graphNodes.get("N22"), graphNodes.get("N30")));
//        graphNodes.get("The Louvre").connectToNodeUndirected(graphNodes.get("N24"), calculateDistance(graphNodes.get("The Louvre"), graphNodes.get("N24")));
//        graphNodes.get("N24").connectToNodeUndirected(graphNodes.get("N31"), calculateDistance(graphNodes.get("N24"), graphNodes.get("N31")));
//        graphNodes.get("N30").connectToNodeUndirected(graphNodes.get("N31"), calculateDistance(graphNodes.get("N30"), graphNodes.get("N31")));
//        graphNodes.get("N24").connectToNodeUndirected(graphNodes.get("N66"), calculateDistance(graphNodes.get("N24"), graphNodes.get("N66")));
//        graphNodes.get("N27").connectToNodeUndirected(graphNodes.get("N67"), calculateDistance(graphNodes.get("N27"), graphNodes.get("N67")));
//        graphNodes.get("N67").connectToNodeUndirected(graphNodes.get("N25"), calculateDistance(graphNodes.get("N67"), graphNodes.get("N25")));
//        graphNodes.get("N30").connectToNodeUndirected(graphNodes.get("N67"), calculateDistance(graphNodes.get("N30"), graphNodes.get("N67")));
//        graphNodes.get("N31").connectToNodeUndirected(graphNodes.get("N25"), calculateDistance(graphNodes.get("N31"), graphNodes.get("N25")));
//        graphNodes.get("N25").connectToNodeUndirected(graphNodes.get("N68"), calculateDistance(graphNodes.get("N25"), graphNodes.get("N68")));
//        graphNodes.get("N68").connectToNodeUndirected(graphNodes.get("River Seine"), calculateDistance(graphNodes.get("N68"), graphNodes.get("River Seine")));
//        graphNodes.get("N66").connectToNodeUndirected(graphNodes.get("N64"), calculateDistance(graphNodes.get("N66"), graphNodes.get("N64")));
//        graphNodes.get("River Seine").connectToNodeUndirected(graphNodes.get("N64"), calculateDistance(graphNodes.get("River Seine"), graphNodes.get("N64")));
//        graphNodes.get("N64").connectToNodeUndirected(graphNodes.get("N29"), calculateDistance(graphNodes.get("N64"), graphNodes.get("N29")));
//        graphNodes.get("N29").connectToNodeUndirected(graphNodes.get("Basilica of the Sacré-Coeur"), calculateDistance(graphNodes.get("N29"), graphNodes.get("Basilica of the Sacré-Coeur")));
//        graphNodes.get("Basilica of the Sacré-Coeur").connectToNodeUndirected(graphNodes.get("N55"), calculateDistance(graphNodes.get("Basilica of the Sacré-Coeur"), graphNodes.get("N55")));
//        graphNodes.get("N55").connectToNodeUndirected(graphNodes.get("The Panthéon"), calculateDistance(graphNodes.get("N55"), graphNodes.get("The Panthéon")));
//        graphNodes.get("N28").connectToNodeUndirected(graphNodes.get("N49"), calculateDistance(graphNodes.get("N28"), graphNodes.get("N49")));
//        graphNodes.get("N49").connectToNodeUndirected(graphNodes.get("N34"), calculateDistance(graphNodes.get("N49"), graphNodes.get("N34")));
//        graphNodes.get("N34").connectToNodeUndirected(graphNodes.get("N42"), calculateDistance(graphNodes.get("N34"), graphNodes.get("N42")));
//        graphNodes.get("N33").connectToNodeUndirected(graphNodes.get("N63"), calculateDistance(graphNodes.get("N33"), graphNodes.get("N63")));
//        graphNodes.get("N49").connectToNodeUndirected(graphNodes.get("N63"), calculateDistance(graphNodes.get("N49"), graphNodes.get("N63")));
//        graphNodes.get("N63").connectToNodeUndirected(graphNodes.get("N48"), calculateDistance(graphNodes.get("N63"), graphNodes.get("N48")));
//        graphNodes.get("N48").connectToNodeUndirected(graphNodes.get("N32"), calculateDistance(graphNodes.get("N48"), graphNodes.get("N32")));
//        graphNodes.get("N32").connectToNodeUndirected(graphNodes.get("The Panthéon"), calculateDistance(graphNodes.get("N32"), graphNodes.get("The Panthéon")));
//        graphNodes.get("N68").connectToNodeUndirected(graphNodes.get("N33"), calculateDistance(graphNodes.get("N68"), graphNodes.get("N33")));
//        graphNodes.get("N32").connectToNodeUndirected(graphNodes.get("N69"), calculateDistance(graphNodes.get("N32"), graphNodes.get("N69")));
//        graphNodes.get("N69").connectToNodeUndirected(graphNodes.get("N70"), calculateDistance(graphNodes.get("N69"), graphNodes.get("N70")));
//        graphNodes.get("N70").connectToNodeUndirected(graphNodes.get("Basilica of the Sacré-Coeur"), calculateDistance(graphNodes.get("N70"), graphNodes.get("Basilica of the Sacré-Coeur")));
//        graphNodes.get("N29").connectToNodeUndirected(graphNodes.get("N71"), calculateDistance(graphNodes.get("N29"), graphNodes.get("N71")));
//        graphNodes.get("N71").connectToNodeUndirected(graphNodes.get("The Panthéon"), calculateDistance(graphNodes.get("N71"), graphNodes.get("The Panthéon")));
//        graphNodes.get("N20").connectToNodeUndirected(graphNodes.get("N72"), calculateDistance(graphNodes.get("N20"), graphNodes.get("N72")));
//        graphNodes.get("N72").connectToNodeUndirected(graphNodes.get("The Louvre"), calculateDistance(graphNodes.get("N72"), graphNodes.get("The Louvre")));
//        graphNodes.get("N50").connectToNodeUndirected(graphNodes.get("N54"), calculateDistance(graphNodes.get("N50"), graphNodes.get("N54")));
//        graphNodes.get("N42").connectToNodeUndirected(graphNodes.get("N54"), calculateDistance(graphNodes.get("N42"), graphNodes.get("N54")));
//        graphNodes.get("N54").connectToNodeUndirected(graphNodes.get("N51"), calculateDistance(graphNodes.get("N54"), graphNodes.get("N51")));
//        graphNodes.get("N53").connectToNodeUndirected(graphNodes.get("N52"), calculateDistance(graphNodes.get("N53"), graphNodes.get("N52")));
//        graphNodes.get("N52").connectToNodeUndirected(graphNodes.get("N54"), calculateDistance(graphNodes.get("N52"), graphNodes.get("N54")));
//        graphNodes.get("Pont Alexandre III").connectToNodeUndirected(graphNodes.get("N52"), calculateDistance(graphNodes.get("Pont Alexandre III"), graphNodes.get("N52")));
//
//        populateMap();
//
//        try {
//            saveXML();
//            System.out.println("Database saved!");
//        } catch (Exception e) {
//            System.err.println("Error writing from file: " + e);
//        }

        try {
            loadXML();
            System.out.println("Database loaded!");

            populateMap();
//            for(GraphNode<String> n : graphNodes.values()){
//                printAdjacentNodes(n);
//            }
        } catch (Exception e) {
            System.err.println("Error writing from file: " + e);
        }
    }

    public void findRoute() {
        if (algoSelection.getSelectedToggle() == null) {
            Utils.showWarningAlert("SELECT AN ALGORITHM", "Please select a radio button of the algorithm you wish to use!");
        } else {
            if (startPointBox.getSelectionModel().getSelectedItem() == null || endPointBox.getSelectionModel().getSelectedItem() == null) {
                Utils.showWarningAlert("ERROR", "SELECT START AND END POINT");
                return;
            }
                showSelectedNodes();

            if (algoSelection.getSelectedToggle().equals(dijkstraButton)) {
                shortestPathDijkstra();
            } else if (algoSelection.getSelectedToggle().equals(dfsButton)) {
                shortestPathDFS();
            } else if (algoSelection.getSelectedToggle().equals(bfsButton)) {
                shortestPathBFS();
            }
        }
    }

    public void shortestPathDijkstra() {
        mapPane.getChildren().removeIf(node -> node instanceof Line);

        Graph.CostedPath cpa = findCheapestPathDijkstra(graphNodes.get(startPointBox.getSelectionModel().getSelectedItem().getName()), endPointBox.getSelectionModel().getSelectedItem().getName(), getAvoidNodes( ));

        if (cpa == null || cpa.pathList.isEmpty()) {
            // Handle the case when no route is found
            systemMessage.setText("No route found while avoiding selected Landmarks");
            return; // Exit the method early
        }

        // If a route is found, continue with visualization
        for (GraphNode<?> n : cpa.pathList)
            clearFeedback();

        systemMessage.setText("Shortest Path from " + startPointBox.getSelectionModel().getSelectedItem().getName() + " to " + endPointBox.getSelectionModel().getSelectedItem().getName() + " using Dijkstra's Algorithm"
        +"\nRoute Cost: " + cpa.pathCost);



        for (int i = 0; i < cpa.pathList.size() - 1; i++) {
            GraphNode<?> firstNode = cpa.pathList.get(i);
            GraphNode<?> secondNode = cpa.pathList.get(i + 1);

            Line line = new Line(firstNode.getGraphX(), firstNode.getGraphY(), secondNode.getGraphX(), secondNode.getGraphY());
            line.setStroke(route);
            line.setStrokeWidth(3);

            mapPane.getChildren().add(line);
        }
    }



    public Set<GraphNode<String>> getAvoidNodes() {
        if(avoidBox.getSelectionModel().getSelectedItem() == null || avoidBox.getSelectionModel().getSelectedItem().getName().equals("AVOID NONE")) {
            if(avoidNodes != null) {
                avoidNodes.clear();
            }

        }

        return avoidNodes;
    }

    public Set<GraphNode<String>> getVisitNodes() {
        if(visitBox.getSelectionModel().getSelectedItem() == null || visitBox.getSelectionModel().getSelectedItem().getName().equals("MUST VISIT NONE")) {
            if(visitNodes != null) {
                visitNodes.clear();
            }

        }

        return visitNodes;
    }




    public void clearFeedback() {

        if (!(dfsListView.getItems() == null)) {
            dfsListView.getItems().clear();
        }
        if (!(systemMessage.getText() == null)) {
            systemMessage.setText(null);
        }
    }


    public void shortestPathBFS() {
        clearAllCircles();
        mapPane.getChildren().removeIf(node -> node instanceof Line);
        clearFeedback();

        // Get the start and end landmarks
        GraphNode<?> startLandmark = startPointBox.getValue();
        GraphNode<?> destLandmark = endPointBox.getValue();
         showSelectedNodes();
        // Check if both start and end landmarks are selected
        if (startLandmark != null && destLandmark != null) {
            // Retrieve the coordinates of the start and end landmarks
            Point2D start = new Point2D(startLandmark.getGraphX(), startLandmark.getGraphY());
            Point2D end = new Point2D(destLandmark.getGraphX(), destLandmark.getGraphY());

            // Perform BFS shortest path algorithm
            ArrayList<Point2D> path = searchBFS(start, end);

            // Calculate the total distance of the path
            double totalDistance = 0.0;
            for (int i = 0; i < path.size() - 1; i++) {
                Point2D p1 = path.get(i);
                Point2D p2 = path.get(i + 1);

                // Calculate the Euclidean distance between consecutive points
                double distance = p1.distance(p2);
                totalDistance += pixelToKilometers(distance, 100);
            }

            // Round the total distance to two decimal places

            String walkingTime = calculateAndFormatTime(totalDistance, 5.0);
            String drivingTime = calculateAndFormatTime(totalDistance, 50.0);
            String cyclingTime = calculateAndFormatTime(totalDistance, 20.0);
            // Set the formatted distance in the systemMessage
            systemMessage.setText("Estimated Distance from " + startLandmark.getName() + " to " + destLandmark.getName() + " is " + String.format("%.2f", totalDistance) + " km" + "\n\n"
            +"Walking Time:  "+walkingTime + "    |    " + "Driving: " + drivingTime + "    |    " + "Cycling: " + cyclingTime + "\n");



            // Add lines between  points in the path to the mapPane
            for (int i = 0; i < path.size() - 1; i++) {
                Point2D p1 = path.get(i);
                Point2D p2 = path.get(i + 1);

                // Create a line between the current point and the next point in the path
                Line line = new Line(p1.getX(), p1.getY(), p2.getX(), p2.getY());
                line.setStroke(route);
                line.setStrokeWidth(3);

                // Add the line to the mapPane
                mapPane.getChildren().add(line);
            }

            // Draw graph nodes
            for (GraphNode<String> node : graphNodes.values()) {
                drawNode(node,landMark);
            }
        }
    }
    private static String calculateAndFormatTime(double distance, double speed) {
        double timeInHours = distance / speed;
        int hours = (int) timeInHours;
        int minutes = (int) ((timeInHours - hours) * 60);
        return String.format("%d hr %d m", hours, minutes);
    }


    public double pixelToKilometers(double distanceInPixels, double pixelsPerKilometer) {
        // Convert pixels to kilometers
        double distanceInKilometers = distanceInPixels / pixelsPerKilometer;

        return Math.round(distanceInKilometers * 100.0) / 100.0;
    }


    public void shortestPathDFS() {
        mapPane.getChildren().removeIf(node -> node instanceof Line);

        List<Graph.CostedPath> cp = searchGraphDepthFirst(graphNodes.get(startPointBox.getSelectionModel().getSelectedItem().getName()),null,0,endPointBox.getSelectionModel().getSelectedItem().getName(), getAvoidNodes());
        clearFeedback();

        int pathCount = 0;// Reset pathCount before counting paths
        for (Graph.CostedPath costedPath : cp) {

            pathCount++;


            costedPath.setIndex(pathCount);
            systemMessage.setText("Routes From " + startPointBox.getSelectionModel().getSelectedItem().getName() + " TO " + endPointBox.getSelectionModel().getSelectedItem().getName() + " Using Depth First Search");
            dfsListView.getItems().add(costedPath);
        }

    }



    public void dfsPathDisplay() {
        if (dfsListView.getSelectionModel().getSelectedItem() != null) {
            mapPane.getChildren().removeIf(node -> node instanceof Line);

            CostedPath costedPath = dfsListView.getSelectionModel().getSelectedItem();

            for (GraphNode<?> n : costedPath.pathList)
                System.out.println(n.name);
           // System.out.println("The total path cost is: " + costedPath.pathCost);

            Random random = new Random();
            int blue = random.nextInt(106) + 150; // Generates random shades of blue to match UI Theme :)
            Color randomColor = Color.rgb(0, 0, blue);

            for (int i = 0; i < costedPath.pathList.size() - 1; i++) {
                GraphNode<?> firstNode = costedPath.pathList.get(i);
                GraphNode<?> secondNode = costedPath.pathList.get(i + 1);

                Line line = new Line(firstNode.getGraphX(), firstNode.getGraphY(), secondNode.getGraphX(), secondNode.getGraphY());
                line.setStroke(randomColor);
                line.setStrokeWidth(3);

                mapPane.getChildren().add(line);
            }
        } else {
            Utils.showWarningAlert("SELECT A ROUTE", "Please select a route from the routes provided to show on the map!");
        }
    }




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        routeFinder = this;
        populateDatabase();
        nodeTip = new Tooltip("TEST");
        mapPane.setOnMouseMoved(this::toolTipHover);
        Image mapViewImage = new Image(Objects.requireNonNull(BWConverter.class.getResourceAsStream("ParisVectorMap.png")));
        mapView.setImage(mapViewImage);



    }


    public void clearFields(){
        nameField.clear();
        landmarkBox.setSelected(false);
         junctionBox.setSelected(false);
    }


    public void closeApp(){
        System.exit(0);
    }

    public void minimiseApp(){

        mainStage.setIconified(true);
    }



}