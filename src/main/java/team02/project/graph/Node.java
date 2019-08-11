package team02.project.graph;

import java.util.Map;
import java.util.Set;

public interface Node {
    String getId();
    int getWeight();
    Map<Node, Integer> getOutgoingEdges();
    Map<Node, Integer> getIncomingEdges();
}
