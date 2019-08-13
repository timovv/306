package team02.project.cli;

/**
 * This is interface is for all Config objects
 * This interface allows access to a CLIConfig object
 * which represents the arguments parsed by CLIParser
 *
 * This interface also allows for extendability for further arguments
 */

public interface CLIConfig {

    /**
     * Get the filename as {@link String} of input dotfile
     * @return The input's filename
     */
    public String inputDOTFile();

    /**
     * Get number of processors that tasks will be assigned to
     * @return The number of processors
     */
    public int numberOfScheduleProcessors();

    /**
     * Get number of parallel threads to utilize while performing scheduling
     * @return The number of parallel threads
     */
    public int numberOfParallelCores();

    /**
     * Determine if visualization is to be displayed
     * @return True if visualization expected
     */
    public boolean isVisualize();

    /**
     * Get the filename as {@link String} of output dotfile
     * @return The output's filename
     */
    public String outputDOTFile();

    /**
     * Get the algorithm to use.
     * @return The name of the algorithm to use
     */
    public String algorithmToUse();
}
