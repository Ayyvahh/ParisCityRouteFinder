package com.example.parisroutefinderdsaca2.DataStructure;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GraphNodeConnectionTest {

    @Test
    void testingConnectNodeUndirectedCreatesBidirectionalLink() {
        /*Creating two Nodes */
            GraphNode<String> n1 = new GraphNode<>("Node1", false, 0, 0, 0);
            GraphNode<String> n2 = new GraphNode<>("Node2", false, 0, 0, 0);
            GraphNode<String> n3 = new GraphNode<>("Node3", false, 0, 0, 0);

        /*Linking Node 1 to Node 2 */
            n1.connectToNodeUndirected(n2, 5);
        /*Node 1 and Node 2 should both have an entry in their adjacency list for the link to be bidirectional */
            assertEquals(1, n1.getAdjList().size());
            assertEquals(1, n2.getAdjList().size());

            GraphLink l1 = n1.getAdjList().getFirst();
            GraphLink l2 = n2.getAdjList().getFirst();
            GraphLink l3 = n2.getAdjList().getFirst();
        /* Checking both nodes are each others destNode*/

            assertEquals(n2, l1.getDestNode());
            assertEquals(n1, l2.getDestNode());

        /*Connecting Node 3 to Node 1*/
        n1.connectToNodeUndirected(n3, 7);

        /*Node 1 should now have two links, Node 2 should still have one*/
        assertEquals(2, n1.getAdjList().size());
        assertEquals(1, n2.getAdjList().size());
        assertEquals(1, n3.getAdjList().size());


        /*Node 3 should not contain a link to node 1 since they are not connected */
        assertFalse(n3.getAdjList().contains(l1));

        /*Node 3 and Node 2 should both have links to Node 1*/
        assertEquals(l3.getDestNode(), l2.getDestNode());

    }

}