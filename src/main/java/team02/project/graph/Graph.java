package team02.project.graph;

import java.util.List;

public interface Graph {
    /**
     * Get the id (name) of graph
     * @return The id as {@link String}
     */
    String getId();

    /**
     * Get all nodes in the graph
     * @return A {@link List} of {@link Node}
     */
    List<? extends Node> getNodes();

    /**
     * Lookup and return a node via it's id
     * @param id The id as {@link String}
     * @return The {@link Node} with corresponding id
     */
    Node getNode(String id);
}
