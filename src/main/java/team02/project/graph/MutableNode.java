package team02.project.graph;

import lombok.Data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
public class MutableNode implements Node {
    String id;
    int weight;
    int bottomLevel;
    int topLevel;
    Map<Node, Integer> incomingEdges = new HashMap<>();
    Map<Node, Integer> outgoingEdges = new HashMap<>();

    Node[] incomingEdgeNodes;
    int[] incomingEdgeWeights;
    Node[] outgoingEdgeNodes;
    int[] outgoingEdgeWeights;

    Set<Node> dependencies = new HashSet<>();
    Set<Node> dependents = new HashSet<>();
    int index;

    public MutableNode(String id, int weight, int index) {
        this.id = id;
        this.weight = weight;
        this.index = index;
    }

    public void buildArrays() {
        incomingEdgeNodes = new Node[incomingEdges.size()];
        incomingEdgeWeights = new int[incomingEdges.size()];
        outgoingEdgeNodes = new Node[outgoingEdges.size()];
        outgoingEdgeWeights = new int[outgoingEdges.size()];

        int i = 0;
        for(Map.Entry<Node, Integer> e : incomingEdges.entrySet()) {
            incomingEdgeNodes[i] = e.getKey();
            incomingEdgeWeights[i] = e.getValue();
            ++i;
        }

        i = 0;
        for(Map.Entry<Node, Integer> e : outgoingEdges.entrySet()) {
            outgoingEdgeNodes[i] = e.getKey();
            outgoingEdgeWeights[i] = e.getValue();
            ++i;
        }
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof MutableNode) && id.equals(((MutableNode) other).getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "MutableNode{id=" + id + ",weight=" + weight + "}";
    }
}
