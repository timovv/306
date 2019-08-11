package team02.project.graph;

public interface GraphBuilder {
    /**
     * Set the graph's id (name)
     * @param id The id as {@link String}
     */
    void setId(String id);

    /**
     * Add a node to the graph
     * @param id The id of node as {@link String}
     * @param weight The duration of task represented by node
     */
    void addNode(String id, int weight);

    /**
     * Add an edge to the graph
     * @param fromId The parent {@link Node}
     * @param toId The child {@link Node}
     * @param weight The delay caused by communication
     */
    void addEdge(String fromId, String toId, int weight);

    /**
     * Build the graph
     * @return The {@link Graph} representation
     */
    Graph build();
}
