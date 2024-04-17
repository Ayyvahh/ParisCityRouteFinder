package com.example.parisroutefinderdsaca2;

import java.util.ArrayList;
import java.util.List;

public class GraphNode<T> {
    public T name;
    public int nodeValue = Integer.MAX_VALUE;
    public List<GraphLink> adjList = new ArrayList<>();
    public boolean isLandmark;
    public int graphX;
    public int graphY;

    public GraphNode(T name, boolean isLandmark, int graphX, int graphY) {
        this.name = name;
        this.isLandmark = isLandmark;
        this.graphX = graphX;
        this.graphY = graphY;
    }

    public void connectToNodeDirected(GraphNode<T> destNode, int cost) {
        adjList.add(new GraphLink(destNode, cost));
    }

    public void connectToNodeUndirected(GraphNode<T> destNode, int cost) {
        adjList.add(new GraphLink(destNode, cost));
        destNode.adjList.add(new GraphLink(this,cost));
    }
}
