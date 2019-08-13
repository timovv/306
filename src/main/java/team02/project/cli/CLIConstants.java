package team02.project.cli;

public final class CLIConstants {
    public static final String PARALLEL_OPTION_DESCRIPTION = "Use N cores for execution in parallel (default is sequential)";
    public static final String VISUALISE_OPTION_DESCRIPTION = "Visualise the search";
    public static final String OUTPUT_NAME_OPTION_DESCRIPTION = "Output file is named OUTPUT (default is INPUT−output.dot)";
    public static final String INVALID_NUMBER_OF_ARGS_MSG = "Please pass arguments for parameters INPUT.dot and P.";
    public static final String INVALID_OPTIONAL_ARGS = "Unable to parse optional arguments";
    public static final String TOO_MANY_ARGS = "Specified too many optional arguments";
    public static final String DOT_SUFFIX = ".dot";
    public static final String INVALID_INPUT = "Input file must be a .dot file";
    public static final String DEFAULT_OUTPUT_SUFFIX = "-output.dot";
    public static final String NEGATIVE_SCHEDULE_CORES = "P must be a positive integer";
    public static final String NEGATIVE_PARALLEL_CORES = "N must be a positive integer";
    public static final String PARALLEL_FLAG = "p";
    public static final String VISUALIZE_FLAG = "v";
    public static final String CUSTOM_OUTPUT_NAME_FLAG = "o";
    public static final String DEFAULT_INPUT_DOT_FILE_NAME = "INPUT.dot";
    public static final int DEFAULT_SCHEDULE_PROCESSORS = 1;
    public static final int DEFAULT_PARALLEL_CORES = 1;
    public static final boolean DEFAULT_VISUALIZATION = false;
    public static final String DEFAULT_OUTPUT_DOT_FILE_NAME = "INPUT-output.dot";
    public static final String MAIN_ERROR_MESSAGE_PRE = "There has been a problem with parsing arguments: ";
    public static final String INPUT_FILE_NOT_FOUND_ERROR = "The specified input file could not be found.";
    public static final String OUTPUT_FILE_ALREADY_EXISTS_WARNING = "The output file already exists. It will be overwritten.";

    // for the demo:
    public static final String ALGORITHM_FLAG = "a";
    public static final String ALGORITHM_OPTION_DESCRIPTION = "Algorithm to use, options are" +
            "\"Topological\", \"BnB\", \"A*\"(default is Topological)";
    public static final String DEFAULT_ALGORITHM_TO_USE = "Topological";
}
