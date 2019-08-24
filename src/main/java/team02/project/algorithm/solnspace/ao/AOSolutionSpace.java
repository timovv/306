package team02.project.algorithm.solnspace.ao;

import team02.project.algorithm.SchedulingContext;
import team02.project.algorithm.solnspace.PartialSolution;
import team02.project.algorithm.solnspace.SolutionSpace;

/**
 * A solution space for the scheduling problem.
 *
 * The Allocation Ordering ("AO") solution space works by first allocating tasks to processors and then
 * once all tasks have been allocated, orders the tasks on each processor. By the end, a schedule which is usually
 * valid will have been produced.
 */
public class AOSolutionSpace implements SolutionSpace {

    /**
     * Generates the root of the solution space, in the form of a partial allocation where no tasks have been allocated
     * yet.
     *
     * {@inheritDoc}
     */
    @Override
    public PartialSolution getRoot(SchedulingContext ctx) {
        return APartialSolution.makeEmpty(ctx);
    }
}
