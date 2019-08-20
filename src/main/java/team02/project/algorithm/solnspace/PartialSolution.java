package team02.project.algorithm.solnspace;

import team02.project.algorithm.Schedule;

import java.util.Set;

public interface PartialSolution {
    int getEstimatedFinishTime();
    Set<PartialSolution> expand();
    boolean isComplete();
    Schedule makeComplete();
}
