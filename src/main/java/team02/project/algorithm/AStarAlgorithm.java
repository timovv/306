package team02.project.algorithm;

import lombok.val;

import java.util.Comparator;
import java.util.PriorityQueue;

public class AStarAlgorithm implements SchedulingAlgorithm {
    private static final int INITIAL_SIZE = 1000;

    @Override
    public Schedule calculateOptimal(SchedulingContext ctx) {

        PriorityQueue<Schedule> scheduleQueue = new PriorityQueue(INITIAL_SIZE);
        scheduleQueue.add(Schedule.empty());

        while(!scheduleQueue.isEmpty()){
            Schedule s = scheduleQueue.poll();
            if(s.isCompleteFor(ctx)){
                return s;
            }

            // expand and compute costs
            val children = s.expand(ctx);
            for(val child : children) {
                scheduleQueue.add(child);
            }
        }
        return null;
    }
}