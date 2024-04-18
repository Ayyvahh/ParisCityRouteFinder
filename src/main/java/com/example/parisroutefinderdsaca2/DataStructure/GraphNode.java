package com.example.parisroutefinderdsaca2.DataStructure;

import java.util.ArrayList;
import java.util.List;

public class GraphNode<String> {
    public String name;
    public int nodeValue = Integer.MAX_VALUE;
    public List<GraphLink> adjList = new ArrayList<>();
    private boolean isLandmark;
    private int graphX;
    private int graphY;

    public GraphNode(String name, boolean isLandmark, int graphX, int graphY) {
        this.name = name;
        this.isLandmark = isLandmark;
        this.graphX = graphX;
        this.graphY = graphY;
    }

    public boolean isLandmark() {
        return isLandmark;
    }
    public void setLandmark(boolean landmark) {
        isLandmark = landmark;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getGraphX() {
        return graphX;
    }
    public void setGraphX(int graphX) {
        this.graphX = graphX;
    }

    public int getGraphY() {
        return graphY;
    }
    public void setGraphY(int graphY) {
        this.graphY = graphY;
    }

    public void connectToNodeDirected(GraphNode<String> destNode, int cost) {
        adjList.add(new GraphLink(destNode, cost));
    }

    public void connectToNodeUndirected(GraphNode<String> destNode, int cost) {
        adjList.add(new GraphLink(destNode, cost));
        destNode.adjList.add(new GraphLink(this,cost));
    }
}
