package com.example.parisroutefinderdsaca2;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

import static com.example.parisroutefinderdsaca2.Graph.findCheapestPathDijkstra;

public class RouteFinder {
    @FXML
    public ImageView mapView;

    @FXML
    public void scene2() throws IOException {
        Main.switchToSecondScene();
    }

    public void mapClicked(MouseEvent event) {
        System.out.println((int) event.getX() + ", " + (int) event.getY());
    }

    public void dijkstraTest() {
        GraphNode<String> a = new GraphNode<>("Eiffel Tower", true,1,1); //1,1 PLACEHOLDER
        GraphNode<String> b = new GraphNode<>("Arc de Triomphe", true,1,1);
        GraphNode<String> c = new GraphNode<>("The Louvre", true,1,1);
        GraphNode<String> d = new GraphNode<>("Grand Palais", true,1,1);
        GraphNode<String> e = new GraphNode<>("Notre-Dame Cathedral", true,1,1);
        GraphNode<String> f = new GraphNode<>("Champs-Élysées", true,1,1);
        GraphNode<String> g = new GraphNode<>("Opera Garnier", true,1,1);
        GraphNode<String> h = new GraphNode<>("Place de la Concorde", true,1,1);
        GraphNode<String> i = new GraphNode<>("River Seine", true,1,1);
        GraphNode<String> j = new GraphNode<>("Basilica of the Sacré-Coeur", true,1,1);
        GraphNode<String> k = new GraphNode<>("The Centre Pompidou", true,1,1);
        GraphNode<String> l = new GraphNode<>("Pont Alexandre III", true,1,1);
        GraphNode<String> m = new GraphNode<>("Musée d’Orsay", true,1,1);

        a.connectToNodeUndirected(b, 5); //adding streets between landmarks/junctions
        a.connectToNodeUndirected(c, 9);
        b.connectToNodeUndirected(c, 2);
        b.connectToNodeUndirected(d, 6);
        c.connectToNodeUndirected(e, 5);
        d.connectToNodeUndirected(h, 8);
        d.connectToNodeUndirected(g, 9);
        e.connectToNodeUndirected(g, 3);
        e.connectToNodeUndirected(f, 7);
        f.connectToNodeUndirected(g, 6);
        g.connectToNodeUndirected(h, 2);
        h.connectToNodeUndirected(i, 5);
        i.connectToNodeUndirected(j, 3);
        j.connectToNodeUndirected(k, 1);
        k.connectToNodeUndirected(l, 3);
        l.connectToNodeUndirected(m, 2);

        System.out.println("The cheapest path from Eiffel Tower to Musée d’Orsay");
        System.out.println("using Dijkstra's algorithm:");
        System.out.println("-------------------------------------");

        Graph.CostedPath cpa = findCheapestPathDijkstra(a, "Musée d’Orsay");

        assert cpa != null;
        for (GraphNode<?> n : cpa.pathList)
            System.out.println(n.name);
        System.out.println("\nThe total path cost is: " + cpa.pathCost);
    }
}