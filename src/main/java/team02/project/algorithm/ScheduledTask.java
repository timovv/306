package team02.project.algorithm;

import lombok.Value;
import team02.project.graph.Node;

/**
 * Represents the assignment of a node to a processor and a start-time.
 */
@Value
public class ScheduledTask {
    int processorId;
    int startTime;
    Node task;

    int getFinishTime() {
        return getStartTime() + task.getWeight();
    }
}
