package com.example.parisroutefinderdsaca2.DataStructure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GraphNodeTest {
    Map<String, GraphNode<String>> nodes = new HashMap<>();

    @BeforeEach
    void setUp() {
        nodes.put("N1" , new GraphNode<>("N1", false, 100, 0, 0));
        nodes.put("N2" , new GraphNode<>("N2", false, 23, 1, 1));
        nodes.put("N3" , new GraphNode<>("N3", false, 4, 100, 100));
        nodes.put("N4" , new GraphNode<>("N4", true, 0, 450, 300));
        nodes.put("N5" , new GraphNode<>("N5", false, 122, 225, 500));
    }

    @Test
    void testIsLandmark() {
        assertFalse(nodes.get("N1").isLandmark());
        assertFalse(nodes.get("N2").isLandmark());
        assertFalse(nodes.get("N3").isLandmark());
        assertFalse(nodes.get("N5").isLandmark());

        assertTrue(nodes.get("N4").isLandmark());
    }

    @Test
    void testConnectToNodeUndirected() {
        int cost = nodes.get("N1").connectToNodeUndirected(nodes.get("N2"),10);
        assertEquals(10, cost);

        List<GraphLink> adjList1 = nodes.get("N1").getAdjList();
        List<GraphLink> adjList2 = nodes.get("N2").getAdjList();

        assertEquals(1, adjList1.size());
        assertEquals(1, adjList1.size());

        GraphLink link1 = adjList1.get(0);
        GraphLink link2 = adjList2.get(0);

        assertEquals(nodes.get("N2"), link1.getDestNode());
        assertEquals(10, link1.getCost());

        assertEquals(nodes.get("N1"), link2.getDestNode());
        assertEquals(10, link2.getCost());
    }

    @Test
    void testGetName() {
        assertEquals("N1", nodes.get("N1").getName());
        assertEquals("N2", nodes.get("N2").getName());
        assertEquals("N3", nodes.get("N3").getName());
        assertEquals("N4", nodes.get("N4").getName());
        assertEquals("N5", nodes.get("N5").getName());

        assertNotEquals("N1", nodes.get("N2").getName());
        assertNotEquals("N2", nodes.get("N3").getName());
        assertNotEquals("N3", nodes.get("N4").getName());
        assertNotEquals("N4", nodes.get("N5").getName());
        assertNotEquals("N5", nodes.get("N1").getName());
    }

    @Test
    void testSetName() {
        assertEquals("N1", nodes.get("N1").getName());
        nodes.get("N1").setName("newNode1");
        assertEquals("newNode1", nodes.get("N1").getName());

        assertEquals("N2", nodes.get("N2").getName());
        nodes.get("N2").setName("newNode2");
        assertEquals("newNode2", nodes.get("N2").getName());

        assertEquals("N3", nodes.get("N3").getName());
        nodes.get("N3").setName("newNode3");
        assertEquals("newNode3", nodes.get("N3").getName());
    }

    @Test
    void testSetCulturalSignificance() {
        assertEquals(100, nodes.get("N1").getCulturalSignificance());
        nodes.get("N1").setCulturalSignificance(50);
        assertEquals(50, nodes.get("N1").getCulturalSignificance());

        assertEquals(23, nodes.get("N2").getCulturalSignificance());
        nodes.get("N2").setCulturalSignificance(230);
        assertEquals(230, nodes.get("N2").getCulturalSignificance());

        assertEquals(4, nodes.get("N3").getCulturalSignificance());
        nodes.get("N3").setCulturalSignificance(444);
        assertEquals(444, nodes.get("N3").getCulturalSignificance());
    }
}