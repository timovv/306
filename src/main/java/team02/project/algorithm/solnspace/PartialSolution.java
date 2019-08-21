package team02.project.algorithm.solnspace;

import team02.project.algorithm.Schedule;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;

public interface PartialSolution extends Comparable<PartialSolution> {
    int getEstimatedFinishTime();
    Collection<PartialSolution> expand();
    boolean isComplete();
    Schedule makeComplete();

    default int compareTo(PartialSolution p){
        return this.getEstimatedFinishTime() - p.getEstimatedFinishTime();
    }
}