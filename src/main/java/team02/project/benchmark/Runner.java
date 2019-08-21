package team02.project.benchmark;

import lombok.var;
import team02.project.algorithm.NaiveBranchBoundAlgorithm;
import team02.project.algorithm.ParallelBranchAndBound;
import team02.project.algorithm.SchedulingAlgorithm;
import team02.project.algorithm.solnspace.ao.AOSolutionSpace;
import team02.project.benchmark.AlgorithmBenchmark.Result;

import java.util.function.Supplier;

public class Runner {
    public static void main(String[] args) {

        System.out.println("Starting...");

        var loader = new TestGraphLoader(
                (nodes, procs) -> nodes == 10 && procs == 4,
                5);

        var benchmark = new AlgorithmBenchmark(() -> new ParallelBranchAndBound(new AOSolutionSpace()));

        for (var testGraph : loader) {
            Result result = benchmark.run(testGraph.getFile(), testGraph.getNumProcessors());
            System.out.print(testGraph.getName());
            print(testGraph.getOptimal(), result.getScheduleLength(), result.getTimeTaken());
        }
    }

    private static void print(int expectedLength, int actualLength, long timeTaken) {
        System.out.println(
                "\t"
                + ((expectedLength == actualLength) ? "Optimal" : "Not Optimal")
                + "\t"
                + "Expected: " + expectedLength
                + "\t"
                + "Actual: " + actualLength
                + "\t"
                + timeTaken
                + "ms"
        );
    }

}
