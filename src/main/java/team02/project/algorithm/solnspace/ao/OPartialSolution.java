package team02.project.algorithm.solnspace.ao;

import team02.project.algorithm.Schedule;
import team02.project.algorithm.ScheduledTask;
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

    private OPartialSolution(SchedulingContext context, Allocation allocation, OPartialSolution parent, Node task, int depth, int processor) {
        this.context = context;
        this.allocation = allocation;
        this.parent = parent;
        this.task = task;
        this.depth = depth;
        this.processor = processor;
    }

    public static OPartialSolution makeEmpty(SchedulingContext ctx, Allocation allocation) {
        return new OPartialSolution(ctx, allocation, null, null, 0, 0);
    }

    @Override
    public int getEstimate() {
        if(isComplete()) {
            return makeComplete().getFinishTime();
        } else {
            return 0;
        }
    }

    @Override
    public Set<PartialSolution> expand() {
        if(isComplete()) {
            return Collections.emptySet();
        }

        Set<PartialSolution> output = expandProcessor(processor);

        if(output.isEmpty()) {
            output = expandProcessor(processor + 1);
        }

        return output;
    }

    private Set<PartialSolution> expandProcessor(int processorNumber) {
        Set<PartialSolution> output = new HashSet<>();
        for(Node node : allocation.getTasksFor(processorNumber)) {
            if(orderingContains(node) || !orderingSatisfiesDependenciesFor(node, processorNumber)) {
                continue;
            }
            output.add(new OPartialSolution(context, allocation, this, node, depth + 1, processorNumber));
        }

        return output;
    }

    private boolean isEmptyOrdering() {
        return parent == null;
    }

    private boolean orderingContains(Node node) {
        OPartialSolution current = this;
        while(!current.isEmptyOrdering()) {
            if(current.getTask().equals(node)) {
                return true;
            }
            current = current.getParent();
        }

        return false;
    }

    private boolean orderingSatisfiesDependenciesFor(Node node, int processorNumber) {
        OPartialSolution current = this;

        int expected = 0, actual = 0;

        // find dependencies on this processor
        for(Node n : allocation.getTasksFor(processorNumber)) {
            if(node.getDependencies().contains(n) && !orderingContains(node)) {
                ++expected;
            }
        }

        while(!current.isEmptyOrdering()) {
            if(current.processor == processorNumber && node.getDependencies().contains(current.getTask())) {
                ++actual;
            }

            current = current.getParent();
        }

        return expected == actual;
    }

    public OPartialSolution getParent() {
        return parent;
    }

    @Override
    public boolean isComplete() {
        return depth == context.getTaskGraph().getNodes().size();
    }

    @Override
    public Schedule makeComplete() {
        Map<Node, ScheduledTask> scheduleMap = new HashMap<>();
        int[] lastFinishTimes = new int[context.getProcessorCount()];

        List<LinkedList<OPartialSolution>> solutions = new ArrayList<>(context.getProcessorCount());
        for(int i = 0; i < context.getProcessorCount(); ++i) {
            solutions.add(new LinkedList<>());
        }

        OPartialSolution current = this;
        while(!current.isEmptyOrdering()) {
            solutions.get(current.processor).addFirst(current);
            current = current.getParent();
        }

        boolean removedSomething = true;
        while(removedSomething) {
            removedSomething = false;

            outer:
            for(LinkedList<OPartialSolution> processorTasks : solutions) { // try to schedule 1 task from each processor
                if(processorTasks.isEmpty()) {
                    continue;
                }

                OPartialSolution solution = processorTasks.getFirst();

                int startTime = lastFinishTimes[solution.processor];
                for(Map.Entry<Node, Integer> edge : solution.getTask().getIncomingEdges().entrySet()) {
                    ScheduledTask scheduled = scheduleMap.get(edge.getKey());
                    if(scheduled == null) {
                        continue outer; // can't schedule this node right now
                    }

                    startTime = Math.max(
                            startTime,
                            scheduled.getFinishTime() + (scheduled.getProcessorId() == solution.processor ? 0 : edge.getValue())
                    );
                }

                ScheduledTask sc = new ScheduledTask(solution.processor, startTime, solution.task);
                lastFinishTimes[solution.processor] = sc.getFinishTime();
                scheduleMap.put(solution.task, sc);
                removedSomething = true;
                processorTasks.removeFirst(); // it's scheduled
            }
        }

        return new Schedule(new HashSet<>(scheduleMap.values()));
    }

    public Node getTask() {
        return task;
    }
}
