package team02.project.algorithm.solnspace.ao;

import lombok.val;
import team02.project.algorithm.SchedulingContext;
import team02.project.graph.Node;

import java.util.*;
import java.util.stream.Collectors;

public class Allocation {

    private final List<Set<Node>> tasks;
    private final int[] loadsPerProcessor;
    private final Map<Node, Integer> processors;
    private final Map<Node, Integer> topLevelAllocated;
    private final Map<Node, Integer> bottomLevelAllocated;
    private final int estimatedCost;

    private Allocation(List<Set<Node>> tasks, int[] loadsPerProcessor,  Map<Node, Integer> processors,
                       Map<Node, Integer> topLevelAllocated, Map<Node, Integer> bottomLevelAllocated, int estimatedCost) {
        this.tasks = tasks;
        this.loadsPerProcessor = loadsPerProcessor;
        this.processors = processors;
        this.topLevelAllocated = topLevelAllocated;
        this.bottomLevelAllocated = bottomLevelAllocated;
        this.estimatedCost = estimatedCost;
    }

    public static Allocation fromAPartialSolution(APartialSolution alloc) {
        SchedulingContext ctx = alloc.getContext();
        List<Set<Node>> tasks = new ArrayList<>(ctx.getProcessorCount());
        int[] loadsPerProcessor = new int[ctx.getProcessorCount()];
        for(int i = 0; i < ctx.getProcessorCount(); ++i) {
            tasks.add(new HashSet<>());
        }

        Map<Node, Integer> topLevelAllocated = new HashMap<>();
        Map<Node, Integer> processorLookup = new HashMap<>();

        APartialSolution current = alloc;
        while(!current.isEmpty()) {
            tasks.get(current.getProcessor()).add(current.getTask());
            loadsPerProcessor[current.getProcessor()] += current.getTask().getWeight();
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
        return new Allocation(tasks, loadsPerProcessor,  processorLookup, topLevelAllocated,
                bottomLevelAllocated, alloc.getEstimatedFinishTime());
    }

    public Set<Node> getTasksFor(int processor) {
        return tasks.get(processor);
    }

    public int getTopLevelFor(Node task) {
        return topLevelAllocated.get(task);
    }

    public int getBottomLevelFor(Node task) {
        return bottomLevelAllocated.get(task);
    }

    public int getProcessorFor(Node task) {
        return processors.get(task);
    }

    public int getLoadFor(int processor) {
        return loadsPerProcessor[processor];
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
