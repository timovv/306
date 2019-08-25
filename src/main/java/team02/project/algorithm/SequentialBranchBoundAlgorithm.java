package team02.project.algorithm;

import lombok.val;
import team02.project.algorithm.solnspace.PartialSolution;
import team02.project.algorithm.solnspace.SolutionSpace;
import team02.project.algorithm.solnspace.ao.APartialSolution;
import team02.project.graph.Node;

import java.util.LinkedList;

/**
 * Bounds any complete schedules which exceed the current upper bound
 */
public class SequentialBranchBoundAlgorithm implements SchedulingAlgorithm {

    private SolutionSpace solutionSpace;
    private AlgorithmMonitor monitor = null;

    public SequentialBranchBoundAlgorithm(SolutionSpace solutionSpace) {
        this.solutionSpace = solutionSpace;
    }

    public SequentialBranchBoundAlgorithm(SolutionSpace solutionSpace, AlgorithmMonitor monitor) {
        this.solutionSpace = solutionSpace;
        this.monitor = monitor;
    }

    @Override
    public Schedule calculateOptimal(SchedulingContext ctx) {
        Schedule simpleListSchedule = new TopologicalSortAlgorithm().calculateOptimal(ctx);
        int ubound = simpleListSchedule.getFinishTime();
        PartialSolution best = null;

        long schedulesCreated = 0;
        long allocationsExpanded = 0;
        long orderingsExpanded = 0;

        LinkedList<PartialSolution> scheduleStack = new LinkedList<>();
        scheduleStack.add(solutionSpace.getRoot(ctx));
        while(!scheduleStack.isEmpty()) {
            val schedule = scheduleStack.pop();
            if (schedule.isComplete()) { // don't expand
                schedulesCreated++;
                val estimate = schedule.getEstimatedFinishTime();
                if(estimate < ubound) { // update the upper bound
                    best = schedule;
                    if(monitor != null){
                        monitor.setCurrentBest(best.makeComplete());
                    }
                    ubound = estimate;
                }
                continue;
            }

            if (monitor != null) {
                if (schedule instanceof APartialSolution) {
                    allocationsExpanded++;
                } else {
                    orderingsExpanded++;
                }
            }

            val children = schedule.expand(); // branch
            for(val child : children) {
                if(child.getEstimatedFinishTime() < ubound) { // bound
                    scheduleStack.push(child);
                }
            }

            if (monitor !=  null) {
                monitor.setAllocationsExpanded(allocationsExpanded);
                monitor.setOrderingsExpanded(orderingsExpanded);
                monitor.setCompleteSchedules(schedulesCreated);
            }
        }

        if(best == null) {
            return simpleListSchedule;
        }

        return best.makeComplete();
    }
}
