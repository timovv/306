package team02.project.algorithm;

import lombok.Value;
import team02.project.graph.Node;

@Value
public class ScheduledTask {
    int processorId;
    int startTime;
    Node task;

    int getFinishTime() {
        return getStartTime() + task.getWeight();
    }
}
