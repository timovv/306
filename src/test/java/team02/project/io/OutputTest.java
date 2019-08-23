package team02.project.io;

import org.junit.Test;
import team02.project.TestUtils;
import team02.project.algorithm.AStarAlgorithm;
import team02.project.algorithm.Schedule;
import team02.project.algorithm.SchedulingAlgorithm;
import team02.project.algorithm.SchedulingContext;
import team02.project.algorithm.solnspace.ao.AOSolutionSpace;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class OutputTest {

    @Test
    public void testOutput() throws IOException {
        SchedulingContext ctx = TestUtils.getContext("smol_boi.dot");
        SchedulingAlgorithm algorithm = new AStarAlgorithm(new AOSolutionSpace());
        Schedule schedule = algorithm.calculateOptimal(ctx);

        OutputSchedule.outputGraph(Paths.get("output.dot"), ctx, schedule);

        File file = new File("output.dot");
        file.delete();

    }

}
