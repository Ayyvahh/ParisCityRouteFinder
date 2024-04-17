package com.example.parisroutefinderdsaca2;

import java.util.ArrayList;
import java.util.List;

public class GraphNode<T> {
    public T data;
    public int nodeValue = Integer.MAX_VALUE;
    public List<GraphLink> adjList = new ArrayList<>();
    public boolean isLandmark;

    public GraphNode(T data, boolean isLandmark) {
        this.data = data;
        this.isLandmark = isLandmark;
    }

    public void connectToNodeDirected(GraphNode<T> destNode, int cost) {
        adjList.add(new GraphLink(destNode, cost));
    }

    public void connectToNodeUndirected(GraphNode<T> destNode, int cost) {
        adjList.add(new GraphLink(destNode, cost));
        destNode.adjList.add(new GraphLink(this,cost));
    }
}
