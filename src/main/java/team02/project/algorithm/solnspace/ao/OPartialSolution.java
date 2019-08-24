package team02.project.algorithm.solnspace.ao;

import team02.project.algorithm.Schedule;
import team02.project.algorithm.SchedulingContext;
import team02.project.algorithm.solnspace.PartialSolution;
import team02.project.graph.Node;

import java.util.*;

/**
 * Represents a node in the ordering part of the AO solution space. These nodes are found in the lower part of
 * the solution space tree. The ordering phase takes a created allocation and orders the tasks on the processors.
 */
public class OPartialSolution implements PartialSolution {

    private final SchedulingContext context;
    private final Allocation allocation;
    private final OPartialSolution parent;
    private final Node task;

    private final int depth;
    private final int processor;

    /**
     * In the interest of performance, we keep track of which tasks have been ordered and are ready to
     * order in a set of bitfields, one per processor. Bit n is 1 if the node with index n is ordered
     * or ready to be ordered respectively.
     */
    private final long orderedBits;
    private final long readyToOrderBits;

    private final int estimatedStartTime;
    private final int heuristicCost;

    private OPartialSolution(SchedulingContext context, Allocation allocation, OPartialSolution parent,
                             Node task, int depth, int processor, long orderedBits, long readyToOrderBits,
                             int estimatedStartTime, int heuristicCost) {
        this.context = context;
        this.allocation = allocation;
        this.parent = parent;
        this.task = task;
        this.depth = depth;
        this.processor = processor;
        this.orderedBits = orderedBits;
        this.readyToOrderBits = readyToOrderBits;
        this.estimatedStartTime = estimatedStartTime;
        this.heuristicCost = heuristicCost;
    }

    /**
     * Creates an empty OPartialSolution from the given parameters, including the given allocation.
     * @param ctx the context for which to solve the scheduling problem
     * @param allocation the allocation for which orderings should be created
     * @return an empty OPartialSolution corresponding to the given allocation
     */
    public static OPartialSolution makeEmpty(SchedulingContext ctx, Allocation allocation) {
        Objects.requireNonNull(ctx);
        Objects.requireNonNull(allocation);

        long orderedBits = 0L;
        long readyToOrderBits = 0L;

        // find tasks that are ready initially and
        for(int i = 0; i < ctx.getProcessorCount(); ++i) {
            for (Node node : allocation.getTasksFor(i)) {
                if (isTaskReadyToOrder(node, orderedBits, allocation.getTasksFor(i))) {
                    readyToOrderBits |= (1L << node.getIndex());
                }
            }
        }

        return new OPartialSolution(ctx, allocation, null, null, 0, 0, orderedBits,
                readyToOrderBits, 0, 0);
    }

