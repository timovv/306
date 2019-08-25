package team02.project.graph;

import lombok.Data;

import java.util.*;

@Data
public class MutableGraph implements Graph {
    private String id;
    private List<MutableNode> nodesList = new ArrayList<>();
    private Node[] nodes;
    private Map<String, MutableNode> lookup = new HashMap<>();

    @Override
    public MutableNode getNode(String id) {
        return lookup.get(id);
    }

    @Override
    public Node[] getNodes() {
        return nodes;
    }

    public void initArrays() {
        nodes = nodesList.toArray(new Node[0]);
        for(Node node : nodes) {
            ((MutableNode)node).buildArrays();
        }
    }

    public void addNode(MutableNode node) {
        nodesList.add(node);
        lookup.put(node.getId(), node);
    }
}
