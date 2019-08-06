package team02.project.graph;

import lombok.Data;
import lombok.val;

import java.util.HashSet;
import java.util.Set;

@Data
public class MutableGraph implements Graph {
    private String id;
    private Set<MutableNode> nodes = new HashSet<>();
    private MutableNode[] lookup = new MutableNode[500]; // probably enough xd;

    @Override
    public MutableNode getNode(int id) {
        return lookup[id];
    }

    @Override
    public Graph minus(int nodeId) {
        val builder = new GraphBuilderImpl();
        for(val existingNode : nodes) {
            if(existingNode.getId() != nodeId) {
                builder.addNode(existingNode.getId(), existingNode.getWeight());
            }
        }

        for(val existingNode : nodes) {
            if(existingNode.getId() != nodeId) {
                for (val existingEdge : existingNode.getOutgoingEdges().entrySet()) {
                    if(existingEdge.getKey().getId() != nodeId) {
                        builder.addEdge(existingEdge.getKey().getId(), existingEdge.getKey().getId(), existingEdge.getValue());
                    }
                }
            }
        }

        builder.setId(id);
        return builder.build();
    }

    public void addNode(MutableNode node) {
        nodes.add(node);
        lookup[node.getId()] = node;
    }
}
