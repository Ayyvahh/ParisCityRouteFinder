package org.example;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.RunnerException;
import sun.tools.jar.Main;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Measurement(iterations = 10)
@Warmup(iterations = 5)
@Fork(value = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)

public class MyBenchmark {
    Map<String, GraphNode<String>> nodes = new HashMap<>();

    @Setup
    public void setUp() {
        Set<GraphNode<String>> nodesToAvoid = new HashSet<>();

        nodes.put("N1" , new GraphNode<>("N1", false, 100, 0, 0));
        nodes.put("N2" , new GraphNode<>("N2", false, 23, 1, 1));
        nodes.put("N3" , new GraphNode<>("N3", false, 4, 100, 100));
        nodes.put("N4" , new GraphNode<>("N4", true, 0, 450, 300));

        nodes.get("N1").connectToNodeUndirected(nodes.get("N2"), 5);
        nodes.get("N1").connectToNodeUndirected(nodes.get("N3"), 10);
        nodes.get("N2").connectToNodeUndirected(nodes.get("N3"), 3);
        nodes.get("N2").connectToNodeUndirected(nodes.get("N4"), 7);
        nodes.get("N3").connectToNodeUndirected(nodes.get("N4"), 2);
    }

    @Benchmark
    public void testDijkstra() {
        Set<GraphNode<String>> nodesToAvoid = new HashSet<>();
        Graph.CostedPath shortestPath = Graph.findCheapestPathDijkstra(nodes.get("N1"), "N4", nodesToAvoid);
    }

    @Benchmark
    public void testDFS() {
        Set<GraphNode<String>> nodesToAvoid = new HashSet<>();
        List<Graph.CostedPath> paths = Graph.searchGraphDepthFirst(nodes.get("N1"), null , 0, "N4", nodesToAvoid);
    }

    @Benchmark
    public void testGraphNodeSetters() {
        for (Map.Entry<String, GraphNode<String>> entry : nodes.entrySet()) {
            entry.getValue().setName("newName");
            entry.getValue().setGraphX(1000);
            entry.getValue().setGraphY(200);
            entry.getValue().setAdjList(new ArrayList<>());
            entry.getValue().setCulturalSignificance(2);
        }
    }

    @Benchmark
    public void testGraphNodeGetters() {
        for (Map.Entry<String, GraphNode<String>> entry : nodes.entrySet()) {
            entry.getValue().getName();
            entry.getValue().getGraphX();
            entry.getValue().getGraphY();
            entry.getValue().getAdjList();
            entry.getValue().getCulturalSignificance();
        }
    }

    public static void main(String[] args) throws RunnerException, IOException {
        Main.main(args);
    }
}
