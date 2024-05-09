package com.example.parisroutefinderdsaca2.DataStructure;

import com.example.parisroutefinderdsaca2.Utils;

import java.util.ArrayList;
import java.util.List;

public class GraphNode<String> {
    public String name;
    public int nodeValue = Integer.MAX_VALUE;
    public List<GraphLink> adjList = new ArrayList<>();
    private boolean isLandmark;
    GraphNode<String> previous;
    public int culturalSignificance;
    private int graphX;
    private int graphY;

    public GraphNode(String name, boolean isLandmark, int culturalSignificance, int graphX, int graphY) {
        this.name = name;
        this.isLandmark = isLandmark;
        this.culturalSignificance = culturalSignificance;
        this.graphX = graphX;
        this.graphY = graphY;
    }

    public GraphNode<String> getPrevious() {
        return previous;
    }

    public boolean isLandmark() {
        return isLandmark;
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

    public int getCulturalSignificance() {
        return culturalSignificance;
    }

    public List<GraphLink> getAdjList() {
        return adjList;
    }

    public void setAdjList(List<GraphLink> adjList) {
        this.adjList = adjList;
    }

    @Override
    public java.lang.String toString() {
        return name.toString();
    }

    public int getGraphY() {
        return graphY;
    }
    public void setGraphY(int graphY) {
        this.graphY = graphY;
    }

    public int connectToNodeUndirected(GraphNode<String> destNode, int cost) {
        adjList.add(new GraphLink(destNode, cost));
        destNode.adjList.add(new GraphLink(this,cost));
        return cost;
    }





}
