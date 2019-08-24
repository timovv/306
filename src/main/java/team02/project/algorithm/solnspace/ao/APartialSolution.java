package team02.project.algorithm.solnspace.ao;

import team02.project.algorithm.Schedule;
import team02.project.algorithm.SchedulingContext;
import team02.project.algorithm.solnspace.PartialSolution;
import team02.project.graph.Node;

import java.util.*;

/**
 * The allocation part of the solution space. This represents the top part of the solution space tree, where tasks are
 * allocated to a processor. The allocation phase of the solution space ends once all tasks have been allocated.
 *
 * The nature of the expand method in APartialSolution is such that no two generated allocations are duplicates, meaning
 * they are the same schedule but with the processor numbers switched around. This is done by treating the collection
 * of allocations as a set: allocations are made without respect to the ordering of the processors.
 */
public class APartialSolution implements PartialSolution {

    // attributes relating to this allocation

    private final SchedulingContext context;
    private final int processor;
    private final Node task;
    private final int depth; // also used to index next node to schedule
    private final APartialSolution parent;

    // attributes relating to heuristic calculation

    private final int[] loads;
    private final int topLevelAllocated;
    private final int criticalPathAllocated;

    /**
     * The number of processors that have already got at least 1 task.
     */
    private int processorsWithTasks;

    /**
     * Create a new empty APartialSolution for the given context.
     * @param context Context for which to create the partial solution
     * @return the created partial solution
     */
    public static APartialSolution makeEmpty(SchedulingContext context) {
        return new APartialSolution(context, null, null, 0, 0, 0,
                new int[context.getProcessorCount()], 0, 0 );
    }

    private APartialSolution(SchedulingContext context, APartialSolution parent, Node task,
                             int processor, int depth, int processorsWithTasks, int[] loads,
                             int topLevelAllocated, int criticalPathAllocated) {
        this.context = context;
        this.parent = parent;
        this.processor = processor;
        this.task = task;
        this.depth = depth;
        this.processorsWithTasks = processorsWithTasks;
        this.loads = loads;
        this.topLevelAllocated = topLevelAllocated;
        this.criticalPathAllocated = criticalPathAllocated;
    }

    @Override
    public int getEstimatedFinishTime() {
        return Math.max(getMaxLoad(), criticalPathAllocated);
    }

    /**
     * @return the calculated maxLoad heuristic
     */
    private int getMaxLoad() {
        int max = 0;
        for (int load : loads) {
            if (max < load) {
                max = load;
            }
        }
        return max;
    }

    /**
     * Allocates the next task onto a processor.
     * {@inheritDoc}
     */
    @Override
    public Set<PartialSolution> expand() {
        // if we are done with the allocation, then we should start with the ordering
        if (isCompleteAllocation()) {
            return Collections.singleton(OPartialSolution.makeEmpty(context, Allocation.fromAPartialSolution(this)));
        }

        Set<PartialSolution> output = new HashSet<>();

        // get the next one from nodesToSchedule
        Node next = getContext().getTaskGraph().getNodes().get(getDepth());

        // Full mapping of Nodes -> Processors
        Map<Node, Integer> processorLookupTable = new HashMap<>();

        // Contains the TLA values of all allocated Nodes
        Map<Node, Integer> tla = new HashMap<>();

        APartialSolution current = this;
        while (!current.isEmpty()) {
            processorLookupTable.put(current.getTask(), current.getProcessor());
            tla.put(current.getTask(), current.getTopLevelAllocated());
            current = current.getParent();
        }

        // add to processors with tasks
        for (int i = 0; i < processorsWithTasks; i++) {
            int[] newLoads = new int[getContext().getProcessorCount()];
            System.arraycopy(loads, 0, newLoads, 0, loads.length);
            newLoads[i] += next.getWeight();

            int newTopLevelAllocated = 0;
            for (Map.Entry<Node, Integer> pred : next.getIncomingEdges().entrySet()) {
                if (!processorLookupTable.getOrDefault(pred.getKey(), i).equals(i)) {
                    newTopLevelAllocated = Math.max(newTopLevelAllocated, tla.get(pred.getKey()) + pred.getValue());
                } else {
                    newTopLevelAllocated =  Math.max(newTopLevelAllocated, pred.getKey().getTopLevel());
                }
            }

            output.add(new APartialSolution(
                    this.getContext(),
                    this,
                    next,
                    i,
                    getDepth() + 1,
                    processorsWithTasks,
                    newLoads,
                    newTopLevelAllocated + next.getWeight(),
                    Math.max(this.criticalPathAllocated, newTopLevelAllocated + next.getBottomLevel()))
            );
        }

        // adding to empty set if available
        if (processorsWithTasks < getContext().getProcessorCount()) {
            int[] newLoads = new int[getContext().getProcessorCount()];
            System.arraycopy(loads, 0, newLoads, 0, loads.length);
            newLoads[processorsWithTasks] += next.getWeight();

            int newTopLevelAllocated = 0;
            for (Map.Entry<Node, Integer> pred : next.getIncomingEdges().entrySet()) {
                newTopLevelAllocated = Math.max(newTopLevelAllocated, tla.get(pred.getKey()) + pred.getValue());
            }

            output.add(new APartialSolution(
                    this.getContext(),
                    this,
                    next,
                    processorsWithTasks,
                    getDepth() + 1,
                    processorsWithTasks + 1,
                    newLoads,
                    newTopLevelAllocated + next.getWeight(),
                    Math.max(this.criticalPathAllocated, newTopLevelAllocated + next.getBottomLevel())));
        }

        return output;
    }

    /**
     * Determines if this allocation represents an empty allocation where no tasks have been allocated.
     * @return true if the allocation is empty; false otherwise.
     */
    public boolean isEmpty() {
        return getParent() == null;
    }

    /**
     * Determines whether this allocation represents a complete allocation where all tasks have been allocated processors.
     *
     * If the allocation is complete, calling {@link #expand()} will move the solution space into the ordering phase,
     * resulting in the output of {@link OPartialSolution} objects.
     *
     * @return true if this allocation is complete, otherwise false.
     */
    public boolean isCompleteAllocation() {
        return getDepth() == getContext().getTaskGraph().getNodes().size();
    }

    /**
     * {@inheritDoc}
     * @return false always: an APartialSolution will never represent a complete schedule since allocations need to be ordered first.
     */
    @Override
    public boolean isComplete() {
        return false;
    }

    /**
     * This method is not supported by APartialSolution since no APartialSolution is a complete solution.
     * @throws UnsupportedOperationException always
     */
    @Override
    public Schedule makeComplete() {
        throw new UnsupportedOperationException();
    }

    /**
     * @return the SchedulingContext associated with this APartialSolution.
     */
    public SchedulingContext getContext() {
        return context;
    }

    /**
     * The zero-based index of the processor that the task allocated on this level of the APartialSolution was allocated.
     * @return the index of the task's processor.
     */
    public int getProcessor() {
        return processor;
    }

    /**
     * The task that was allocated by this level of the APartialSolution
     * @return the allocated task
     */
    public Node getTask() {
        return task;
    }

    /**
     * The current depth, representing the number of tasks that have so far been allocated, including this one.
     * @return The current depth. This value will be zero if {@link #isEmpty()} returns false.
     */
    private int getDepth() {
        return depth;
    }

    /**
     * @return The parent APartialSolution in the solution space tree, or null if this represents the root.
     */
    public APartialSolution getParent() {
        return parent;
    }

    /**
     * @return The top level of the allocated node with respect to the allocation so far.
     */
    public int getTopLevelAllocated() {
        return topLevelAllocated;
    }
}
