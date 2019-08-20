package team02.project.graph;

import lombok.Data;

import java.util.*;

@Data
public class MutableGraph implements Graph {
    private String id;
    private List<MutableNode> startingNodes = new ArrayList<>();
    private List<MutableNode> nodes = new ArrayList<>();
    private Map<String, MutableNode> lookup = new HashMap<>();

    @Override
    public MutableNode getNode(String id) {
        return lookup.get(id);
    }

    public void addNode(MutableNode node) {
        nodes.add(node);
        lookup.put(node.getId(), node);
    }
}
