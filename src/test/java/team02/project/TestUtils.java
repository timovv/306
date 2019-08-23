package team02.project;

import team02.project.algorithm.SchedulingContext;
import team02.project.graph.Graph;
import team02.project.graph.GraphBuilder;
import team02.project.graph.GraphBuilderImpl;
import team02.project.io.GraphReader;

import java.nio.file.Path;
import java.nio.file.Paths;

public class TestUtils {

    private static final String PATH_PREFIX = "./test_graphs/dotfiles/";
    private static final Integer PROCESSOR_COUNT = 1;

    public static SchedulingContext getContext(String fileName) {
        GraphBuilder builder = new GraphBuilderImpl();
        GraphReader.readInto(returnPath(fileName), builder);
        Graph graph = builder.build();
        SchedulingContext ctx = new SchedulingContext(graph, PROCESSOR_COUNT);
        return ctx;
    }

    private static Path returnPath(String fileName) {
        Path inputPath = Paths.get(PATH_PREFIX + fileName);
        return inputPath;
    }
}
