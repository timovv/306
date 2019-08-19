package team02.project.algorithm.solnspace.ao;

import team02.project.algorithm.SchedulingContext;
import team02.project.graph.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Allocation {

    private final List<List<Node>> tasks;

    private Allocation(List<List<Node>> tasks) {
        this.tasks = tasks;
    }

    public static Allocation fromAPartialSolution(APartialSolution alloc) {
        SchedulingContext ctx = alloc.getContext();
        List<List<Node>> tasks = new ArrayList<>(ctx.getProcessorCount());
        for(int i = 0; i < ctx.getProcessorCount(); ++i) {
            tasks.add(new ArrayList<>());
        }

        APartialSolution current = alloc;
        while(!current.isEmpty()) {
            tasks.get(current.getProcessor()).add(current.getTask());
            current = current.getParent();
        }

        return new Allocation(tasks);
    }

    public List<Node> getTasksFor(int processor) {
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
}
