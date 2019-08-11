package team02.project.io;

import lombok.val;
import lombok.var;
import team02.project.algorithm.Schedule;
import team02.project.algorithm.ScheduledTask;
import team02.project.algorithm.SchedulingContext;
import team02.project.graph.Node;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

import java.util.HashMap;

public class outputSchedule {
    private outputSchedule() {
    }

    public static void outputGraph(String pathName, String fileName, SchedulingContext context, Schedule optimalSchedule) throws IOException {
        File fname = new File(pathName + "/" + fileName + ".dot");
        FileOutputStream fos = new FileOutputStream(fname);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        bw.write("digraph \"" + fileName + "\" {");
        bw.newLine();

        HashMap<Integer, ScheduledTask> scheduledTaskHashMap = new HashMap<>();
        for (var scheduledTask : optimalSchedule) {
            scheduledTaskHashMap.put(scheduledTask.getTask().getId(), scheduledTask);
        }

        for (Node n : context.getTaskGraph().getNodes()) {
            String nodeStr = "\t" + n.getId() + " [Weight=" + n.getWeight() + ", Start=" + scheduledTaskHashMap.get(n.getId()).getStartTime() + ", Processor=" + scheduledTaskHashMap.get(n.getId()).getProcessorId() + "];";
            bw.write(nodeStr);
            bw.newLine();

            for (val edge : n.getIncomingEdges().entrySet()) {
                String edgeStr = "\t" + edge.getKey().getId() + " -> " + n.getId() + " [Weight=" + edge.getValue() + "];";
                bw.write(edgeStr);
                bw.newLine();
            }
        }
        bw.write("}");
        bw.flush();
        bw.close();
    }

}
