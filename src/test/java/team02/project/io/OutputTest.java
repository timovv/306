package team02.project.io;

import org.junit.Test;
import team02.project.TestUtils;
import team02.project.algorithm.*;
import team02.project.algorithm.solnspace.ao.AOSolutionSpace;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import static org.junit.Assert.assertEquals;


public class OutputTest {
    @Test
    public void testAStarOutput() throws IOException {
        SchedulingContext ctx = TestUtils.getContext("smol_boi.dot");
        SchedulingAlgorithm algorithm = new AStarAlgorithm(new AOSolutionSpace());
        Schedule schedule = algorithm.calculateOptimal(ctx);


        Set<String> EXPECTED_OUTPUT = new LinkedHashSet<>();
        EXPECTED_OUTPUT.addAll(Arrays.asList(
                    "digraph \"output.dot\" {",
                    "\t1 [Weight=3, Start=0, Processor=1];",
                    "\t2 [Weight=2, Start=3, Processor=1];",
                    "\t1 -> 2 [Weight=1];",
                    "\t3 [Weight=3, Start=5, Processor=1];",
                    "\t1 -> 3 [Weight=2];",
                    "\t4 [Weight=1, Start=8, Processor=1];",
                    "\t2 -> 4 [Weight=3];",
                    "\t3 -> 4 [Weight=2];",
                    "}"
        ));


        OutputSchedule.outputGraph(Paths.get("output.dot"), ctx, schedule);
        File outputFile = new File("output.dot");
        List<String> outputList = Files.readAllLines(outputFile.toPath(), Charset.forName("UTF-8"));
        Set<String> outputSet = new LinkedHashSet<>(outputList);


        assertEquals(EXPECTED_OUTPUT, outputSet);
        outputFile.delete();
    }

    @Test
    public void testBNBOutput() throws IOException {
        SchedulingContext ctx = TestUtils.getContext("smol_boi.dot");
        SchedulingAlgorithm algorithm = new NaiveBranchBoundAlgorithm(new AOSolutionSpace());
        Schedule schedule = algorithm.calculateOptimal(ctx);


        Set<String> EXPECTED_OUTPUT = new LinkedHashSet<>();
        EXPECTED_OUTPUT.addAll(Arrays.asList(
                "digraph \"output.dot\" {",
                "\t1 [Weight=3, Start=0, Processor=1];",
                "\t2 [Weight=2, Start=3, Processor=1];",
                "\t1 -> 2 [Weight=1];",
                "\t3 [Weight=3, Start=5, Processor=1];",
                "\t1 -> 3 [Weight=2];",
                "\t4 [Weight=1, Start=8, Processor=1];",
                "\t2 -> 4 [Weight=3];",
                "\t3 -> 4 [Weight=2];",
                "}"
        ));


        OutputSchedule.outputGraph(Paths.get("output.dot"), ctx, schedule);
        File outputFile = new File("output.dot");
        List<String> outputList = Files.readAllLines(outputFile.toPath(), Charset.forName("UTF-8"));
        Set<String> outputSet = new LinkedHashSet<>(outputList);


        assertEquals(EXPECTED_OUTPUT, outputSet);
        outputFile.delete();

    }


}
