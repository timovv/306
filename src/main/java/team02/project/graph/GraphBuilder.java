package team02.project.graph;

public interface GraphBuilder {
    void setId(String id);
    void addNode(int id, int weight);
    void addEdge(int fromId, int toId, int weight);

    Graph build();
}
