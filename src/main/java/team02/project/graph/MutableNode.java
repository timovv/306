package team02.project.graph;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class MutableNode implements Node {
    int id;
    int weight;
    Map<Node, Integer> incomingEdges = new HashMap<>();
    Map<Node, Integer> outgoingEdges = new HashMap<>();

    public MutableNode(int id, int weight) {
        this.id = id;
        this.weight = weight;
    }

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
