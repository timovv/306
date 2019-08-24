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
    Set<Node> dependencies = new HashSet<>();
    Set<Node> dependents = new HashSet<>();
    int index;

    public MutableNode(String id, int weight, int index) {
        this.id = id;
        this.weight = weight;
        this.index = index;
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

    @Override
    public boolean isIdentical(MutableNode other) {
        return (other.getWeight() == weight && other.incomingEdges.equals(incomingEdges) && other.outgoingEdges.equals(outgoingEdges));
    }
}
