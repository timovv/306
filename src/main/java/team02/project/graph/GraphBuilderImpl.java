package team02.project.graph;

import lombok.val;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

public class GraphBuilderImpl implements GraphBuilder {
    private MutableGraph wip = new MutableGraph();
    private boolean built = false;

    @Override
    public Graph build() {
        built = true;

        // set up the dependencies
        // TODO: as we add more preprocessing steps it would be nice to move them outside of GraphBuilder.
        setupDependencies();

        // create a topological order, if this is too slow then we can do it using the normal algorithm
        wip.getNodes().sort((a, b) -> {
            if (a.getDependencies().contains(b)) {
                // a depends on b => (a > b)
                return 1;
            } else if (b.getDependencies().contains(a)) {
                // b depends on a => (b > a)
                return -1;
            } else {
                return 0;
            }
        });

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
        calculateNodeLevels(true);
        calculateNodeLevels(false);
    }

    private void calculateNodeLevels(boolean isBottomLevel) {
        // do the BFS to get bottom levels of wip

        Queue<MutableNode> queue = wip.getNodes().stream()
                                      .filter(x -> {
                                          if (isBottomLevel) {
                                              return x.getOutgoingEdges().isEmpty();
                                          } else {
                                              return x.getIncomingEdges().isEmpty();
                                          }
                                      })
                                      .collect(Collectors.toCollection(LinkedList::new));

        while(!queue.isEmpty()){
            val node = queue.poll();
            Set<Node> neighbours;
            if (isBottomLevel) {
                neighbours = node.getOutgoingEdges().keySet();
            } else {
                neighbours = node.getIncomingEdges().keySet();
            }
            val level = neighbours.stream()
                                  .mapToInt(Node::getTopLevel)
                                  .max()
                                  .orElse(0) + node.getWeight();

            if (isBottomLevel) {
                node.setBottomLevel(level);
                for(val node2 : node.getIncomingEdges().keySet()) {
                    queue.offer((MutableNode)node2);
                }
            } else {
                node.setTopLevel(level);
                for(val node2 : node.getOutgoingEdges().keySet()) {
                    queue.offer((MutableNode)node2);
                }
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
        wip.addNode(new MutableNode(id, weight));
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
