package team02.project.io;

public interface GraphBuilder {
    void setId(String id);
    void addNode(String name, int weight);
    void addEdge(String toNode, String fromNode, int weight);
}
