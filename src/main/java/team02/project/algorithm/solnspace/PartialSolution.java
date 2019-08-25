package team02.project.algorithm.solnspace;

import team02.project.algorithm.Schedule;

import java.util.Set;

/**
 * A partial solution to the weighted task scheduling problem.
 */
public interface PartialSolution {

    /**
     * The estimated (heuristic) finish time of any complete solution this PartialSolution is a part of.
     * This value is guaranteed to be an underestimate of the true finish time.
     *
     * @return the estimated finish time.
     */
    int getEstimatedFinishTime();

    /**
     * Expands this PartialSolution, outputting child partial solutions of which this PartialSolution is part of.
     * @return children partial solutions
     */
    Set<PartialSolution> expand();

    /**
     * Determines whether this PartialSolution represents a complete solution to the problem
     * @return true if this is a complete solution; false otherwise
     */
    boolean isComplete();

    /**
     * Creates a schedule from this PartialSolution. {@link #isComplete()} must return true to call this method, since
     * it will raise an exception if this is not the case.
     * @return the completed schedule
     * @throws UnsupportedOperationException if this PartialSolution is not a complete solution and thus no complete schedule can be made
     */
    Schedule makeComplete();
}
