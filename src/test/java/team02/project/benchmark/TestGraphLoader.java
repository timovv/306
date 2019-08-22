package team02.project.benchmark;

import lombok.Value;
import lombok.var;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiFunction;

public class TestGraphLoader implements Iterable<TestGraphLoader.TestGraph> {

    private static final String PREFIX = "./test_graphs/dotfiles/";
    private static final String GRAPH_FILE = "./test_graphs/graphs.csv";
    private static final String DELIMITER = ",";
    private static final String EXTENSION = ".dot";

    private final ArrayList<TestGraph> testGraphs = new ArrayList<>();


    /**
     * Loads all test graphs from ./test_graphs/ directory which meets selection criteria
     * @param selectionPred Given number of nodes and number of processors in input graph, return true
     *                      if the current graph should be added to selection
     */
    public TestGraphLoader(BiFunction<Integer, Integer, Boolean> selectionPred) {
        new TestGraphLoader(selectionPred, Integer.MAX_VALUE);
    }

    /**
     * Loads a selection of test graphs from ./test_graphs/ directory
     * @param selectionPred Given number of nodes and number of processors in input graph, return true
     *                      if the current graph should be added to selection
     * @param size Number of graphs to load and benchmark
     * @param matchList The graph file name should match any string in this commas seperated list
     *                  e.g. "Fork_Join,Random,OutTree"
     */
    public TestGraphLoader(BiFunction<Integer, Integer, Boolean> selectionPred, int size, String matchList) {
        try {
            var scanner = new Scanner(Paths.get(GRAPH_FILE));
            int count = 0;
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(DELIMITER);
                var name = parts[0];
                int numNodes = Integer.parseInt(parts[1]);
                int numProcessors = Integer.parseInt(parts[2]);
                int optimal = Integer.parseInt(parts[3]);
                String[] match = matchList.split(",");
                if (!selectionPred.apply(numNodes, numProcessors)
                        || !Arrays.stream(match).parallel().anyMatch(name::contains)) {
                    continue;
                }
                var tg = new TestGraph(
                        Paths.get(PREFIX + name + EXTENSION), name,
                        numNodes, numProcessors, optimal);
                testGraphs.add(tg);
                if (size == ++count) {
                    break;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public TestGraphLoader(BiFunction<Integer, Integer, Boolean> selectionPred, int size) {
        this(selectionPred, size, "");
    }

    /**
     * Loads a selection of graphs from a text file of graph names on each line.
     * @param path The path of the text file to load.
     */
    public TestGraphLoader(String path) {
        Scanner scanner = new Scanner(getClass().getResourceAsStream(path));
        while (scanner.hasNextLine()) {
            String[] parts = scanner.nextLine().split(DELIMITER);
            var name = parts[0];
            int numNodes = Integer.parseInt(parts[1]);
            int numProcessors = Integer.parseInt(parts[2]);
            int optimal = Integer.parseInt(parts[3]);
            var tg = new TestGraph(Paths.get(PREFIX + name + EXTENSION), name, numNodes, numProcessors, optimal);
            testGraphs.add(tg);
        }
        scanner.close();
    }

    /**
     * Lookup a loaded {@link TestGraph} based on its filename
     * @param fileName The filename as {@link String} of expected test graph
     * @return The {@link TestGraph} or null if doesn't exist
     */
    public TestGraph get(String fileName) {
        return testGraphs.stream().filter(x -> x.getName().equalsIgnoreCase(fileName)).findFirst().orElse(null);
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     * @return an Iterator.
     */
    @Override
    public Iterator<TestGraph> iterator() {
        return testGraphs.iterator();
    }


    @Value
    public static class TestGraph {
        Path file;
        String name;
        int numNodes;
        int numProcessors;
        int optimal;
    }
}
