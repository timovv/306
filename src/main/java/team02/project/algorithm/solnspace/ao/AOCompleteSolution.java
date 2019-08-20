package team02.project.algorithm.solnspace.ao;

import team02.project.algorithm.Schedule;
import team02.project.algorithm.ScheduledTask;
import team02.project.algorithm.SchedulingContext;
import team02.project.algorithm.solnspace.PartialSolution;
import team02.project.graph.Node;

import java.util.*;

public class AOCompleteSolution implements PartialSolution {

    private Schedule completeSchedule = null;
    private boolean isValid = true;

    private final SchedulingContext context;
    private final OPartialSolution ordering;

    public AOCompleteSolution(SchedulingContext context, OPartialSolution ordering) {
        this.context = context;
        this.ordering = ordering;
    }

    @Override
    public int getEstimatedFinishTime() {
        if(completeSchedule == null) {
            buildComplete();
        }

        if(isValid) {
            return completeSchedule.getFinishTime();
        } else {
            return Integer.MAX_VALUE;
        }
    }

    @Override
    public Set<PartialSolution> expand() {
        return Collections.emptySet();
    }

    @Override
    public boolean isComplete() {
        // only complete if solution is valid
        if(completeSchedule == null) {
            buildComplete();
        }

        return isValid;
    }

    @Override
    public Schedule makeComplete() {
        if(completeSchedule == null) {
            buildComplete();
        }

        if(!isValid) {
            throw new UnsupportedOperationException();
        }

        return completeSchedule;
    }

    private void buildComplete() {
        Map<Node, ScheduledTask> scheduleMap = new HashMap<>();
        int[] lastFinishTimes = new int[context.getProcessorCount()];

        List<LinkedList<OPartialSolution>> solutions = new ArrayList<>(context.getProcessorCount());
        for(int i = 0; i < context.getProcessorCount(); ++i) {
            solutions.add(new LinkedList<>());
        }

        OPartialSolution current = ordering;
        while(!current.isEmptyOrdering()) {
            solutions.get(current.getProcessor()).addFirst(current);
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

        completeSchedule = new Schedule(new HashSet<>(scheduleMap.values()));
    }
}