    @Override
    public int getEstimatedFinishTime() {
        return Math.max(allocation.getEstimatedFinishTime(), heuristicCost);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<PartialSolution> expand() {
        if(isOrderingComplete()) {
            // if complete, return AOCompleteSolution which can calculate the completed schedule.
            return Collections.singleton(new AOCompleteSolution(this));
        }

        Set<PartialSolution> output = expandProcessor(processor);

        if(output.isEmpty()) {
            // no tasks left on that processor, on to the next one.
            output = expandProcessor(processor + 1);
        }

        return output;
    }

    /**
     * Adds tasks that can be ordered on a particular processor
     * @param processorNumber the processor
     * @return OPartialSolutions with one more task ordered on the given processor. May be none.
     */
    private Set<PartialSolution> expandProcessor(int processorNumber) {
        Set<PartialSolution> output = new HashSet<>();

        // Build a table of all the previously calculated estimated start time
        Map<Node, Integer> historicEstimatedStartTimes = new HashMap<>();

        // The sum of weights already ordered on each processor
        int[] totalOrdered = new int[getContext().getProcessorCount()];

        // Latest finish time for each processor
        int[] latestFinishTime = new int[getContext().getProcessorCount()];

        calculateHeuristicInfo(totalOrdered, latestFinishTime, historicEstimatedStartTimes);

        try {
            allocation.getTasksFor(processorNumber);
        } catch (Exception e) {
            OPartialSolution current = this;
            while(current.depth > 3) current = current.parent;
            current.expand();
        }

        // Check for fixed task order
        // 1. Same parent and child (or no parent/child)
        Node commonParent = null, commonChild = null;
        boolean canFixOrder = false;
        outer:
        for(Node node : allocation.getTasksFor(processorNumber)) {
            if ((readyToOrderBits & (1L << node.getIndex())) == 0) {
                continue;
            }

            canFixOrder = true;

            if (node.getIncomingEdges().size() > 1 || node.getOutgoingEdges().size() > 1) {
                canFixOrder = false;
                break;
            }

            // will iterate at most once.
            // -- checking for same parent
            for (Node parent : node.getIncomingEdges().keySet()) {
                if (commonParent == null) {
                    commonParent = parent;
                }

                if (!commonParent.equals(parent)) {
                    canFixOrder = false;
                    break outer;
                }
            }

            // -- checking for same child
            for (Node child : node.getOutgoingEdges().keySet()) {
                if (commonChild == null) {
                    commonChild = child;
                }

                if (!commonChild.equals(child)) {
                    canFixOrder = false;
                    break outer;
                }
            }
        }

        if(canFixOrder && (readyToOrderBits & (readyToOrderBits - 1)) != 0) {
            final Node commonParentFinal = commonParent;
            final Node commonChildFinal = commonChild;

            // 1. Place the tasks in fork order.
            List<Node> nodes = new ArrayList<>();
            for(Node node : allocation.getTasksFor(processorNumber)) {
                if((readyToOrderBits & (1L << node.getIndex())) != 0) {
                    nodes.add(node);
                }
            }

            // Order in fork order, resolving conflicts using join order.
            // if the result is the join order, then we can perform the optimisation.
            nodes.sort(Comparator.<Node>comparingInt(x -> {
                Integer value = x.getIncomingEdges().get(commonParentFinal);
                return value != null ? value : 0;
            }).thenComparingInt(x -> {
                Integer value = x.getOutgoingEdges().get(commonChildFinal);
                // sorting this in descending order
                return value != null ? -value : 0;
            }));

            // Check if this is also in join order.
            boolean inJoinOrder = true;
            int lastJoinValue = Integer.MAX_VALUE;
            for(Node node : nodes) {
                Integer value = node.getOutgoingEdges().get(commonChildFinal);
                value = value == null ? 0 : value;
                if(value > lastJoinValue) {
                    inJoinOrder = false;
                    break;
                }

                lastJoinValue = value;
            }

            if (inJoinOrder) {
                // yes, we can perform the optimisation
                OPartialSolution current = createChild(nodes.get(0), processorNumber);
                for(int i = 1; i < nodes.size(); ++i) {
                    current = current.createChild(nodes.get(i), processorNumber);
                }

                return Collections.singleton(current);
            }
        }

        for (Node node : allocation.getTasksFor(processorNumber)) {
            if ((readyToOrderBits & (1L << node.getIndex())) == 0) {
                continue;
            }

            output.add(createChild(node, processorNumber, totalOrdered, latestFinishTime, historicEstimatedStartTimes));
        }

        return output;
    }

    /**
     * Helper to calculate info used for the heuristics
     * @param totalOrdered Total ordered for each processor (output; this array will be written to)
     * @param latestFinishTime Latest finish time for each processor (output; this array will be written to)
     * @param historicEstimatedStartTimes Historic estimated start time map (output; this map will be written to)
     */
    private void calculateHeuristicInfo(int[] totalOrdered, int[] latestFinishTime, Map<Node, Integer> historicEstimatedStartTimes) {
        OPartialSolution prev = this;
        while (!prev.isEmptyOrdering()) {
            historicEstimatedStartTimes.put(prev.getTask(), prev.getEstimatedStartTime());
            totalOrdered[prev.getProcessor()] += prev.getTask().getWeight();
            latestFinishTime[prev.getProcessor()] = Math.max(latestFinishTime[prev.getProcessor()],
                    prev.getEstimatedStartTime() + prev.getTask().getWeight());
            prev = prev.getParent();
        }
    }

    /**
     * Creates a child OPartialSolution, placing the given node on the given processor
     * @param node Node to order
     * @param processorNumber zero-based index for processor
     * @return Created OPartialSolution
     */
    private OPartialSolution createChild(Node node, int processorNumber) {
        int[] totalOrdered = new int[getContext().getProcessorCount()];
        int[] latestFinishTime = new int[getContext().getProcessorCount()];
        Map<Node, Integer> historicEstimatedStartTimes = new HashMap<>();
        calculateHeuristicInfo(totalOrdered, latestFinishTime, historicEstimatedStartTimes);
        return createChild(node, processorNumber, totalOrdered, latestFinishTime, historicEstimatedStartTimes);
    }

    /**
     * Creates a child OPartialSolution, placing the given node on the given processor.
     * @param node Node to order
     * @param processorNumber zero-based index for processor
     * @param totalOrdered precalculated totalOrdered values
     * @param latestFinishTime precalculated latest finish time values
     * @param historicEstimatedStartTimes precalculated historic estimated start times
     * @see #calculateHeuristicInfo(int[], int[], Map)
     * @return Created OPartialSolution
     */
    private OPartialSolution createChild(Node node, int processorNumber, int[] totalOrdered, int[] latestFinishTime,
                                         Map<Node, Integer> historicEstimatedStartTimes) {
        long newOrderedBits = orderedBits;
        long newReadyBits = readyToOrderBits;
        newOrderedBits |= 1 << node.getIndex();
        newReadyBits &= ~(1 << node.getIndex());

        for(Node dependent : node.getDependents()) {
            if(allocation.getTasksFor(processorNumber).contains(dependent) && isTaskReadyToOrder(dependent,
                    newOrderedBits,
                    allocation.getTasksFor(processorNumber))) {
                newReadyBits |= 1 << dependent.getIndex();
            }
        }

        int newDataReadyTime = 0;
        for (Map.Entry<Node, Integer> pred : node.getIncomingEdges().entrySet()) {
            int predFinishTime;
            if (historicEstimatedStartTimes.containsKey(pred.getKey())) {
                predFinishTime = historicEstimatedStartTimes.get(pred.getKey()) + pred.getKey().getWeight();
            } else {
                predFinishTime = allocation.getTopLevelFor(pred.getKey());
            }
            if (allocation.getProcessorFor(pred.getKey()) != processorNumber) {
                predFinishTime += pred.getValue();
            }
            newDataReadyTime = Math.max(newDataReadyTime, predFinishTime);
        }

        int maxOrderedLoad = 0;
        for (int i = 0; i < getContext().getProcessorCount(); i++) {
            maxOrderedLoad = Math.max(maxOrderedLoad,
                    latestFinishTime[i] + (allocation.getLoadFor(i) - totalOrdered[i]));
        }


        int newEstStartTime = Math.max(latestFinishTime[processorNumber], newDataReadyTime);

        return new OPartialSolution(
                getContext(),
                allocation,
                this,
                node,
                depth + 1,
                processorNumber,
                newOrderedBits,
                newReadyBits,
                newEstStartTime,
                Math.max(this.heuristicCost, Math.max(newEstStartTime + allocation.getBottomLevelFor(node), maxOrderedLoad))
        );
    }

    /**
     * Helper method to determine whether the given task is ready to be ordered
     * @param task Task to test
     * @param orderedBits bits of ordered tasks on this processor
     * @param allocatedTasks set of tasks allocated to this processor
     * @return true if the task is a candidate for ordering
     */
    private static boolean isTaskReadyToOrder(Node task, long orderedBits, Set<Node> allocatedTasks) {
        for(Node dependency : task.getDependencies()) {
            // if this dependency is on this processor and it hasn't been ordered yet then we can't
            // schedule it
            if(allocatedTasks.contains(dependency)
                && ((orderedBits & (1L << dependency.getIndex())) == 0)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Determine if this OPartialSolution represents an empty ordering where no tasks have been ordered yet.
     * @return true if this is an empty ordering
     */
    public boolean isEmptyOrdering() {
        return parent == null;
    }

    /**
     * Find the parent of this OPartialSolution, if any
     * @return the parent OPartialSolution, or null if this represents an empty ordering
     */
    public OPartialSolution getParent() {
        return parent;
    }

    /**
     * {@inheritDoc}
     *
     * Even though an OPartialSolution can represent a complete ordering, the validation of it is left
     * to {@link AOCompleteSolution}. Hence, false is always returned so that algorithms will call {@link #expand()}.
     * If the ordering is complete, {@link #expand()} will return {@link AOCompleteSolution} for which isComplete
     * may return true.
     *
     * @return false always
     */
    @Override
    public boolean isComplete() {
        return false;
    }

    /**
     * Determines whether this is a complete ordering, i.e. an ordering where all tasks have been ordered.
     * @return true if the ordering is complete, otherwise false.
     */
    public boolean isOrderingComplete() {
        return depth == getContext().getTaskGraph().getNodes().size();
    }

    /**
     * {@inheritDoc}
     *
     * An OPartialSolution cannot make a complete ordering as {@link #isComplete()} always returns false.
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    public Schedule makeComplete() {
        throw new UnsupportedOperationException();
    }

    /**
     * The task being ordered by this OPartialSolution
     * @return task being ordered
     */
    public Node getTask() {
        return task;
    }

    /**
     * Allocated processor of the task being ordered by this OPartialSolution
     * @return the allocated processor's zero-based index
     */
    public int getProcessor() {
        return processor;
    }

    private int getEstimatedStartTime() {
        return estimatedStartTime;
    }

    /**
     * The SchedulingContext associated with the solution space this OPartialSolution belongs to
     * @return associated SchedulingContext
     */
    public SchedulingContext getContext() {
        return context;
    }
}
