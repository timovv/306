package team02.project.algorithm.solnspace.ao;

import team02.project.algorithm.Schedule;
import team02.project.algorithm.ScheduledTask;
import team02.project.algorithm.SchedulingContext;
import team02.project.algorithm.solnspace.PartialSolution;
import team02.project.graph.Node;

import java.util.*;

/**
 * A complete solution in the AO solution space. This class contains methods
 * that help with the creation of a {@link Schedule} from the now complete
 * ordering and allocation.
 */
public class AOCompleteSolution implements PartialSolution {

    /**
     * Cache the complete schedule so that we don't call the expensive {@link AOCompleteSolution#buildComplete()}
     * multiple times
     */
    private Schedule completeSchedule = null;

    /**
     * Stores whether the schedule built by {@link AOCompleteSolution#buildComplete()} is complete
     */
    private boolean isValid = true;

    private final SchedulingContext context;

    /**
     * The final node in this solution in the tree.
     */
    private final OPartialSolution ordering;

    /**
     * Create a new AOCompleteSolution from the given ordering.
     * @param ordering A leaf node in the solution space where all tasks have been allocated and ordered.
     *                 {@link OPartialSolution#isOrderingComplete()} must return true.
     */
    public AOCompleteSolution(OPartialSolution ordering) {
        if(!ordering.isOrderingComplete()) {
            throw new IllegalArgumentException("ordering must represent a complete ordering!");
        }

        this.ordering = Objects.requireNonNull(ordering);
        this.context = ordering.getContext();
    }

    /**
     * {@inheritDoc}
     *
     * In the case of AOCompleteSolution, the estimated finish time is no longer an estimate, as a complete
     * schedule can be constructed. Note that calling this method will cause the schedule to be built, which could
     * be an expensive operation.
     *
     * @return The true finish time of the complete schedule. If the schedule represented by this object is invalid (i.e.
     *         it contains a cycle), then {@link Integer#MAX_VALUE} will be returned.
     */
    @Override
    public int getEstimatedFinishTime() {
        if(completeSchedule == null) {
            buildComplete();
        }

        if(isValid) {
            return completeSchedule.getFinishTime();
        } else {
            // If it's an invalid schedule, output a large number so that the algorithm knows it's a bad output.
            return Integer.MAX_VALUE;
        }
    }

    /**
     * Since this schedule is complete, there is no more expansion to do.
     * @return The empty set
     */
    @Override
    public Set<PartialSolution> expand() {
        return Collections.emptySet();
    }

    /**
     * {@inheritDoc}
     *
     * @return true if the schedule represented is valid, otherwise false.
     */
    @Override
    public boolean isComplete() {
        // only complete if solution is valid
        if(completeSchedule == null) {
            buildComplete();
        }

        return isValid;
    }

    /**
     * Makes a complete schedule.
     * @return the completed schedule, if it is valid
     * @throws UnsupportedOperationException if the completed schedule is not valid
     */
    @Override
    public Schedule makeComplete() {
        if(completeSchedule == null) {
            buildComplete();
        }

        if(!isValid) {
            throw new UnsupportedOperationException("Tried to call makeComplete on an invalid schedule");
        }

        return completeSchedule;
    }

    /**
     * Builds the complete schedule.
     */
    private void buildComplete() {
        Map<Node, ScheduledTask> scheduleMap = new HashMap<>();
        int[] lastFinishTimes = new int[context.getProcessorCount()];

        // 1. construct the ordering from first to last

        List<LinkedList<OPartialSolution>> solutions = new ArrayList<>(context.getProcessorCount());
        for(int i = 0; i < context.getProcessorCount(); ++i) {
            solutions.add(new LinkedList<>());
        }

        OPartialSolution current = ordering;
        while(!current.isEmptyOrdering()) {
            solutions.get(current.getProcessor()).addFirst(current);
            current = current.getParent();
        }

        // 2. remove an item from each processor at a time, skipping to the next processor where it's not possible
        //    when we fail to remove an item, we are either done or we have encountered a cycle and should stop
        boolean removedSomething = true;
        while(removedSomething) {
            removedSomething = false;

            outer:
            for(LinkedList<OPartialSolution> processorTasks : solutions) { // try to schedule 1 task from each processor
                if(processorTasks.isEmpty()) {
                    continue;
                }

                OPartialSolution solution = processorTasks.getFirst();

                int startTime = lastFinishTimes[solution.getProcessor()];
                for(Map.Entry<Node, Integer> edge : solution.getTask().getIncomingEdges().entrySet()) {
                    ScheduledTask scheduled = scheduleMap.get(edge.getKey());
                    if(scheduled == null) {
                        continue outer; // can't schedule this node right now
                    }

                    startTime = Math.max(
                            startTime,
                            scheduled.getFinishTime() + (scheduled.getProcessorId() == solution.getProcessor() ? 0 : edge.getValue())
                    );
                }

                ScheduledTask sc = new ScheduledTask(solution.getProcessor(), startTime, solution.getTask());
                lastFinishTimes[solution.getProcessor()] = sc.getFinishTime();
                scheduleMap.put(solution.getTask(), sc);
                removedSomething = true;
                processorTasks.removeFirst(); // it's scheduled
            }
        }

        // if one of the lists still has something there is a cycle so we should mark the solution as invalid.
        for(LinkedList<OPartialSolution> solution : solutions) {
            if (!solution.isEmpty()) {
                isValid = false;
                break;
            }
        }

        // all done
        completeSchedule = new Schedule(new HashSet<>(scheduleMap.values()));
    }
}
