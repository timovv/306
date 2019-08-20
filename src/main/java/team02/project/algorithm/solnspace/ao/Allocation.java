package team02.project.algorithm.solnspace.ao;

import team02.project.algorithm.SchedulingContext;
import team02.project.graph.Node;

import java.util.*;

public class Allocation {

    private final List<Set<Node>> tasks;
    private final int estimatedCost;

    private Allocation(List<Set<Node>> tasks, int estimatedCost) {
        this.tasks = tasks;
        this.estimatedCost = estimatedCost;
    }

    public static Allocation fromAPartialSolution(APartialSolution alloc) {
        SchedulingContext ctx = alloc.getContext();
        List<Set<Node>> tasks = new ArrayList<>(ctx.getProcessorCount());
        for(int i = 0; i < ctx.getProcessorCount(); ++i) {
            tasks.add(new HashSet<>());
        }

        APartialSolution current = alloc;
        while(!current.isEmpty()) {
            tasks.get(current.getProcessor()).add(current.getTask());
            current = current.getParent();
        }

        return new Allocation(tasks, alloc.getEstimatedFinishTime());
    }

    public Set<Node> getTasksFor(int processor) {
        return tasks.get(processor);
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
