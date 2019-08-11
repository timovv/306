package team02.project.algorithm;

@FunctionalInterface
public interface SchedulingAlgorithm {
    /**
     * Given a {@link SchedulingContext} containing the {@link team02.project.graph.Graph task graph}
     * and the number of processors in schedule, compute the optimal schedule.
     * @param ctx The {@link SchedulingContext} of the algorithm
     * @return The optimal schedule
     */
    Schedule calculateOptimal(SchedulingContext ctx);
}
