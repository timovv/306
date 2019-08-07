package team02.project.graph;

import lombok.val;

public class GraphBuilderImpl implements GraphBuilder {
    private MutableGraph wip = new MutableGraph();
    private boolean built = false;

    @Override
    public Graph build() {
        built = true;
        return wip;
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
    public void addNode(int id, int weight) {
        throwIfBuilt();
        wip.addNode(new MutableNode(id, weight));
    }

    @Override
    public void addEdge(int fromId, int toId, int weight) {
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
