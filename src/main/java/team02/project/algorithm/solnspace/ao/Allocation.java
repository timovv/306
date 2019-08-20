package team02.project.algorithm.solnspace.ao;

import lombok.val;
import team02.project.algorithm.SchedulingContext;
import team02.project.graph.Node;

import java.util.*;
import java.util.stream.Collectors;

public class Allocation {

    private final List<List<Node>> tasks;
    private Map<Node, Integer> topLevelAllocated;
    private Map<Node, Integer> bottomLevelAllocated;
    private final int estimatedCost;

    private Allocation(List<List<Node>> tasks, Map<Node, Integer> topLevelAllocated, Map<Node, Integer> bottomLevelAllocated, int estimatedCost) {
        this.tasks = tasks;
        this.topLevelAllocated = topLevelAllocated;
        this.bottomLevelAllocated = bottomLevelAllocated;
        this.estimatedCost = estimatedCost;
    }

    public static Allocation fromAPartialSolution(APartialSolution alloc) {
        SchedulingContext ctx = alloc.getContext();
        List<List<Node>> tasks = new ArrayList<>(ctx.getProcessorCount());
        Map<Node, Integer> topLevelAllocated = new HashMap<>();
        Map<Node, Integer> processorLookup = new HashMap<>();
        for(int i = 0; i < ctx.getProcessorCount(); ++i) {
            tasks.add(new ArrayList<>());
        }

        APartialSolution current = alloc;
        while(!current.isEmpty()) {
            tasks.get(current.getProcessor()).add(current.getTask());
            topLevelAllocated.put(current.getTask(), current.getTopLevelAllocated());
            processorLookup.put(current.getTask(), current.getProcessor());
            current = current.getParent();
        }

        Queue<Node> queue = alloc.getContext().getTaskGraph().getNodes().stream()
                .filter(x -> x.getOutgoingEdges().isEmpty())
                .collect(Collectors.toCollection(LinkedList::new));

        Map<Node, Integer> bottomLevelAllocated = new HashMap<>();
        while(!queue.isEmpty()){
            Node node = queue.poll();
            int bottomLevel = node.getOutgoingEdges().entrySet()
                    .stream()
                    .mapToInt(childEntry -> {
                        if (processorLookup.get(node).equals(processorLookup.get(childEntry.getKey()))) {
                            return childEntry.getKey().getBottomLevel();
                        } else {
                            return childEntry.getKey().getBottomLevel() + childEntry.getValue();
                        }
                    })
                    .max()
                    .orElse(0) + node.getWeight();

            bottomLevelAllocated.put(node, bottomLevel);

            for(val node2 : node.getIncomingEdges().keySet()) {
                queue.offer(node2);
            }
        }
        return new Allocation(tasks, topLevelAllocated, bottomLevelAllocated, alloc.getEstimatedFinishTime());
    }

    public List<Node> getTasksFor(int processor) {
        return tasks.get(processor);
    }

    public int getTopLevelFor(Node task) {
        return topLevelAllocated.get(task);
    }

    public int getBottomLevelFor(Node task) {
        return bottomLevelAllocated.get(task);
    }

    public Map<Node, Integer> createProcessorLookupTable() {
        Map<Node, Integer> output = new HashMap<>();
        for(int i = 0; i < tasks.size(); ++i) {
            for(Node node : tasks.get(i)) {
                output.put(node, i);
            }
        }

        return output;
    }

    public int getEstimatedFinishTime() {
        return estimatedCost;
    }
}
