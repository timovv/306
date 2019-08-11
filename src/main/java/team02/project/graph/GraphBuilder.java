package team02.project.graph;

public interface GraphBuilder {
    void setId(String id);
    void addNode(String id, int weight);
    void addEdge(String fromId, String toId, int weight);

    Graph build();
}
