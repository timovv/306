package team02.project;

import team02.project.algorithm.Schedule;
import team02.project.algorithm.SchedulingAlgorithm;
import team02.project.algorithm.SchedulingContext;
import team02.project.algorithm.TopologicalSortAlgorithm;
import team02.project.cli.CLIConfig;
import team02.project.cli.CLIException;
import team02.project.cli.CLIParser;
import team02.project.graph.Graph;
import team02.project.graph.GraphBuilder;
import team02.project.graph.GraphBuilderImpl;
import team02.project.io.GraphParseException;
import team02.project.io.GraphReader;
import team02.project.io.OutputSchedule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static team02.project.cli.CLIConstants.*;


public class App {

    private static int EXIT_FAILURE = 1;

    public static void main(String[] args)  {
        CLIConfig config = getOptions(args);

        // TODO: would probably be better if the input file validation was with the CLI stuff but it's a bit tricky
        Path inputFile = Paths.get(config.inputDOTFile());
        if(!Files.exists(inputFile)) {
            System.out.println(INPUT_FILE_NOT_FOUND_ERROR);
            System.exit(EXIT_FAILURE);
            return;
        }

        Path outputFile = Paths.get(config.outputDOTFile());
        if(Files.exists(outputFile)) {
            System.out.println(OUTPUT_FILE_ALREADY_EXISTS_WARNING);
        }

        // Input and output paths are found, time to create the graph!
        Graph graph = createGraph(inputFile);
        SchedulingContext ctx = new SchedulingContext(graph, config.numberOfScheduleProcessors());
        Schedule maybeOptimal = calculateSchedule(config, graph, ctx);
        writeOutput(outputFile, ctx, maybeOptimal);
        System.out.println("Schedule output successfully");
    }

    private static CLIConfig getOptions(String[] args) {
        CLIConfig config;
        CLIParser parser = new CLIParser();
        try {
            config = parser.parse(args);
        } catch (CLIException e) {
            System.out.println(MAIN_ERROR_MESSAGE_PRE + e.getExceptionMessage() + "\n");
            System.out.println(parser.getHelp());
            System.exit(EXIT_FAILURE);
            return null;
        }

        return config;
    }

    private static void writeOutput(Path outputFile, SchedulingContext ctx, Schedule maybeOptimal) {
        System.out.println("Schedule calculated, outputting to " + outputFile.toString() + "...");
        try {
            OutputSchedule.outputGraph(outputFile, ctx, maybeOptimal);
        } catch(IOException e) {
            System.out.println("Error outputting graph: " + e.getMessage());
            System.exit(EXIT_FAILURE);
        }
    }

    private static Schedule calculateSchedule(CLIConfig config, Graph graph, SchedulingContext ctx) {
        System.out.println("Determining solution for "
                + config.numberOfScheduleProcessors() +
                " processors on a graph of " + graph.getNodes().size() + " nodes");
        // TODO: command-line switch to allow users to select algorithm
        SchedulingAlgorithm algorithm = new TopologicalSortAlgorithm();
        return algorithm.calculateOptimal(ctx);
    }

    private static Graph createGraph(Path inputFile) {
        GraphBuilder builder = new GraphBuilderImpl();
        try {
            GraphReader.readInto(inputFile, builder);
        } catch (GraphParseException e) {
            handleGraphParseError(e);
        }

        try {
            return builder.build();
        } catch(Exception e) {
            System.out.println("Could not build graph: " + e.getMessage());
            System.exit(EXIT_FAILURE);
            return null;
        }
    }

    private static void handleGraphParseError(GraphParseException e) {
        int line = e.getLineNumber();

        if(line != GraphParseException.NO_LINE_NUMBER) {
            System.out.println("Error parsing graph at line " + line + ": " + e.getMessage());
        } else {
            System.out.println("Error parsing graph: " + e.getMessage());
        }

        System.exit(EXIT_FAILURE);
    }


}
