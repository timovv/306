package team02.project.algorithm.solnspace.els;

import team02.project.algorithm.SchedulingContext;
import team02.project.algorithm.solnspace.PartialSolution;
import team02.project.algorithm.solnspace.SolutionSpace;

/**
 * The Exhaustive List Scheduling ("ELS") solution space works by scheduling tasks one by one, placing them on each
 * processor separately.
 */
public class ELSSolutionSpace implements SolutionSpace {
    @Override
    public PartialSolution getRoot(SchedulingContext ctx) {
        return ELSPartialSolution.makeEmpty(ctx);
    }
}
