package com.example.parisroutefinderdsaca2;

import com.example.parisroutefinderdsaca2.DataStructure.GraphNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RouteFinderTest {
    Map<String, GraphNode<String>> nodes = new HashMap<>();

    int calculateDistance(GraphNode<String> n1, GraphNode<String> n2) {
        int x1 = n1.getGraphX();
        int x2 = n2.getGraphX();
        int y1 = n1.getGraphY();
        int y2 = n2.getGraphY();

        return (int) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    @BeforeEach
    void setUp() {

        nodes.put("N1" , new GraphNode<>("N1", false, 0, 0, 0));
        nodes.put("N2" , new GraphNode<>("N2", false, 0, 1, 1));
        nodes.put("N3" , new GraphNode<>("N3", false, 0, 100, 100));
        nodes.put("N4" , new GraphNode<>("N4", false, 0, 450, 300));
        nodes.put("N5" , new GraphNode<>("N5", false, 0, 225, 500));

    }

    @AfterEach
    void tearDown() {
        nodes.clear();
    }

    @Test
    void testDistanceCalculation() {
        /*Running multiple different calculations and ensuring they match up with manually calculated values to test that method is accurate*/

         int distance = nodes.get("N1").connectToNodeUndirected(nodes.get("N2"), calculateDistance(nodes.get("N1"),nodes.get("N2")));
                assertEquals(1, distance);

                distance = nodes.get("N1").connectToNodeUndirected(nodes.get("N3"), calculateDistance(nodes.get("N1"),nodes.get("N3")));
                assertEquals(141, distance);

                distance = nodes.get("N4").connectToNodeUndirected(nodes.get("N3"), calculateDistance(nodes.get("N4"),nodes.get("N3")));
                assertEquals(403, distance);

                distance = nodes.get("N5").connectToNodeUndirected(nodes.get("N4"), calculateDistance(nodes.get("N5"),nodes.get("N4")));
                assertEquals(301, distance);
            }



}