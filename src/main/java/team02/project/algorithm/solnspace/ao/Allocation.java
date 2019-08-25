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
    private final int[] processors;
    private final int[] topLevelAllocated;
    private final int[] bottomLevelAllocated;
    private final int estimatedCost;

    private Allocation(List<Set<Node>> tasks, int[] loadsPerProcessor, int[] processors,
                       int[] topLevelAllocated, int[] bottomLevelAllocated, int estimatedCost) {
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

        int[] topLevelAllocated = new int[ctx.getTaskGraph().getNodes().length];
        int[] processorLookup = new int[ctx.getTaskGraph().getNodes().length];

        // go through allocation steps and allocate them while also calculating allocated top level for heuristics
        APartialSolution current = alloc;
        while(!current.isEmpty()) {
            tasks.get(current.getProcessor()).add(current.getTask());
            loadsPerProcessor[current.getProcessor()] += current.getTask().getWeight();
            topLevelAllocated[current.getTask().getIndex()] = current.getTopLevelAllocated();

            processorLookup[current.getTask().getIndex()] = current.getProcessor();
            current = current.getParent();
        }

        Queue<Node> queue = Arrays.stream(alloc.getContext().getTaskGraph().getNodes())
                .filter(x -> x.getOutgoingEdgeNodes().length == 0)
                .collect(Collectors.toCollection(LinkedList::new));

        // calculating allocated bottom level, used for ordering heuristics
        int[] bottomLevelAllocated = new int[ctx.getTaskGraph().getNodes().length];
        while(!queue.isEmpty()){
            Node node = queue.poll();


            int bottomLevel = 0;
            for(int i = 0; i < node.getOutgoingEdgeNodes().length; ++i) {
                val otherNode = node.getOutgoingEdgeNodes()[i];
                int edgeWeight = node.getOutgoingEdgeWeights()[i];

                val newValue = processorLookup[node.getIndex()] == processorLookup[otherNode.getIndex()]
                        ? otherNode.getBottomLevel()
                        : otherNode.getBottomLevel() + edgeWeight;

                if(newValue > bottomLevel) {
                    bottomLevel = newValue;
                }
            }

            bottomLevelAllocated[node.getIndex()] = bottomLevel;

            for(val node2 : node.getIncomingEdgeNodes()) {
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
        return topLevelAllocated[task.getIndex()];
    }

    /**
     * Get the bottom level of the given node with respect to this allocation.
     * @param task Task to find bottom level for
     * @return Bottom level
     */
    public int getBottomLevelFor(Node task) {
        return bottomLevelAllocated[task.getIndex()];
    }

    /**
     * Get the processor that the given task is assigned to.
     * @param task The task
     * @return the zero-indexed number of the processor the input task is assigned to
     */
    public int getProcessorFor(Node task) {
        return processors[task.getIndex()];
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
