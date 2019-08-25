package team02.project.algorithm.solnspace.ao;

import lombok.val;
import team02.project.algorithm.SchedulingContext;
import team02.project.graph.Node;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a completed allocation in the AO solution space, where ordering is yet to performed.
 *
 * This class stores the complete allocation in an easy-to-access way while also storing information used to calculate
 * heuristics used in the ordering part of the calculation.
 */
public class Allocation {

    /**
     * List of sets indexed by processor. Each set represents the tasks allocated to that processor.
     */
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

    /**
     * Creates an Allocation from the given complete {@link APartialSolution} object.
     * @param alloc The {@link APartialSolution} to create this allocation from, which must be complete.
     * @return The created allocation.
     */
    public static Allocation fromAPartialSolution(APartialSolution alloc) {
        if(!Objects.requireNonNull(alloc).isCompleteAllocation()) {
            throw new IllegalArgumentException("allocation to create must be complete!");
        }

        SchedulingContext ctx = alloc.getContext();
        List<Set<Node>> tasks = new ArrayList<>(ctx.getProcessorCount());
        int[] loadsPerProcessor = new int[ctx.getProcessorCount()];
        for(int i = 0; i < ctx.getProcessorCount(); ++i) {
            tasks.add(new HashSet<>());
        }

        Map<Node, Integer> topLevelAllocated = new HashMap<>();
        Map<Node, Integer> processorLookup = new HashMap<>();

        // go through allocation steps and allocate them while also calculating allocated top level for heuristics
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

        // calculating allocated bottom level, used for ordering heuristics
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

    /**
     * Get the tasks allocated to the given processor.
     * @param processor The zero-indexed processor to find the tasks for
     * @return set of nodes allocated to the processor
     */
    public Set<Node> getTasksFor(int processor) {
        return tasks.get(processor);
    }

    /**
     * Get the top level of the given node with respect to this allocation.
     * @param task Task to find top level for
     * @return Top level
     */
    public int getTopLevelFor(Node task) {
        return topLevelAllocated.get(task);
    }

    /**
     * Get the bottom level of the given node with respect to this allocation.
     * @param task Task to find bottom level for
     * @return Bottom level
     */
    public int getBottomLevelFor(Node task) {
        return bottomLevelAllocated.get(task);
    }

    /**
     * Get the processor that the given task is assigned to.
     * @param task The task
     * @return the zero-indexed number of the processor the input task is assigned to
     */
    public int getProcessorFor(Node task) {
        return processors.get(task);
    }

    public int getLoadFor(int processor) {
        return loadsPerProcessor[processor];
    }

    /**
     * Get the estimated finishing time of the allocation as according to {@link APartialSolution#getEstimatedFinishTime()}
     * @return underestimate of the finish time
     */
    public int getEstimatedFinishTime() {
        return estimatedCost;
    }
}
