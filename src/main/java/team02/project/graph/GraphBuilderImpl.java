package team02.project.graph;

import lombok.val;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class GraphBuilderImpl implements GraphBuilder {
    private MutableGraph wip = new MutableGraph();
    private boolean built = false;

    private int currentIndex = 0;

    @Override
    public Graph build() {
        built = true;

        // set up the dependencies
        // TODO: as we add more preprocessing steps it would be nice to move them outside of GraphBuilder.
        setupDependencies();
        return wip;
    }

    private void setupDependencies() {
        // todo: could optimise this a bit
        for(Node node : wip.getNodes()) {
            Set<Node> dependencies = new HashSet<>();
            LinkedList<Node> queue = new LinkedList<>();
            queue.addLast(node);
            while(!queue.isEmpty()) {
                Node current = queue.removeFirst();
                for(Node dependency : current.getIncomingEdges().keySet()) {
                    dependencies.add(dependency);
                    queue.addLast(dependency);
                }
            }

            node.getDependencies().addAll(dependencies);
        }
    }

    private void throwIfBuilt() {
        if(built) {
            throw new RuntimeException("Can't update; graph has been already built!");
        }
    }

    @Override
    public void setId(String id) {
        throwIfBuilt();
        wip.setId(id);
    }

    @Override
    public void addNode(String id, int weight) {
        throwIfBuilt();
        wip.addNode(new MutableNode(id, weight, currentIndex++));
    }

    @Override
    public void addEdge(String fromId, String toId, int weight) {
        throwIfBuilt();
        val head = wip.getNode(toId);
        val tail = wip.getNode(fromId);

        if(head == null || tail == null) {
            throw new RuntimeException("Node(s) do not exist for edge");
        }

        head.getIncomingEdges().put(tail, weight);
        tail.getOutgoingEdges().put(head, weight);
    }
}
