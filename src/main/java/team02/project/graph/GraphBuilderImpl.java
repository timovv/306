package team02.project.graph;

import lombok.val;

import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.Collectors;

public class GraphBuilderImpl implements GraphBuilder {
    private MutableGraph wip = new MutableGraph();
    private boolean built = false;

    @Override
    public Graph build() {
        built = true;
        // run the processing steps e.g. set bottom levels of nodes
        calculateBottomLevels();

        return wip;
    }

    private void calculateBottomLevels() {
        // do the BFS to get bottom levels of wip

        Queue<MutableNode> queue = wip.getNodes().stream()
                .filter(x -> x.getOutgoingEdges().isEmpty())
                .collect(Collectors.toCollection(LinkedList::new));

        while(!queue.isEmpty()){
            val node = queue.poll();
            val bottomLevel = node.getOutgoingEdges().keySet()
                    .stream()
                    .mapToInt(Node::getBottomLevel)
                    .max()
                    .orElse(0) + node.getWeight();
            node.setBottomLevel(bottomLevel);

            for(val node2 : node.getIncomingEdges().keySet()) {
                queue.offer((MutableNode)node2);
            }
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
