import team02.project.cli.CLIConfig;
import team02.project.cli.CLIException;
import team02.project.cli.CLIParser;
import static team02.project.cli.CLIConstants.*;


public class App {

    public static CLIConfig config;

    public static void main(String[] args)  {
        CLIParser parser = new CLIParser();

        try {
            config = parser.parse(args);
            System.out.println(config);
        } catch (CLIException e) {
            System.out.println(MAIN_ERROR_MESSAGE_PRE + e.getExceptionMessage() + "\n");
            System.out.println(parser.getHelp());
            return;
        }
    }
}
