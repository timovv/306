package team02.project.graph;

import java.util.Map;
import java.util.Set;

public interface Node {
    /**
     * Get id of {@link Node}
     * @return The id of current node
     */
    String getId();

    /**
     * Get weight of {@link Node}
     * @return The weight of current node
     */
    int getWeight();

    /**
     * Get the parent {@link Node nodes} and their associated weight
     * @return
     */
    Map<Node, Integer> getOutgoingEdges();
    Map<Node, Integer> getIncomingEdges();

    Set<Node> getDependencies();
}
