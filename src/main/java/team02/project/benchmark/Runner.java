package team02.project.benchmark;

import lombok.var;
import team02.project.algorithm.NaiveBranchBoundAlgorithm;
import team02.project.benchmark.AlgorithmBenchmark.Result;

public class Runner {
    public static void main(String[] args) {

        System.out.println("Starting...");

        var loader = new TestGraphLoader(
                (nodes, procs) -> nodes == 10  && procs == 4,
                5);

        var benchmark = new AlgorithmBenchmark(NaiveBranchBoundAlgorithm::new);

        for (var testGraph : loader) {
            Result result = benchmark.run(testGraph.getFile(), testGraph.getNumProcessors());
            print(testGraph.getName(), testGraph.getOptimal(), result.getScheduleLength(), result.getTimeTaken());
        }
    }

    private static void print(String name, int expectedLength, int actualLength, long timeTaken) {
        System.out.println(name
                + "\t"
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
