package team02.project.benchmark;

import lombok.Value;
import lombok.var;
import team02.project.algorithm.SchedulingAlgorithm;
import team02.project.algorithm.SchedulingContext;
import team02.project.graph.GraphBuilderImpl;
import team02.project.io.GraphReader;
import team02.project.io.output_schedule;
import team02.project.io.output_schedule_unordered;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Supplier;

public class AlgorithmBenchmark {

    private SchedulingAlgorithm algorithm;

    public AlgorithmBenchmark(Supplier<SchedulingAlgorithm> algorithmSupplier) {
        this.algorithm = algorithmSupplier.get();
    }

    public Result run(Path testGraph, int processorCount) {
        var graphBuilder = new GraphBuilderImpl();
        GraphReader.readInto(testGraph, graphBuilder);
        var context = new SchedulingContext(graphBuilder.build(), processorCount);

        // Only recording time taken by algorithm
        long start = System.currentTimeMillis();
        var schedule = algorithm.calculateOptimal(context);
        long end = System.currentTimeMillis();

        try {
            String current = new java.io.File(".").getCanonicalPath();
            output_schedule_unordered.outputGraph(current, "something_else", context, schedule);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Result(end - start, schedule.getFinishTime());
    }

    @Value
    public static class Result {
        long timeTaken;
        int scheduleLength;
    }
}
