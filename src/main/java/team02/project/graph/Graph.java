package team02.project.graph;

import java.util.List;
import java.util.Queue;

public interface Graph {
    /**
     * Get the id (name) of graph
     * @return The id as {@link String}
     */
    String getId();

    /**
     * Get all nodes in the graph
     * @return An array of {@link Node} by index
     */
    Node[] getNodes();

    /**
     * Lookup and return a node via it's id
     * @param id The id as {@link String}
     * @return The {@link Node} with corresponding id
     */
    Node getNode(String id);

}
