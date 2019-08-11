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

    public static void outputGraph(Path pathName, SchedulingContext context, Schedule optimalSchedule) throws IOException {
        PrintWriter writer = new PrintWriter(pathName.toString());

        writer.println("digraph \"" + pathName.getFileName() + "\" {");

        HashMap<Integer, ScheduledTask> scheduledTaskHashMap = new HashMap<>();
        for (var scheduledTask : optimalSchedule) {
            scheduledTaskHashMap.put(scheduledTask.getTask().getId(), scheduledTask);
        }

        for (Node n : context.getTaskGraph().getNodes()) {
            String nodeStr = "\t" + n.getId() + " [Weight=" + n.getWeight() + ", Start=" + scheduledTaskHashMap.get(n.getId()).getStartTime() + ", Processor=" + scheduledTaskHashMap.get(n.getId()).getProcessorId() + "];";
            writer.println(nodeStr);

            for (val edge : n.getIncomingEdges().entrySet()) {
                String edgeStr = "\t" + edge.getKey().getId() + " -> " + n.getId() + " [Weight=" + edge.getValue() + "];";
                writer.println(edgeStr);
            }
        }
        writer.println("}");
        writer.flush();
        writer.close();
    }

}
