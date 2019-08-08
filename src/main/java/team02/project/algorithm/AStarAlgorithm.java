package team02.project.algorithm;

import lombok.val;

import java.util.Comparator;
import java.util.PriorityQueue;

public class AStarAlgorithm implements SchedulingAlgorithm {
    private static final int INITIAL_SIZE = 100;
    @Override
    public Schedule calculateOptimal(SchedulingContext ctx) {

        PriorityQueue<ComparableSchedule> scheduleQueue = new PriorityQueue(INITIAL_SIZE);
        scheduleQueue.add(ComparableSchedule.empty());

        while(!scheduleQueue.isEmpty()){
            ComparableSchedule s = scheduleQueue.poll();
            if(s.isCompleteFor(ctx)){
                return s;
            }

            // expand and compute costs

        }
    }
}
