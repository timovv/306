package team02.project.algorithm.solnspace.ao;

import team02.project.algorithm.Schedule;
import team02.project.algorithm.SchedulingContext;
import team02.project.algorithm.solnspace.PartialSolution;
import team02.project.graph.Node;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Allocation
 */
public class APartialSolution implements PartialSolution {
    private final SchedulingContext context;
    private final int processor;
    private final Node task;
    private final int depth; // also used to index next node to schedule
    private final APartialSolution parent;

    private final int[] loads;
    private final int topLevelAllocated;
    private final int criticalPathAllocated;

    /**
     * The number of processors that have already got at least 1 task.
     */
    private int processorsWithTasks;

    public static APartialSolution makeEmpty(SchedulingContext context) {
        return new APartialSolution(context,
                null,
                null,
                0,
                0,
                0,
                new int[context.getProcessorCount()],
                0,
                0
        );
    }

    private APartialSolution(SchedulingContext context,
                             APartialSolution parent,
                             Node task,
                             int processor,
                             int depth,
                             int processorsWithTasks,
                             int[] loads,
                             int topLevelAllocated,
                             int criticalPathAllocated) {

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

    private int getMaxLoad() {
        int max = 0;
        for(int i = 0; i < loads.length; ++i) {
            if(max < loads[i]) {
                max = loads[i];
            }
        }
        return max;
    }

    @Override
    public Set<PartialSolution> expand() {
        if (isCompleteAllocation()) {
            Set<PartialSolution> output = new HashSet<>();
            output.add(OPartialSolution.makeEmpty(context, Allocation.fromAPartialSolution(this)));
            return output;
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

    public int getProcessorFor(Node task) {
        APartialSolution current = this;
        while (!current.isEmpty()) {
            if (current.getTask().equals(task)) {
                return current.getProcessor();
            }
            current = current.getParent();
        }

        return -1;
    }


    public boolean isEmpty() {
        return getParent() == null;
    }

    private boolean isCompleteAllocation() {
        return getDepth() == getContext().getTaskGraph().getNodes().size();
    }

    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public Schedule makeComplete() {
        throw new UnsupportedOperationException();
    }

    public SchedulingContext getContext() {
        return context;
    }

    public int getProcessor() {
        return processor;
    }

    public Node getTask() {
        return task;
    }

    public int getDepth() {
        return depth;
    }

    public APartialSolution getParent() {
        return parent;
    }

    public int getTopLevelAllocated() {
        return topLevelAllocated;
    }

    public int getCriticalPathAllocated() {
        return criticalPathAllocated;
    }

}
