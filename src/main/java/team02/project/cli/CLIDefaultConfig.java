package team02.project.cli;
import static team02.project.cli.CLIConstants.*;

public class CLIDefaultConfig implements CLIConfig {

    protected String inputDOTFile = DEFAULT_INPUT_DOT_FILE_NAME;
    protected int scheduleProcessors = DEFAULT_SCHEDULE_PROCESSORS;
    protected int parallelCores = DEFAULT_PARALLEL_CORES;
    protected boolean visualize = DEFAULT_VISUALIZATION;
    protected String outputDOTFile = DEFAULT_OUTPUT_DOT_FILE_NAME;

    protected CLIDefaultConfig() {}

    @Override
    public String inputDOTFile() {
        return inputDOTFile;
    }

    @Override
    public int numberOfScheduleProcessors() {
        return scheduleProcessors;
    }

    @Override
    public int numberOfParallelCores() {
        return parallelCores;
    }

    @Override
    public boolean isVisualize() {
        return visualize;
    }

    @Override
    public String outputDOTFile() {
        return outputDOTFile;
    }

    @Override
    public String toString() {
        return "The name of the input DOT file is: " + inputDOTFile() + "\n"
                + "The number of schedule cores is: " + numberOfScheduleProcessors() + "\n"
                + "The number of parallel cores is: " + numberOfParallelCores() + "\n"
                + "Visualize: " + isVisualize() + "\n"
                + "The name of the output DOT file is: " + outputDOTFile();
    }
}
