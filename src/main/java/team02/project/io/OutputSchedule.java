package team02.project.io;

import lombok.val;
import lombok.var;
import team02.project.algorithm.Schedule;
import team02.project.algorithm.ScheduledTask;
import team02.project.algorithm.SchedulingContext;
import team02.project.graph.Node;

import java.io.PrintWriter;
import java.io.IOException;
import java.nio.file.Path;


import java.util.HashMap;

public class OutputSchedule {
    private OutputSchedule() {
    }

    /**
     * Outputs the optimal graph in dotfile format. Order of nodes in output file is same as input file.
     * @param pathName the path to output dotfile
     * @param context current scheduling context
     * @param optimalSchedule optimal schedule returned by scheduling algorithm
     * @throws IOException
     */
    public static void outputGraph(Path pathName, SchedulingContext context, Schedule optimalSchedule) throws IOException {
        PrintWriter writer = new PrintWriter(pathName.toString());

        writer.println("digraph \"" + pathName.getFileName() + "\" {");

        HashMap<String, ScheduledTask> scheduledTaskHashMap = new HashMap<>();
        for (var scheduledTask : optimalSchedule.getTasks()) {
            scheduledTaskHashMap.put(scheduledTask.getTask().getId(), scheduledTask);
        }

        for (Node n : context.getTaskGraph().getNodes()) {
            String nodeStr = "\t" + n.getId()
                    + " [Weight=" + n.getWeight()
                    + ", Start=" + scheduledTaskHashMap.get(n.getId()).getStartTime()
                    + ", Processor=" + (scheduledTaskHashMap.get(n.getId()).getProcessorId() + 1) + "];";
            writer.println(nodeStr);

            for(int i = 0; i < n.getIncomingEdgeNodes().length; ++i) {
                String edgeStr = "\t" + n.getIncomingEdgeNodes()[i].getId() + " -> " + n.getId() + " [Weight=" + n.getIncomingEdgeWeights()[i] + "];";
                writer.println(edgeStr);
            }
        }
        writer.println("}");
        writer.flush();
        writer.close();
    }

}
