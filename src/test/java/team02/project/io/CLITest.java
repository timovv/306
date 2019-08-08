package team02.project.io;

import org.junit.Before;
import org.junit.Test;
import team02.project.cli.CLIConfig;
import team02.project.cli.CLIException;
import team02.project.cli.CLIParser;
import static org.junit.Assert.assertEquals;


public class CLITest {

    private CLIParser parser;

    @Before
    public void setUp() {
        parser = new CLIParser();
    }

    @Test
    public void testConfigWithNoOptions() throws CLIException {
        String[] args = new String[]{"test.dot", "4"};
        CLIConfig config = parser.parse(args);
        assertEquals(config.inputDOTFile(), "test.dot");
        assertEquals(config.numberOfScheduleProcessors(), 4);
        assertEquals(config.numberOfParallelCores(), 1);
        assertEquals(config.isVisualize(), false);
        assertEquals(config.outputDOTFile(), "test-output.dot");
    }
}
