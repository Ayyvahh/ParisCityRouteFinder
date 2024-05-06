package com.example.parisroutefinderdsaca2.DataStructure;

import com.example.parisroutefinderdsaca2.BWConverter;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;

import java.net.URL;
import java.util.*;

public class Graph implements Initializable {
    public static Graph graph;
    List<CostedPath> allPaths;

    public static class CostedPath{
        public int pathCost=0;
        public List<GraphNode<?>> pathList=new ArrayList<>();
        public int index; //Just for displaying in listView, Ex. Route 1, Route 2 etc. for clarity
        @Override
        public String toString() {

            String pathString = "[ ROUTE " + index + " ]  -   PATH COST : " + pathCost + "\n\n";
            for (int i = 0; i < pathList.size(); i++) {
                GraphNode<?> node = pathList.get(i);
                pathString += node.getName();

                if (i < pathList.size() - 1) {
                    pathString += "  ->  ";
                }
            }
            return pathString + "\n\n";
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }

    public static <T> CostedPath findCheapestPathDijkstra(GraphNode<T> startNode, T lookingFor, Set<GraphNode<T>> nodesToAvoid) {
    // Initialize the CostedPath object and set the starting node's nodeValue to 0
    CostedPath cp = new CostedPath();
    LinkedList<GraphNode<T>> encountered = new LinkedList<>();
    LinkedList<GraphNode<T>> notEncountered = new LinkedList<>();
    startNode.nodeValue = 0;
    notEncountered.add(startNode);
    GraphNode<T> currentNode;

    // Check if nodesToAvoid is null, treat it as an empty set
    if (nodesToAvoid == null) {
        nodesToAvoid = new HashSet<>();
    }

    do {
        // Get the current node and remove it from the notEncountered list
        currentNode = notEncountered.removeFirst();
        encountered.add(currentNode);

        if (currentNode.name.equals(lookingFor)) {
            // If the current node is the lookingFor node, add it to the path and return the CostedPath object
            cp.pathList.add(currentNode);
            cp.pathCost = currentNode.nodeValue;

            while (currentNode!= startNode) {
                // Loop through the encountered nodes and check if the previous node is in the path
                boolean foundPrevPathNode = false;
                for (GraphNode<T> n : encountered) {
                    for (GraphLink e : n.adjList) {
                        // IF THE NEXT node is NOT the current node AND IT'S NOT A NODE TO AVOID
                        if (e.destNode == currentNode && currentNode.nodeValue - e.cost == n.nodeValue &&!nodesToAvoid.contains(n)) {
                            // Add the previous node to the path and set the current node to it
                            cp.pathList.add(0,n);
                            currentNode = n;
                            foundPrevPathNode = true;
                            break;
                        }
                    }
                    if (foundPrevPathNode) {
                        break;
                    }
                }
            }
            // Set all the nodeValues to Integer.MAX_VALUE to indicate that they have been visited
            for (GraphNode<T> n : encountered) {
                n.nodeValue = Integer.MAX_VALUE;
            }
            for (GraphNode<T> n : notEncountered) {
                n.nodeValue = Integer.MAX_VALUE;
            }
            return cp;
        }

        // Loop through the current node's adjacent nodes and update their nodeValues if they haven't been visited
        for (GraphLink e : currentNode.adjList) {
            if (!encountered.contains(e.destNode) &&!nodesToAvoid.contains(e.destNode)) {
                e.destNode.nodeValue = Integer.min(e.destNode.nodeValue, currentNode.nodeValue + e.cost);
                if (!notEncountered.contains(e.destNode)) {
                    notEncountered.add(e.destNode);
                }
            }
        }
        // Sort the notEncountered list based on the nodeValues
        notEncountered.sort(Comparator.comparingInt(n -> n.nodeValue));
    } while (!notEncountered.isEmpty());
    // If no path was found, return null
    return null;
}

    public static <T> List<CostedPath> searchGraphDepthFirst(GraphNode<?> from, List<GraphNode<?>> encountered, int totalCost, T lookingfor, Set<GraphNode<T>>nodesToAvoid) {
        List<CostedPath> allPaths = new ArrayList<>();

        if (from.name.equals(lookingfor)) {
            CostedPath cp = new CostedPath();
            cp.pathList.add(from);
            cp.pathCost = totalCost;
            allPaths.add(cp);
            return allPaths;
        }
        if (nodesToAvoid == null) {
            nodesToAvoid = new HashSet<>();
        }

        if (encountered == null) encountered = new ArrayList<>();
        encountered.add(from);

        for (GraphLink adjLink : from.adjList) {
            if (!encountered.contains(adjLink.destNode) && !nodesToAvoid.contains(adjLink.destNode)) {
                List<CostedPath> pathsFromAdj = searchGraphDepthFirst(adjLink.destNode, encountered, totalCost + adjLink.cost, lookingfor, nodesToAvoid);
                for (CostedPath path : pathsFromAdj) {
                    path.pathList.add(0, from);
                    allPaths.add(path);
                }
            }
        }
        return allPaths;
    }

    public static ArrayList<Point2D> searchBFS(Point2D start, Point2D end) {
        Image im = BWConverter.convert();
        int width = (int) im.getWidth();
        int height = (int) im.getHeight();

        Set<Point2D> visitedPixels = new HashSet<>();
        Queue<Point2D> queue = new LinkedList<>();
        Map<Point2D, Point2D> cameFrom = new HashMap<>();

        queue.add(start);
        visitedPixels.add(start);

        while (!queue.isEmpty()) {
            Point2D current = queue.poll();
            if (current.equals(end)) {
                ArrayList<Point2D> path = new ArrayList<>();
                while (current != null) {
                    path.add(0, current);
                    current = cameFrom.get(current);
                }
                return path;
            }

            for (Point2D neighbor : getNeighbors(current, width, height)) {
                if (!visitedPixels.contains(neighbor)) {
                    queue.add(neighbor);
                    visitedPixels.add(neighbor);
                    cameFrom.put(neighbor, current);
                }
            }
        }
        return new ArrayList<>();
    }

    private static List<Point2D> getNeighbors(Point2D current, int width, int height) {
        List<Point2D> neighbors = new ArrayList<>();
        int[] pixelArray = BWConverter.getPixelArray();
        // Define the changes in x and y directions for moving in four directions: up, left, down, and right
        int[] dx = {-1, 0, 1, 0};
        int[] dy = {0, -1, 0, 1};

        int currX = (int) current.getX();
        int currY = (int) current.getY();

        // Explore neighboring pixels
        for (int i = 0; i < dx.length; i++) {
            int x = currX + dx[i];
            int y = currY + dy[i];

            // Check if the neighboring pixel is within bounds
            if (x >= 0 && x < width && y >= 0 && y < height) {
                int pixelValue = pixelArray[y * width + x];
                // Check if the pixel value indicates a white color
                if (pixelValue == 0) { // Assuming 0 represents white in the pixel array
                    Point2D neighbor = new Point2D(x, y);
                    neighbors.add(neighbor);
                }
            }
        }

        return neighbors;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        graph = this;
    }
}