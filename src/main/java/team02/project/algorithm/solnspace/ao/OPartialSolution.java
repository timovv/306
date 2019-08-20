package team02.project.algorithm.solnspace.ao;

import team02.project.algorithm.Schedule;
import team02.project.algorithm.SchedulingContext;
import team02.project.algorithm.solnspace.PartialSolution;
import team02.project.graph.Node;

import java.util.*;

public class OPartialSolution implements PartialSolution {

    private final SchedulingContext context;
    private final Allocation allocation;
    private final OPartialSolution parent;
    private final Node task;

    private final int depth;
    private final int processor;

    private final List<Set<Node>> ordered;
    private final List<Set<Node>> readyToOrder;

    private OPartialSolution(SchedulingContext context, Allocation allocation, OPartialSolution parent, Node task, int depth, int processor, List<Set<Node>> ordered, List<Set<Node>> readyToOrder) {
        this.context = context;
        this.allocation = allocation;
        this.parent = parent;
        this.task = task;
        this.depth = depth;
        this.processor = processor;
        this.ordered = ordered;
        this.readyToOrder = readyToOrder;
    }

    public static OPartialSolution makeEmpty(SchedulingContext ctx, Allocation allocation) {
        List<Set<Node>> ordered = new ArrayList<>(ctx.getProcessorCount());
        List<Set<Node>> readyToOrder = new ArrayList<>(ctx.getProcessorCount());

        // add stuff to readyToOrder (kinda pricy oops)
        for(int i = 0; i < ctx.getProcessorCount(); ++i) {
            Set<Node> processorOrdered = new HashSet<>();
            Set<Node> processorReadyToOrder = new HashSet<>();

            for (Node node : allocation.getTasksFor(i)) {
                if (isTaskReadyToOrder(node, processorOrdered, allocation.getTasksFor(i))) {
                    processorReadyToOrder.add(node);
                }
            }

            ordered.add(processorOrdered);
            readyToOrder.add(processorReadyToOrder);
        }

        return new OPartialSolution(ctx, allocation, null, null, 0, 0, ordered, readyToOrder);
    }

    @Override
    public int getEstimatedFinishTime() {
        if(isComplete()) {
            return makeComplete().getFinishTime();
        } else {
            return allocation.getEstimatedFinishTime();
        }
    }

    @Override
    public Set<PartialSolution> expand() {
        if(isOrderingComplete()) {
            Set<PartialSolution> output = new HashSet<>();
            output.add(new AOCompleteSolution(context, this));
            return output;
        }

        Set<PartialSolution> output = expandProcessor(processor);

        if(output.isEmpty()) {
            output = expandProcessor(processor + 1);
        }

        return output;
    }

    private Set<PartialSolution> expandProcessor(int processorNumber) {
        Set<PartialSolution> output = new HashSet<>();
        for(Node node : readyToOrder.get(processorNumber)) {
            List<Set<Node>> newOrdered = new ArrayList<>(ordered);
            Set<Node> newProcessorOrdered = new HashSet<>(ordered.get(processorNumber));
            newOrdered.set(processorNumber, newProcessorOrdered);
            newProcessorOrdered.add(node);

            List<Set<Node>> newReadyToOrder = new ArrayList<>(readyToOrder);
            Set<Node> newProcessorReady = new HashSet<>(readyToOrder.get(processorNumber));
            newReadyToOrder.set(processorNumber, newProcessorReady);
            newProcessorReady.remove(node);

            for(Node maybeNowReady : node.getOutgoingEdges().keySet()) {
                if(allocation.getTasksFor(processorNumber).contains(maybeNowReady)) {
                    boolean isNowReady = isTaskReadyToOrder(maybeNowReady, newOrdered.get(processorNumber),
                            allocation.getTasksFor(processorNumber));

                    if(isNowReady) {
                        newProcessorReady.add(maybeNowReady);
                    }
                }
            }

            output.add(new OPartialSolution(context, allocation, this, node, depth + 1, processorNumber, newOrdered, newReadyToOrder));
        }

        return output;
    }

    private static boolean isTaskReadyToOrder(Node task, Set<Node> orderedTasks, Set<Node> allocatedTasks) {
        for(Node directDependency : task.getIncomingEdges().keySet()) {
            // if this dependency is on this processor and it hasn't been ordered yet then we can't
            // schedule it
            if(allocatedTasks.contains(directDependency)
                && !orderedTasks.contains(directDependency)) {
                return false;
            }
        }

        return true;
    }

    public boolean isEmptyOrdering() {
        return parent == null;
    }

    public OPartialSolution getParent() {
        return parent;
    }

    @Override
    public boolean isComplete() {
        return false;
    }

    public boolean isOrderingComplete() {
        return depth == context.getTaskGraph().getNodes().size();
    }

    @Override
    public Schedule makeComplete() {
        throw new UnsupportedOperationException();
    }

    public Node getTask() {
        return task;
    }

    public int getProcessor() {
        return processor;
    }
}
