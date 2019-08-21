package team02.project.algorithm.solnspace.ao;

import javafx.scene.layout.Priority;
import team02.project.algorithm.Schedule;
import team02.project.algorithm.SchedulingContext;
import team02.project.algorithm.solnspace.PartialSolution;
import team02.project.graph.Node;

import java.util.*;

/**
 * Allocation
 */
public class APartialSolution implements PartialSolution {
    private final SchedulingContext context;
    private int processor;
    private Node task;
    private int depth; // also used to index next node to schedule
    private APartialSolution parent;

    private int[] loads;

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
                new int[context.getProcessorCount()]
        );
    }

    private APartialSolution(SchedulingContext context,
                             APartialSolution parent,
                             Node task,
                             int processor,
                             int depth,
                             int processorsWithTasks,
                             int[] loads) {
        this.context = context;
        this.parent = parent;
        this.processor = processor;
        this.task = task;
        this.depth = depth;
        this.processorsWithTasks = processorsWithTasks;
        this.loads = loads;
    }

    @Override
    public int getEstimatedFinishTime() {
        return getMaxLoad();
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
    public Collection<PartialSolution> expand() {
        if (isCompleteAllocation()) {
            return Collections.singletonList(OPartialSolution.makeEmpty(context, Allocation.fromAPartialSolution(this)));
        }

        PriorityQueue<PartialSolution> output = new PriorityQueue<>(processorsWithTasks + 1);

        // get the next one from nodesToSchedule
        Node next = getContext().getTaskGraph().getNodes().get(getDepth());

        // add to processors with tasks
        for (int i = 0; i < processorsWithTasks; i++) {
            int[] newLoads = new int[getContext().getProcessorCount()];
            System.arraycopy(loads, 0, newLoads, 0, loads.length);
            newLoads[i] += next.getWeight();

            output.add(new APartialSolution(
                    this.getContext(),
                    this,
                    next,
                    i,
                    getDepth() + 1,
                    processorsWithTasks,
                    newLoads)
            );
        }

        // adding to empty set if available
        if (processorsWithTasks < getContext().getProcessorCount()) {
            int[] newLoads = new int[getContext().getProcessorCount()];
            System.arraycopy(loads, 0, newLoads, 0, loads.length);
            newLoads[processorsWithTasks] += next.getWeight();
            output.add(new APartialSolution(getContext(), this, next, processorsWithTasks, getDepth() + 1,
                    processorsWithTasks + 1, newLoads));
        }

        return output;
    }

    public int getProcessorFor(Node task) {
        APartialSolution current = this;
        while (!isEmpty()) {
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
}
