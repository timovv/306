package team02.project.io.algorithm.AStar;

import org.junit.Before;
import org.junit.Test;
import team02.project.algorithm.*;
import team02.project.algorithm.solnspace.PartialSolution;
import team02.project.algorithm.solnspace.SolutionSpace;
import team02.project.algorithm.solnspace.ao.AOCompleteSolution;
import team02.project.algorithm.solnspace.ao.AOSolutionSpace;
import team02.project.algorithm.solnspace.ao.APartialSolution;
import team02.project.graph.Graph;
import team02.project.graph.GraphBuilder;
import team02.project.graph.GraphBuilderImpl;
import team02.project.io.GraphReader;

import java.nio.file.Path;
import java.nio.file.Paths;

public class AStarTest {


    SchedulingContext testCtx;

    @Before
    public void setUp() {
        Path inputFile = Paths.get("./test_graphs/dotfiles/smol_boi-output.dot");
        GraphBuilder builder = new GraphBuilderImpl();
        GraphReader.readInto(inputFile, builder);
        Graph graph = builder.build();
        SchedulingContext ctx = new SchedulingContext(graph, 1);
        this.testCtx = ctx;
    }

    @Test
    public void testFirst() {
        SchedulingAlgorithm alg = new AStarAlgorithm();
        Schedule sched = alg.calculateOptimal(this.testCtx);

        SolutionSpace solutionSpace = new AOSolutionSpace();
        solutionSpace.getRoot(this.testCtx);

    }


}
