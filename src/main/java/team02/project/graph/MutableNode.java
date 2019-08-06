package team02.project.graph;

import lombok.Value;

import java.util.HashMap;
import java.util.Map;

@Value
public class MutableNode implements Node {
    int id;
    int weight;
    Map<Node, Integer> incomingEdges = new HashMap<>();
    Map<Node, Integer> outgoingEdges = new HashMap<>();

    @Override
    public boolean equals(Object other) {
        return (other instanceof MutableNode) && id == ((MutableNode)other).id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "MutableNode{id=" + id + ",weight=" + weight + "}";
    }
}
