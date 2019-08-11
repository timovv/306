package team02.project.graph;

import java.util.Set;

public interface Graph {
    String getId();

    Set<? extends Node> getNodes();

    Node getNode(String id);
}
