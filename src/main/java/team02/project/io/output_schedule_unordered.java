package team02.project.io;

import lombok.val;
import lombok.var;
import team02.project.algorithm.Schedule;
import team02.project.algorithm.ScheduledTask;
import team02.project.algorithm.SchedulingContext;
import team02.project.graph.Node;

import java.io.*;
import java.util.HashMap;

public class output_schedule_unordered {
    private output_schedule_unordered() {
    }

    public static void outputGraph(String pathName, String fileName, SchedulingContext context, Schedule optimalSchedule) throws IOException {
        File fname = new File(pathName + "/" + fileName + ".dot");
        FileOutputStream fos = new FileOutputStream(fname);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        bw.write("digraph \"" + fileName + "\" {");
        bw.newLine();

        for (var task : optimalSchedule) {
            String nodeStr = "\t" + task.getTask().getId() + " [Weight=" + task.getTask().getWeight() + ", Start=" + task.getStartTime() + ", Processor=" + task.getProcessorId() + "];";
            bw.write(nodeStr);
            bw.newLine();
            for (val edge : task.getTask().getIncomingEdges().entrySet()) {
                String edgeStr = "\t" + edge.getKey().getId() + " -> " + task.getTask().getId() + " [Weight=" + edge.getValue() + "];";
                bw.write(edgeStr);
                bw.newLine();
            }
        }

        bw.write("}");
        bw.close();
    }
}
