package team02.project.algorithm;

import lombok.val;

import java.util.LinkedList;

/**
 * Bounds any complete schedules which exceed the current upper bound
 */
public class NaiveBranchBoundAlgorithm implements SchedulingAlgorithm {
    @Override
    public Schedule calculateOptimal(SchedulingContext ctx) {
        int ubound = Integer.MAX_VALUE; // cache the upper bound
        Schedule best = null;

        LinkedList<Schedule> scheduleStack = new LinkedList<>();
        scheduleStack.add(Schedule.empty());
        while(!scheduleStack.isEmpty()) {
            val schedule = scheduleStack.pop();
            if (schedule.isCompleteFor(ctx)) { // don't expand
                val finishTime = schedule.getFinishTime();
                if(finishTime < ubound) { // update the upper bound
                    best = schedule;
                    ubound = finishTime;
                }

                continue;
            }

            val children = schedule.expand(ctx); // branch
            for(val child : children) {
                if(child.getFinishTime() < ubound) { // bound
                    scheduleStack.push(child);
                }
            }
        }

        return best;
    }
}
