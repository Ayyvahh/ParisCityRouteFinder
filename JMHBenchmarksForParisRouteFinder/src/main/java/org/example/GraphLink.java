package org.example;

public class GraphLink<T> {
    public GraphNode<T> destNode;
    public int cost;

    public GraphLink(GraphNode<T> destNode, int cost) {
        this.destNode = destNode;
        this.cost = cost;
    }

    public GraphNode<T> getDestNode() {
        return destNode;
    }
    public void setDestNode(GraphNode<T> destNode) {
        this.destNode = destNode;
    }

    public int getCost() {
        return cost;
    }
}