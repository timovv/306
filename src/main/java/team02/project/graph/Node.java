package team02.project.graph;

import java.util.Map;

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
     * @return A map of edges and weights
     */
    Map<Node, Integer> getOutgoingEdges();
    Map<Node, Integer> getIncomingEdges();

    /**
     * Get the bottom level of the node
     * @return The bottom level
     */
    int getBottomLevel();
}
