package team02.project.cli;

import org.apache.commons.cli.*;

import static team02.project.cli.CLIConstants.*;


public class CLIParser {
    /**
     * @param args
     * @return A config object {@link CLIConfig} which contains all the information about arguments and optional
     * arguments the user has given
     * @throws CLIException
     */
    public CLIConfig parse(String[] args) throws CLIException {
        if (invalidNumberOfArgs(args.length)) {
            throw new CLIException(INVALID_NUMBER_OF_ARGS_MSG);
        }

        CommandLine cmd = parseOptionalArgs(getOptionalArgs(args));

        String inputDOTFile = args[0];
        int scheduleProcessors = getScheduleProcessors(args[1]);
        int parallelCores = getParallelCores(cmd);
        boolean visualize = getVisualizeOption(cmd);
        String outputDOTFile = getOutputFileName(cmd);

        CLIConfigBuilder builder = new CLIConfigBuilder();
        CLIConfig config = builder.setInputDOTFile(inputDOTFile)
                .setNumberOfScheduleProcessors(scheduleProcessors)
                .setNumberOfParallelCores(parallelCores)
                .setVisualize(visualize)
                .setOutputDOTFile(outputDOTFile)
                .build();

        return config;
    }

    /**
     * @return A string which contains all the help information required for the command line when
     * the user passes in invalid arguments
     */
    public String getHelp() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 85; i++) {
            sb.append("-");
        }

        sb.append("\n");

        String helpString = "Usage: "+
                "java -jar " + getJarFileName() + " INPUT.dot P [OPTIONS] \n \n" +
                "INPUT.dot (A task graph with integer weights in dot format) \n" +
                "P (Number of processors to schedule the INPUT graph on.) \n" +
                "\n" +
                "Optional: \n" +
                "-p N (Use N cores for execution in parallel (default is sequential).) \n"+
                "-v (Visualise the search.) \n"+
                "-o OUTPUT (Output file is named OUTPUT (default is INPUT-output.dot).)";

        sb.append(helpString);

        sb.append("\n");

        for (int i = 0; i < 85; i++) {
            sb.append("-");
        }

        return sb.toString();
    }


    /**
     *
     * @param P Number of processors in string format
     * @return An integer representation of the number of processors
     * @throws CLIException
     */
    private int getScheduleProcessors(String P) throws CLIException {
        int scheduleProcessors = DEFAULT_SCHEDULE_PROCESSORS;

        try {
            scheduleProcessors = Integer.parseInt(P);
        } catch (NumberFormatException e) {
            throw new CLIException(NEGATIVE_SCHEDULE_CORES);
        }

        return scheduleProcessors;
    }

    /**
     * @param cmd
     * @return A value of how many parallel cores the user wants the algorithm to run on
     * @throws CLIException
     */
    private int getParallelCores(CommandLine cmd) throws CLIException {
        int parallelCores = DEFAULT_PARALLEL_CORES;

        if (cmd.hasOption(PARALLEL_FLAG)) {
            try {
                int tempParallelCores = Integer.parseInt(cmd.getOptionValue(PARALLEL_FLAG));
                parallelCores = tempParallelCores;
            } catch (NumberFormatException e) {
                throw new CLIException(NEGATIVE_PARALLEL_CORES);
            }
        }

        return parallelCores;

    }

    /**
     * @param cmd
     * @return A value specifying whether or not the user want a visualization or not
     */
    private boolean getVisualizeOption(CommandLine cmd) {
        return cmd.hasOption(VISUALIZE_FLAG);
    }


    /**
     * @param cmd
     * @return The output file name the user wants
     */
    private String getOutputFileName(CommandLine cmd) {
        String outputFileName = DEFAULT_OUTPUT_DOT_FILE_NAME;

        if (cmd.hasOption(CUSTOM_OUTPUT_NAME_FLAG)) {
            String userOutputFileName = cmd.getOptionValue(CUSTOM_OUTPUT_NAME_FLAG);
            outputFileName = userOutputFileName;
        }

        return outputFileName;
    }

    /**
     * @param length of the arguments
     * @return a boolean telling us whether the number of arguments is valid or not
     */
    private boolean invalidNumberOfArgs(int length) {
        return length < 2;
    }

    /**
     * @return The options object passed into parsing the optional args
     */
    private Options getOptions() {
        Options options = new Options();

        options.addOption(PARALLEL_FLAG, true, PARALLEL_OPTION_DESCRIPTION);

        options.addOption(VISUALIZE_FLAG, VISUALISE_OPTION_DESCRIPTION);

        options.addOption(CUSTOM_OUTPUT_NAME_FLAG, true, OUTPUT_NAME_OPTION_DESCRIPTION);

        return options;
    }

    /**
     * @param args The arguments passed by the user
     * @return Only the <i> optional </i> arguments
     */
    private String[] getOptionalArgs(String[] args) {
        String[] optional = new String[args.length - 2];

        for (int i = 2; i < args.length; i++) {
            optional[i - 2] = args[i];
        }

        return optional;
    }

    /**
     * @param optionalArgs All the optional arguments
     * @return CommandLine object which contains information about the optional arguments
     * @throws CLIException
     */
    private CommandLine parseOptionalArgs(String[] optionalArgs) throws CLIException {
        CommandLineParser clp = new DefaultParser();
        CommandLine cmd;

        try {
            cmd = clp.parse(getOptions(), optionalArgs);
        } catch (ParseException e) {
            throw new CLIException(INVALID_OPTIONAL_ARGS);
        }

        // After parsing make sure there is no more args left to parse
        if (cmd.getArgList().size() != 0) {
            throw new CLIException(TOO_MANY_ARGS);
        }

        return cmd;
    }

    /**
     * @return A string representation of the jar file when running the application
     */
    private String getJarFileName() {
        return new java.io.File(CLIParser.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath())
                .getName();
    }
}