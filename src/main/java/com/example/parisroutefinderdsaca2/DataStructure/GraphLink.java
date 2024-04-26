package com.example.parisroutefinderdsaca2.DataStructure;

public class GraphLink {
    public GraphNode<?> destNode;
    public int cost;

    public GraphLink(GraphNode<?> destNode, int cost) {
        this.destNode = destNode;
        this.cost = cost;
    }

    public GraphNode<?> getDestNode() {
        return destNode;
    }

    public void setDestNode(GraphNode<?> destNode) {
        this.destNode = destNode;
    }
    public int getCost() {
        return cost;
    }
}
