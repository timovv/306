package team02.project.cli;

/**
 * This is interface is for all Config objects
 * This interface allows access to a CLIConfig object
 * which represents the arguments parsed by CLIParser
 *
 * This interface also allows for extendability for further arguments
 */

public interface CLIConfig {

    public String inputDOTFile();

    public int numberOfScheduleProcessors();

    public int numberOfParallelCores();

    public boolean isVisualize();

    public String outputDOTFile();
}
