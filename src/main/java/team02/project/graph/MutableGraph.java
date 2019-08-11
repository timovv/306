package team02.project.graph;

import lombok.Data;
import lombok.val;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
public class MutableGraph implements Graph {
    private String id;
    private Set<MutableNode> nodes = new HashSet<>();
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
