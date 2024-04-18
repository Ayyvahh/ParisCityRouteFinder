package com.example.parisroutefinderdsaca2.DataStructure;

import java.util.ArrayList;
import java.util.List;

public class GraphNode<String> {
    public String name;
    public int nodeValue = Integer.MAX_VALUE;
    public List<GraphLink> adjList = new ArrayList<>();
    public boolean isLandmark;
    public int graphX;
    public int graphY;


    public GraphNode(String name, boolean isLandmark, int graphX, int graphY) {
        this.name = name;
        this.isLandmark = isLandmark;
        this.graphX = graphX;
        this.graphY = graphY;
    }
//ALGORITHMS
    public void connectToNodeDirected(GraphNode<String> destNode, int cost) {
        adjList.add(new GraphLink(destNode, cost));
    }

    public void connectToNodeUndirected(GraphNode<String> destNode, int cost) {
        adjList.add(new GraphLink(destNode, cost));
        destNode.adjList.add(new GraphLink(this,cost));
    }
}
