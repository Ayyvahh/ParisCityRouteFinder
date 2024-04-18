package com.example.parisroutefinderdsaca2.DataStructure;

import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class Graph implements Initializable {
    public static Graph graph;

    public static class CostedPath{
        public int pathCost=0;
        public List<GraphNode<?>> pathList=new ArrayList<>();
    }

    public static <T> CostedPath findCheapestPathDijkstra(GraphNode<?> startNode, T lookingFor){
        CostedPath cp=new CostedPath(); //Create result object for cheapest path
        List<GraphNode<?>> encountered=new ArrayList<>(), notEncountered=new ArrayList<>(); //Create encountered/notEncountered lists
        startNode.nodeValue=0; //Set the starting node value to zero
        notEncountered.add(startNode); //Add the start node as the only value in the notEncountered list to start
        GraphNode<?> currentNode;

        do{ //Loop until notEncountered list is empty
            currentNode=notEncountered.remove(0); //Get the first notEncountered node (sorted list, so will have the lowest value)
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
                                cp.pathList.add(0,n); //Add the identified path node to the front of the result list
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        graph = this;
    }
}
