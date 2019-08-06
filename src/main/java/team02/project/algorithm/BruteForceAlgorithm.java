package team02.project.algorithm;

import lombok.val;

import java.util.Comparator;
import java.util.LinkedList;

/**
 * Exhaustive-search based scheduling algorithm
 */
public class BruteForceAlgorithm implements SchedulingAlgorithm {
    public Schedule calculateOptimal(SchedulingContext ctx) {
        val start = Schedule.empty();
        val open = new LinkedList<Schedule>();
        val complete = new LinkedList<Schedule>();

        open.push(start);

        while(!open.isEmpty()) {
            val schedule = open.pop();
            val children = schedule.expand(ctx);
            if(children.isEmpty()) {
                complete.add(schedule);
            }
            open.addAll(children);
        }

        return complete.stream()
                .min(Comparator.comparing(Schedule::getFinishTime))
                .orElseThrow(() -> new RuntimeException("no schedule found wtF?"));
    }
}
