package team02.project.algorithm;

import lombok.val;
import team02.project.algorithm.solnspace.PartialSolution;
import team02.project.algorithm.solnspace.SolutionSpace;
import team02.project.algorithm.solnspace.ao.AOSolutionSpace;
import team02.project.algorithm.solnspace.els.ELSSolutionSpace;
import team02.project.graph.Node;

import java.util.LinkedList;

/**
 * Bounds any complete schedules which exceed the current upper bound
 */
public class SequentialBranchBoundAlgorithm implements SchedulingAlgorithm {
    private SolutionSpace solutionSpace;

    public SequentialBranchBoundAlgorithm(SolutionSpace solutionSpace) {
        this.solutionSpace = solutionSpace;
    }

    @Override
    public Schedule calculateOptimal(SchedulingContext ctx) {
        Schedule simpleListSchedule = new TopologicalSortAlgorithm().calculateOptimal(ctx);
        int ubound = simpleListSchedule.getFinishTime();
        PartialSolution best = null;

        LinkedList<PartialSolution> scheduleStack = new LinkedList<>();
        scheduleStack.add(solutionSpace.getRoot(ctx));
        while(!scheduleStack.isEmpty()) {
            val schedule = scheduleStack.pop();
            if (schedule.isComplete()) { // don't expand
                val estimate = schedule.getEstimatedFinishTime();
                if(estimate < ubound) { // update the upper bound
                    best = schedule;
                    ubound = estimate;
                }

                continue;
            }

            val children = schedule.expand(); // branch
            for(val child : children) {
                if(child.getEstimatedFinishTime() < ubound) { // bound
                    scheduleStack.push(child);
                }
            }
        }

        if(best == null) {
            return simpleListSchedule;
        }

        return best.makeComplete();
    }
}
