package team02.project.algorithm.solnspace;

import team02.project.algorithm.SchedulingContext;

/**
 * A solution space in the task scheduling problem.
 */
public interface SolutionSpace {

    /**
     * Generate the root of the solution space for this instance of the task scheduling problem.
     * Search algorithms are able to use this root to look through the solution space.
     *
     * @param ctx Context for which to build the solution space for.
     * @return The root of the solution space.
     */
    PartialSolution getRoot(SchedulingContext ctx);
}
