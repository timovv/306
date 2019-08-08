package team02.project.graph;

import lombok.var;

import java.sql.SQLOutput;
import java.util.*;

/*
    Stores the bottom level of each node
 */
public class BottomLevelStore {
    private int[] store;

    public BottomLevelStore(Graph g) {
        store = new int[500]; //probably enough
        populateStore(g);
    }

    private void populateStore(Graph g) {
        // for each node in the mutable graph, find its bottom level and put into the store
        Queue q = TopologicalSort(g);

        // run dijkstras on the queue, keep in mind independant nodes?

        System.out.println(q);
    }

    public int getBottomLevel(int nodeId) {
        return store[nodeId];
    }

    private class BLGraph{

    }

    private class BLNode {

    }

    public Queue<Node> TopologicalSort(Graph g) {
        Queue<Node> q = new LinkedList();
        Set<Integer> s = new HashSet<>(); // set of all nodes with no incoming edge

        for(Node n : g.getNodes()){
            if(n != null) {
                if(n.getIncomingEdges().isEmpty()){
                    // no incoming edges
                    s.add(n.getId());
                }
            }
        }

        while (!s.isEmpty()){
            int nodeToQueue = s.iterator().next();
            q.add(g.getNode(nodeToQueue));
            List<Integer> nodesWithIncomingEdge = new ArrayList<>();
            for(var e : g.getNode(nodeToQueue).getOutgoingEdges().entrySet()){
                nodesWithIncomingEdge.add(e.getKey().getId());
            }
            g = g.minus(nodeToQueue);
            for(int i : nodesWithIncomingEdge) {
                if(g.getNode(i).getIncomingEdges().isEmpty()){
                    s.add(i);
                }
            }
            s.remove(nodeToQueue);
        }

        return q;
    }

}
