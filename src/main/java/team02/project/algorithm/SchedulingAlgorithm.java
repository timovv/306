package team02.project.algorithm;

@FunctionalInterface
public interface SchedulingAlgorithm {
    Schedule calculateOptimal(SchedulingContext ctx);
}
