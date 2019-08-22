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

    private final long[] orderedBits;
    private final long[] readyToOrderBits;

    private final int estimatedStartTime;
    private final int heuristicCost;

    private OPartialSolution(SchedulingContext context, Allocation allocation, OPartialSolution parent,
                             Node task, int depth, int processor, long[] orderedBits, long[] readyToOrderBits,
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

    public static OPartialSolution makeEmpty(SchedulingContext ctx, Allocation allocation) {
        long[] orderedBits = new long[ctx.getProcessorCount()];
        long[] readyToOrderBits = new long[ctx.getProcessorCount()];

        // add stuff to readyToOrder (kinda pricy oops)
        for(int i = 0; i < ctx.getProcessorCount(); ++i) {
            for (Node node : allocation.getTasksFor(i)) {
                if (isTaskReadyToOrder(node, orderedBits[i], allocation.getTasksFor(i))) {
                    readyToOrderBits[i] |= (1 << node.getIndex());
                }
            }
        }

        return new OPartialSolution(ctx, allocation, null, null, 0, 0, orderedBits,
                readyToOrderBits, 0, 0);
    }

    @Override
    public int getEstimatedFinishTime() {
        if(isComplete()) {
            return makeComplete().getFinishTime();
        } else {
            return Math.max(allocation.getEstimatedFinishTime(), heuristicCost);
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

        // Build a table of all the previously calculated estimated start time
        Map<Node, Integer> historicEstimatedStartTimes = new HashMap<>();

        // The sum of weights already ordered on each processor
        int[] totalOrdered = new int[context.getProcessorCount()];

        // Latest finish time for each processor
        int[] latestFinishTime = new int[context.getProcessorCount()];

        calculateHeuristicInfo(totalOrdered, latestFinishTime, historicEstimatedStartTimes);

        // Check for fixed task order
        // 1. Same parent and child (or no parent/child)
        Node commonParent = null, commonChild = null;
        boolean canFixOrder = false;
        outer:
        for(Node node : allocation.getTasksFor(processorNumber)) {
            if ((readyToOrderBits[processorNumber] & (1 << node.getIndex())) == 0) {
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

        if(canFixOrder) {
            final Node commonParentFinal = commonParent;
            final Node commonChildFinal = commonChild;

            // 1. Place the tasks in fork order.
            List<Node> nodes = new ArrayList<>();
            for(Node node : allocation.getTasksFor(processorNumber)) {
                if((readyToOrderBits[processorNumber] & (1 << node.getIndex())) != 0) {
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
            if ((readyToOrderBits[processorNumber] & (1 << node.getIndex())) == 0) {
                continue;
            }

            output.add(createChild(node, processorNumber, totalOrdered, latestFinishTime, historicEstimatedStartTimes));
        }

        return output;
    }

    private void calculateHeuristicInfo(int[] totalOrdered, int[] latestFinishTime, Map<Node, Integer> historicEstimatedStartTimes) {
        OPartialSolution prev = this;
        while (!prev.isEmpty()) {
            historicEstimatedStartTimes.put(prev.getTask(), prev.getEstimatedStartTime());
            totalOrdered[prev.getProcessor()] += prev.getTask().getWeight();
            latestFinishTime[prev.getProcessor()] = Math.max(latestFinishTime[prev.getProcessor()],
                    prev.getEstimatedStartTime() + prev.getTask().getWeight());
            prev = prev.getParent();
        }
    }

    private OPartialSolution createChild(Node node, int processorNumber) {
        int[] totalOrdered = new int[context.getProcessorCount()];
        int[] latestFinishTime = new int[context.getProcessorCount()];
        Map<Node, Integer> historicEstimatedStartTimes = new HashMap<>();
        calculateHeuristicInfo(totalOrdered, latestFinishTime, historicEstimatedStartTimes);
        return createChild(node, processorNumber, totalOrdered, latestFinishTime, historicEstimatedStartTimes);
    }

    private OPartialSolution createChild(Node node, int processorNumber, int[] totalOrdered, int[] latestFinishTime,
                                         Map<Node, Integer> historicEstimatedStartTimes) {
        long[] newOrderedBits = Arrays.copyOf(orderedBits, orderedBits.length);
        long[] newReadyBits = Arrays.copyOf(readyToOrderBits, readyToOrderBits.length);
        newOrderedBits[processorNumber] |= 1 << node.getIndex();
        newReadyBits[processorNumber] &= ~(1 << node.getIndex());

        for(Node maybeNowReady : node.getOutgoingEdges().keySet()) {
            if(allocation.getTasksFor(processorNumber).contains(maybeNowReady)) {
                boolean isNowReady = isTaskReadyToOrder(maybeNowReady, newOrderedBits[processorNumber],
                        allocation.getTasksFor(processorNumber));

                if(isNowReady) {
                    newReadyBits[processorNumber] |= 1 << maybeNowReady.getIndex();
                }
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
        for (int i = 0; i < context.getProcessorCount(); i++) {
            maxOrderedLoad = Math.max(maxOrderedLoad,
                    latestFinishTime[i] + (allocation.getLoadFor(i) - totalOrdered[i]));
        }


        int newEstStartTime = Math.max(latestFinishTime[processorNumber], newDataReadyTime);

        return new OPartialSolution(
                context,
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

    private static boolean isTaskReadyToOrder(Node task, long orderedBits, Set<Node> allocatedTasks) {
        for(Node directDependency : task.getIncomingEdges().keySet()) {
            // if this dependency is on this processor and it hasn't been ordered yet then we can't
            // schedule it
            if(allocatedTasks.contains(directDependency)
                && ((orderedBits & (1 << directDependency.getIndex())) == 0)) {
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

    public boolean isEmpty() {
        return this.parent == null;
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

    public int getEstimatedStartTime() {
        return estimatedStartTime;
    }
}
