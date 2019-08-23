package team02.project.graph;

import lombok.val;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

public class GraphBuilderImpl implements GraphBuilder {
    private MutableGraph wip = new MutableGraph();
    private boolean built = false;

    private int currentIndex = 0;

    @Override
    public Graph build() {
        built = true;

        LinkedHashSet<MutableNode> order = new LinkedHashSet<>();
        LinkedHashSet<MutableNode> reverseOrder = new LinkedHashSet<>();

        for(MutableNode node : wip.getNodes()) {
            topologicalVisit(order,  node);
            reverseTopologicalVisit(reverseOrder, node);
        }

        wip.setNodes(new ArrayList<>(order)); // LinkedHashSet is not a List =(

        calculateBottomLevels();
        calculateTopLevels();
        return wip;
    }

    /**
     * Visits each Node, records the topological order and set their depedents
     *
     * @param order
     * @param toVisit
     * @return
     */
    private Set<Node> topologicalVisit(LinkedHashSet<MutableNode> order, MutableNode toVisit) {
        Set<Node> dependencies = new HashSet<>();

        if (order.contains(toVisit)) {
            return toVisit.getDependencies();
        }

        for (Node node : toVisit.getIncomingEdges().keySet()) {
            dependencies.addAll(topologicalVisit(order, (MutableNode) node));
            dependencies.add(node);
        }

        order.add(toVisit);
        toVisit.setDependencies(dependencies);
        return dependencies;
    }

    private Set<Node> reverseTopologicalVisit(LinkedHashSet<MutableNode> reverseOrder, MutableNode toVisit) {
        if(reverseOrder.contains(toVisit)) {
            return toVisit.getDependents();
        }

        Set<Node> dependents = new HashSet<>();

        for(Node node : toVisit.getOutgoingEdges().keySet()) {
            dependents.addAll(reverseTopologicalVisit(reverseOrder, (MutableNode)node));
            dependents.add(node);
        }

        reverseOrder.add(toVisit);
        toVisit.setDependents(dependents);
        return dependents;
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

    private void calculateTopLevels() {
        Queue<MutableNode> queue = wip.getNodes().stream()
                .filter(x -> x.getIncomingEdges().isEmpty())
                .collect(Collectors.toCollection(LinkedList::new));

        while(!queue.isEmpty()){
            val node = queue.poll();
            val topLevel = node.getIncomingEdges().keySet()
                    .stream()
                    .mapToInt(Node::getTopLevel)
                    .max()
                    .orElse(0) + node.getWeight();
            node.setTopLevel(topLevel);

            for(val node2 : node.getOutgoingEdges().keySet()) {
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
