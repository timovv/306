package team02.project.algorithm;

import lombok.val;
import team02.project.algorithm.solnspace.PartialSolution;
import team02.project.algorithm.solnspace.SolutionSpace;
import team02.project.algorithm.solnspace.ao.AOSolutionSpace;

import java.util.Comparator;
import java.util.PriorityQueue;

public class AStarAlgorithm implements SchedulingAlgorithm {
    private static final int INITIAL_SIZE = 1000;

    @Override
    public Schedule calculateOptimal(SchedulingContext ctx) {

        PriorityQueue<PartialSolution> scheduleQueue = new PriorityQueue<>(INITIAL_SIZE, new Comparator<PartialSolution>() {
            @Override
            public int compare(PartialSolution p1, PartialSolution p2) {
                return Integer.compare(p1.getEstimatedFinishTime(), p2.getEstimatedFinishTime());
            }
        });
        SolutionSpace solutionSpace = new AOSolutionSpace();
        scheduleQueue.add(solutionSpace.getRoot(ctx));

        while(!scheduleQueue.isEmpty()){
            PartialSolution s = scheduleQueue.poll();
            if(s.isComplete()){
                return s.makeComplete();
            }

            // expand and compute costs
            val children = s.expand();
            scheduleQueue.addAll(children);
        }
        return null;
    }
}