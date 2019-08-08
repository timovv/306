package team02.project.benchmark;

import lombok.Value;
import lombok.var;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.function.BiFunction;

public class TestGraphLoader implements Iterable<TestGraphLoader.TestGraph> {

    private static final String PREFIX = "/team02/project/testGraphs/";
    private static final String GRAPH_FILE = "/team02/project/graphs.csv";
    private static final String DELIMITER = ",";
    private static final String EXTENSION = ".dot";

    private final Map<String, TestGraph> testGraphs = new HashMap<>();

    /**
     * Loads a selection of test graphs from resources folder
     * @param selectionPred Given number of nodes and number of processors in input graph, return true
     *                      if the current graph should be added to selection
     * @param size Number of graphs to load and benchmark
     */
    public TestGraphLoader(BiFunction<Integer, Integer, Boolean> selectionPred, int size) {
        var clazz = getClass();
        var scanner = new Scanner(clazz.getResourceAsStream(GRAPH_FILE));
        int count = 0;
        while (scanner.hasNextLine()) {
            String[] parts = scanner.nextLine().split(DELIMITER);
            var name = parts[0];
            int numNodes = Integer.parseInt(parts[1]);
            int numProcessors = Integer.parseInt(parts[2]);
            int optimal = Integer.parseInt(parts[3]);

            if (!selectionPred.apply(numNodes, numProcessors)) {
                continue;
            }
            var tg = new TestGraph(
                    clazz.getResourceAsStream(PREFIX + name + EXTENSION), name,
                    numNodes, numProcessors, optimal);

            testGraphs.put(name, tg);
            if (size == ++count) {
                break;
            }
        }
    }

    public TestGraph get(String fileName) {
        return testGraphs.get(fileName);
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<TestGraph> iterator() {
        return testGraphs.values().iterator();
    }


    @Value
    public static class TestGraph {
        InputStream file;
        String name;
        int numNodes;
        int numProcessors;
        int optimal;
    }
}
