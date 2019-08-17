package team02.project.algorithm.solnspace;

import team02.project.algorithm.SchedulingContext;

public interface SolutionSpace {
    PartialSolution getRoot(SchedulingContext ctx);
}
