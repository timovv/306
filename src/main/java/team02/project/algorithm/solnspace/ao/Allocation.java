package team02.project.algorithm.solnspace.ao;

import team02.project.algorithm.SchedulingContext;
import team02.project.graph.Node;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Allocations of levels for a specific processor
 */
public class Allocation {

    List<List<Node>> levels;

    private Allocation(List<List<Node>> levels) {
        this.levels = levels;
    }

    public Allocation getAllocation(SchedulingContext context, APartialSolution partialSolution, int processorId) {
        // do the topological for each processor

        List<List<Node>> levels = new ArrayList<>();

        LinkedList<Node> queue = context.getTaskGraph().getNodes().stream().filter(x -> x.getIncomingEdges().isEmpty())
                .collect(Collectors.toCollection(LinkedList::new));

        Map<Node, Integer> nodeLevels = new HashMap<>();

        while(!queue.isEmpty()) {
            Node current = queue.poll();

            int maxParentLevel = current.getIncomingEdges().keySet().stream()
                    .mapToInt(nodeLevels::get)
                    .max()
                    .orElse(0);

            int currentLevel = maxParentLevel;
            if(partialSolution.getProcessorFor(current) == processorId) {
                ++currentLevel;
                if(levels.size() == currentLevel) {
                    List<Node> thisLevel = new ArrayList<>();
                    thisLevel.add(current);
                    levels.add(thisLevel);
                } else {
                    levels.get(currentLevel - 1).add(current);
                }
            }

            nodeLevels.put(current, currentLevel);

            for(Node child : current.getOutgoingEdges().keySet()) {
                queue.offer(child);
            }
        }
    }
}
