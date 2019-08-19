package team02.project.algorithm;

import lombok.val;
import team02.project.algorithm.solnspace.PartialSolution;
import team02.project.algorithm.solnspace.SolutionSpace;
import team02.project.algorithm.solnspace.ao.AOSolutionSpace;
import team02.project.algorithm.solnspace.els.ELSSolutionSpace;

import java.util.LinkedList;

/**
 * Bounds any complete schedules which exceed the current upper bound
 */
public class NaiveBranchBoundAlgorithm implements SchedulingAlgorithm {
    @Override
    public Schedule calculateOptimal(SchedulingContext ctx) {
        int ubound = Integer.MAX_VALUE; // cache the upper bound
        PartialSolution best = null;

        LinkedList<PartialSolution> scheduleStack = new LinkedList<>();
        SolutionSpace solutionSpace = new AOSolutionSpace();
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

        assert best != null;
        return best.makeComplete();
    }
}
