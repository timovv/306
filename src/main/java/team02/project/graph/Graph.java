package team02.project.graph;

import java.util.Set;

public interface Graph {
    /**
     * Get the id (name) of graph
     * @return The id as {@link String}
     */
    String getId();

    /**
     * Get all nodes in the graph
     * @return A {@link Set} of {@link Node}
     */
    Set<? extends Node> getNodes();

    /**
     * Lookup and return a node via it's id
     * @param id The id as {@link String}
     * @return The {@link Node} with corresponding id
     */
    Node getNode(String id);
}
