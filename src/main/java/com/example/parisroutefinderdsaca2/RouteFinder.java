package com.example.parisroutefinderdsaca2;

import com.example.parisroutefinderdsaca2.DataStructure.Graph;
import com.example.parisroutefinderdsaca2.DataStructure.GraphLink;
import com.example.parisroutefinderdsaca2.DataStructure.GraphNode;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
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

import java.io.*;
import java.net.URL;
import java.util.*;

import static com.example.parisroutefinderdsaca2.DataStructure.Graph.*;
import static com.example.parisroutefinderdsaca2.Main.mainStage;

public class RouteFinder implements Initializable {
    public static RouteFinder routeFinder;

    /*-----------JAVAFX------------*/
    public AnchorPane mapPane = new AnchorPane();
    public ComboBox<GraphNode<String>> startPointBox = new ComboBox<>();
    public ComboBox<GraphNode<String>> avoidBox = new ComboBox<>();
    public ComboBox<GraphNode<String>> endPointBox = new ComboBox<>();

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
    public ComboBox<GraphNode<String>> waypointsBox;
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
    public List<GraphNode<String>> waypointNodes = new ArrayList<>();

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
            waypointsBox.getItems().add(new GraphNode<>("MUST VISIT NONE", false, 0, 0, 0));

            for (GraphNode<String> node : graphNodes.values()) {
                drawNode(node,landMark);

                if (node.isLandmark()) {
                    startPointBox.getItems().add(node);
                    endPointBox.getItems().add(node);
                    avoidBox.getItems().add(node);
                    waypointsBox.getItems().add(node);

                }
            }
            isMapPopulated = true;
        } else {
            System.out.println("Map already populated!");
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
        if (startPointBox == null || endPointBox == null) return;

        clearAllCircles();
        String startPointName = startPointBox.getSelectionModel().getSelectedItem().getName();
        String endPointName = endPointBox.getSelectionModel().getSelectedItem().getName();

        for (GraphNode<String> node : graphNodes.values()) {
            if (avoidNodes != null && avoidNodes.contains(node)) drawNode(node, Color.RED);
            else if (node.getName().equals(startPointName)) drawNode(node, route);
            else if (node.getName().equals(endPointName)) drawNode(node, route);
            else drawNode(node, landMark);

            if (waypointNodes != null && waypointNodes.contains(node)) drawNode(node, visit);
        }
    }

    private void clearAllCircles() {
        mapPane.getChildren().removeIf(node -> node instanceof Circle);
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
            nodeTip.setText(landmarkOn.getName() + ",\n Historic Significance: " + landmarkOn.getCulturalSignificance() +
                    "\nX :  " + landmarkOn.getGraphX() +
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

    public void addToAvoid() {
        if (startPointBox.getSelectionModel().getSelectedItem() == null || endPointBox.getSelectionModel().getSelectedItem() == null) {
            Utils.showWarningAlert("PLEASE SELECT A START AND END POINT","Please select a start and end point before adding locations to avoid!");
        } else if (avoidBox.getSelectionModel().getSelectedItem() == null) {
            Utils.showWarningAlert("PLEASE SELECT A LOCATION TO AVOID", "Please select a location to avoid!");
        } else {
            GraphNode<String> selectedItem = avoidBox.getSelectionModel().getSelectedItem();

            if (selectedItem != null && !waypointNodes.contains(selectedItem) && !startPointBox.getSelectionModel().getSelectedItem().getName().equals(selectedItem.getName()) && !endPointBox.getSelectionModel().getSelectedItem().getName().equals(selectedItem.getName())) {
                for (GraphNode<String> g : avoidNodes) {
                    if (g.equals(selectedItem)) {
                        Utils.showWarningAlert("ALREADY AVOIDING LOCATION", "You are already avoiding " + g.getName() + "!");
                        return;
                    }
                }

                avoidNodes.add(selectedItem);
                printAvoidNodes();  // Print the updated avoidNodes
                showSelectedNodes();  // Show the newly avoided node
            } else Utils.showWarningAlert("CANNOT AVOID LOCATION", "You have entered a location that is either: the start point, the end point or a waypoint!");
        }
    }

    public void addToVisit() {
        if (startPointBox.getSelectionModel().getSelectedItem() == null || endPointBox.getSelectionModel().getSelectedItem() == null) {
            Utils.showWarningAlert("PLEASE SELECT A START AND END POINT","Please select a start and end point before adding a waypoint!");
        } else if (waypointsBox.getSelectionModel().getSelectedItem() == null) {
            Utils.showWarningAlert("PLEASE SELECT A WAYPOINT", "Please select a waypoint!");
        } else {
            GraphNode<String> selectedItem = waypointsBox.getSelectionModel().getSelectedItem();

            if (selectedItem != null && !avoidNodes.contains(selectedItem) && !startPointBox.getSelectionModel().getSelectedItem().getName().equals(selectedItem.getName()) && !endPointBox.getSelectionModel().getSelectedItem().getName().equals(selectedItem.getName())) {
                for (GraphNode<String> g : waypointNodes) {
                    if (g.equals(selectedItem)) {
                        Utils.showWarningAlert("ALREADY A WAYPOINT", g.getName() + " is already a waypoint!");
                        return;
                    }
                }

                waypointNodes.add(selectedItem);
                printVisitNodes();  // Print the updated visitNodes
                showSelectedNodes();  // Show the newly visited node
            } else Utils.showWarningAlert("CANNOT ADD WAYPOINT", "You have entered a location that is either: the start point, the end point or a location you are avoiding!");
        }
    }

    private void printAvoidNodes() {
        StringBuilder s = new StringBuilder("AVOIDING :  ");
        // Check if the adjacent list is not empty
        if (!avoidNodes.isEmpty()) {
            for (GraphNode<String> a : avoidNodes) s.append(a.getName()).append(",  ");
            avoidingLabel.setText(s.toString());
        } else avoidingLabel.setText(null);
    }

    private void printVisitNodes() {
        StringBuilder s = new StringBuilder("VISITING :  ");

        if (!waypointNodes.isEmpty()) {
            for (GraphNode<String> a : waypointNodes) s.append(a.getName()).append(",  ");
            visitLabel.setText(s.toString());
        } else visitLabel.setText(null);
    }

    public void removeAvoid() {
       GraphNode<String> selected = avoidBox.getSelectionModel().getSelectedItem();

        if(!avoidNodes.isEmpty()) {
            if (avoidNodes.contains(selected)) {
                avoidNodes.remove(selected);
                clearFeedback();
                printAvoidNodes();
                showSelectedNodes();
            } else {
                Utils.showWarningAlert("CANNOT REMOVE LOCATION TO AVOID!", "Cannot remove a location to avoid that does not exist!");
            }
        } else {
            Utils.showWarningAlert("NO LOCATIONS TO AVOID!", "There are currently no locations being avoided, so there is nothing to remove!");
        }
    }

    public void removeWaypoint() {
        GraphNode<String> selected = waypointsBox.getSelectionModel().getSelectedItem();

        if(!waypointNodes.isEmpty()) {
            if (waypointNodes.contains(selected)) {
                waypointNodes.remove(selected);
                clearFeedback();
                printVisitNodes();
                showSelectedNodes();
            } else {
                Utils.showWarningAlert("CANNOT REMOVE WAYPOINT!", "Cannot remove a waypoint that does not exist!");
            }
        } else {
            Utils.showWarningAlert("NO WAYPOINTS!", "There are currently no waypoints, so there is nothing to remove!");
        }
    }

    public void findHistoricRouteDijkstra() {
        int val = (int) historicalVal.getValue();
        Set<GraphNode<String>> nonMatchNodes = new HashSet<>();

        if (startPointBox.getSelectionModel().getSelectedItem() == null || endPointBox.getSelectionModel().getSelectedItem() == null) {
            Utils.showWarningAlert("PLEASE SELECT A START AND END POINT","Please select a start and end point before generating a historical route!");
        } else {
            showSelectedNodes();
            mapPane.getChildren().removeIf(node -> node instanceof Line);

            for (GraphNode<String> node : graphNodes.values()) {
                // Exclude nodes with a historical value different from the specified value, except for 0, start, and destination nodes
                if (node.getCulturalSignificance() > val &&
                        node.getCulturalSignificance() != 0 &&
                        !node.equals(graphNodes.get(startPointBox.getSelectionModel().getSelectedItem().getName())) &&
                        !node.equals(graphNodes.get(endPointBox.getSelectionModel().getSelectedItem().getName()))) {
                    nonMatchNodes.add(node);

                    System.out.println("Added node to avoid list: " + node.getName());
                }
            }

            for (GraphNode<String> node : nonMatchNodes) {
                if (!avoidNodes.contains(node) && !waypointNodes.contains(node)) {
                    drawNode(node, Color.BLACK);
                }
            }

            // Check if avoidNodes list is correctly populated
            System.out.println("Nodes to avoid: " + nonMatchNodes);

            // Finding the shortest path using Dijkstra's algorithm
            Graph.CostedPath cpa = findCheapestPathDijkstra(
                    graphNodes.get(startPointBox.getSelectionModel().getSelectedItem().getName()),
                    endPointBox.getSelectionModel().getSelectedItem().getName(),
                    nonMatchNodes);

            // Handling cases where no route is found
            if (cpa == null || cpa.pathList.isEmpty()) {
                systemMessage.setText("No route found while avoiding selected Landmarks");
                return;
            }

            // Visualizing the shortest path
            for (GraphNode<?> ignored : cpa.pathList) clearFeedback();

            systemMessage.setText("Shortest Path From " + startPointBox.getSelectionModel().getSelectedItem().getName() +
                    " to " + endPointBox.getSelectionModel().getSelectedItem().getName() +
                    " with a Historical Significance of " + val +
                    "\nRoute Cost: " + cpa.pathCost);

            for (int i = 0; i < cpa.pathList.size() - 1; i++) {
                GraphNode<?> firstNode = cpa.pathList.get(i);
                GraphNode<?> secondNode = cpa.pathList.get(i + 1);

                Line line = new Line(firstNode.getGraphX(), firstNode.getGraphY(),
                        secondNode.getGraphX(), secondNode.getGraphY());
                line.setStroke(route);
                line.setStrokeWidth(3);

                mapPane.getChildren().add(line);
            }
        }
    }

    public void populateDatabase() {
        try {
            loadXML();
            System.out.println("Database loaded!");
            populateMap();
        } catch (Exception e) {
            System.err.println("Error writing from file: " + e);
        }
    }

    public void findRoute() {
        if (algoSelection.getSelectedToggle() == null) Utils.showWarningAlert("SELECT AN ALGORITHM", "Please select a radio button of the algorithm you wish to use!");
        else {
            if (startPointBox.getSelectionModel().getSelectedItem() == null || endPointBox.getSelectionModel().getSelectedItem() == null) {
                Utils.showWarningAlert("ERROR", "SELECT START AND END POINT");
                return;
            }

            showSelectedNodes();

            if (algoSelection.getSelectedToggle().equals(dijkstraButton)) shortestPathDijkstra();
            else if (algoSelection.getSelectedToggle().equals(dfsButton)) shortestPathDFS();
            else if (algoSelection.getSelectedToggle().equals(bfsButton)) shortestPathBFS();
        }
    }

    public void shortestPathDijkstra() {
        mapPane.getChildren().removeIf(node -> node instanceof Line);

        // Get start and end nodes
        GraphNode<String> startNode = graphNodes.get(startPointBox.getSelectionModel().getSelectedItem().getName());
        GraphNode<String> endNode = graphNodes.get(endPointBox.getSelectionModel().getSelectedItem().getName());

        // Check if waypoints are specified
        List<GraphNode<String>> waypoints = new ArrayList<>(waypointNodes); // Convert to List<GraphNode<String>>

        // Initialize the path list
        List<GraphNode<?>> pathList = new ArrayList<>(); // Make sure pathList is of the same type as startNode and endNode

        // If waypoints are specified, find the path considering waypoints
        if (!waypoints.isEmpty()) {
            // Add start and end nodes as waypoints if not already included
            if (!waypoints.contains(startNode)) waypoints.add(0, startNode);
            if (!waypoints.contains(endNode)) waypoints.add(endNode);

            for (int i = 0; i < waypoints.size() - 1; i++) {
                Graph.CostedPath cpa = findCheapestPathDijkstra(waypoints.get(i), waypoints.get(i + 1).getName(), avoidNodes);
                if (cpa == null || cpa.pathList.isEmpty()) {
                    // Handle the case when no route is found
                    systemMessage.setText("No route found while avoiding selected landmarks");
                    return; // Exit the method early
                }
                // Add the path to the overall path list
                pathList.addAll(cpa.pathList);
            }
        } else {
            // No waypoints specified, find the direct path between start and end nodes
            Graph.CostedPath cpa = findCheapestPathDijkstra(startNode, endNode.getName(), avoidNodes);
            if (cpa == null || cpa.pathList.isEmpty()) {
                // Handle the case when no route is found
                systemMessage.setText("No route found while avoiding selected landmarks");
                return; // Exit the method early
            }
            pathList = cpa.pathList;
        }
        // Visualize the path
        visualizePath(pathList);
    }

    private void visualizePath(List<GraphNode<?>> pathList) {
        for (int i = 0; i < pathList.size() - 1; i++) {
            GraphNode<?> firstNode = pathList.get(i);
            GraphNode<?> secondNode = pathList.get(i + 1);

            Line line = new Line(firstNode.getGraphX(), firstNode.getGraphY(), secondNode.getGraphX(), secondNode.getGraphY());
            line.setStroke(route);
            line.setStrokeWidth(3);

            mapPane.getChildren().add(line);
        }
        systemMessage.setText("Shortest Path from " + startPointBox.getSelectionModel().getSelectedItem().getName() + " to " + endPointBox.getSelectionModel().getSelectedItem().getName() + " using Dijkstra's Algorithm");
    }

    public Set<GraphNode<String>> getAvoidNodes() {
        if(avoidBox.getSelectionModel().getSelectedItem() == null || avoidBox.getSelectionModel().getSelectedItem().getName().equals("AVOID NONE")) if(avoidNodes != null) avoidNodes.clear();
        return avoidNodes;
    }

    public void clearFeedback() {
        if (!(dfsListView.getItems() == null)) dfsListView.getItems().clear();
        if (!(systemMessage.getText() == null)) systemMessage.setText(null);
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

    public static double pixelToKilometers(double distanceInPixels, double pixelsPerKilometer) {
        // Convert pixels to kilometers
        double distanceInKilometers = distanceInPixels / pixelsPerKilometer;

        return Math.round(distanceInKilometers * 100.0) / 100.0;
    }

    public void shortestPathDFS() {
        List<Graph.CostedPath> cp = searchGraphDepthFirst(graphNodes.get(startPointBox.getSelectionModel().getSelectedItem().getName()),null,0,endPointBox.getSelectionModel().getSelectedItem().getName(), getAvoidNodes());

        mapPane.getChildren().removeIf(node -> node instanceof Line);
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
        } else Utils.showWarningAlert("SELECT A ROUTE", "Please select a route from the routes provided to show on the map!");
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

    public void closeApp(){
        System.exit(0);
    }

    public void minimiseApp() {
        mainStage.setIconified(true);
    }
}