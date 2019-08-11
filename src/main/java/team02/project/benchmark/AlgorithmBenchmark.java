package team02.project.benchmark;

import lombok.Value;
import lombok.var;
import team02.project.algorithm.SchedulingAlgorithm;
import team02.project.algorithm.SchedulingContext;
import team02.project.graph.GraphBuilderImpl;
import team02.project.io.GraphReader;

import java.nio.file.Path;
import java.util.function.Supplier;

/**
 * Basic benchmark to time (in milliseconds) the run time of any {@link SchedulingAlgorithm}
 */
public class AlgorithmBenchmark {

    private SchedulingAlgorithm algorithm;


    public AlgorithmBenchmark(Supplier<SchedulingAlgorithm> algorithmSupplier) {
        this.algorithm = algorithmSupplier.get();
    }

    /**
     * Run the supplied algorithm and return the time taken to {@link SchedulingAlgorithm#calculateOptimal}
     * @param testGraph The file as {@link Path} of the input dotfile
     * @param processorCount The number of processors tasks are to be assigned to
     * @return The {@link Result} containing time taken (ms) and the computed schedule length
     */
    public Result run(Path testGraph, int processorCount) {
        var graphBuilder = new GraphBuilderImpl();
        GraphReader.readInto(testGraph, graphBuilder);
        var context = new SchedulingContext(graphBuilder.build(), processorCount);

        // Only recording time taken by algorithm
        long start = System.currentTimeMillis();
        var schedule = algorithm.calculateOptimal(context);
        long end = System.currentTimeMillis();

        return new Result(end - start, schedule.getFinishTime());
    }

    @Value
    public static class Result {
        long timeTaken;
        int scheduleLength;
    }
}
