package team02.project.algorithm.solnspace.ao;

import team02.project.algorithm.Schedule;
import team02.project.algorithm.SchedulingContext;
import team02.project.algorithm.solnspace.PartialSolution;
import team02.project.graph.Node;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Allocation
 */
public class APartialSolution implements PartialSolution {
    private final SchedulingContext context;
    private int processor;
    private Node task;
    private int depth; // also used to index next node to schedule
    private APartialSolution parent;

    /**
     * The number of processors that have already got at least 1 task.
     */
    private int processorsWithTasks;

    public static APartialSolution makeEmpty(SchedulingContext context) {
        return new APartialSolution(context, null ,null, 0, 0, 0);
    }

    private APartialSolution(SchedulingContext context, APartialSolution parent, Node task, int processor, int depth, int processorsWithTasks) {
        this.context = context;
        this.parent = parent;
        this.processor = processor;
        this.task = task;
        this.depth = depth;
        this.processorsWithTasks = processorsWithTasks;
    }

    @Override
    public int getEstimate() {
        return 0;
    }

    @Override
    public Set<PartialSolution> expand() {
        if(isCompleteAllocation()) {
            // should do OPartialSolution stuff
            return null;
        }

        Set<PartialSolution> output = new HashSet<>();

        // get the next one from nodesToSchedule
        Node next = context.getTaskGraph().getNodes().get(depth);

        // add to processors with tasks
        for(int i = 0; i < processorsWithTasks; i++) {
            output.add(new APartialSolution(this.context, this, next, i, depth + 1, processorsWithTasks));
        }

        // adding to empty set if available
        if(processorsWithTasks < context.getProcessorCount()) {
            output.add(new APartialSolution(context, this, next, processorsWithTasks, depth + 1, processorsWithTasks + 1));
        }

        return output;
    }

    public int getProcessorFor(Node task) {
        APartialSolution current = this;
        while(current.parent != null) {
            if(current.task.equals(task)) {
                return current.processor;
            }
            current = current.parent;
        }

        return -1;
    }

    private boolean isCompleteAllocation() {
        return depth == context.getTaskGraph().getNodes().size();
    }

    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public Schedule makeComplete() {
        return null;
    }
}
