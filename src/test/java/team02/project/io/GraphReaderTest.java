package team02.project.io;

import lombok.var;
import org.junit.Before;
import org.junit.Test;
import team02.project.graph.Graph;
import team02.project.graph.GraphBuilder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class GraphReaderTest {

    private static Set<String> EXPECTED_OUTPUT = new HashSet<>();
    static {
        EXPECTED_OUTPUT.addAll(Arrays.asList(
                "id: \"OutTree-Balanced-MaxBf-3_Nodes_7_CCR_2.0_WeightType_Random\"",
                "node: 0 weight: 5",
                "node: 1 weight: 6",
                "edge: 0,1 weight: 15",
                "node: 2 weight: 5",
                "edge: 0,2 weight: 11",
                "node: 3 weight: 6",
                "edge: 0,3 weight: 11",
                "node: 4 weight: 4",
                "edge: 1,4 weight: 19",
                "node: 5 weight: 7",
                "edge: 1,5 weight: 4",
                "node: 6 weight: 7",
                "edge: 1,6 weight: 21"
        ));
    }

    private Set<String> output;

    @Before
    public void setUp() {
        output = new HashSet<>();
    }

    private class GraphBuilderStub implements GraphBuilder {
        @Override
        public void setId(String id) {
            output.add("id: " + id);
        }

        @Override
        public void addNode(String id, int weight) {
            output.add("node: " + id + " weight: " + weight);
        }

        @Override
        public void addEdge(String fromId, String toId, int weight) {
            output.add("edge: " + fromId + "," + toId + " weight: " + weight);
        }

        @Override
        public Graph build() {
            throw new UnsupportedOperationException();
        }
    }

    @Test
    public void graphReaderWorks() throws Exception {
        var builder = new GraphBuilderStub();
        GraphReader.readInto(getClass().getResourceAsStream("/team02/project/testGraphs/Nodes_7_OutTree.dot"), builder);
        assertEquals(EXPECTED_OUTPUT, output);
    }
}
