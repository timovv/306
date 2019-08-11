package team02.project.cli;

import static team02.project.cli.CLIConstants.*;

/**
 * Builds a custom {@link CLIConfig} implementation
 */
public class CLIConfigBuilder {

    private CLIDefaultConfig config;

    public CLIConfigBuilder() {
        clearExistingConfig();
    }

    private void clearExistingConfig() {
        config = new CLIDefaultConfig();
    }

    public CLIConfigBuilder setInputDOTFile(String inputDOTFile) throws CLIException {
        if (!isValidDotSuffix(inputDOTFile)) {
            throw new CLIException(INVALID_INPUT);
        }

        config.inputDOTFile = inputDOTFile;

        String outputFileString = inputDOTFile.substring(0, inputDOTFile.length() - 4) + DEFAULT_OUTPUT_SUFFIX;
        config.outputDOTFile = outputFileString;

        return this;
    }

    public CLIConfigBuilder setNumberOfScheduleProcessors(int scheduleProcessors) throws CLIException {
        if (scheduleProcessors <= 0) {
            throw new CLIException(NEGATIVE_SCHEDULE_CORES);
        }

        config.scheduleProcessors = scheduleProcessors;
        return this;
    }

    public CLIConfigBuilder setNumberOfParallelCores(int parallelCores) throws CLIException {
        if (parallelCores <= 0) {
            throw new CLIException(NEGATIVE_PARALLEL_CORES);
        }

        config.parallelCores = parallelCores;
        return this;
    }

    public CLIConfigBuilder setVisualize(boolean visualize) {
        config.visualize = visualize;
        return this;
    }

    public CLIConfigBuilder setOutputDOTFile(String outputDOTFile) {
        if (!outputDOTFile.equals(DEFAULT_OUTPUT_DOT_FILE_NAME)) {
            config.outputDOTFile = outputDOTFile;
        }
        return this;
    }

    public CLIConfig build() {
        CLIConfig returnConfig = config;
        clearExistingConfig();
        return returnConfig;
    }

    private boolean isValidDotSuffix(String inputDOTFile) {
        int length = inputDOTFile.length();
        return inputDOTFile.substring(length - 4, length).equals(DOT_SUFFIX);
    }
}
