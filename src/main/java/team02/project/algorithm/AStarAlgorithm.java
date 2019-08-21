package team02.project.algorithm;

import lombok.val;
import team02.project.algorithm.solnspace.PartialSolution;
import team02.project.algorithm.solnspace.SolutionSpace;
import team02.project.algorithm.solnspace.ao.AOSolutionSpace;

import java.util.Comparator;
import java.util.PriorityQueue;

public class AStarAlgorithm implements SchedulingAlgorithm {
    private static final int INITIAL_SIZE = 1000;
    private SolutionSpace solutionSpace;

    public AStarAlgorithm(SolutionSpace solutionSpace) {
        this.solutionSpace = solutionSpace;
    }

    @Override
    public Schedule calculateOptimal(SchedulingContext ctx) {

        PriorityQueue<PartialSolution> scheduleQueue = new PriorityQueue<>(INITIAL_SIZE,
                Comparator.comparingInt(PartialSolution::getEstimatedFinishTime));
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