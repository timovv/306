package team02.project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import team02.project.algorithm.*;
import team02.project.algorithm.solnspace.ao.AOSolutionSpace;
import team02.project.algorithm.AlgorithmMonitor;
import team02.project.cli.CLIConfig;
import team02.project.cli.CLIException;
import team02.project.cli.CLIParser;
import team02.project.graph.Graph;
import team02.project.graph.GraphBuilder;
import team02.project.graph.GraphBuilderImpl;
import team02.project.io.GraphParseException;
import team02.project.io.GraphReader;
import team02.project.io.OutputSchedule;
import team02.project.visualization.MainController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static team02.project.cli.CLIConstants.*;


public class App extends Application{

    private static int EXIT_FAILURE = 1;

    private static CLIConfig config;
    private static AlgorithmMonitor monitor;
    private static Path inputFile;
    private static Path outputFile;

    public static void main(String[] args)  {
        config = getOptions(args);

        inputFile = Paths.get(config.inputDOTFile());
        if(!Files.exists(inputFile)) {
            System.out.println(INPUT_FILE_NOT_FOUND_ERROR);
            System.exit(EXIT_FAILURE);
            return;
        }

        outputFile = Paths.get(config.outputDOTFile());
        if(Files.exists(outputFile)) {
            System.out.println(OUTPUT_FILE_ALREADY_EXISTS_WARNING);
        }

        if (config.isVisualize()) {
            // run with visualization
            launch();
        } else {
            // run it normally
            runAlgorithm();
        }
        System.exit(0);
    }

    private static void runAlgorithm() {
        Graph graph = createGraph(inputFile);
        System.out.println("Determining solution for "
                + config.numberOfScheduleProcessors() +
                " processors on a graph of " + graph.getNodes().size() + " nodes");
        SchedulingContext ctx = new SchedulingContext(graph, config.numberOfScheduleProcessors());
        SchedulingAlgorithm algorithm = selectAlgorithm(config);
        Schedule maybeOptimal =  algorithm.calculateOptimal(ctx);
        writeOutput(outputFile, ctx, maybeOptimal);
        System.out.println("Schedule output successfully");
    }

    /**
     * Select the algorithm to use based on what the user specifies and the input graph
     * @param config
     * @return
     */
    private static SchedulingAlgorithm selectAlgorithm(CLIConfig config) {
        if(config.isVisualize()) {
            return new SequentialBranchBoundAlgorithm(new AOSolutionSpace(), monitor);
        } else if(config.numberOfParallelCores() == 1) {
            return new AStarAlgorithm(new AOSolutionSpace());
        } else {
            return new ParallelBranchAndBound(new AOSolutionSpace(), config.numberOfParallelCores());
        }
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

    public static void writeOutput(Path outputFile, SchedulingContext ctx, Schedule maybeOptimal) {
        System.out.println("Schedule calculated, outputting to " + outputFile.toString() + "...");
        try {
            OutputSchedule.outputGraph(outputFile, ctx, maybeOptimal);
        } catch(IOException e) {
            System.out.println("Error outputting graph: " + e.getMessage());
            System.exit(EXIT_FAILURE);
        }
    }

    public static Graph createGraph(Path inputFile) {
        GraphBuilder builder = new GraphBuilderImpl();
        try {
            GraphReader.readInto(inputFile, builder);
        } catch (GraphParseException e) {
            handleGraphParseError(e);
        }

        try {
            return builder.build();
        } catch(Exception e) {
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

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/Main.fxml"));
            MainController mainController = new MainController();
            loader.setController(mainController);
            Parent root = loader.load();

            // initialise everything
            mainController.injectConfig(config);
            monitor = new AlgorithmMonitor(null, 0, 0, 0);
            mainController.injectMonitor(monitor);
            mainController.init();

            // Register MainController as a listener;

            // Run the algorithm on another thread
            new Thread(App::runAlgorithm).start();

            primaryStage.setTitle("Team-02 Algorithm Scheduler");

            // initializing scene
            Scene mainScene = new Scene(root);

            //setting and showing stage
            primaryStage.setResizable(false);
            primaryStage.setScene(mainScene);

            // proper exit
            primaryStage.setOnCloseRequest(event -> System.exit(0));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
