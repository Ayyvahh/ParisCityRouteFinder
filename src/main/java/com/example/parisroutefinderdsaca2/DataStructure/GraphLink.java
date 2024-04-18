package com.example.parisroutefinderdsaca2.DataStructure;

public class GraphLink {
    public GraphNode<?> destNode;
    public int cost;
    public float distance;

    public GraphLink(GraphNode<?> destNode, int cost) {
        this.destNode = destNode;
        this.cost = cost;
        //this.distance = distance; d=√((x2 – x1)² + (y2 – y1)²)
    }
}
