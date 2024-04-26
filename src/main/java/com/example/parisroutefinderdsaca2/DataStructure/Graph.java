package com.example.parisroutefinderdsaca2.DataStructure;

import javafx.fxml.Initializable;

import java.net.URL;
import java.util.*;

public class Graph implements Initializable {
    public static Graph graph;
    List<CostedPath> allPaths;

    public static class CostedPath{
        public int pathCost=0;
        public List<GraphNode<?>> pathList=new ArrayList<>();
        public int index; //Just for displaying in listView, Ex. Route 1, Route 2 etc. for clarity
        @Override
        public String toString() {

            String pathString = "[ ROUTE " + index + " ]  -   PATH COST : " + pathCost + "\n\n";
            for (int i = 0; i < pathList.size(); i++) {
                GraphNode<?> node = pathList.get(i);
                pathString += node.getName();

                if (i < pathList.size() - 1) {
                    pathString += "  ->  ";
                }
            }
            return pathString + "\n\n";
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }

    public static <T> CostedPath findCheapestPathDijkstra(GraphNode<?> startNode, T lookingFor){
        CostedPath cp=new CostedPath(); //Create result object for cheapest path
        LinkedList<GraphNode<?>> encountered=new LinkedList<>(), notEncountered=new LinkedList<>(); //Create encountered/notEncountered lists
        startNode.nodeValue=0; //Set the starting node value to zero
        notEncountered.add(startNode); //Add the start node as the only value in the notEncountered list to start
        GraphNode<?> currentNode;

        do{ //Loop until notEncountered list is empty
            currentNode=notEncountered.removeFirst(); //Get the first notEncountered node (sorted list, so will have the lowest value)
            encountered.add(currentNode); //Record current node in encountered list

            if(currentNode.name.equals(lookingFor)){ //Found goal - assemble path list back to start and return it
                cp.pathList.add(currentNode); //Add the current (goal) node to the result list (only element)
                cp.pathCost=currentNode.nodeValue; //The total cheapest path cost is the node value of the current/goal node

                while(currentNode!=startNode) { //While we're not back to the start node...
                    boolean foundPrevPathNode=false; //Use a flag to identify when the previous path node is identified
                    for(GraphNode<?> n : encountered) { //For each node in the encountered list...
                        for(GraphLink e : n.adjList) //For each edge from that node...
                            if(e.destNode==currentNode && currentNode.nodeValue-e.cost==n.nodeValue){ //If that edge links to the
                                //current node and the difference in node values is the cost of the edge -> found path node!
                                cp.pathList.addFirst(n); //Add the identified path node to the front of the result list
                                currentNode=n; //Move the currentNode reference back to the identified path node
                                foundPrevPathNode=true; //Set the flag to break the outer loop
                                break; //We've found the correct previous path node and moved the currentNode reference
                                //back to it so break the inner loop
                            }
                        if(foundPrevPathNode) break; //We've identified the previous path node, so break the inner loop to continue
                    }
                }
                //Reset the node values for all nodes to (effectively) infinity, so we can search again (leave no footprint!)
                for(GraphNode<?> n : encountered) n.nodeValue=Integer.MAX_VALUE;
                for(GraphNode<?> n : notEncountered) n.nodeValue=Integer.MAX_VALUE;
                return cp; //The costed (cheapest) path has been assembled, so return it!
            }
            //We're not at the goal node yet, so...
            for(GraphLink e : currentNode.adjList) //For each edge/link from the current node...
                if(!encountered.contains(e.destNode)) { //If the node it leads to has not yet been encountered (i.e. processed)
                    e.destNode.nodeValue=Integer.min(e.destNode.nodeValue, currentNode.nodeValue+e.cost); //Update the node value at the end
                    //of the edge to the minimum of its current value or the total of the current node's value plus the cost of the edge
                    if(!notEncountered.contains(e.destNode)) notEncountered.add(e.destNode);
                }
            notEncountered.sort(Comparator.comparingInt(n -> n.nodeValue)); //Sort in ascending node value order
        } while(!notEncountered.isEmpty());
        return null; //No path found, so return null
    }

//    //Retrieve the cheapest path by expanding all paths recursively depth-first
//    public static <T> CostedPath searchGraphDepthFirstCheapestPath(GraphNode<?> from, List<GraphNode<?>> encountered, int totalCost, T lookingfor){
//        if(from.name.equals(lookingfor)){ //Found it - end of path
//            CostedPath cp=new CostedPath(); //Create a new CostedPath object
//            cp.pathList.add(from); //Add the current node to it - only (end of path) element
//            cp.pathCost=totalCost; //Use the current total cost
//            return cp; //Return the CostedPath object
//        }
//
//        if(encountered==null) encountered=new ArrayList<>(); //First node so create new (empty) encountered list
//        encountered.add(from);
//        List<CostedPath> allPaths=new ArrayList<>(); //Collection for all candidate costed paths from this node
//
//        for(GraphLink adjLink : from.adjList) //For every adjacent node
//            if(!encountered.contains(adjLink.destNode)) //That has not yet been encountered
//            {
//                //Create a new CostedPath from this node to the searched for item (if a valid path exists)
//                CostedPath temp = searchGraphDepthFirstCheapestPath(adjLink.destNode,encountered, totalCost+adjLink.cost,lookingfor);
//                if(temp==null) continue; //No path was found, so continue to the next iteration
//                temp.pathList.add(0,from); //Add the current node to the front of the path list
//                allPaths.add(temp); //Add the new candidate path to the list of all costed paths
//            }
//        //If no paths were found then return null. Otherwise, return the cheapest path (i.e. the one with min pathCost)
//        return allPaths.isEmpty() ? null : Collections.min(allPaths, Comparator.comparingInt(p -> p.pathCost));
//    }

    public static <T> List<CostedPath> searchGraphDepthFirst(GraphNode<?> from, List<GraphNode<?>> encountered, int totalCost, T lookingfor) {
        List<CostedPath> allPaths = new ArrayList<>();

        if (from.name.equals(lookingfor)) {
            CostedPath cp = new CostedPath();
            cp.pathList.add(from);
            cp.pathCost = totalCost;
            allPaths.add(cp);
            return allPaths;
        }

        if (encountered == null) encountered = new ArrayList<>();
        encountered.add(from);

        for (GraphLink adjLink : from.adjList) {
            if (!encountered.contains(adjLink.destNode)) {
                List<CostedPath> pathsFromAdj = searchGraphDepthFirst(adjLink.destNode, encountered, totalCost + adjLink.cost, lookingfor);
                for (CostedPath path : pathsFromAdj) {
                    path.pathList.add(0, from);
                    allPaths.add(path);
                }
            }
        }
        return allPaths;
    }

    @Override
    public String toString() {
        return "Graph{" +
                "allPaths=" + allPaths +
                '}';
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        graph = this;
    }
}
